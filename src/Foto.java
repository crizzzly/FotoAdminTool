import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Date;
import java.io.FilenameFilter;


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
    private File newDir;
    private Metadata meta;
    protected int id;
    private Date creationDateTime;

    // private Path newDir;
    //private Metadata meta;
    //private Path path;
    //private String fileName;

    /**
     * creates new instance of class "foto" on base of path and filename of the foto.
     * checks if file is a foto (jpg or jpeg), reads metadata and saves it as Metadata.
     *
     * @param path     path of the foto
     * @param filename filename of the foto
     */
    public Foto(String path, String filename) {
        dir = new File(path);
        file = new File(dir, filename);
        //creationDateTime = getCreationDateTime();

        if (file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("jpeg")) {
            int maxWidth = 91;
            int maxHeight = 52;

            image = new ImageIcon(path + filename);
            int imgWidth = image.getIconWidth();
            int imgHeight = image.getIconHeight();

            if (imgHeight > imgWidth) {
                int save = maxHeight;
                maxHeight = maxWidth;
                maxWidth = maxHeight;
            }
            thumbnail = Toolkit.getDefaultToolkit().getImage(path + filename)
                    .getScaledInstance(maxWidth, maxHeight, Image.SCALE_SMOOTH);
            //.getScaledInstance((int) ((image.getIconWidth() * 1.5) / 100) + 1, (int) ((image.getIconHeight() * 1.5) / 100) + 1, Image.SCALE_SMOOTH);
            try {
                meta = ImageMetadataReader.readMetadata(file);
                // showMetadata();

            } catch (ImageProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Constructor implementing meta: ");
                e.printStackTrace();
            }
        } else {
            try {
                thumbnail = ImageIO.read(getClass().getResource("video.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Foto(String filename) {
        //
        //
        // System.out.println(path.toString()+ filename);
        file = new File(filename);//"C:\\Users\\Chrissi\\Pictures\\imagesTest\\1-111.jpg");
        dir = file.getParentFile();
        // creationDateTime = getCreationDateTime();


        if (file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("jpeg")) {
            image = new ImageIcon(file.getAbsolutePath());
            thumbnail = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath())
                    .getScaledInstance((int) ((image.getIconWidth() * 1.5) / 100) + 1, (int) ((image.getIconHeight() * 1.5) / 100) + 1, Image.SCALE_SMOOTH);
            try {
                meta = ImageMetadataReader.readMetadata(file);
                // showMetadata();

            } catch (ImageProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Constructor implementing meta: ");
                e.printStackTrace();
            }
        } else {
            try {
                thumbnail = ImageIO.read(getClass().getResource("video.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Foto(File path, String filename) {
        //
        //
        // System.out.println(path.toString()+ filename);
        file = new File(path, filename);//"C:\\Users\\Chrissi\\Pictures\\imagesTest\\1-111.jpg");
        dir = file.getParentFile();
        //creationDateTime = getCreationDateTime();
        if (file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("jpeg")) {
            image = new ImageIcon(path.toString() + "" + filename);
            thumbnail = Toolkit.getDefaultToolkit().getImage(path + filename)
                    .getScaledInstance((int) ((image.getIconWidth() * 1.5) / 100) + 1, (int) ((image.getIconHeight() * 1.5) / 100) + 1, Image.SCALE_SMOOTH);
            try {
                meta = ImageMetadataReader.readMetadata(file);
                showMetadata();

            } catch (ImageProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Constructor implementing meta: ");
                e.printStackTrace();
            }
        } else {
            try {
                thumbnail = ImageIO.read(getClass().getResource("video.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
/*
    /**
     * constructor of class foto.
     * creates new instance of class "foto" on base of path and filename of the foto.
     * checks if file is a foto (jpg or jpeg), reads metadata and saves it as Metadata.
     *
     * @param id       id of the foto
     * @param path     path to the foto
     * @param fileName filename
     */
/*
    public Foto(int id, String path, String fileName) {
        dir = new File(path);
        file = new File(dir, fileName);
        this.id = id;

        image = new ImageIcon(path + fileName);
        thumbnail = Toolkit.getDefaultToolkit().getImage(path + fileName)
                .getScaledInstance((int) ((image.getIconWidth() * 1.5) / 100) + 1, (int) ((image.getIconHeight() * 1.5) / 100) + 1, Image.SCALE_SMOOTH);

        try {
            meta = ImageMetadataReader.readMetadata(file);
            //showMetadata();

        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Constructor implementing meta: " + e);
            e.printStackTrace();
        }
    }*/

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
     * returns a scaled instance of the foto
     *
     * @return scaled instance of foto
     */
    public ImageIcon getThumbnail() {
        return new ImageIcon(thumbnail);
    }

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
    public void moveFile(String path) {
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

    /*
    public void moveBackFile() {
        try {
            if (file.renameTo(new File(dir.getAbsolutePath(), file.getName()))) {
                System.out.println("File moved back successfully!");
            } else {
                System.out.println("File is failed to move!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    //shows metadata of the foto
    public void showMetadata() {
        for (Directory directory : meta.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.format("[%s] - %s = %s \n", directory.getName(), tag.getTagName(), tag.getDescription());
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.format("ERROR: %s", error);
                }
            }
        }
    }

    /**
     * returns date and time when foto or video was created
     *
     * @return date and time of creation (Date)
     */
    public Date getCreationDateTime() throws IOException {
        if (file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("jpeg")) {
            ExifSubIFDDirectory directory = meta.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            return directory.getDateOriginal();
            //if its not an image, it's a video (check is done in SortImages)
        } else {
            Path vid = file.toPath();
            System.out.println("vid path: " + vid.getFileName());
            Date vidCreaDate;
            BasicFileAttributes atr;
            //  try {
            atr = Files.readAttributes(vid, BasicFileAttributes.class);
            vidCreaDate = new Date(atr.creationTime().toMillis());
            Date vidLastModDate = new Date(atr.lastModifiedTime().toMillis());
            if (vidCreaDate.getTime() > vidLastModDate.getTime()) {
                vidCreaDate = vidLastModDate;
            }
            //System.out.printf("atr.creationTime(): %s%n", atr.creationTime().toMillis() +"\n)");
            System.out.printf("vid CreaDate: %s%n", vidCreaDate);
            return vidCreaDate;
           /* } catch (IOException e) {
                System.out.println("Problems with reading attributes of vid: " + file.getAbsolutePath());
                e.printStackTrace();
                return null;
            }*/

        }
    }

    /**
     * returns filename of the foto as string
     *
     * @return filename
     */
    public String getFilename() {
        return file.getName();
    }

    /**
     * returns directory of foto as string
     *
     * @return directory
     */
    public String getDirectory() {
        return dir.getAbsolutePath();
    }
/*
    /**
     * returns id of foto as int
     *
     * @return id
     */
 /*   public int getId() {
        return id;
    }*/
}




