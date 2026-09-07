package pos.view;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import pos.demo.DemoStore;

public final class PosFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, Ui.Page> pages = new LinkedHashMap<>();
    private final Map<String, JToggleButton> navigation = new LinkedHashMap<>();

    public PosFrame() {
        super("An Nhiên POS · Giao diện mẫu");
        DemoStore store = new DemoStore();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 800)); setSize(1380, 900);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(Ui.BG);
        JPanel sidebar = new JPanel(new BorderLayout(0, 32)); sidebar.setBackground(Ui.NAVY);
        sidebar.setPreferredSize(new Dimension(210, 0)); sidebar.setBorder(new EmptyBorder(30, 16, 24, 16));
        sidebar.add(Ui.column(6, Ui.label("AN NHIÊN", 23, Color.WHITE, true),
            Ui.label("QUẢN LÝ BÁN HÀNG", 10, new Color(0xB0C8BA), true)), BorderLayout.NORTH);
        JPanel links = new JPanel(); links.setOpaque(false); links.setLayout(new BoxLayout(links, BoxLayout.Y_AXIS));
        String[] names = {"Tổng quan", "Bán hàng", "Sản phẩm", "Khách hàng", "Hóa đơn", "Báo cáo"};
        ButtonGroup group = new ButtonGroup();
        for (String name : names) {
            JToggleButton button = new JToggleButton(name) {
                @Override protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(isSelected() ? new Color(0x28503D) : getModel().isRollover() ? new Color(0x1D3B2E) : Ui.NAVY);
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    if (isSelected() || isFocusOwner()) {
                        g.setColor(new Color(0x99D7B4)); g.fillRoundRect(0, 12, 3, getHeight() - 24, 3, 3);
                    }
                    g.dispose(); super.paintComponent(graphics);
                }
            };
            button.setName("nav:" + name); button.setContentAreaFilled(false); button.setBorderPainted(false); button.setFocusPainted(false);
            button.setForeground(Color.WHITE); button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            button.setHorizontalAlignment(SwingConstants.LEFT); button.setBorder(new EmptyBorder(14, 18, 14, 18));
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.addActionListener(e -> navigate(name)); navigation.put(name, button); group.add(button); links.add(button); links.add(Box.createVerticalStrut(6));
        }
        sidebar.add(links);
        sidebar.add(Ui.column(8, Ui.label("Cửa hàng mẫu", 13, Color.WHITE, true),
            Ui.label("Dữ liệu đặt lại khi mở ứng dụng", 10, new Color(0xB0C8BA), false)), BorderLayout.SOUTH);
        JPanel workspace = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new BorderLayout()); top.setBackground(Color.WHITE); top.setBorder(new EmptyBorder(16, 28, 16, 28));
        top.add(Ui.label("Không gian làm việc  /  Cửa hàng An Nhiên", 12, Ui.MUTED, false));
        top.add(Ui.label("CHẾ ĐỘ MẪU   ·   Không lưu dữ liệu thật", 12, Ui.GREEN, true), BorderLayout.EAST);
        workspace.add(top, BorderLayout.NORTH); workspace.add(content);
        pages.put("Tổng quan", new InsightsPage(store, false, () -> navigate("Bán hàng")));
        pages.put("Bán hàng", new SalesPage(store, this::refreshAll));
        pages.put("Sản phẩm", new ProductsPage(store, this::refreshAll));
        pages.put("Khách hàng", new CustomersPage(store, this::refreshAll));
        pages.put("Hóa đơn", new InvoicesPage(store));
        pages.put("Báo cáo", new InsightsPage(store, true, () -> navigate("Bán hàng")));
        pages.forEach((name, page) -> content.add(page, name));
        root.add(sidebar, BorderLayout.WEST); root.add(workspace);
        setContentPane(root); navigate("Tổng quan"); setLocationRelativeTo(null);
    }
    public void navigate(String name) {
        Ui.Page page = pages.get(name);
        if (page == null) return;
        page.refresh(); cards.show(content, name); navigation.get(name).setSelected(true);
    }
    private void refreshAll() { pages.values().forEach(Ui.Page::refresh); }
}
