import FileBrowser.controller.SortImages;
import FileBrowser.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

import static FileBrowser.util.UIConstants.TESTING;


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
            String osName = System.getProperty("os.name").toLowerCase();
            boolean isDarkMode = isSystemDarkMode();
            
            // Set the system look and feel
            if (osName.contains("mac")) {
                UIManager.setLookAndFeel("com.apple.laf.AquaLookAndFeel");
            } else if (osName.contains("windows")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else if (osName.contains("linux")) {
                // Force GTK3 on Linux for better dark mode support
                System.setProperty("jdk.gtk.version", "3");
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

            // Apply dark FileBrowser.theme if in dark mode
            if (isDarkMode) {
                applyDarkTheme();
            }

            // Update the UI to reflect the new look and feel
            updateAllUIs();
        } catch (Exception e) {
            LOGGER.severe("Error setting system look and feel: " + e.getMessage());
        }
    }
    
    private static boolean isSystemDarkMode() {
        try {
            // Try to detect dark mode on Linux
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                // Check GTK FileBrowser.theme for dark mode
                Process process = Runtime.getRuntime().exec(new String[]{"gsettings", "get", "org.gnome.desktop.interface", "gtk-FileBrowser.theme"});
                process.waitFor();
                String theme = new String(process.getInputStream().readAllBytes()).toLowerCase().trim();
                return theme.contains("dark") || theme.endsWith("-dark");
            }
            // Add other OS-specific dark mode detection here if needed
        } catch (Exception e) {
            System.err.println("Error detecting dark mode: " + e.getMessage());
        }
        return false;
    }
    
    private static void applyDarkTheme() {
        // Set dark color scheme
        UIManager.put("control", new Color(64, 64, 64));
        UIManager.put("text", Color.WHITE);
        UIManager.put("nimbusBase", new Color(18, 30, 49));
        UIManager.put("nimbusFocus", new Color(115, 164, 209));
        UIManager.put("nimbusLightBackground", new Color(45, 45, 45));
        UIManager.put("nimbusSelectionBackground", new Color(75, 110, 175));
        
        // Update UI colors
        UIManager.put("Panel.background", new Color(45, 45, 45));
        UIManager.put("Panel.foreground", Color.WHITE);
        UIManager.put("List.background", new Color(45, 45, 45));
        UIManager.put("List.foreground", Color.WHITE);
        UIManager.put("List.selectionBackground", new Color(75, 110, 175));
        UIManager.put("List.selectionForeground", Color.WHITE);
        UIManager.put("Viewport.background", new Color(45, 45, 45));
        UIManager.put("ScrollPane.background", new Color(45, 45, 45));
        UIManager.put("ScrollBar.background", new Color(45, 45, 45));
        UIManager.put("ScrollBar.thumb", new Color(80, 80, 80));
        UIManager.put("ScrollBar.thumbDarkShadow", new Color(30, 30, 30));
        UIManager.put("ScrollBar.thumbShadow", new Color(60, 60, 60));
        UIManager.put("ScrollBar.thumbHighlight", new Color(100, 100, 100));
        UIManager.put("ScrollBar.track", new Color(45, 45, 45));
    }

    private static void updateAllUIs() {
        // Ensure proper font scaling
        UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        // Enable anti-aliasing for better text rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
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