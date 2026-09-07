package pos.view;

import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pos.demo.DemoStore;
import pos.demo.DemoStore.Invoice;
import pos.util.ExcelExporter;

public final class InsightsPage extends Ui.Page {
    private final DemoStore store;
    private final boolean report;
    private final JLabel revenue = Ui.label(" ", 26, Ui.TEXT, true), orders = Ui.label(" ", 26, Ui.TEXT, true), third = Ui.label(" ", 26, Ui.TEXT, true);
    private final JComboBox<String> period = new JComboBox<>(new String[]{"Hôm nay", "7 ngày qua", "Tháng này", "6 tháng qua"});
    private final RevenueChart chart = new RevenueChart();
    private final JLabel chartTitle = Ui.label(" ", 16, Ui.TEXT, true);
    private final DefaultTableModel sideModel;
    private final DefaultTableModel bottomModel;
    private final JLabel note = Ui.label(" ", 12, Ui.MUTED, false);

    /** Dữ liệu hóa đơn trong kỳ hiện tại – được cập nhật mỗi lần refresh() */
    private List<Invoice> currentInvoices = List.of();
    private String currentPeriod = "";

    public InsightsPage(DemoStore store, boolean report, Runnable goToSales) {
        super(report ? "Thống kê & báo cáo" : "Tổng quan cửa hàng",
            report ? "Theo dõi doanh thu theo ngày, tháng và sản phẩm bán chạy." : "Một góc nhìn nhanh về hoạt động bán hàng hôm nay.");
        this.store = store; this.report = report;
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0)); stats.setOpaque(false);
        stats.add(metric(report ? "DOANH THU TRONG KỲ" : "DOANH THU HÔM NAY", revenue, "Tổng thanh toán sau chiết khấu và VAT"));
        stats.add(metric(report ? "HÓA ĐƠN TRONG KỲ" : "HÓA ĐƠN HÔM NAY", orders, "Các hóa đơn đã thanh toán trong dữ liệu mẫu"));
        stats.add(metric(report ? "GIÁ TRỊ TRUNG BÌNH" : "CẦN BỔ SUNG HÀNG", third,
            report ? "Tổng doanh thu / số hóa đơn" : "Sản phẩm có tồn kho dưới mức tối thiểu"));

        JPanel top;
        if (report) {
            JButton exportBtn = Ui.button("Xuất báo cáo Excel", false, this::exportExcel);
            top = Ui.column(16, Ui.row(Ui.field("Kỳ báo cáo", period), exportBtn), stats);
        } else {
            top = Ui.column(16, Ui.row(
                Ui.label(LocalDate.now().format(DateTimeFormatter.ofPattern("'Hôm nay, 'dd/MM/yyyy")), 13, Ui.MUTED, false),
                Ui.button("+ Tạo đơn bán hàng", true, goToSales)), stats);
        }
        body.add(top, BorderLayout.NORTH);
        JPanel main = new JPanel(new GridLayout(2, 1, 0, 16)); main.setOpaque(false);
        JPanel chartRow = new JPanel(new GridLayout(1, 2, 16, 0)); chartRow.setOpaque(false);
        JPanel chartCard = Ui.card(new BorderLayout(0, 6)); chartCard.add(chartTitle, BorderLayout.NORTH); chartCard.add(chart);
        JPanel side = Ui.card(new BorderLayout(0, 12));
        side.add(Ui.label(report ? "Sản phẩm bán chạy" : "Cảnh báo tồn kho", 16, Ui.TEXT, true), BorderLayout.NORTH);
        sideModel = report ? Ui.model("Sản phẩm", "Đã bán") : Ui.model("Sản phẩm", "Tồn / tối thiểu");
        JTable sideTable = Ui.table(sideModel); sideTable.setRowHeight(36); sideTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        side.add(Ui.scroll(sideTable)); chartRow.add(chartCard); chartRow.add(side); main.add(chartRow);
        JPanel bottom = Ui.card(new BorderLayout(0, 10));
        bottom.add(Ui.label(report ? "Chi tiết doanh thu" : "Hóa đơn gần đây", 16, Ui.TEXT, true), BorderLayout.NORTH);
        bottomModel = report ? Ui.model("Kỳ", "Số hóa đơn", "Doanh thu") : Ui.model("Mã hóa đơn", "Ngày bán", "Khách hàng", "Tổng tiền");
        JTable bottomTable = Ui.table(bottomModel); bottomTable.setRowHeight(34);
        bottom.add(Ui.scroll(bottomTable)); bottom.add(note, BorderLayout.SOUTH); main.add(bottom);
        body.add(main);
        period.setSelectedIndex(1); period.addActionListener(e -> refresh()); refresh();
    }
    private JPanel metric(String title, JLabel value, String detail) {
        JPanel card = Ui.card(new BorderLayout(0, 10));
        card.add(Ui.label(title, 11, Ui.MUTED, true), BorderLayout.NORTH);
        card.add(value);
        JLabel hint = Ui.label(detail, 11, Ui.MUTED, false); hint.setToolTipText(detail);
        card.add(hint, BorderLayout.SOUTH); return card;
    }
    @Override public void refresh() {
        if (store == null) return;
        LocalDate today = LocalDate.now();
        LocalDate start = !report || period.getSelectedIndex() == 0 ? today : switch (period.getSelectedIndex()) {
            case 1 -> today.minusDays(6);
            case 2 -> today.withDayOfMonth(1);
            default -> today.withDayOfMonth(1).minusMonths(5);
        };
        currentPeriod = (String) period.getSelectedItem();
        List<Invoice> selected = store.invoices().stream()
            .filter(i -> !i.date().toLocalDate().isBefore(start) && !i.date().toLocalDate().isAfter(today)).toList();
        currentInvoices = selected;
        long sum = selected.stream().mapToLong(Invoice::total).sum();
        revenue.setText(Ui.money(sum)); orders.setText(Integer.toString(selected.size()));
        third.setText(report ? Ui.money(selected.isEmpty() ? 0 : sum / selected.size())
            : Long.toString(store.products().stream().filter(DemoStore.Product::lowStock).count()));
        sideModel.setRowCount(0); bottomModel.setRowCount(0);
        if (report) {
            DemoStore.bestSellers(selected).entrySet().stream()
                .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed()).limit(5)
                .forEach(e -> sideModel.addRow(new Object[]{e.getKey(), e.getValue()}));
        } else {
            store.products().stream().filter(DemoStore.Product::lowStock)
                .forEach(p -> sideModel.addRow(new Object[]{p.name(), p.stock() + " / " + p.minimum()}));
            store.invoices().stream().limit(5).forEach(i -> bottomModel.addRow(new Object[]{
                i.id(), i.date().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")), i.customer().name(), Ui.money(i.total())}));
        }
        List<RevenueChart.Point> points = new ArrayList<>();
        boolean monthly = report && period.getSelectedIndex() == 3;
        LocalDate chartStart = report ? start : today.minusDays(6);
        chartTitle.setText(monthly ? "Doanh thu theo tháng (đ)" : "Doanh thu theo ngày (đ)");
        for (LocalDate d = chartStart; !d.isAfter(today); d = monthly ? d.plusMonths(1) : d.plusDays(1)) {
            LocalDate date = d;
            List<Invoice> bucket = store.invoices().stream().filter(i ->
                monthly ? YearMonth.from(i.date()).equals(YearMonth.from(date))
                        : i.date().toLocalDate().equals(date)).toList();
            long value = bucket.stream().mapToLong(Invoice::total).sum();
            String label = d.format(DateTimeFormatter.ofPattern(monthly ? "MM/yy" : "dd/MM"));
            points.add(new RevenueChart.Point(label, value));
            if (report) bottomModel.addRow(new Object[]{label, bucket.size(), Ui.money(value)});
        }
        chart.setPoints(points);
        note.setText(report
            ? selected.size() + " hóa đơn trong kỳ · " + (selected.isEmpty() ? "Chưa có doanh thu mẫu trong kỳ này." : "Số liệu từ các hóa đơn mẫu trong phiên.")
            : "Hiển thị 5 hóa đơn mới nhất · Xem toàn bộ tại mục Hóa đơn");
    }
    private void exportExcel() {
        ExcelExporter.exportRevenue(this, currentInvoices, currentPeriod);
    }
}
