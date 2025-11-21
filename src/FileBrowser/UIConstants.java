package FileBrowser;

import java.awt.Color;
import java.util.logging.Logger;
import javax.swing.UIManager;

public class UIConstants {
    private static final Logger LOGGER = Logger.getLogger(UIConstants.class.getName());

    public static final boolean TESTING = true;
    
    // Theme detection
    private static final boolean IS_DARK_MODE = isDarkMode();
    
    // Colors - now dynamic based on theme
    public static final Color SELECTION_COLOR = IS_DARK_MODE ? new Color(75, 110, 175) : new Color(7, 110, 246);
    public static final Color BACKGROUND = IS_DARK_MODE ? new Color(45, 45, 48) : new Color(240, 240, 240);
    public static final Color TEXT_LIGHT = IS_DARK_MODE ? Color.WHITE : Color.BLACK;
    public static final Color TEXT_MUTED = IS_DARK_MODE ? new Color(200, 200, 200) : new Color(100, 100, 100);
    public static final Color PANEL_BACKGROUND = IS_DARK_MODE ? new Color(60, 63, 65) : new Color(240, 240, 240);
    
    private static boolean isDarkMode() {
        try {
            // Check if running on Linux with GNOME
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                Process process = Runtime.getRuntime().exec(new String[]{"gsettings", "get", "org.gnome.desktop.interface", "gtk-theme"});
                process.waitFor();
                String theme = new String(process.getInputStream().readAllBytes()).toLowerCase().trim();
                LOGGER.info("Dark mode detected: " + theme);
                return theme.contains("dark") || theme.endsWith("-dark") || theme.contains("adwaita");
            }
            // Add more OS-specific checks here if needed
        } catch (Exception e) {
            System.err.println("Error detecting dark mode: " + e.getMessage());
        }
        return false;
    }

    // Dimensions
    public static final int THUMBNAIL_WIDTH = 150;
    public static final int CELL_HORIZONTAL_PADDING = 10;
    public static final int CELL_VERTICAL_PADDING = 10;
    public static final int CELL_HORIZONTAL_GAP = 20;
    public static final int CELL_VERTICAL_GAP = 30;
    public static final int FILENAME_LABEL_HEIGHT = 20;

    // Fonts
    public static final float FILENAME_FONT_SIZE = 12f;

    public static final String[] SUPPORTED_IMAGE_FORMATS = {
        "jpg", "jpeg", "png", "gif", "bmp", "cr2", "nef", "arw", "raf", "dng", "crw", "cr3", "raw", "rw2", "pef", "srf", "sr2", "x3f"
    };

    // Button Text & Tooltips
    public static class Buttons {
        public static final String CHOOSE_FOLDER = "Select Folder";
        public static final String SORT_FILES = "Sort Files";
        public static final String UNDO_CHANGES = "Undo Changes";
        
        public static class Tooltips {
            public static final String CHOOSE_FOLDER = "Select folder to browse";
            public static final String SORT_FILES = "Sort files in the current folder";
            public static final String UNDO_CHANGES = "Undo the last sorting operation";
        }
    }
    
    public static final String[] SUPPORTED_VIDEO_FORMATS = {
        "mp4", "avi", "mov", "mkv", "wmv", "flv", "webm"
    };


    private UIConstants() {
        // Prevent instantiation
    }
}
