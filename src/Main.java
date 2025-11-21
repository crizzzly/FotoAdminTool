
import FileBrowser.MainFrame;
import FileBrowser.SortImages;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sets up Look & feel
 * creates instance of FileBrowser.MainFrame
 * Created by Chrissi on 23.06.2017.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
      /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | ClassNotFoundException | InstantiationException ex) {
            LOGGER.log(Level.SEVERE, "Failed to set look and feel", ex);
        }
        /* Turn off metal's use of bold fonts */
        //UIManager.put("swing.", Boolean.FALSE);

        MainFrame mainFrame = new MainFrame();
        //frame.setVisible(true);
        Runtime.getRuntime().addShutdownHook(new Thread(SortImages::undoChanges));
    }
}

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