package pos.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pos.demo.DemoStore;
import pos.demo.DemoStore.Invoice;

public final class InvoicesPage extends Ui.Page {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private final DemoStore store;
    private final DefaultTableModel model = Ui.model("Mã hóa đơn", "Ngày bán", "Khách hàng", "Tổng tiền", "Trạng thái");
    private final JTable table = Ui.table(model);
    private final JTextField query = Ui.search("Tìm mã hóa đơn", 17, this::refresh);
    private final JTextField from = new JTextField(10), to = new JTextField(10);
    private final JLabel summary = Ui.label(" ", 12, Ui.MUTED, false);
    private LocalDate start, end;
    private List<Invoice> rows = List.of();
    public InvoicesPage(DemoStore store) {
        super("Hóa đơn", "Tra cứu giao dịch và xem lại từng sản phẩm trong hóa đơn.");
        this.store = store;
        from.setToolTipText("dd/MM/yyyy, để trống nếu không giới hạn"); to.setToolTipText(from.getToolTipText());
        JPanel card = Ui.card(new BorderLayout(0, 16));
        card.add(Ui.column(12, Ui.row(Ui.field("Mã hóa đơn", query), Ui.field("Từ ngày (dd/MM/yyyy)", from),
            Ui.field("Đến ngày (dd/MM/yyyy)", to)),
            Ui.row(Ui.button("Lọc theo ngày", true, this::applyDates), Ui.button("Xóa bộ lọc", false, () -> {
                start = null; end = null; from.setText(""); to.setText(""); query.setText(""); refresh();
            }), Ui.button("Xem chi tiết", false, this::details))), BorderLayout.NORTH);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) details(); } });
        card.add(Ui.scroll(table)); card.add(summary, BorderLayout.SOUTH); body.add(card); refresh();
    }
    @Override public void refresh() {
        if (store == null) return;
        rows = store.invoices().stream().filter(i -> Ui.key(i.id()).contains(Ui.key(query.getText())))
            .filter(i -> start == null || !i.date().toLocalDate().isBefore(start))
            .filter(i -> end == null || !i.date().toLocalDate().isAfter(end)).toList();
        model.setRowCount(0);
        for (Invoice i : rows) model.addRow(new Object[]{i.id(), i.date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            i.customer().name(), Ui.money(i.total()), "Đã thanh toán · mẫu"});
        summary.setText(rows.isEmpty() ? "Không có hóa đơn phù hợp với bộ lọc." : rows.size() + " hóa đơn  ·  Tổng cộng " + Ui.money(rows.stream().mapToLong(Invoice::total).sum()));
    }
    private void applyDates() {
        try {
            LocalDate a = from.getText().isBlank() ? null : LocalDate.parse(from.getText().trim(), DATE);
            LocalDate b = to.getText().isBlank() ? null : LocalDate.parse(to.getText().trim(), DATE);
            if (a != null && b != null && a.isAfter(b)) { Ui.error(this, "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc."); return; }
            start = a; end = b; refresh();
        } catch (DateTimeParseException ex) { Ui.error(this, "Nhập ngày hợp lệ theo dd/MM/yyyy, ví dụ 07/09/2026."); }
    }
    private void details() {
        int row = table.getSelectedRow();
        if (row < 0) Ui.error(this, "Chọn một hóa đơn để xem chi tiết.");
        else ReceiptDialog.show(this, rows.get(row));
    }
}
