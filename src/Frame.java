//package Gui;

import ImageViewer.ImageViewer;
import java.awt.*;
import java.awt.event.*;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoLong;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoXpString;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;

import java.awt.image.BufferedImage;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.Position;
import javax.swing.tree.*;
import javax.swing.DefaultListModel;


/**
 * Created by Chrissi on 31.05.2017.
 * jajaj ich schreib ja schon was
 */
public class Frame extends JFrame implements ActionListener, PropertyChangeListener {
    private final Container c = getContentPane();

    //opens the windows standard directory for pictures on startup
    private final File sourceDir;

    private JList<Object> listContent;
    //private JPanel progressPanel;

    private ArrayList<Image> thumbnails;
    private ArrayList<File> content;
    private File selected = null;

    //JDialog for sorting setup
    private JDialog sortjd = null;

    private int hoursBetweenFotos;
    private JFormattedTextField hoursTextfield;

    //counts the loaded images
    private int loadCount;
    private FileTree fileTree;
    private final Dimension screenSize;
    private static final Logger LOGGER = Logger.getLogger(Frame.class.getName());

    Frame() {

        super("Foto Administration Tool");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        screenSize = getToolkit().getScreenSize();
        setMaximumSize(screenSize);
        setBounds(0, 0, screenSize.width, screenSize.height);

        c.setLayout(new BorderLayout());

        // TODO: change this to a user-selected directory
        sourceDir = new File(System.getProperty("user.home")+"/Pictures");


        buildToolbar();
        buildFileTree();
        buildContentPanel();
        //buildProgressBar();


        pack();
        setVisible(true);
    }


    /*
    private void buildProgressBar() {
        progressPanel = new JPanel();
        progressPanel.removeAll();
        JLabel progress;

        if(content.size() != 0) {
             progress = new JLabel(loadCount + " images of " + content.size() + " loaded.");
        }
        progressPanel.add(progress);
        //}
        c.add(progressPanel, BorderLayout.SOUTH);
        pack();
        progressPanel.validate();
        progressPanel.updateUI();
        progressPanel.updateUI();

    }
    */

