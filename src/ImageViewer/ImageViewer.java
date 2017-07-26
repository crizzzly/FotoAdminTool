package ImageViewer;

import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * ImageViewer is the main class of the image viewer application. It builds and
 * displays the application GUI and initialises all other components.
 * <p>
 * To start the application, create an object of this class.
 *
 * @author Michael Kölling and David J. Barnes.
 * @version 1.0
 */
public class ImageViewer implements TreeSelectionListener {
    // static fields:
    private static final String VERSION = "Version 1.0";
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

    // fields:
    private JFrame frame;
    private ImagePanel imagePanel;
    private JScrollPane scrollPane;
    private JLabel filenameLabel;
    private JLabel statusLabel;
    private OFImage currentImage;
    private BufferedImage scaledImage;
    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    private int maxSize;


    /**
     * Create an ImageViewer show it on screen.
     */
    public ImageViewer() {
        currentImage = null;
        makeFrame();
    }

    public ImageViewer(File file) {
        System.out.println("imageViewer filePath: " + file.getAbsolutePath());
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // currentImage = new OFImage(image);
        currentImage = new OFImage(image);//ImageFileManager.loadImage(file);//new OFImage(image);
        System.out.println("loaded bufferedImage: " + Arrays.toString(currentImage.getPropertyNames()));
        assert currentImage != null;
        //showFilename(file.getName());
        makeFrame();
        if(d.height > d.width) maxSize = 5*d.height/6;
        else maxSize = 5*d.width/6;

        OFImage scImage = new OFImage(currentImage.getScaledInstance(image, maxSize));
        imagePanel.setImage(scImage);

    }

    // ---- implementation of menu functions ----

    /**
     * Open function: open a file chooser to select a new image file.
     */
    private void openFile() {
        int returnVal = fileChooser.showOpenDialog(frame);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;  // cancelled
        }
        File selectedFile = fileChooser.getSelectedFile();
        currentImage = ImageFileManager.loadImage(selectedFile);

        if (currentImage == null) {   // image file was not a valid image
            JOptionPane.showMessageDialog(frame,
                    "The file was not in a recognized image file format.",
                    "Image Load Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        imagePanel.setImage(currentImage);
        showFilename(selectedFile.getPath());
        showStatus("File loaded.");
        frame.pack();
    }

    /**
     * Close function: close the current image.
     */
    private void close() {
        currentImage = null;
        imagePanel.clearImage();
        showFilename(null);
    }

    /**
     * Quit function: quit the application.
     */
    private void quit() {
        System.exit(0);
    }


    /**
     * 'Darker' function: make the picture darker.
     */
    private void makeDarker() {
        if (currentImage != null) {
            currentImage.darker();
            frame.repaint();
            showStatus("Applied: darker");
        } else {
            showStatus("No image loaded.");
        }
    }

    /**
     * 'Lighter' function: make the picture lighter
     */
    private void makeLighter() {
        if (currentImage != null) {
            currentImage.brighter();
            frame.repaint();
            showStatus("Applied: lighter");
        } else {
            showStatus("No image loaded.");
        }
    }

    /**
     * 'threshold' function: apply the threshold filter
     */
    private void threshold() {
        if (currentImage != null) {
            currentImage.threshold();
            frame.repaint();
            showStatus("Applied: threshold");
        } else {
            showStatus("No image loaded.");
        }
    }

    /**
     * Show the 'About...' dialog.
     */
    private void showAbout() {
        JOptionPane.showMessageDialog(frame,
                "ImageViewer\n" + VERSION,
                "About ImageViewer",
                JOptionPane.INFORMATION_MESSAGE);
    }


    // ---- support methods ----

    /**
     * Display a file name on the appropriate label.
     *
     * @param filename The file name to be displayed.
     */
    private void showFilename(String filename) {
        if (filename == null) {
            filenameLabel.setText("No file displayed.");
        } else {
            filenameLabel.setText("File: " + filename);
        }
    }

    /**
     * Display a status message in the frame's status bar.
     *
     * @param text The status message to be displayed.
     */
    private void showStatus(String text) {
        statusLabel.setText(text);
    }

    // ---- swing stuff to build the frame and all its components ----

    /**
     * Create the Swing frame and its content.
     */
    private void makeFrame() {
        frame = new JFrame("ImageViewer");
        makeMenuBar(frame);

        Container contentPane = frame.getContentPane();

        // Specify the layout manager with nice spacing
        contentPane.setLayout(new BorderLayout(6, 6));

        filenameLabel = new JLabel();
        contentPane.add(filenameLabel, BorderLayout.NORTH);

        imagePanel = new ImagePanel();
        scrollPane = new JScrollPane();
        scrollPane.getViewport().add(imagePanel);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel(VERSION);
        contentPane.add(statusLabel, BorderLayout.SOUTH);

        // building is done - arrange the components and show
        showFilename(null);
        frame.pack();

        frame.setLocation(d.width / 4 - frame.getWidth() / 4, d.height / 4 - frame.getHeight() / 4);
        frame.setSize((3* d.width/4), (3*d.height/4) );
        frame.setVisible(true);
    }

    /**
     * Create the main frame's menu bar.
     *
     * @param frame The frame that the menu bar should be added to.
     */
    private void makeMenuBar(JFrame frame) {
        final int SHORTCUT_MASK =
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();


        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);

        JMenu menu;
        JMenuItem item;

        // create the File menu
        menu = new JMenu("File");
        menubar.add(menu);

        item = new JMenuItem("Open");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
        item.addActionListener(e -> openFile());
        menu.add(item);

        item = new JMenuItem("Close");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, SHORTCUT_MASK));
        item.addActionListener(e -> close());
        menu.add(item);
        menu.addSeparator();

        item = new JMenuItem("Quit");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        item.addActionListener(e -> quit());
        menu.add(item);


        // create the Filter menu
        menu = new JMenu("Filter");
        menubar.add(menu);

        item = new JMenuItem("Darker");
        item.addActionListener(e -> makeDarker());
        menu.add(item);

        item = new JMenuItem("Lighter");
        item.addActionListener(e -> makeLighter());
        menu.add(item);

        item = new JMenuItem("Threshold");
        item.addActionListener(e -> threshold());
        menu.add(item);


        // create the Help menu
        menu = new JMenu("Help");
        menubar.add(menu);

        item = new JMenuItem("About ImageViewer...");
        item.addActionListener(e -> showAbout());
        menu.add(item);

    }

    public static void main(String[] args) {
        new ImageViewer();
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {

    }
}
