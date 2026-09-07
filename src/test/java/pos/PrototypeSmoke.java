package pos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import pos.demo.DemoStore;
import pos.demo.DemoStore.Customer;
import pos.demo.DemoStore.Product;
import pos.view.PosFrame;
import pos.view.Ui;

/** Explicit smoke runner: data checks are headless; --ui also requires a desktop session. */
public final class PrototypeSmoke {
    private PrototypeSmoke() { }
    public static void main(String[] args) throws Exception {
        checkData();
        if (args.length > 0 && args[0].equals("--ui")) {
            Path output = args.length > 1 ? Path.of(args[1]) : Path.of("target/ui-preview");
            Files.createDirectories(output);
            SwingUtilities.invokeAndWait(() -> checkUi(output));
        }
        System.out.println("PASS: prototype checks" + (args.length > 0 ? " including Swing UI" : " (data only)"));
    }
    private static void checkData() {
        DemoStore store = new DemoStore();
        int initialInvoices = store.invoices().size(), initialStock = store.product("SP001").stock();
        var paid = store.checkout(Map.of("SP001", 2), DemoStore.GUEST, 10, 8);
        check(paid.subtotal() == 58000 && paid.discount() == 5800 && paid.vat() == 4176 && paid.total() == 56376, "Discount/VAT arithmetic");
        check(store.invoices().size() == initialInvoices + 1 && store.product("SP001").stock() == initialStock - 2, "Checkout in-session updates");
        check(store.invoices().stream().anyMatch(i -> i.id().equals(paid.id())), "Paid invoice listed");
        Product original = store.product("SP001");
        store.saveProduct(new Product(original.id(), "Tên đã sửa", original.category(), original.unit(), 40000, original.stock(), original.minimum()), original.id());
        check(paid.lines().getFirst().name().equals("Cà phê sữa đá") && paid.total() == 56376, "Invoice retains product snapshot");
        Map<String, Integer> invalid = new LinkedHashMap<>(); invalid.put("SP001", 1); invalid.put("SP008", 1);
        rejected(() -> store.checkout(invalid, DemoStore.GUEST, 0, 0));
        check(store.product("SP001").stock() == initialStock - 2 && store.invoices().size() == initialInvoices + 1, "Rejected checkout has no partial changes");
        rejected(() -> store.checkout(Map.of(), null, 0, 0));
        rejected(() -> store.preview(Map.of("SP001", -1), null, 0, 0));
        rejected(() -> store.preview(Map.of("SP001", 1), null, 101, 0));
        rejected(() -> store.saveProduct(original, null));
        rejected(() -> store.saveCustomer(new Customer("KH_NEW", "Tên mẫu", "abc"), null));
        store.saveCustomer(new Customer("KH_NEW", "Khách mới", "0900000000"), null);
        check(store.customers().size() == 6, "Customer creation");
        check(Ui.key("Cà phê ĐÁ").equals("ca phe da"), "Vietnamese search normalization");
        check(new DemoStore().invoices().size() == initialInvoices, "New sessions reset fixtures");
    }
    private static void checkUi(Path output) {
        Ui.install(); PosFrame frame = new PosFrame();
        try {
            frame.setVisible(true); frame.validate();
            String[] pages = {"Tổng quan", "Bán hàng", "Sản phẩm", "Khách hàng", "Hóa đơn", "Báo cáo"};
            for (int i = 0; i < pages.length; i++) {
                String current = pages[i]; frame.navigate(current); frame.validate();
                check(find(frame, JToggleButton.class).stream().anyMatch(b -> b.getText().equals(current) && b.isSelected()), "Navigation selected");
                capture(frame, output.resolve((i + 1) + ".png"));
            }
            frame.navigate("Sản phẩm");
            JTextField search = visible(frame, JTextField.class).getFirst();
            search.setText("ca phe");
            check(visible(frame, JTable.class).getFirst().getRowCount() == 1, "Product search filters table");
            search.setText("no-match-xyz");
            check(visible(frame, JTable.class).getFirst().getRowCount() == 0, "Empty product result");
            search.setText("");
            frame.navigate("Khách hàng");
            visible(frame, JTextField.class).getFirst().setText("0912345678");
            check(visible(frame, JTable.class).getFirst().getRowCount() == 1, "Customer phone search");
            frame.navigate("Bán hàng");
            List<JTable> tables = visible(frame, JTable.class);
            JTable catalog = tables.stream().filter(t -> t.getColumnName(2).equals("Còn lại")).findFirst().orElseThrow();
            JTable cart = tables.stream().filter(t -> t.getColumnName(1).equals("SL")).findFirst().orElseThrow();
            catalog.setRowSelectionInterval(0, 0); click(frame, "Thêm vào hóa đơn");
            check(cart.getRowCount() == 1 && cart.getValueAt(0, 1).equals(1), "Add to cart");
            cart.setRowSelectionInterval(0, 0); click(frame, "+ 1");
            check(cart.getValueAt(0, 1).equals(2), "Increase cart quantity");
            click(frame, "Bỏ dòng"); check(cart.getRowCount() == 0, "Remove cart line");
            check(!visible(frame, JButton.class).stream().filter(b -> b.getText().equals("Thanh toán mẫu")).findFirst().orElseThrow().isEnabled(), "Empty cart blocks payment");
            frame.setSize(1180, 800);
            for (int i = 0; i < pages.length; i++) {
                frame.navigate(pages[i]); frame.validate(); capture(frame, output.resolve("small-" + (i + 1) + ".png"));
            }
        } finally { frame.dispose(); }
    }
    private static void capture(JFrame frame, Path path) {
        BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics(); frame.paintAll(graphics); graphics.dispose();
        try { ImageIO.write(image, "png", path.toFile()); } catch (Exception ex) { throw new RuntimeException(ex); }
    }
    private static void click(Container parent, String text) {
        visible(parent, JButton.class).stream().filter(b -> text.equals(b.getText())).findFirst().orElseThrow().doClick();
    }
    private static <T extends Component> List<T> visible(Container parent, Class<T> type) {
        return find(parent, type).stream().filter(Component::isShowing).toList();
    }
    private static <T extends Component> List<T> find(Container parent, Class<T> type) {
        List<T> found = new ArrayList<>();
        for (Component child : parent.getComponents()) {
            if (type.isInstance(child)) found.add(type.cast(child));
            if (child instanceof Container container) found.addAll(find(container, type));
        }
        return found;
    }
    private static void rejected(Runnable action) {
        try { action.run(); throw new AssertionError("Expected invalid input to be rejected"); }
        catch (IllegalArgumentException expected) { }
    }
    private static void check(boolean valid, String message) { if (!valid) throw new AssertionError(message); }
}
