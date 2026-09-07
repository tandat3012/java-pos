package pos.view;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import pos.demo.DemoStore.Invoice;

public final class ReceiptDialog {
    private ReceiptDialog() { }
    public static void show(Component parent, Invoice invoice) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Chi tiết " + invoice.id(), Dialog.ModalityType.APPLICATION_MODAL);
        JPanel receipt = Ui.card(new BorderLayout(0, 20));
        receipt.add(Ui.column(8, Ui.label("CỬA HÀNG AN NHIÊN", 20, Ui.TEXT, true),
            Ui.label("HÓA ĐƠN MẪU  ·  " + invoice.id(), 13, Ui.GREEN, true),
            Ui.label(invoice.date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "   |   " + invoice.customer().name(), 13, Ui.MUTED, false)), BorderLayout.NORTH);
        var model = Ui.model("Sản phẩm", "SL", "Đơn giá", "Thành tiền");
        invoice.lines().forEach(l -> model.addRow(new Object[]{l.name(), l.quantity() + " " + l.unit(), Ui.money(l.price()), Ui.money(l.total())}));
        JTable table = Ui.table(model); table.getColumnModel().getColumn(0).setPreferredWidth(240);
        receipt.add(Ui.scroll(table));
        receipt.add(Ui.column(8,
            Ui.label("Tạm tính: " + Ui.money(invoice.subtotal()) + "   ·   Chiết khấu " + invoice.discountPercent() + "%: −" + Ui.money(invoice.discount()), 13, Ui.MUTED, false),
            Ui.label("VAT " + invoice.vatPercent() + "%: " + Ui.money(invoice.vat()), 13, Ui.MUTED, false),
            Ui.label("Tổng thanh toán    " + Ui.money(invoice.total()), 22, Ui.GREEN, true),
            Ui.label("Bản xem trước · Chưa phát hành hóa đơn thật", 12, Ui.MUTED, false),
            Ui.row(Ui.deferred("In hóa đơn"), Ui.deferred("Xuất PDF"), Ui.deferred("Xuất Excel"), Ui.button("Đóng", true, dialog::dispose)),
            Ui.label("In và xuất file sẽ được triển khai ở giai đoạn sau.", 12, Ui.MUTED, false)), BorderLayout.SOUTH);
        dialog.setContentPane(receipt); dialog.setSize(720, 560); dialog.setLocationRelativeTo(parent); dialog.setVisible(true);
    }
}
