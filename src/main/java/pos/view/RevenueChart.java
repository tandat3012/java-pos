package pos.view;

import java.awt.*;
import java.util.List;
import javax.swing.JPanel;

/** Small native Swing chart, with the same figures also available as text on the page. */
public final class RevenueChart extends JPanel {
    public record Point(String label, long value) { }
    private List<Point> points = List.of();
    public RevenueChart() {
        setOpaque(false); setPreferredSize(new Dimension(420, 200));
        getAccessibleContext().setAccessibleName("Biểu đồ doanh thu mẫu");
    }
    public void setPoints(List<Point> values) {
        points = List.copyOf(values);
        getAccessibleContext().setAccessibleDescription(points.stream().map(p -> p.label() + ": " + Ui.money(p.value())).reduce("", (a, b) -> a + b + "; "));
        repaint();
    }
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        int left = 54, top = 24, base = getHeight() - 30, width = getWidth() - left - 14, height = base - top;
        if (height < 30 || width < 40 || points.isEmpty()) { g.dispose(); return; }
        long max = Math.max(1, points.stream().mapToLong(Point::value).max().orElse(1));
        for (int n = 0; n <= 2; n++) {
            int y = base - height * n / 2;
            g.setColor(Ui.LINE); g.drawLine(left, y, getWidth() - 8, y);
            g.setColor(Ui.MUTED); g.drawString(compact(max * n / 2), 0, y + 4);
        }
        int slot = width / points.size(), barWidth = Math.max(8, Math.min(42, slot - 16));
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            int h = (int) ((double) point.value() / max * height), x = left + slot * i + (slot - barWidth) / 2;
            g.setColor(i == points.size() - 1 ? Ui.GREEN : new Color(0xA4CAB5));
            if (h > 0) g.fillRoundRect(x, base - h, barWidth, h, 6, 6);
            g.setColor(Ui.MUTED);
            int stride = Math.max(1, (int) Math.ceil(45d / slot));
            if (i % stride == 0 || i == points.size() - 1 && slot >= 45) {
                int textWidth = g.getFontMetrics().stringWidth(point.label());
                g.drawString(point.label(), x + (barWidth - textWidth) / 2, base + 19);
            }
        }
        g.dispose();
    }
    private String compact(long value) {
        if (value >= 1_000_000) return String.format(java.util.Locale.ROOT, "%.1f tr", value / 1_000_000d);
        if (value >= 1000) return value / 1000 + "k";
        return Long.toString(value);
    }
}
