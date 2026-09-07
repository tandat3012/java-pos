package pos.util;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pos.demo.DemoStore;
import pos.demo.DemoStore.Invoice;

/**
 * Xuất báo cáo doanh thu và sản phẩm bán chạy ra file Excel (.xlsx).
 * Chỉ dùng dữ liệu mẫu ở giai đoạn UI prototype; thay bằng DAO khi tích hợp DB thật.
 */
public final class ExcelExporter {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ExcelExporter() { }

    /**
     * Hiển thị hộp thoại chọn nơi lưu file, sau đó xuất báo cáo doanh thu theo kỳ.
     *
     * @param parent     Component cha để JFileChooser định vị
     * @param invoices   Danh sách hóa đơn trong kỳ (đã lọc sẵn theo thời gian)
     * @param periodName Tên kỳ hiển thị trong tiêu đề (ví dụ "7 ngày qua")
     */
    public static void exportRevenue(Component parent, List<Invoice> invoices, String periodName) {
        File file = chooseFile(parent, "bao-cao-doanh-thu");
        if (file == null) return;

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Doanh thu");
            CellStyle header = headerStyle(wb);
            CellStyle money = moneyStyle(wb);
            CellStyle bold = boldStyle(wb);

            int r = 0;
            writeTitle(sheet, r++, "BÁO CÁO DOANH THU — " + periodName.toUpperCase(), 5);
            writeTitle(sheet, r++, "Xuất ngày " + LocalDate.now().format(DATE_FMT), 5);
            r++;

            // ─── Bảng hóa đơn ─────────────────────────────────────────────
            Row hdr = sheet.createRow(r++);
            String[] cols = {"Mã HĐ", "Ngày bán", "Khách hàng", "Tạm tính (đ)", "Giảm (đ)", "VAT (đ)", "Tổng tiền (đ)"};
            for (int c = 0; c < cols.length; c++) cell(hdr, c, cols[c], header);

            long grandTotal = 0;
            for (Invoice inv : invoices) {
                Row row = sheet.createRow(r++);
                cell(row, 0, inv.id());
                cell(row, 1, inv.date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                cell(row, 2, inv.customer().name());
                cellNum(row, 3, inv.subtotal(), money);
                cellNum(row, 4, inv.discount(), money);
                cellNum(row, 5, inv.vat(), money);
                cellNum(row, 6, inv.total(), money);
                grandTotal += inv.total();
            }

            // ─── Dòng tổng cộng ──────────────────────────────────────────
            Row total = sheet.createRow(r++);
            cell(total, 0, "TỔNG CỘNG", bold);
            cell(total, 1, invoices.size() + " hóa đơn", bold);
            cellNum(total, 6, grandTotal, money);

            r++;

            // ─── Sản phẩm bán chạy ────────────────────────────────────────
            writeTitle(sheet, r++, "SẢN PHẨM BÁN CHẠY", 2);
            Row bh = sheet.createRow(r++);
            cell(bh, 0, "Sản phẩm", header); cell(bh, 1, "Số lượng đã bán", header);
            Map<String, Long> sellers = DemoStore.bestSellers(invoices);
            sellers.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> {
                    Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                    cell(row, 0, e.getKey());
                    cellNum(row, 1, e.getValue(), null);
                });

            // Tự động rộng cột
            for (int c = 0; c < 7; c++) sheet.autoSizeColumn(c);

            try (FileOutputStream out = new FileOutputStream(file)) {
                wb.write(out);
            }
            JOptionPane.showMessageDialog(parent,
                "Đã xuất báo cáo thành công:\n" + file.getAbsolutePath(),
                "Xuất Excel", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                "Lỗi khi ghi file: " + ex.getMessage(),
                "Lỗi xuất Excel", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private static File chooseFile(Component parent, String defaultName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file Excel");
        chooser.setFileFilter(new FileNameExtensionFilter("Excel workbook (*.xlsx)", "xlsx"));
        chooser.setSelectedFile(new File(defaultName + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx"));
        return chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    private static void writeTitle(Sheet sheet, int r, String text, int span) {
        Row row = sheet.createRow(r);
        Cell c = row.createCell(0); c.setCellValue(text);
        if (span > 1) sheet.addMergedRegion(new CellRangeAddress(r, r, 0, span - 1));
    }

    private static Cell cell(Row row, int col, String value) {
        Cell c = row.createCell(col); c.setCellValue(value); return c;
    }

    private static void cell(Row row, int col, String value, CellStyle style) {
        Cell c = cell(row, col, value); if (style != null) c.setCellStyle(style);
    }

    private static void cellNum(Row row, int col, long value, CellStyle style) {
        Cell c = row.createCell(col); c.setCellValue(value); if (style != null) c.setCellStyle(style);
    }

    private static CellStyle headerStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont(); f.setBold(true); s.setFont(f);
        s.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setBorderBottom(BorderStyle.THIN);
        return s;
    }

    private static CellStyle moneyStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        DataFormat fmt = wb.createDataFormat();
        s.setDataFormat(fmt.getFormat("#,##0"));
        return s;
    }

    private static CellStyle boldStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont(); f.setBold(true); s.setFont(f);
        return s;
    }
}
