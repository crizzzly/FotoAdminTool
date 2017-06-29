import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;


/**
 * Created by Chrissi on 11.05.2017.
 */

/**
 * Testkommentar
 */
public class Foto {
    private File file;
    private ImageIcon image;
    private Image thumbnail;
    private File dir;


    /**
     * creates new instance of class "foto" on base of path and filename of the foto.
     * calls method to create thumbnail
     *
     * @param path     path of the foto
     * @param filename filename of the foto
     */
    Foto(String path, String filename) {
        dir = new File(path);
        file = new File(dir, filename);
        //creationDateTime = getCreationDateTime();

        //setThumbnail();
    }

    /**
     * constructor of class Foto. saves picture/video in File file, saves parent directory in File dir
     * calls method to create thumbnail
     *
     * @param filename of the foto/video (String)
     */
    Foto(String filename) {
        //
        //
        // System.out.println(path.toString()+ filename);
        file = new File(filename);//"C:\\Users\\Chrissi\\Pictures\\imagesTest\\1-111.jpg");
        dir = file.getParentFile();
        //setThumbnail();
        // creationDateTime = getCreationDateTime();
    }

    public Foto(File path, String filename) {
        //
        //
        // System.out.println(path.toString()+ filename);
        file = new File(path, filename);//"C:\\Users\\Chrissi\\Pictures\\imagesTest\\1-111.jpg");
        dir = file.getParentFile();
        //creationDateTime = getCreationDateTime();
        //setThumbnail();
    }

/*
    public static class CompDate implements Comparator<Foto> {
        private int mod = 1;

        /**
         * sorts Fotos by creationDateTime. you can choose if first Foto in the list will be the oldest or youngest
         * by setting desc (
         *
         * @param desc if true, first item will be the youngest. if false, first item will be the oldest.
         */
        /*
        public CompDate(boolean desc) {
            if (desc) mod =-1;
        }
        @Override
        public int compare(Foto arg0, Foto arg1) {
            System.out.println("called compareFotos function");
            return mod*arg0.getCreationDateTime().compareTo(arg1.getCreationDateTime());
        }
    }*/

    /**
     * if Instance is a jpg file, thumbnail will be a scaled instance of the jpg.
     * if it is a mp4 video file, thumbnail will be the video.png file in src folder.
     */
    Image getThumbnail() {
        //checks if suffix is "jpg" or "jpeg"
        if (file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("jpeg")) {
            image = new ImageIcon(file.getAbsolutePath());
            int height, width;
            int maxWidth = 91;
            int maxHeight = 52;
            if (image.getIconHeight() > image.getIconWidth()) {
                width = maxHeight;
                height = maxWidth;
            } else {
                height = maxHeight;
                width = maxWidth;
            }
            thumbnail = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath())
                    .getScaledInstance(width, height, Image.SCALE_SMOOTH);

        } else if (file.getName().toLowerCase().endsWith("mp4") || file.getName().toLowerCase().endsWith("mpeg4")) {
            try {
                thumbnail = ImageIO.read(getClass().getResource("video.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return thumbnail;
    }

    /* public ImageIcon getThumbnail() {
        return new ImageIcon(thumbnail);
    }*/

    /**
     * returns the foto as image file
     * @return (Image) Foto
     */
    public Image getImage() {
        return Toolkit.getDefaultToolkit().getImage(image.getAccessibleContext().toString());
    }

    /**
     * moves file to given path
     *
     * @param path path where file is moved to
     */
    void moveFile(String path) {
        try {
            //check if another file with same filename already exists. if not, just move to the new dir
            if (!(new File(path, file.getName()).exists())) {
                if (file.renameTo(new File(path, file.getName()))) {   //newDir.getAbsolutePath(), file.getName()))) {
                    System.out.println("File " + file.getName() + " is moved successfully!");
                } else {
                    System.out.println(file.getName() + " is failed to move!" + path);
                }
            }
            //if a file with the same filename exists, change name of the file
            else {
                //perhabs check metadata first!!!!
                try {
                    if (file.renameTo(new File(path, file.getName() + "-1"))) {
                        System.out.println(file.getName() + " needed to be renamed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * prints metaData of Foto in terminal
     */
    public void showMetadata() {
        BasicFileAttributes atr;
        try {
            atr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            System.out.println("created: " + atr.creationTime());
            System.out.println("last modified: " + atr.lastModifiedTime());
            System.out.println("last access: " + atr.lastAccessTime());
            System.out.println("is directory : " + atr.isDirectory());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns date and time when foto or video was created
     *
     * @return (Date) date and time of creation
     */
    Date getCreationDateTime() throws IOException {

        //System.out.println("vid path: " + vid.getFileName());
            Date vidCreaDate;
            BasicFileAttributes atr;
            //  try {
        atr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            vidCreaDate = new Date(atr.creationTime().toMillis());
            Date vidLastModDate = new Date(atr.lastModifiedTime().toMillis());
            if (vidCreaDate.getTime() > vidLastModDate.getTime()) {
                vidCreaDate = vidLastModDate;
            }
            //System.out.printf("atr.creationTime(): %s%n", atr.creationTime().toMillis() +"\n)");
        //System.out.printf("vid CreaDate: %s%n", vidCreaDate);
            return vidCreaDate;
           /* } catch (IOException e) {
                System.out.println("Problems with reading attributes of vid: " + file.getAbsolutePath());
                e.printStackTrace();
                return null;
            }*/

        }


    /**
     * returns filename of the foto as string
     *
     * @return (String) filename
     */
    public String getFilename() {
        return file.getName();
    }

    /**
     * returns directory of foto as string
     *
     * @return (String) directory
     */
    public String getDirectory() {
        return dir.getAbsolutePath();
    }

}




