package FileBrowser;

import javax.imageio.ImageIO;
import java.awt.Image;
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

    public static Image createDefaultIcon(boolean isDirectory) {
        try {
            String resource = isDirectory ? "assets/folder.png" : "file.png";
            return ImageIO.read(Objects.requireNonNull(FileOperations.class.getResource(resource)));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading default icon", e);
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public static boolean isImageFile(File file) {
        if (file == null || file.isDirectory()) {
            return false;
        }
        String name = file.getName().toLowerCase();
        return name.matches(".*\\.(jpg|jpeg|png|gif|bmp|cr2|nef|arw|raf|dng|crw|cr3|raw|rw2|pef|srf|sr2|x3f)$");
    }
}