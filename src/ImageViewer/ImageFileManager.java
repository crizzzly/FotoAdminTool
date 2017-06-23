package ImageViewer;

import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;

/**
 * ImageFileManager is a small utility class with static methods to load
 * and save images.
 * <p>
 * The files on disk can be in JPG or PNG image format. For files written
 * by this class, the format is determined by the constant IMAGE_FORMAT.
 *
 * @author Michael Kölling and David J. Barnes.
 * @version 2.0
 */
public class ImageFileManager {
    // A constant for the image format that this writer uses for writing.
    // Available formats are "jpg" and "png".
    private static final String IMAGE_FORMAT = "jpg";

    /**
     * Read an image file from disk and return it as an image. This method
     * can read JPG and PNG file formats. In case of any problem (e.g the file
     * does not exist, is in an undecodable format, or any other read error)
     * this method returns null.
     *
     * @param imageFile The image file to be loaded.
     * @return The image object or null if it could not be read.
     */
    public static OFImage loadImage(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null || (image.getWidth(null) < 0)) {
                // we could not load the image - probably invalid file format
                return null;
            }
            return new OFImage(image);
        } catch (IOException exc) {
            return null;
        }
    }

    /**
     * Write an image file to disk. The file format is JPG. In case of any
     * problem the method just silently returns.
     *
     * @param image The image to be saved.
     * @param file  The file to save to.
     */
    public static void saveImage(OFImage image, File file) {
        try {
            ImageIO.write(image, IMAGE_FORMAT, file);
        } catch (IOException exc) {
            return;
        }
    }

    public static OFImage getImage() {
        /*
        JFileChooser fc = new JFileChooser();
        //first directory shown when the file chooser window is opened
        fc.setCurrentDirectory(new File("C:\\Users\\Chrissi"));
        //implements a new FileFilter (function below)
        fc.setFileFilter(new MyFileFilter());
        //directories_only important because else you would choose files instead of dirs
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            //if a valid choice is made, the selected folder of the filechooser will be saved in sourceDir
            return fc.getSelectedFile();
        }else{
            return null;
        }
    }
        private static class MyFileFilter extends javax.swing.filechooser.FileFilter {
            public boolean accept(File file) {
                return file.isDirectory();
            }

            public String getDescription() {
                return "directory";
            }
*/
        return null;
    }
}
