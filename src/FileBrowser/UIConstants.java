package FileBrowser;

import java.awt.Color;

public class UIConstants {
    public static final boolean TESTING = true;
    // Colors
    public static final Color SELECTION_COLOR = new Color(7, 110, 246);
    public static final Color BACKGROUND_DARK = new Color(45, 45, 48);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(200, 200, 200);

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

    public static final String[] SUPPORTED_VIDEO_FORMATS = {
        "mp4", "avi", "mov", "mkv", "wmv", "flv", "webm"
    };

    public static final String FOLDER_ICON_S = "assets/folder.png";
    public static final String FOLDER_ICON_L = "assets/folder.png";

    private UIConstants() {
        // Prevent instantiation
    }
}
