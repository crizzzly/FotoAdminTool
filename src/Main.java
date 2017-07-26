
import javax.swing.*;

import java.util.Random;

import static javafx.scene.input.KeyCode.O;


/**
 * Sets up Look & feel
 * creates instance of Frame
 * Created by Chrissi on 23.06.2017.
 */
public class Main {
    /*
        static ProgressMonitor pbar;
        static int counter = 0;
        public Main() {
            setSize(250, 100);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            pbar = new ProgressMonitor(null, "Monitoring Progress",
                    "Initializing . . .", 0, 100);
            // Fire a timer every once in a while to update the progress.
            Timer timer = new Timer(500, this);
            timer.start();
            setVisible(true);
        }
        */
    public static void main(String[] args) {
      /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | ClassNotFoundException | InstantiationException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);


        new Frame();
        //frame.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> SortImages.undoChanges()));

        // Ende Code Part ----------------------------------------------------------------------------------------------------


    }

}
//thumbs erzeugen - in versch, größen (16px, 32, 64,...512 px)
//als img (jpg) speichern, mit iostream komprimieren u in byteArray speichern (file.db)

/*
    private static void getImageThumbs(String path){
        ImageInputStream input = null;
        try {
            input = ImageIO.createImageInputStream(new File(path+"\\thumbs.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageReader reader = ImageIO.getImageReaders(input).next();
        reader.setInput(input);

        try {
            for (int i = 0; i < reader.getNumImages(true); i++) {
                BufferedImage thumb = reader.read(i);

                // do something with it...
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        reader.dispose(); // These last two, preferably in a finally block or "try-with-resource"
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

