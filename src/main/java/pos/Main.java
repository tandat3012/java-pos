package pos;

import javax.swing.SwingUtilities;
import pos.view.PosFrame;
import pos.view.Ui;

/** Desktop UI prototype using session-only sample data. */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        // Must be set before any AWT/Swing class is loaded.
        // Required when running inside Maven exec:java (shared JVM) so
        // Swing is not treated as headless and can paint correctly.
        System.setProperty("java.awt.headless", "false");

        SwingUtilities.invokeLater(() -> {
            Ui.install();
            PosFrame frame = new PosFrame();
            frame.setVisible(true);
            // Force repaint after the frame is shown — avoids white-screen
            // artifacts that can occur when Swing initialises inside a
            // Maven JVM where the EDT starts late.
            frame.revalidate();
            frame.repaint();
        });
    }
}
