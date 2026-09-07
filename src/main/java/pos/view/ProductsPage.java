package pos.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pos.demo.DemoStore;
import pos.demo.DemoStore.Product;

public final class ProductsPage extends Ui.Page {
    private final DemoStore store;
    private final Runnable changed;
    private final DefaultTableModel model = Ui.model("Mã SP", "Tên sản phẩm", "Loại", "Đơn vị", "Đơn giá", "Tồn kho", "Trạng thái");
    private final JTable table = Ui.table(model);
    private final JTextField search = Ui.search("Tìm mã hoặc tên sản phẩm", 22, this::refresh);
    private final JComboBox<String> category = new JComboBox<>(new String[]{"Tất cả loại", "Đồ uống", "Đồ ăn", "Sữa & thực phẩm", "Tiện ích"});
    private final JCheckBox lowOnly = new JCheckBox("Dưới mức tối thiểu");
    private final JLabel count = Ui.label(" ", 12, Ui.MUTED, false);
    private List<Product> rows = List.of();

    public ProductsPage(DemoStore store, Runnable changed) {
        super("Sản phẩm", "Quản lý danh mục và theo dõi tồn kho của cửa hàng.");
        this.store = store; this.changed = changed;
        JPanel card = Ui.card(new BorderLayout(0, 18));
        category.addActionListener(e -> refresh()); lowOnly.addActionListener(e -> refresh()); lowOnly.setOpaque(false);
        card.add(Ui.column(14, Ui.row(Ui.field("Tìm kiếm", search), Ui.field("Loại sản phẩm", category), lowOnly),
            Ui.row(Ui.button("+ Thêm sản phẩm", true, () -> edit(null)), Ui.button("Sửa sản phẩm", false, this::editSelected),
                Ui.button("Xóa", false, this::removeSelected))), BorderLayout.NORTH);
        table.getColumnModel().getColumn(1).setPreferredWidth(230);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);
        table.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) editSelected(); } });
        card.add(Ui.scroll(table)); card.add(count, BorderLayout.SOUTH); body.add(card); refresh();
    }
    @Override public void refresh() {
        if (store == null) return;
        String query = Ui.key(search.getText());
        rows = store.products().stream().filter(p -> Ui.key(p.id() + " " + p.name()).contains(query))
            .filter(p -> category.getSelectedIndex() == 0 || p.category().equals(category.getSelectedItem()))
            .filter(p -> !lowOnly.isSelected() || p.lowStock()).toList();
        model.setRowCount(0);
        for (Product p : rows) model.addRow(new Object[]{p.id(), p.name(), p.category(), p.unit(), Ui.money(p.price()), p.stock(),
            p.stock() == 0 ? "Hết hàng" : p.lowStock() ? "Sắp hết · dưới " + p.minimum() : "Còn hàng"});
        long low = store.products().stream().filter(Product::lowStock).count();
        count.setText(rows.isEmpty() ? "Không tìm thấy sản phẩm. Thử đổi từ khóa hoặc bộ lọc." : rows.size() + " sản phẩm hiển thị  ·  " + low + " sản phẩm dưới mức tối thiểu");
    }
    private Product selected() {
        int index = table.getSelectedRow();
        if (index < 0) { Ui.error(this, "Chọn một sản phẩm trong bảng trước."); return null; }
        return rows.get(index);
    }
    private void editSelected() { Product product = selected(); if (product != null) edit(product); }
    private void removeSelected() {
        Product product = selected();
        if (product != null && Ui.confirm(this, "Xóa “" + product.name() + "” khỏi dữ liệu của phiên mẫu?")) {
            store.removeProduct(product.id()); changed.run();
        }
    }
    private void edit(Product p) {
        JTextField id = new JTextField(p == null ? "" : p.id(), 18); id.setEditable(p == null);
        JTextField name = new JTextField(p == null ? "" : p.name(), 24);
        JComboBox<String> kind = new JComboBox<>(new String[]{"Đồ uống", "Đồ ăn", "Sữa & thực phẩm", "Tiện ích"});
        if (p != null) kind.setSelectedItem(p.category());
        JTextField unit = new JTextField(p == null ? "Cái" : p.unit());
        JTextField price = new JTextField(p == null ? "0" : Long.toString(p.price()));
        JTextField stock = new JTextField(p == null ? "0" : Integer.toString(p.stock()));
        JTextField minimum = new JTextField(p == null ? "5" : Integer.toString(p.minimum()));
        JPanel form = new JPanel(new GridLayout(0, 2, 16, 16)); form.setOpaque(false);
        form.add(Ui.field("Mã sản phẩm *", id)); form.add(Ui.field("Tên sản phẩm *", name));
        form.add(Ui.field("Loại sản phẩm *", kind)); form.add(Ui.field("Đơn vị tính *", unit));
        form.add(Ui.field("Đơn giá (đ) *", price)); form.add(Ui.field("Số lượng tồn *", stock));
        form.add(Ui.field("Mức tồn tối thiểu *", minimum));
        Ui.editor(this, p == null ? "Thêm sản phẩm mẫu" : "Sửa sản phẩm mẫu", form, () -> {
            store.saveProduct(new Product(id.getText().trim(), name.getText().trim(), kind.getSelectedItem().toString(),
                unit.getText().trim(), Long.parseLong(price.getText().trim()), Integer.parseInt(stock.getText().trim()),
                Integer.parseInt(minimum.getText().trim())), p == null ? null : p.id());
            changed.run();
        });
    }
}
