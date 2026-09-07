package pos.view;

import java.awt.*;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class Ui {
    public static final Color NAVY = new Color(0x142822), BG = new Color(0xF3F6F4), WHITE = Color.WHITE;
    public static final Color TEXT = new Color(0x20332B), MUTED = new Color(0x63756B), LINE = new Color(0xDFE7E1);
    public static final Color GREEN = new Color(0x19734D), PALE = new Color(0xE7F3EC), AMBER = new Color(0x946018);
    public static final Color RED = new Color(0xAB3434);
    private Ui() { }

    public static void install() {
        // Improve text rendering — must be set before the first Swing window.
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            for (var look : UIManager.getInstalledLookAndFeels()) {
                if (look.getName().equals("Nimbus")) {
                    UIManager.setLookAndFeel(look.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { /* Falls back to system L&F — still usable. */ }
        UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        UIManager.put("control", BG);
        UIManager.put("text", TEXT);
        UIManager.put("nimbusSelectionBackground", PALE);
        UIManager.put("nimbusSelectedText", TEXT);
        UIManager.put("nimbusFocus", GREEN);
        UIManager.put("Table.alternateRowColor", new Color(0xF8FAF8));
    }
    public static JLabel label(String text, int size, Color color, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, bold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(color);
        return label;
    }
    public static JPanel row(Component... children) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        for (Component child : children) panel.add(child);
        return panel;
    }
    public static JPanel column(int gap, Component... children) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 0; i < children.length; i++) {
            if (i > 0) panel.add(Box.createVerticalStrut(gap));
            if (children[i] instanceof JComponent c) c.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(children[i]);
        }
        return panel;
    }
    public static JPanel card(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE), new EmptyBorder(20, 20, 20, 20)));
        return panel;
    }
    public static JButton button(String text, boolean primary, Runnable action) {
        JButton button = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = primary ? GREEN : WHITE;
                if (!isEnabled()) base = LINE;
                else if (getModel().isPressed()) base = primary ? GREEN.darker() : PALE;
                else if (getModel().isRollover()) base = primary ? new Color(0x125F3F) : BG;
                g2.setColor(base); g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(isFocusOwner() ? GREEN : (primary ? base : LINE));
                g2.setStroke(new BasicStroke(isFocusOwner() ? 2 : 1));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setFocusPainted(false);
        button.setForeground(primary ? WHITE : TEXT);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }
    public static JTextField search(String accessibleName, int columns, Runnable changed) {
        JTextField field = new JTextField(columns);
        field.getAccessibleContext().setAccessibleName(accessibleName);
        field.setToolTipText(accessibleName);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 36));
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { changed.run(); }
            public void removeUpdate(DocumentEvent e) { changed.run(); }
            public void changedUpdate(DocumentEvent e) { changed.run(); }
        });
        return field;
    }
    public static JPanel field(String title, JComponent component) {
        JLabel label = label(title, 12, MUTED, true);
        label.setLabelFor(component);
        component.getAccessibleContext().setAccessibleName(title);
        return column(6, label, component);
    }
    public static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }
    public static JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(44);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setSelectionBackground(PALE);
        table.setSelectionForeground(TEXT);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setBackground(selected ? PALE : row % 2 == 0 ? WHITE : new Color(0xF8FAF8));
                setForeground(TEXT);
                return this;
            }
        });
        return table;
    }
    public static JScrollPane scroll(Component component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBorder(BorderFactory.createLineBorder(LINE));
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }
    public static String money(long amount) { return NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(amount) + " đ"; }
    public static String key(String text) {
        return Normalizer.normalize(text.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "").replace('đ', 'd').trim();
    }
    public static void error(Component parent, String message) { JOptionPane.showMessageDialog(parent, message, "Kiểm tra thông tin", JOptionPane.WARNING_MESSAGE); }
    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Xác nhận thao tác mẫu", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }
    public static JButton deferred(String text) {
        JButton button = button(text, false, () -> {});
        button.setEnabled(false);
        button.setToolTipText("Sẽ triển khai sau khi duyệt giao diện mẫu.");
        return button;
    }
    public static void editor(Component parent, String title, JPanel fields, Runnable save) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        JPanel content = card(new BorderLayout(16, 20));
        JLabel error = label(" ", 12, RED, false);
        JButton submit = button("Lưu trong phiên mẫu", true, () -> {
            try { save.run(); dialog.dispose(); }
            catch (NumberFormatException ex) { error.setText("Giá và số lượng cần là số nguyên hợp lệ."); }
            catch (IllegalArgumentException ex) { error.setText(ex.getMessage()); }
        });
        content.add(fields);
        content.add(column(10, error, row(button("Hủy", false, dialog::dispose), submit)), BorderLayout.SOUTH);
        dialog.setContentPane(content); dialog.getRootPane().setDefaultButton(submit);
        dialog.pack(); dialog.setMinimumSize(new Dimension(620, dialog.getHeight()));
        dialog.setLocationRelativeTo(parent); dialog.setVisible(true);
    }
    public abstract static class Page extends JPanel {
        protected final JPanel body = new JPanel(new BorderLayout(16, 16));
        protected Page(String title, String subtitle) {
            super(new BorderLayout(0, 24));
            setBackground(BG);
            setBorder(new EmptyBorder(28, 28, 24, 28));
            add(column(6, label(title, 27, TEXT, true), label(subtitle, 13, MUTED, false)), BorderLayout.NORTH);
            body.setOpaque(false); add(body);
        }
        public abstract void refresh();
    }
}
