package FileBrowser;//package Gui;

import ImageViewer.ImageViewer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Position;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static FileBrowser.UIHelpers.getSystemIcon;

//import java.util.List;
/**
 * Created by Chrissi on 31.05.2017.
 * jajaj ich schreib ja schon was
 */
public class MainFrame extends JFrame implements ActionListener, PropertyChangeListener {
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
//    private int loadCount;
    private FileTree fileTree;
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());

    private final ThumbnailLoader thumbnailLoader;
//    private final ExecutorService thumbnailExecutor = Executors.newCachedThreadPool();

    public MainFrame() {
        super("FileBrowser.Foto Administration Tool");
        // Initialize thumbnail loader
        this.thumbnailLoader = new ThumbnailLoader(UIConstants.THUMBNAIL_WIDTH);


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension screenSize = getToolkit().getScreenSize();
        setMaximumSize(screenSize);
        setBounds(0, 0, screenSize.width, screenSize.height);

        c.setLayout(new BorderLayout());

        // TODO: change this to a user-selected directory
        sourceDir = new File(System.getProperty("user.home")+"/Pictures");


        buildToolbar();
        buildFileTree();
        buildScrollPane();
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
    public void buildScrollPane() {
        content = new ArrayList<>();
        thumbnails = new ArrayList<>();

        //creates JList that holds thumbnails and names of files / directories
//        DefaultListModel<Object> listModel = new DefaultListModel<>();
        // Create and configure the list
        listContent = new JList<>();
        listContent.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        listContent.setVisibleRowCount(-1);
        listContent.setFixedCellWidth(UIConstants.THUMBNAIL_WIDTH + UIConstants.CELL_HORIZONTAL_GAP);
        listContent.setFixedCellHeight(UIConstants.THUMBNAIL_WIDTH + UIConstants.CELL_VERTICAL_GAP + UIConstants.FILENAME_LABEL_HEIGHT);
        listContent.setBackground(UIConstants.BACKGROUND_DARK);
        listContent.setForeground(UIConstants.TEXT_LIGHT);
        listContent.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listContent.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        listContent.setCellRenderer(new ThumbnailCellRenderer(UIConstants.THUMBNAIL_WIDTH, thumbnails));

        //if selected folder contains files or folders, content will be added to content ArrayList<File>
        if (selected == null){
            selected = sourceDir;
        }

        // Clear previous data
//        listContent.removeAll();
        content.clear();
        thumbnails.clear();


        // Load Files
        File[] files = selected.listFiles();
        if (files != null) {
            for (File file : files) {
                content.add(file);
                thumbnails.add(null); // Placeholder for the thumbnail
            }
        }

        // Set the list data
        listContent.setListData(content.toArray());

        // Load thumbnails in the background
        loadThumbnailsForContent();

        // Set up mouse listener
        listContent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = listContent.locationToIndex(e.getPoint());
                    if (index >= 0 && index < content.size()) {
                        File clicked = content.get(index);
                        if (clicked.isDirectory()) {
                            selected = clicked;
                            buildScrollPane();
                        } else {
                            new ImageViewer(clicked);
                        }
                    }
                }
            }
        });


        JScrollPane scrollPane = createContentScrollPane();
        c.add(scrollPane, BorderLayout.CENTER);
        pack();
    }

    private void buildMenuBar() {
        /**
         * builds the menuBar
         * made by Jana Seemann
         */
        // creates menuBar
        JMenuBar menu = new JMenuBar();
        // Menü wird hinzugefügt
        menu.add(new JMenu("Datei"));

        JButton bearbeiten = new JButton("Bearbeiten");
        JButton hilfe = new JButton("Hilfe");


        bearbeiten.setBackground(UIConstants.BACKGROUND_DARK);
        bearbeiten.setForeground(UIConstants.TEXT_LIGHT
);

        hilfe.setBackground(UIConstants.BACKGROUND_DARK);
        hilfe.setForeground(UIConstants.TEXT_LIGHT
);

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

        bar.setBackground(UIConstants.BACKGROUND_DARK);
        bar.setForeground(UIConstants.TEXT_LIGHT
);
        bar.setFloatable(false);
        c.add(bar, BorderLayout.NORTH);
        //new JButton, which image is located in the src-folder

        //---
        JButton chooseFolder = new JButton();
        chooseFolder.setIcon(getSystemIcon("folder"));
        chooseFolder.setToolTipText("Select Folder");

        JButton sortButton = new JButton();
        sortButton.setIcon(getSystemIcon("sort"));
        sortButton.setToolTipText("Sort Items");

        JButton undoChanges = new JButton();
        undoChanges.setIcon(getSystemIcon("undo"));
        undoChanges.setToolTipText("Undo Changes");
        chooseFolder.setBackground(UIConstants.BACKGROUND_DARK);
        chooseFolder.setForeground(UIConstants.TEXT_LIGHT
);

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

    //Creates a JScrollPane, creates a new FileBrowser.FileTree instance with given directory saved in sourceDir
    //adds the FileBrowser.FileTree to the treeScrollPane on left side
    private void buildFileTree() {
        JScrollPane treeScrollPane = new JScrollPane();
        fileTree = new FileTree(sourceDir, this);
        treeScrollPane.getViewport().add(fileTree);
        c.add(treeScrollPane, BorderLayout.WEST);
    }

    /**
     * calls the sort() function of FileBrowser.SortImages class. uses swingWorker to do it in background.
     * When swingWorker is done with its work, scrollPane and fileTree will be rebuilt
     *
     * @param pathToSort        File path to the directory which will be sorted
     * @param hoursBetweenFotos int time (hours) or distance(km) to specify when FileBrowser.Foto will bnne dropped into a new folder
     */
    private void sortImagesByDateTime(File pathToSort, int hoursBetweenFotos) {
        try {
            SortImages.sort(hoursBetweenFotos, pathToSort.toPath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error message", e);
        }

        selected = pathToSort;
        buildScrollPane();
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
                //opens the pop-up window to search through the local stored folders.
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
                    buildScrollPane();

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
                    //buildscrollPane();
                    //buildFileTree();
                }

            }

            if (cmd.equals("undo")) {
                SortImages.undoChanges();
                buildScrollPane();
                buildFileTree();
            }
        }
        //labels in scrollPane
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
    private void loadThumbnailsForContent() {
        if (content == null || content.isEmpty()) return;

        for (int i = 0; i < content.size(); i++) {
            final int index = i;
            final File file = content.get(i);

            thumbnailLoader.loadThumbnail(file, new ThumbnailLoader.ThumbnailLoadListener() {
                @Override
                public void onThumbnailLoaded(File file, Image thumbnail) {
                    SwingUtilities.invokeLater(() -> {
                        if (index < thumbnails.size()) {
                            thumbnails.set(index, thumbnail);
                            listContent.repaint(listContent.getCellBounds(index, index));
                        }
                    });
                }

                @Override
                public void onThumbnailError(File file, Exception e) {
                    LOGGER.log(Level.WARNING, "Error loading thumbnail for: " + file.getAbsolutePath(), e);
                }
            });
        }
    }

    private JScrollPane createContentScrollPane() {
        JScrollPane scrollPane = new JScrollPane(listContent);
        scrollPane.setLayout(new ScrollPaneLayout());
        //---- change appearance of scrollPane
        scrollPane.setPreferredSize(new Dimension(900, 800));
        scrollPane.setBackground(UIConstants.BACKGROUND_DARK);
        scrollPane.setForeground(UIConstants.TEXT_LIGHT);
        scrollPane.setWheelScrollingEnabled(true);

        //sets the scrollbar. horizontal is turned off, vertical is shown when needed
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setAutoscrolls(true);
        scrollPane.getViewport().updateUI();

        return scrollPane;
    }
}

