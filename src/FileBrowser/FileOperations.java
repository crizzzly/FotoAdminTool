package FileBrowser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileOperations {
    private static final Logger LOGGER = Logger.getLogger(FileOperations.class.getName());

    private FileOperations() {
        // Prevent instantiation
    }

    public static List<File> listImageFiles(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        File[] files = directory.listFiles((dir, name) ->
                name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|bmp|cr2|nef|arw|raf|dng|crw|cr3|raw|rw2|pef|srf|sr2|x3f)$")
                        || new File(dir, name).isDirectory()
        );

        return files != null ? new ArrayList<>(Arrays.asList(files)) : new ArrayList<>();
    }

    public static Image loadImage(File file) throws IOException {
        if (file == null || !file.exists()) {
            return null;
        }

        try {
            if (file.isDirectory()) {
                return ImageIO.read(Objects.requireNonNull(FileOperations.class.getResource("assets/folder.png")));
            }
            return ImageIO.read(file);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading image: " + file.getAbsolutePath(), e);
            throw e;
        }
    }

    public static Image getDefaultSystemIcon(boolean isDirectory) {
        try {
            // Create a temporary file to get the system icon
            File tempFile = isDirectory 
                ? new File(System.getProperty("user.home"))
                : File.createTempFile("icon", ".tmp");
            
            try {
                Icon icon = FileSystemView.getFileSystemView().getSystemIcon(tempFile);
                
                // Convert Icon to Image
                if (icon != null) {
                    BufferedImage image = new BufferedImage(
                            Math.max(1, icon.getIconWidth()),
                            Math.max(1, icon.getIconHeight()),
                            BufferedImage.TYPE_INT_ARGB
                    );
                    icon.paintIcon(null, image.getGraphics(), 0, 0);
                    return image;
                }
            } finally {
                if (!isDirectory) {
                    tempFile.deleteOnExit();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error creating system icon", e);
        }

        // Fallback to a simple colored rectangle if system icon fails
        BufferedImage fallback = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = fallback.createGraphics();
        g2d.setColor(isDirectory ? Color.BLUE : Color.GRAY);
        g2d.fillRect(0, 0, 16, 16);
        g2d.dispose();
        return fallback;
    }

    public static boolean isImageFile(File file) {
        if (file == null || file.isDirectory()) {
            return false;
        }
        String name = file.getName().toLowerCase();
        // TODO: Replace with constant
        return name.matches(".*\\.(jpg|jpeg|png|gif|bmp|cr2|nef|arw|raf|dng|crw|cr3|raw|rw2|pef|srf|sr2|x3f)$");
    }
}