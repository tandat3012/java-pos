package pos.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pos.demo.DemoStore;
import pos.demo.DemoStore.Customer;
import pos.demo.DemoStore.Product;

public final class SalesPage extends Ui.Page {
    private final DemoStore store;
    private final Runnable changed;
    private final Map<String, Integer> cart = new LinkedHashMap<>();
    private final DefaultTableModel catalogModel = Ui.model("Sản phẩm", "Đơn giá", "Còn lại");
    private final DefaultTableModel cartModel = Ui.model("Sản phẩm", "SL", "Thành tiền");
    private final JTable catalog = Ui.table(catalogModel), cartTable = Ui.table(cartModel);
    private final JTextField query = Ui.search("Tìm sản phẩm theo tên hoặc mã", 20, this::refreshCatalog);
    private final JTextField customerQuery = Ui.search("Tìm khách hàng theo tên hoặc số điện thoại", 14, this::refreshCustomers);
    private final JComboBox<String> category = new JComboBox<>(new String[]{"Tất cả loại", "Đồ uống", "Đồ ăn", "Sữa & thực phẩm", "Tiện ích"});
    private final JComboBox<Customer> customer = new JComboBox<>();
    private final JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final JSpinner discount = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner vat = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JLabel subtotal = Ui.label(" ", 13, Ui.MUTED, false), reduction = Ui.label(" ", 13, Ui.MUTED, false);
    private final JLabel total = Ui.label("0 đ", 27, Ui.GREEN, true), status = Ui.label(" ", 12, Ui.MUTED, false);
    private final JButton pay = Ui.button("Thanh toán mẫu", true, this::checkout);
    private final JLabel catalogCount = Ui.label(" ", 12, Ui.MUTED, false);
    private List<Product> products = List.of();
    private List<String> cartKeys = List.of();