    /**
     * build the screen in the middle where all files and directories are shown. perhabs sooon with thumbnail!!
     * adds a new Jpanel = contentPane in flow layout
     * <p>
     * checks if the last selected item of the filetree is set. if it is so it will set the sourceDir to lastSelectet
     * if not, sourceDir will be used instead
     * <p>
     * saves the complete list of the selected item in content as array
     * for each item in selected, it creates a JLabel and adds it to the contentPane
     */
    private void buildContentPanel() {
        content = new ArrayList<>();
        thumbnails = new ArrayList<>();

        //creates JList that holds thumbnails and names of files / directories
        DefaultListModel<Object> listModel = new DefaultListModel<>();
        // listModel.removeAllElements();
        listContent = new JList<>(listModel);

        listContent.removeAll();
        content.clear();
        thumbnails.clear();


        //if selected folder contains files or folders, content will be added to content ArrayList<File>
        if (selected == null){
            selected = sourceDir;
        }

        //creates a File and puts it  in the array of content-files
        content.addAll(Arrays.asList(Objects.requireNonNull(selected.listFiles())));
        MyThread t1 = new MyThread();
        t1.run(content);
        

        // Set up the list with a placeholder model first
        // DefaultListModel<Object> listModel = new DefaultListModel<>();
        for (File file : content) {
            listModel.addElement(file);
        }
        listContent.setModel(listModel);
        
        // Start loading thumbnails in the background
        new Thread(() -> {
            // Process all files and create thumbnails
            for (int i = 0; i < content.size(); i++) {
                final int index = i;
                try {
                    Image thumbnail;
                    if (content.get(i).isDirectory()) {
                        thumbnail = ImageIO.read(Objects.requireNonNull(getClass().getResource("folder.png")));
                    } else if (content.get(i).getName().toLowerCase().matches(".*\\.(jpg|jpeg|png)$")) {
                        thumbnail = new Foto(content.get(i).getAbsolutePath()).getThumbnail();
                    } else if (content.get(i).getName().toLowerCase().endsWith(".cr2") || 
                              content.get(i).getName().toLowerCase().endsWith(".nef") ||
                              content.get(i).getName().toLowerCase().endsWith(".arw") ||
                              content.get(i).getName().toLowerCase().matches(".*\\.(raf|dng|crw|cr3|raw|rw2|pef|srf|sr2|x3f)$")) {
                        thumbnail = createRawThumbnail(content.get(i));
                    } else {
                        // Default icon for unsupported file types
                        try {
                            thumbnail = ImageIO.read(Objects.requireNonNull(getClass().getResource("file.png")));
                        } catch (Exception e) {
                            thumbnail = null;
                        }
                    }
                    
                    // Update the UI on the Event Dispatch Thread
                    Image finalThumbnail = thumbnail;
                    SwingUtilities.invokeLater(() -> {
                        if (thumbnails.size() > index) {
                            thumbnails.set(index, finalThumbnail);
                        } else {
                            // If the index doesn't exist yet, fill up to that index with nulls
                            while (thumbnails.size() < index) {
                                thumbnails.add(null);
                            }
                            thumbnails.add(finalThumbnail);
                        }
                        // Trigger a repaint of just this cell
                        listContent.repaint(listContent.getCellBounds(index, index));
                    });
                    
                    // Small delay to prevent UI freezing
                    Thread.sleep(50);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error creating thumbnail for: " + content.get(i).getAbsolutePath(), e);
                }
            }
        }).start();

        //the call to setLayoutOrientation, invoking setVisibleRowCount(-1) makes the list display the maximum number of items possible in the available space
        listContent.setVisibleRowCount(-1);

        // Display an icon and a string for each object in the list.
        class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {

            // This is the only method defined by ListCellRenderer.
            // We just reconfigure the JLabel each time we're called.
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,           // the list
                    Object value,            // value to display
                    int index,               // cell index
                    boolean isSelected,      // is the cell selected
                    boolean cellHasFocus)    // does the cell have focus
            {
                File file = (File) value;
                setText(file.getName());
                
                // Set the icon if it's loaded, otherwise use a loading placeholder
                if (index < thumbnails.size() && thumbnails.get(index) != null) {
                    setIcon(new ImageIcon(thumbnails.get(index)));
                } else {
                    // Create a placeholder icon or use a loading indicator
                    try {
                        // Try to use a loading icon or file icon as placeholder
                        ImageIcon loadingIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("loading.png")));
                        setIcon(loadingIcon);
                    } catch (Exception e) {
                        // If no loading icon is available, use a default system icon
                        setIcon(UIManager.getIcon("FileView.fileIcon"));
                    }
                }
                
