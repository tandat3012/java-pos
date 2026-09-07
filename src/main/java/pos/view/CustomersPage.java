package pos.view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pos.demo.DemoStore;
import pos.demo.DemoStore.Customer;

public final class CustomersPage extends Ui.Page {
    private final DemoStore store;
    private final Runnable changed;
    private final DefaultTableModel model = Ui.model("Mã khách hàng", "Họ và tên", "Số điện thoại", "Hóa đơn mẫu", "Tổng mua");
    private final JTable table = Ui.table(model);
    private final JTextField search = Ui.search("Tìm theo mã, tên hoặc số điện thoại", 30, this::refresh);
    private final JLabel count = Ui.label(" ", 12, Ui.MUTED, false);
    private List<Customer> rows = List.of();
    public CustomersPage(DemoStore store, Runnable changed) {
        super("Khách hàng", "Tra cứu khách quen nhanh chóng. Có thể bán hàng mà không chọn khách.");
        this.store = store; this.changed = changed;
        JPanel card = Ui.card(new BorderLayout(0, 18));
        card.add(Ui.row(Ui.field("Tìm khách hàng", search), Ui.button("+ Thêm khách hàng", true, () -> edit(null)),
            Ui.button("Sửa thông tin", false, () -> {
                int index = table.getSelectedRow();
                if (index < 0) Ui.error(this, "Chọn khách hàng cần sửa."); else edit(rows.get(index));
            })), BorderLayout.NORTH);
        card.add(Ui.scroll(table)); card.add(count, BorderLayout.SOUTH);
        body.add(card); refresh();
    }
    @Override public void refresh() {
        if (store == null) return;
        String query = Ui.key(search.getText());
        rows = store.customers().stream().filter(c -> Ui.key(c.id() + " " + c.name() + " " + c.phone()).contains(query)).toList();
        model.setRowCount(0);
        for (Customer c : rows) {
            var purchases = store.invoices().stream().filter(i -> i.customer().id().equals(c.id())).toList();
            model.addRow(new Object[]{c.id(), c.name(), c.phone(), purchases.size(), Ui.money(purchases.stream().mapToLong(DemoStore.Invoice::total).sum())});
        }
        count.setText(rows.isEmpty() ? "Không tìm thấy khách hàng. Thử tên hoặc số điện thoại khác." : rows.size() + " khách hàng  ·  Thông tin chỉ lưu trong phiên mẫu");
    }
    private void edit(Customer customer) {
        JTextField id = new JTextField(customer == null ? "" : customer.id(), 24); id.setEditable(customer == null);
        JTextField name = new JTextField(customer == null ? "" : customer.name(), 24);
        JTextField phone = new JTextField(customer == null ? "" : customer.phone(), 24);
        JPanel form = Ui.column(16, Ui.field("Mã khách hàng *", id), Ui.field("Họ và tên *", name), Ui.field("Số điện thoại *", phone));
        Ui.editor(this, customer == null ? "Thêm khách hàng mẫu" : "Sửa khách hàng mẫu", form, () -> {
            store.saveCustomer(new Customer(id.getText().trim(), name.getText().trim(), phone.getText().trim()), customer == null ? null : customer.id());
            changed.run();
        });
    }
}