    public SalesPage(DemoStore store, Runnable changed) {
        super("Bán hàng", "Chọn sản phẩm, kiểm tra hóa đơn và thử thanh toán bằng dữ liệu mẫu.");
        this.store = store; this.changed = changed;
        category.addActionListener(e -> refreshCatalog());
        discount.addChangeListener(e -> refreshCart()); vat.addChangeListener(e -> refreshCart());
        JPanel left = Ui.card(new BorderLayout(0, 14));
        left.add(Ui.column(12, Ui.label("Danh mục sản phẩm", 17, Ui.TEXT, true),
            Ui.field("Tìm tên hoặc mã sản phẩm", query), category), BorderLayout.NORTH);
        catalog.getColumnModel().getColumn(0).setPreferredWidth(240);
        catalog.getColumnModel().getColumn(2).setPreferredWidth(60);
        catalog.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) addSelected(); } });
        left.add(Ui.scroll(catalog));
        quantity.setPreferredSize(new Dimension(65, 36));
        left.add(Ui.column(10, Ui.row(Ui.label("Số lượng", 12, Ui.MUTED, true), quantity, Ui.button("Thêm vào hóa đơn", true, this::addSelected)), catalogCount), BorderLayout.SOUTH);

        JPanel right = Ui.card(new BorderLayout(0, 12));
        JPanel heading = new JPanel(new BorderLayout()); heading.setOpaque(false);
        heading.add(Ui.label("Hóa đơn mới", 17, Ui.TEXT, true));
        heading.add(Ui.button("Làm mới", false, this::newInvoice), BorderLayout.EAST);
        right.add(Ui.column(10, heading, Ui.field("Tìm khách quen · bỏ trống để bán khách lẻ", customerQuery), customer), BorderLayout.NORTH);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(185);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(35);
        right.add(Ui.scroll(cartTable));
        discount.setPreferredSize(new Dimension(65, 30)); vat.setPreferredSize(new Dimension(65, 30));
        right.add(Ui.column(10,
            Ui.row(Ui.button("− 1", false, () -> adjust(-1)), Ui.button("+ 1", false, () -> adjust(1)), Ui.button("Bỏ dòng", false, this::removeLine)),
            Ui.row(Ui.field("Chiết khấu (%)", discount), Ui.field("VAT (%)", vat)),
            subtotal, reduction, total, pay, status), BorderLayout.SOUTH);
        left.setMinimumSize(new Dimension(360, 0)); right.setMinimumSize(new Dimension(385, 0));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setBorder(null); split.setDividerSize(16); split.setResizeWeight(0.53);
        split.setContinuousLayout(true); body.add(split); refresh();
    }
    @Override public void refresh() { refreshCatalog(); refreshCustomers(); refreshCart(); }
    private void refreshCatalog() {
        if (store == null) return;
        String key = Ui.key(query.getText());
        products = store.products().stream().filter(p -> Ui.key(p.id() + " " + p.name()).contains(key))
            .filter(p -> category.getSelectedIndex() == 0 || category.getSelectedItem().equals(p.category())).toList();
        catalogModel.setRowCount(0);
        products.forEach(p -> catalogModel.addRow(new Object[]{p.name(), Ui.money(p.price()), p.stock() == 0 ? "Hết hàng" : p.stock()}));
        catalogCount.setText(products.isEmpty() ? "Không tìm thấy sản phẩm phù hợp." : products.size() + " sản phẩm  ·  Nhấp đúp để thêm nhanh");
    }
    private void refreshCustomers() {
        if (store == null) return;
        Customer selected = (Customer) customer.getSelectedItem();
        customer.removeAllItems(); customer.addItem(DemoStore.GUEST);
        String key = Ui.key(customerQuery.getText());
        for (Customer c : store.customers()) if (Ui.key(c.id() + " " + c.name() + " " + c.phone()).contains(key)) {
            customer.addItem(c);
            if (selected != null && c.id().equals(selected.id())) customer.setSelectedItem(c);
        }
    }
    private void refreshCart() {
        if (store == null) return;
        int selected = cartTable.getSelectedRow();
        cartKeys = new ArrayList<>(cart.keySet()); cartModel.setRowCount(0);
        for (String id : cartKeys) {
            try {
                Product p = store.product(id);
                cartModel.addRow(new Object[]{p.name(), cart.get(id), Ui.money(p.price() * cart.get(id))});
            } catch (IllegalArgumentException ex) { cartModel.addRow(new Object[]{id + " (đã xóa)", cart.get(id), "—"}); }
        }
        if (selected >= 0 && selected < cartKeys.size()) cartTable.setRowSelectionInterval(selected, selected);
        try {
            var draft = store.preview(cart, (Customer) customer.getSelectedItem(), (int) discount.getValue(), (int) vat.getValue());
            subtotal.setText("Tạm tính: " + Ui.money(draft.subtotal()));
            reduction.setText("Giảm: −" + Ui.money(draft.discount()) + "   |   VAT: " + Ui.money(draft.vat()));
            total.setText(Ui.money(draft.total())); pay.setEnabled(!cart.isEmpty());
            status.setText(cart.isEmpty() ? "Hóa đơn trống · hãy thêm sản phẩm" : cart.size() + " dòng hàng · chỉ lưu trong phiên này");
            status.setForeground(Ui.MUTED); status.setToolTipText(null);
        } catch (IllegalArgumentException ex) {
            total.setText("Cần kiểm tra giỏ hàng"); subtotal.setText("Một sản phẩm đã thay đổi hoặc không đủ tồn.");
            reduction.setText("Giảm số lượng hoặc bỏ dòng không hợp lệ."); pay.setEnabled(false);
            status.setText("Giỏ hàng chưa hợp lệ"); status.setForeground(Ui.RED); status.setToolTipText(ex.getMessage());
        }
    }
    private void addSelected() {
        int row = catalog.getSelectedRow();
        if (row < 0) { Ui.error(this, "Chọn sản phẩm từ danh mục trước."); return; }
        try {
            quantity.commitEdit(); Product p = products.get(row);
            changeQuantity(p.id(), cart.getOrDefault(p.id(), 0) + (int) quantity.getValue());
        } catch (java.text.ParseException ex) { Ui.error(this, "Số lượng cần là số nguyên từ 1 đến 9999."); }
    }
    private void changeQuantity(String id, int value) {
        try {
            if (value <= 0) cart.remove(id);
            else {
                if (value > store.product(id).stock()) { Ui.error(this, "Số lượng vượt tồn kho mẫu."); return; }
                cart.put(id, value);
            }
            refreshCart();
        } catch (IllegalArgumentException ex) { Ui.error(this, ex.getMessage()); }
    }
    private void adjust(int delta) {
        int row = cartTable.getSelectedRow();
        if (row < 0) { Ui.error(this, "Chọn một dòng trong hóa đơn."); return; }
        String id = cartKeys.get(row); changeQuantity(id, cart.get(id) + delta);
    }
    private void removeLine() {
        int row = cartTable.getSelectedRow();
        if (row < 0) { Ui.error(this, "Chọn dòng cần bỏ khỏi hóa đơn."); return; }
        cart.remove(cartKeys.get(row)); refreshCart();
    }
    private void newInvoice() {
        if (!cart.isEmpty() && !Ui.confirm(this, "Bỏ giỏ hàng hiện tại và tạo hóa đơn mẫu mới?")) return;
        cart.clear(); discount.setValue(0); vat.setValue(0); customerQuery.setText(""); customer.setSelectedIndex(0); refreshCart();
    }
    private void checkout() {
        try {
            discount.commitEdit(); vat.commitEdit();
            var draft = store.preview(cart, (Customer) customer.getSelectedItem(), (int) discount.getValue(), (int) vat.getValue());
            if (!Ui.confirm(this, "Xác nhận thanh toán mẫu " + Ui.money(draft.total()) + "?\nHóa đơn chỉ được lưu trong phiên chạy này.")) return;
            var paid = store.checkout(cart, (Customer) customer.getSelectedItem(), (int) discount.getValue(), (int) vat.getValue());
            cart.clear(); discount.setValue(0); vat.setValue(0); changed.run(); ReceiptDialog.show(this, paid);
        } catch (java.text.ParseException ex) { Ui.error(this, "Chiết khấu và VAT cần là số nguyên từ 0 đến 100."); }
        catch (IllegalArgumentException ex) { Ui.error(this, ex.getMessage()); }
    }
}