                setEnabled(list.isEnabled());
                setFont(list.getFont());
                setOpaque(true);

                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(Color.DARK_GRAY);
                    setForeground(list.getForeground());
                }
                return this;
            }
        }
        ListCellRenderer<Object> renderer = new MyCellRenderer();
        listContent.setCellRenderer(renderer);

        //setup for selection mode: Multiple Intervall means you can select one or more item no matter if there is one non-selected between them
        listContent.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listContent.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        //changes color of listContent
        listContent.setBackground(Color.DARK_GRAY);
        listContent.setForeground(Color.WHITE);

        //action performed when mouse is clicked twice:
        //opens ImageViewer if clicked file is File, else opens the folder
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                int index = listContent.locationToIndex(e.getPoint());
                if (index < 0) return;
                LOGGER.log(Level.INFO, "Double clicked on Item " + index);
                File clicked = new File(listContent.getModel().getElementAt(index).toString());

                // TODO: Not working, Directory not opening in file browser
                if(clicked.isDirectory()) {
                    fileTree.tree.clearSelection();
                    fileTree.setSelectedTreeNode(clicked.getAbsolutePath());
                }
                else{
                    new ImageViewer(clicked);// .getAccessibleContext().get
                    LOGGER.log(Level.INFO, "Opened ImageViewer for file: " + clicked);

                }
            }
            }
        };
        //adds the mouseListener to the JList
        listContent.addMouseListener(mouseListener);
        listContent.updateUI();

        JScrollPane contentPanel = createContentScrollPane();
        c.add(contentPanel, BorderLayout.CENTER);
        pack();
    }

    /**
     * Creates and configures a JScrollPane for the content list.
     * @return Configured JScrollPane containing the list content
     */
    private JScrollPane createContentScrollPane() {
        JScrollPane contentPanel = new JScrollPane(listContent);
        contentPanel.setLayout(new ScrollPaneLayout());
        //---- change appearance of contentPanel
        contentPanel.setPreferredSize(new Dimension(900, 800));
        contentPanel.setBackground(Color.DARK_GRAY);
        contentPanel.setForeground(Color.WHITE);
        contentPanel.setWheelScrollingEnabled(true);

        //sets the scrollbar. horizontal is turned off, vertical is shown when needed
        contentPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        contentPanel.setAutoscrolls(true);
        contentPanel.getViewport().updateUI();
        
        return contentPanel;
    }
    
    // creates a thread to run progress in background
    private class MyThread extends Thread {
        /**
         * creates MultiThreads to create thumbnails.
         * To prevent out of memory exceptions, let the threads sleep for 300/600 millisek.
         */
        MyThread() {
            super();
        }


        /**
         * @param fileArrayList ArrayList<File> with contents of the folder
         */
        void run(ArrayList<File> fileArrayList) {
            // Prepare to execute and store the Futures
            int threadNum = fileArrayList.size();
            try (ExecutorService executor = Executors.newFixedThreadPool(threadNum)) {
                //arrayList for created values in future task
                ArrayList<FutureTask<Integer>> taskList = new ArrayList<>();

                //to prevent out of memory exception, let the threads sleep for 300/600ms
                for (int t = 0; t < threadNum; t++) {
                    int finalT = t;

                    if ((finalT % 2) == 0) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            LOGGER.log(Level.SEVERE, "Error message", e);
                        }
                    } else {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            LOGGER.log(Level.SEVERE, "Error message", e);
                        }

                    }

                    // Start thread for the first half of the numbers
                    FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
                        @Override
                        public Integer call() {
                            // for (int i = 0; i < content.size(); i++) {
                            //if file is directory, an image of a directory will be pushed in the array of thumbnails on the same position as the file is
                            if (fileArrayList.get(finalT).isDirectory()) {
                                try {
                                    thumbnails.add(ImageIO.read(Objects.requireNonNull(getClass().getResource("folder.png"))));//new ImageIcon(getClass().getResource("folder.png")));
                                    //System.out.println("added folder thumb");

                                } catch (IOException e) {
                                    LOGGER.log(Level.SEVERE, "Error loading folder icon", e);
                                }
                            }
                            //if file is an image (jpg) or movie (mp4) it creates a Foto, gets the thumbnail of it an pushes it into the array of thumbnails
                            //clas Foto will check if it's a jpg or mp4 and will return a scaled instance or an icon for movies.
                            else {
                                if (fileArrayList.get(finalT).getName().toLowerCase().endsWith("jpg")
                                        || fileArrayList.get(finalT).getName().toLowerCase().endsWith("jpeg")
                                        || fileArrayList.get(finalT).getName().toLowerCase().endsWith("png")) {
                                    try {
                                        thumbnails.add(new Foto(fileArrayList.get(finalT).getAbsolutePath()).getThumbnail());
                                        //System.out.println("added photo thumb");
                                    } catch (Exception e) {
                                        LOGGER.log(Level.SEVERE, "Error creating thumbnail for: " + fileArrayList.get(finalT).getAbsolutePath(), e);
                                    }
                                } else if (fileArrayList.get(finalT).getName().toLowerCase().endsWith("mp4")) {
                                    try {
                                        thumbnails.add(ImageIO.read(Objects.requireNonNull(getClass().getResource("assets/video.png"))));
                                        //System.out.println("added video thumb");

                                    } catch (IOException e) {
                                        LOGGER.log(Level.SEVERE, "Error message", e);
                                    }
                                } else if (fileArrayList.get(finalT).getName().toLowerCase().endsWith("mpeg4")) {
                                    try {
                                        thumbnails.add(ImageIO.read(Objects.requireNonNull(getClass().getResource("assets/video.png"))));
                                        //System.out.println("added video thumb");

                                    } catch (IOException e) {
                                        LOGGER.log(Level.SEVERE, "Error message", e);
                                    }
                                } else {
                                    Icon sysIco = FileSystemView.getFileSystemView().getSystemIcon(fileArrayList.get(finalT));
                                    thumbnails.add(((ImageIcon) sysIco).getImage());
                                    //.out.println("added sysIcon thumb");

                                }
                            }
                            return 0;
                        }
                    });
                    taskList.add(futureTask);
                    executor.execute(futureTask);
                }

                // Wait until all results are available and combine them at the same time
                int amount = 0;
                for (int j = 0; j < threadNum; j++) {
                    FutureTask<Integer> futureTask = taskList.get(j);
                    try {
                        amount += futureTask.get();
                    } catch (InterruptedException | ExecutionException e) {
                        LOGGER.log(Level.SEVERE, "Error message", e);
                    }
                }
                executor.shutdown();
            }
        }
    }


    /**
     * builds the menuBar
     * made by Jana Seemann
     */
    private void buildMenuBar() {
        // creates menuBar
        JMenuBar menu = new JMenuBar();
        // Menü wird hinzugefügt
        menu.add(new JMenu("Datei"));

        JButton bearbeiten = new JButton("Bearbeiten");
        JButton hilfe = new JButton("Hilfe");


        bearbeiten.setBackground(Color.DARK_GRAY);
        bearbeiten.setForeground(Color.WHITE);

        hilfe.setBackground(Color.DARK_GRAY);
        hilfe.setForeground(Color.WHITE);

        bearbeiten.setActionCommand("Bearbeiten");
        hilfe.setActionCommand("Hilfe");

        bearbeiten.addActionListener(this);
        hilfe.addActionListener(this);

        //bar.add(bearbeiten);
        //bar.add(hilfe);
    }

    /**
     * Builds a toolbar that is not movable. if you want it movable, use setFloatable(true)
     * adds the toolbar to the JFrame on top of the frame
     * creates a button to set up the location of the folder you want to sort.
     */
    private void buildToolbar() {
        JToolBar bar = new JToolBar();
        bar.setSize(230, 20);

        bar.setBackground(Color.DARK_GRAY);
        bar.setForeground(Color.WHITE);

        //----

        bar.setFloatable(false);
        c.add(bar, BorderLayout.NORTH);
        //new JButton, which image is located in the src-folder

        //---
        JButton chooseFolder = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource("folder.png"))));
        JButton sortButton = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource("assets/sort-s.png"))));
        JButton undoChanges = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource("assets/undo.png"))));

        chooseFolder.setBackground(Color.DARK_GRAY);
        chooseFolder.setForeground(Color.WHITE);

        chooseFolder.setToolTipText("set the location of the folder you want to sort");
        chooseFolder.setToolTipText("setup for sort function");

        //if you use setActionCommand you don't have to write one actionListener for each actionEvent.
        chooseFolder.setActionCommand("chooseFolder");
        sortButton.setActionCommand("sort");
        undoChanges.setActionCommand("undo");

        //adds actionListener to the items
        chooseFolder.addActionListener(this);
        sortButton.addActionListener(this);
        undoChanges.addActionListener(this);

        bar.add(chooseFolder);
        bar.add(sortButton);
        bar.add(undoChanges);
    }
    //----

    //Creates a JScrollPane, creates a new FileTree instance with given directory saved in sourceDir
    //adds the FileTree to the treeScrollPane on left side
    private void buildFileTree() {
        JScrollPane treeScrollPane = new JScrollPane();
        fileTree = new FileTree(sourceDir);
        treeScrollPane.getViewport().add(fileTree);
        c.add(treeScrollPane, BorderLayout.WEST);
    }

    /**
     * Display a file system in a JTree view
     *
     * @author Ian Darwin
     * @version $Id: FileTree.java,v 1.9 2004/02/23 03:39:22 ian Exp $
     */
    public class FileTree extends JPanel {
        /**
         * Construct a FileTree
         */
        //final Object[] selectedFile = new File[1];
        private final JTree tree;

        private int minHeight;
        private int minWidth;

        FileTree(File dir) {
            setLayout(new BorderLayout());
            setResizable(true);

            // Make a tree list with all the nodes, and make it a JTree
            tree = new JTree(addNodes(null, dir));
            //  tree.addItemListener
            ToolTipManager.sharedInstance().registerComponent(tree);

            tree.setBackground(Color.DARK_GRAY);
            tree.setForeground(Color.WHITE);

            DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
            renderer.setBackground(new Color(0, 0, 0, 0));
            renderer.setBackgroundNonSelectionColor(new Color(0, 0, 0, 0));
            renderer.setMinimumSize(tree.getMinimumSize());
            // renderer.setToolTipText(tree.get);
            tree.setCellRenderer(renderer);

            // Add a listener
            tree.addTreeSelectionListener(e -> {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                        .getPath().getLastPathComponent();
                System.out.println("Filetree: You selected " + node);
                if (!Objects.equals(selected, new File(node.toString()))) {
                    selected = new File(node.toString());
                    // cPanel.removeAll();
                    if (node.getChildCount() > 0) {
                        try {
                            buildContentPanel();
                        } catch (Exception ex) {
                            System.err.println("buildContent didn't successfully complete");
                        }
                    }
                }
                //printAll(getGraphics());
            });
            // Lastly, put the JTree into a JScrollPane.
            add(tree);
        }

        /**
         * Add nodes from under "dir" into curTop. Highly recursive.
         */
        private DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
            String curPath = dir.getPath();
            DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);

            if (curTop != null) {
                curTop.add(curDir);
            }
            Vector<String> ol = new Vector<>();
            String[] tmp = dir.list();
            assert tmp != null;
            for (String aTmp : tmp) ol.addElement(aTmp);
            ol.sort(String.CASE_INSENSITIVE_ORDER);
            File f;
            Vector<String> files = new Vector<>();
            // Make two passes, one for Dirs and one for Files. This is #1.
            for (int i = 0; i < ol.size(); i++) {
                String thisObject = ol.elementAt(i);
                String newPath;
                if (curPath.equals("."))
                    newPath = thisObject.getClass().getName();
                else
                    newPath = curPath + File.separator + thisObject;
                if ((f = new File(newPath)).isDirectory()) {
                    //renderer.setToolTipText(f.getName());
                    //)tree.setCellRenderer(renderer);

                    addNodes(curDir, f);
                } else
                    files.addElement(thisObject);
            }
            setMinimumHeight(files.size() * 30);
            setMinimumWidth(350);
            // Pass two: for files.
            for (int fnum = 0; fnum < files.size(); fnum++)
                curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
            return curDir;
        }

        private void setMinimumHeight(int height){
            minHeight = height;
        }

        private void setMinimumWidth(int width){
            minWidth = width;
        }
        public Dimension getMinimumSize() {

            return new Dimension(minWidth, minHeight);
        }

        public Dimension getPreferredSize() {
            return new Dimension(350, screenSize.height);
        }

        void setSelectedTreeNode(String path) {
            tree.clearSelection();

            // Search forward from first visible row looking for any visible node
            // whose name starts with prefix.
            int startRow = 0;
            TreePath tPath = tree.getNextMatch(path, startRow, Position.Bias.Forward);
            if(tPath != null){
                tree.setSelectionPath(tPath);
                tree.expandPath(tPath);
            }

        }
    }

    /**
     * calls the sort() function of SortImages class. uses swingWorker to do it in background.
     * When swingWorker is done with it's work, contentPanel and fileTree will be rebuilt
     *
     * @param pathToSort        File path to the directory which will be sorted
     * @param hoursBetweenFotos int time (hours) or distance(km) to specify when Foto will bnne dropped into a new folder
     */
    private void sortImagesByDateTime(File pathToSort, int hoursBetweenFotos) {
        try {
            SortImages.sort(hoursBetweenFotos, pathToSort.toPath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error message", e);
        }

        selected = pathToSort;
        buildContentPanel();
        buildFileTree();
    }

    /**
     * actionListener for all the buttons, JList items in this class
     *
     * @param e ActionEvent given by the instance which calls the actionListener
     */
    public void actionPerformed(ActionEvent e) {
        //obj saves the class of the instance which called actionPerformed
        Object obj = e.getSource();
        //reads the string added to the instance to specify what action will be performed
        String cmd = e.getActionCommand();

        //has to be declared here, else we can't close JDialog when button is clicked


        if (obj instanceof JButton) {
            //folder-Button: opens filechooser to choose directory to sort, saves chosen directory in File selected
            if (cmd.equals("chooseFolder")) {
                //System.out.println("chooseFolder clicked");
                //opens the pop up window to search through the local stored folders.
                JFileChooser fc = new JFileChooser();
                //first directory shown when the file chooser window is opened
                fc.setCurrentDirectory(sourceDir);
                //implements a new FileFilter (function below)
                fc.setFileFilter(new MyFileFilter());
                //directories_only important because else you would choose files instead of dirs
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    //if a valid choice is made, the selected folder of the filechooser will be saved in sourceDir
                    fileTree.tree.clearSelection();
                    TreePath trPth = fileTree.tree.getNextMatch(selected.getPath(), 0, Position.Bias.Forward);
                    fileTree.tree.collapsePath(trPth );
                    selected = fc.getSelectedFile();
                    // sourcheDirSet = true;
                    //show the new content in the treeScrollPane

                    fileTree.setSelectedTreeNode(selected.getAbsolutePath());
                    trPth = fileTree.tree.getNextMatch(selected.getPath(), 0, Position.Bias.Forward);
                    fileTree.tree.expandPath(trPth);
                    buildContentPanel();

                }
            }
            //made by Jana Seemann
            //opens JDialog to specify settings for sorting
            if (cmd.equals("sort")) {
                //System.out.println("Sortieren clicked");

                sortjd = new JDialog();
                sortjd.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));

                sortjd.setSize(400, 400);
                sortjd.setLocation(500, 250);
                sortjd.setModal(true);
                sortjd.add(new JLabel("<html><body> Nach welchen Kriterien wollen Sie sortieren?                       <br>  </body></html>"));

                //creates and adds radioButtons to the JDialog to specify if sorted by date/time or location
                JRadioButton radioButtonByDate = new JRadioButton("nach Datum / Zeitabstand sortieren");
                JRadioButton radioButtonByLocation = new JRadioButton("nach Ort sortieren");

                radioButtonByDate.setEnabled(true);
                radioButtonByDate.setSelected(true);
                radioButtonByLocation.setEnabled(false);

                //creates a buttonGroup
                ButtonGroup sortBybg = new ButtonGroup();
                sortBybg.add(radioButtonByDate);
                sortBybg.add(radioButtonByLocation);

                //adds radioButtons to JDIalog
                sortjd.add(radioButtonByDate);
                sortjd.add(radioButtonByLocation);

                //adds a textLabel and formatted Texfield (where only integers can be written in) to JDialog
                sortjd.add(new JLabel("Bitte Abstand (Stunden bzw. km) eingeben: "));

                hoursTextfield = new JFormattedTextField();
                hoursTextfield.setValue(6);
                hoursTextfield.setColumns(10);
                hoursTextfield.addPropertyChangeListener("hours", this);
                // hoursTextfield.setMinimumSize(new Dimension(100, 15));
                sortjd.add(hoursTextfield);

                //Button to start sorting
                JButton sortByDate = new JButton("jetzt sortieren!");
                sortByDate.setActionCommand("sortNow");
                sortByDate.addActionListener(this);
                sortjd.add(sortByDate);


                sortjd.setVisible(true);

                //plus textfeld wie viele hoursTextfield dazwischen liegen sollen , plus button Sortieren starten
            }

            //button in JDialog
            //if pressed, optionPane opens to check if the right folder and distance between taken fotos are specified.
            // then calls method to sort
            if (cmd.equals("sortNow")) {

                //disposes the JOptionPane where user has specified criteria to sort
                sortjd.dispose();
                hoursBetweenFotos = ((Number) hoursTextfield.getValue()).intValue();

                //opens JOptionPane to ask the user if he is sure he wants to sort by given values
                new JOptionPane();
                int n = JOptionPane.showConfirmDialog(this, "Alle Fotos aus dem Ordner \n"
                                + selected.getAbsolutePath() + "\ndie mehr als " + hoursBetweenFotos + " Std / km voneinander entfernt aufgenommen wurden\n" +
                                "werden nun in separate Unterordner verschoben. \n\n\n" +
                                "Sind Sie sicher, dass Sie das tun wollen?",
                        "Sind Sie sich sicher? ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (n == JOptionPane.YES_OPTION) {

                    sortImagesByDateTime(selected, hoursBetweenFotos);
                    //buildContentPanel();
                    //buildFileTree();
                }

            }

            if (cmd.equals("undo")) {
                SortImages.undoChanges();
                buildContentPanel();
                buildFileTree();
            }
        }
        //labels in contentpanel
        if (obj instanceof JList) {
            if (cmd.equals("folder")) {
              System.out.println("klicked on folder");
            }
            else if (cmd.equals("picture")) {
               System.out.println("klicked on picture");
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();

        if (source == hoursTextfield) {
            hoursBetweenFotos = ((Number) hoursTextfield.getValue()).intValue();
           }
    }


    private static class MyFileFilter extends FileFilter {
        public boolean accept(File file) {
            return file.isDirectory();
        }

        public String getDescription() {
            return "directory";
        }
    }

    /**
     * Creates a thumbnail from a RAW image file (CR2, NEF, ARW, etc.)
     * @param rawFile The RAW image file
     * @return Thumbnail as an Image, or default file icon if creation fails
     */
    private Image createRawThumbnail(File rawFile) {
        // Check if the file exists and is readable
        if (rawFile == null || !rawFile.exists() || !rawFile.canRead()) {
            return getDefaultFileIcon();
        }

        // Try to use ImageIO first as it's more reliable for basic formats
        try {
            BufferedImage image = ImageIO.read(rawFile);
            if (image != null) {
                return scaleImage(image);
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "ImageIO failed to read " + rawFile.getName(), e);
        }

        // If ImageIO fails, try with Apache Commons Imaging if available
        try {
            // Check if Imaging class is available
            Class.forName("org.apache.commons.imaging.Imaging");
            
            // Try to read the image directly
            try {
                BufferedImage image = Imaging.getBufferedImage(rawFile);
                if (image != null) {
                    return scaleImage(image);
                }
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Direct Imaging read failed for " + rawFile.getName(), e);
            }

            // Try to extract thumbnail from metadata
            try {
                ImageMetadata metadata = Imaging.getMetadata(rawFile);
                if (metadata instanceof TiffImageMetadata) {
                    TiffImageMetadata tiffMetadata = (TiffImageMetadata) metadata;
                    
                    // Try to get the thumbnail from the EXIF directory
                    TiffDirectory thumbnailDir = tiffMetadata.findDirectory(TiffDirectoryType.EXIF_DIRECTORY_SUB_IFD.directoryType);
                    if (thumbnailDir != null) {
                        Object offsetObj = thumbnailDir.getFieldValue(TiffTagConstants.TIFF_TAG_JPEG_INTERCHANGE_FORMAT);
                        if (offsetObj != null) {
                            int offset = ((Number) offsetObj).intValue();
                            Object lengthObj = thumbnailDir.getFieldValue(TiffTagConstants.TIFF_TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
                            if (lengthObj != null) {
                                int length = ((Number) lengthObj).intValue();
                                try (RandomAccessFile raf = new RandomAccessFile(rawFile, "r")) {
                                    byte[] thumbnailData = new byte[length];
                                    raf.seek(offset);
                                    int bytesRead = raf.read(thumbnailData);
                                    if (bytesRead > 0) {
                                        return ImageIO.read(new ByteArrayInputStream(thumbnailData));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Failed to extract thumbnail from metadata: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.INFO, "Apache Commons Imaging not found. Limited RAW file support available.");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error initializing image processing: " + e.getMessage(), e);
        }

        // Return default icon if all else fails
        return getDefaultFileIcon();
    }

    /**
     * Scales an image to thumbnail size
     */
    private Image scaleImage(BufferedImage image) {
        int thumbWidth = 100;
        int thumbHeight = (int) (image.getHeight() * ((double) thumbWidth / image.getWidth()));
        return image.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_SMOOTH);
    }

    /**
     * Returns the default file icon
     */
    private Image getDefaultFileIcon() {
        try {
            Image icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("file.png")));
            return icon != null ? icon : new BufferedImage(100, 75, BufferedImage.TYPE_INT_ARGB);
        } catch (Exception e) {
            return new BufferedImage(100, 75, BufferedImage.TYPE_INT_ARGB);
        }
    }
}

