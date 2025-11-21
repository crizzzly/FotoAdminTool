package FileBrowser;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThumbnailLoader {
    private static final Logger LOGGER = Logger.getLogger(ThumbnailLoader.class.getName());
    private final ExecutorService executor;
    private final Map<String, Image> thumbnailCache;
    private final int thumbnailWidth;

    public ThumbnailLoader(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.thumbnailCache = new HashMap<>();
    }

    public void loadThumbnail(File file, ThumbnailLoadListener listener) {
        if (file == null || listener == null) {
            return;
        }

        String filePath = file.getAbsolutePath();

        // Return cached thumbnail if available
        if (thumbnailCache.containsKey(filePath)) {
            listener.onThumbnailLoaded(file, thumbnailCache.get(filePath));
            return;
        }

        // Load in background
        executor.execute(() -> {
            try {
                Image thumbnail = createThumbnail(file);
                thumbnailCache.put(filePath, thumbnail);
                listener.onThumbnailLoaded(file, thumbnail);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error creating thumbnail for: " + filePath, e);
                listener.onThumbnailError(file, e);
            }
        });
    }

    private Image createThumbnail(File file) throws IOException {
        if (file.isDirectory()) {
            return FileOperations.createDefaultIcon(true);
        }

        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new IOException("Unsupported image format");
            }
            return scaleImage(image, thumbnailWidth);
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "Using default icon for: " + file.getName());
            return FileOperations.createDefaultIcon(false);
        }
    }

    private Image scaleImage(BufferedImage image, int targetWidth) {
        if (image == null) return null;
        double ratio = (double) targetWidth / image.getWidth();
        int targetHeight = (int) (image.getHeight() * ratio);
        return image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
    }

    public void shutdown() {
        executor.shutdown();
        thumbnailCache.clear();
    }

    public interface ThumbnailLoadListener {
        void onThumbnailLoaded(File file, Image thumbnail);
        default void onThumbnailError(File file, Exception e) {
            LOGGER.log(Level.WARNING, "Error loading thumbnail for: " + file.getAbsolutePath(), e);
        }
    }
}