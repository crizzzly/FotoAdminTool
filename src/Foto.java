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
    private String extension = null;
    protected int id;

    // private Path newDir;
    //private Metadata meta;
    //private Path path;
    //private String fileName;

    /**
     * creates new instance of class "foto" on base of path and filename of the foto.
     * checks if file is a foto (jpg or jpeg), reads metadata and saves it as Metadata.
     *
     * @param path     path of the foto
     * @param fileName filename of the foto
     */
    public Foto(String path, String fileName) {
        dir = new File(path);
        file = new File(dir, fileName);
        //image = Toolkit.getDefaultToolkit().getImage(path+fileName);
        image = new ImageIcon(path + fileName);
        thumbnail = Toolkit.getDefaultToolkit().getImage(path + fileName)
                .getScaledInstance((int) ((image.getIconWidth() * 1.5) / 100) + 1, (int) ((image.getIconHeight() * 1.5) / 100) + 1, Image.SCALE_SMOOTH);
        try {
            //checks if file is *jpg oder *jpeg. if it is so, it reads the metadata of the file.
            if (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg")) {
                meta = ImageMetadataReader.readMetadata(file);
            } else {
            }

            //showMetadata();

        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Constructor implementing meta: " + e);
            e.printStackTrace();
        }
    }

    /**
     * constructor of class foto.
     * creates new instance of class "foto" on base of path and filename of the foto.
     * checks if file is a foto (jpg or jpeg), reads metadata and saves it as Metadata.
     *
     * @param id       id of the foto
     * @param path     path to the foto
     * @param fileName filename
     */

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
    }

    /**
     * creates new instance of class "foto" on base of path and filename of the foto.
     * checks if file is a foto (jpg or jpeg), reads metadata and saves it as Metadata.
     */
    public Foto(File file) {
        this.file = file;
        dir = file.getParentFile();

        image = new ImageIcon(file.getAbsolutePath());
        thumbnail = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath())
                .getScaledInstance((int) ((image.getIconWidth() * 1.5) / 100) + 1, (int) ((image.getIconHeight() * 1.5) / 100) + 1, Image.SCALE_SMOOTH);

        //dir = new File("C:/Users/Chrissi/Pictures/imagesTest");
        /*
        try {
            BufferedImage bi = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Constructor Foto: ");
            e.printStackTrace();
        }
        */
        try {
            meta = ImageMetadataReader.readMetadata(file);
            showMetadata();

        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Constructor implementing meta: ");
            e.printStackTrace();
        }

    }

    public Foto(String filename) {
        //
        //
        // System.out.println(path.toString()+ filename);
        file = new File(filename);//"C:\\Users\\Chrissi\\Pictures\\imagesTest\\1-111.jpg");
        dir = file.getParentFile();

        image = new ImageIcon(filename);
        thumbnail = Toolkit.getDefaultToolkit().getImage(filename)
                .getScaledInstance((int) ((image.getIconWidth() * 1.5) / 100) + 1, (int) ((image.getIconHeight() * 1.5) / 100) + 1, Image.SCALE_SMOOTH);


        try {
            meta = ImageMetadataReader.readMetadata(file);
            //showMetadata();

        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Constructor implementing meta: ");
            e.printStackTrace();
        }

    }

    public Foto(File path, String filename) {
        //
        //
        // System.out.println(path.toString()+ filename);
        file = new File(path, filename);//"C:\\Users\\Chrissi\\Pictures\\imagesTest\\1-111.jpg");
        dir = file.getParentFile();

        image = new ImageIcon(path.toString() + "" + filename);
        thumbnail = Toolkit.getDefaultToolkit().getImage(path + filename)
                .getScaledInstance((int) ((image.getIconWidth() * 1.5) / 100) + 1, (int) ((image.getIconHeight() * 1.5) / 100) + 1, Image.SCALE_SMOOTH);

        //dir = new File("C:/Users/Chrissi/Pictures/imagesTest");
        /*
        try {
            BufferedImage bi = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Constructor Foto: ");
            e.printStackTrace();
        }
        */
        try {
            meta = ImageMetadataReader.readMetadata(file);
            showMetadata();

        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Constructor implementing meta: ");
            e.printStackTrace();
        }

    }
    /*
    public String createNewSubDirectory(String newDir) {
        //creates new subfolder. if successfull, newDirDone = true
        boolean newDirDone = new File(dir, newDir).mkdir();

        if (newDirDone) {
            this.newDir = new File(dir, newDir);
            return this.newDir.toString();
        }
        else {
            System.out.println("no directory created");
            return null;
        }

    }
    */

    public ImageIcon getThumbnail() {
        return new ImageIcon(thumbnail);
    }

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
     * returns date and time when foto was created
     *
     * @return date and time of creation (Date)
     */
    public Date getCreationDateTime() {
        ExifSubIFDDirectory directory = meta.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        return directory.getDateOriginal();
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

    /**
     * returns id of foto as int
     *
     * @return id
     */
    public int getId() {
        return id;
    }
}




