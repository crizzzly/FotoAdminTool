import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Iterator;


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
        file = new File(filename);
        dir = file.getParentFile();
        //setThumbnail();
        // creationDateTime = getCreationDateTime();
    }

    Foto (File file){
        this.file = file;
        dir = new File(file.getPath());
    }

    void createThumbnail(){
        BufferedImage inputImg = null;
        try {
            inputImg = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int resWidth = 64;
        int resHeight = 64;

        int origWidth = inputImg.getWidth();
        int origHeight = inputImg.getHeight();

        //check if scale is needed
        if(origWidth <= resWidth && origHeight <= resHeight){
            thumbnail = inputImg;
        } else {
            Scalr.Mode scaleMode = Scalr.Mode.AUTOMATIC;

            int maxSize = 0;
            if (origHeight < origWidth){
                scaleMode = Scalr.Mode.FIT_TO_WIDTH;
                maxSize = resWidth;
            } else if (origWidth < origHeight){
                scaleMode = Scalr.Mode.FIT_TO_HEIGHT;
                maxSize = resHeight;
            }

            BufferedImage outputImg = Scalr.resize(inputImg, Scalr.Method.SPEED, scaleMode, maxSize);
            thumbnail =  outputImg;

        }
    }

    /**
     * if Instance is a jpg file, thumbnail will be a scaled instance of the jpg.
     * if it is a mp4 video file, thumbnail will be the video.png file in src folder.
     */
    Image getThumbnail() {

        if (thumbnail == null)  createThumbnail();


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
    /*
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
*/

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
    Date getCreationDateTime() {

        System.out.println("Foto getTime path: " + file.getAbsolutePath());
        Date fileCreationDate;
        BasicFileAttributes atr = null;
            //  try {
        try {
            atr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileCreationDate = new Date(atr.creationTime().toMillis());
        Date fileLastModDate = new Date(atr.lastModifiedTime().toMillis());
        if (fileCreationDate.getTime() > fileLastModDate.getTime()) {
            fileCreationDate = fileLastModDate;
            }
            //System.out.printf("atr.creationTime(): %s%n", atr.creationTime().toMillis() +"\n)");
        return fileCreationDate;

        }


    /**
     * returns filename of the foto as string
     *
     * @return (String) filename
     */
    public String getFilename() {
        return file.getName();
    }

    public File getFile(){ return file;}

    public String getAbsolutePath(){ return file.getAbsolutePath(); }

    /**
     * returns directory of foto as string
     *
     * @return (String) directory
     */
    public String getDirectory() {
        return dir.getAbsolutePath();
    }

}




