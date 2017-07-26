import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import static java.awt.Toolkit.*;


/**
 * Created by Chrissi on 11.05.2017.
 */

/**
 * Testcommentblabla
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

        //setThumbnail();
    }

    /**
     * constructor of class Foto. saves picture/video in File file, saves parent directory in File dir
     * calls method to create thumbnail
     *
     * @param filename of the foto/video (String)
     */
    Foto(String filename) {

        file = new File(filename);
        dir = file.getParentFile();
    }

    Foto (File file){
        this.file = file;
        dir = new File(file.getPath());
    }

    private void createThumbnail(){
        BufferedImage inputImg = null;
        try {
            inputImg = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int resWidth = 64;
        int resHeight = 64;

        assert inputImg != null;
        int origWidth = inputImg.getWidth();
        int origHeight = inputImg.getHeight();

        //check if scale is needed
        if(origWidth <= resWidth && origHeight <= resHeight) thumbnail = inputImg;
        else {
            Scalr.Mode scaleMode = Scalr.Mode.AUTOMATIC;

            int maxSize = 0;
            if (origHeight < origWidth){
                scaleMode = Scalr.Mode.FIT_TO_WIDTH;
                maxSize = resWidth;
            } else if (origWidth < origHeight){
                scaleMode = Scalr.Mode.FIT_TO_HEIGHT;
                maxSize = resHeight;
            }

            thumbnail = Scalr.resize(inputImg, Scalr.Method.SPEED, scaleMode, maxSize);

        }
    }

    /**
     * if Instance is a jpg file, thumbnail will be a scaled instance of the jpg.
     * if it is a mp4 video file, thumbnail will be the video.png file in src folder.
     */
    Image getThumbnail() {

        if (thumbnail == null) createThumbnail();

        return thumbnail;
    }

    /* public ImageIcon getThumbnail() {
        return new ImageIcon(thumbnail);
    }*/

    /**
     * returns the foto as image file
     * @return (Image) Foto
     */
    public Image getImage()  {
        return  (image != null ? getDefaultToolkit().getImage(image.getAccessibleContext().toString()) : null);}



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

        Date fileCreationDate;
        BasicFileAttributes atr = null;
            //  try {
        try {
            atr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //assert atr != null;
        fileCreationDate = new Date(atr != null ? atr.creationTime().toMillis() : 0);
        //assert atr != null;
        Date fileLastModDate = new Date(atr != null ? atr.lastModifiedTime().toMillis() : 0);
        if (fileCreationDate.getTime() > fileLastModDate.getTime()) {
            fileCreationDate = fileLastModDate;
            }
        return fileCreationDate;

        }


    /**
     * returns filename of the foto as string
     *
     * @return (String) filename
     */
    String getFilename() {return file.getName();}

    File getFile(){ return file;}

    public String getAbsolutePath(){return file.getAbsolutePath();}

    /**
     * returns directory of foto as string
     *
     * @return (String) directory
     */
    public String getDirectory() {
        return dir.getAbsolutePath();
    }

}




