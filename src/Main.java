
import FileBrowser.MainFrame;
import FileBrowser.SortImages;
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static FileBrowser.UIConstants.TESTING;

/**
 * Sets up Look & feel
 * creates instance of FileBrowser.MainFrame
 * Created by Chrissi on 23.06.2017.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Set system look and feel
        setSystemLookAndFeel();

        // Set up the main frame
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            //reset changes on exit if testing is enabled
            if(TESTING) Runtime.getRuntime().addShutdownHook(new Thread(SortImages::undoChanges));
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            // Get the system look and feel class name
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

            // Special handling for macOS to get the native Aqua look and feel
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("apple.awt.application.name", "FotoAdminTool");
                lookAndFeel = "com.apple.laf.AquaLookAndFeel";
            }

            // Special handling for Windows to get the native Windows look and feel
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            }

            // Special handling for Linux to get the native Linux look and feel
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            }

            // Set the look and feel
            UIManager.setLookAndFeel(lookAndFeel);

        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            LOGGER.log(Level.WARNING, "Could not set system look and feel", e);
            try {
                // Fall back to Nimbus if system L&F fails
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Could not set Nimbus look and feel", ex);
            }
        } finally {
            // Ensure proper font scaling
            UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 12));

            // Enable anti-aliasing for better text rendering
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            //For better support on high-DPI displays
            System.setProperty("sun.java2d.uiScale", "1.0"); // Or "2.0" for 200% scaling

            // Dark Mode support
            // For Windows
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                UIManager.put("win.title.background", UIManager.get("Panel.background"));
                UIManager.put("win.title.foreground", UIManager.get("Label.foreground"));
            }
            // For macOS
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                UIManager.put("mac.title.background", UIManager.get("Panel.background"));
                UIManager.put("mac.title.foreground", UIManager.get("Label.foreground"));
            }
            // For Linux
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                UIManager.put("gtk.title.background", UIManager.get("Panel.background"));
                UIManager.put("gtk.title.foreground", UIManager.get("Label.foreground"));
            }
        }
    }
}

    /*
        static ProgressMonitor pbar;
        static int counter = 0;
        public Main() {
            setSize(250, 100);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            pbar = new ProgressMonitor(null, "Monitoring Progress",
                    "Initializing . . .", 0, 100);
            // Fire a timer every once in a while to update the progress.
            Timer timer = new Timer(500, this);
            timer.start();
            setVisible(true);
        }
        */