//package Gui;

import ImageViewer.ImageViewer;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.*;
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
    private Container c = getContentPane();

    //opens the windows standard directory for pictures on startup
    private File sourceDir;

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

    private Dimension screenSize;

    Frame() {

        super("Foto Administration Tool");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        screenSize = getToolkit().getScreenSize();
        setMaximumSize(screenSize);
        setBounds(0, 0, screenSize.width, screenSize.height);

        c.setLayout(new BorderLayout());

        sourceDir = new File(System.getProperty("user.home")+"/Bilder");


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
        listModel.removeAllElements();


        listContent = new JList<>(listModel);

        listContent.removeAll();
        content.clear();
        thumbnails.clear();


        //if selected folder contains files or folders, content will be added to content ArrayList<File>
        if (selected == null){
            selected = sourceDir;
        }
        System.out.println("(buildCont) selected file: " + selected);

        //atr != null ? atr.creationTime().toMillis() : 0
        //(selected.listFiles() != null ? selected.listFiles().length : 0)
        if (0 < selected.listFiles().length) {

            //creates a File and puts it  in the array of content-files
            //listModel.addElement(file);
            content.addAll(Arrays.asList(selected.listFiles()));
            MyThread t1 = new MyThread();
            t1.run(content);
            t1.start();
            //while (t1.isAlive()){}
        }

        //add directory content to JList
        listContent.setListData(content.toArray());

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


                setText(content.get(index).getName());
                setIcon((new ImageIcon(thumbnails.get(index))));
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(Color.DARK_GRAY);
                    setForeground(list.getForeground());
                }
                setEnabled(list.isEnabled());
                setFont(list.getFont());
                setOpaque(true);
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
                //System.out.println("Double clicked on Item " + index);
                File clicked = new File(listContent.getModel().getElementAt(index).toString());
                if(clicked.isFile()) {
                    new ImageViewer(clicked);// .getAccessibleContext().get
                    System.out.println("item: " + listContent.getModel().getElementAt(index));
                }
                else{
                    fileTree.tree.clearSelection();
                    fileTree.setSelectedTreeNode(clicked.getAbsolutePath());
                }
            }
            }
        };
        //adds the mouseListener to the JList
        listContent.addMouseListener(mouseListener);
        listContent.updateUI();



        //create JScrollPane and add contentPanel
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
        pack();
        c.add(contentPanel, BorderLayout.CENTER);
    }


    // creates a thread to run progress in background
    public class MyThread
            extends Thread {

        /**
         * creates MultiThreads to create thumbnails.
         * To prevent out of memory exceptions, let the threads sleep for 300/600 millisek.
         */
        MyThread() {
            super();
        }

        // die run Methode aus Thread
        // überschreiben

        /**
         * @param fileArrayList ArrayList<File> with contents of the folder
         */
        void run(ArrayList<File> fileArrayList) {


            // Prepare to execute and store the Futures
            int threadNum = fileArrayList.size();
            ExecutorService executor = Executors.newFixedThreadPool(threadNum);

            //arrayList for created values in future task
            ArrayList<FutureTask<Integer>> taskList = new ArrayList<>();
            taskList.clear();

            //to prevent out of memory exception, let the threads sleep for 300/600ms
            for (int t = 0; t < threadNum; t++) {

                int finalT = t;

                if ((finalT % 2) == 0) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                // Start thread for the first half of the numbers
                FutureTask<Integer> futureTask_1 = new FutureTask<>(new Callable<Integer>() {
                    @Override
                    public Integer call() {

                        // for (int i = 0; i < content.size(); i++) {
                        //if file is directory, an image of a directory will be pushed in the array of thumbnails on the same position as the file is
                        if (fileArrayList.get(finalT).isDirectory()) {
                            try {
                                thumbnails.add(ImageIO.read(getClass().getResource("folder.png")));//new ImageIcon(getClass().getResource("folder.png")));
                                System.out.println("added folder thumb");

                            } catch (IOException e) {
                                e.printStackTrace();
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
                                    System.out.println("added photo thumb");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else if (fileArrayList.get(finalT).getName().toLowerCase().endsWith("mp4")) {
                                try {
                                    thumbnails.add(ImageIO.read(getClass().getResource("video.png")));
                                    System.out.println("added video thumb");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (fileArrayList.get(finalT).getName().toLowerCase().endsWith("mpeg4")) {
                                try {
                                    thumbnails.add(ImageIO.read(getClass().getResource("video.png")));
                                    System.out.println("added video thumb");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Icon sysIco = FileSystemView.getFileSystemView().getSystemIcon(fileArrayList.get(finalT));
                                thumbnails.add(((ImageIcon) sysIco).getImage());
                                System.out.println("added sysIcon thumb");

                            }
                        }
                        return 0;
                    }
                });
                taskList.add(futureTask_1);
                executor.execute(futureTask_1);
            }

            // Wait until all results are available and combine them at the same time
            int amount = 0;
            for (int j = 0; j < threadNum; j++) {
                FutureTask<Integer> futureTask = taskList.get(j);
                try {
                    amount += futureTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
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
        JButton chooseFolder = new JButton(new ImageIcon(getClass().getResource("folder.png")));
        JButton sortButton = new JButton(new ImageIcon(getClass().getResource("sort-s.png")));
        JButton undoChanges = new JButton(new ImageIcon(getClass().getResource("undo.png")));

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
        private JTree tree;
        private DefaultTreeCellRenderer renderer;

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

            renderer = new DefaultTreeCellRenderer();
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
                if (selected != new File(node.toString())) {
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
            tree.setSelectionPath(tPath);
            tree.expandPath(tPath);
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
            e.printStackTrace();
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
                System.out.println("chooseFolder clicked");
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
            if (cmd.equals("picture")) {
                System.out.println("klicked on picture");
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();
        System.out.println("called PropertyChangeListener.");

        if (source == hoursTextfield) {
            hoursBetweenFotos = ((Number) hoursTextfield.getValue()).intValue();
            System.out.println("Amount of hours changed to: " + hoursBetweenFotos);
        }
    }


    private class MyFileFilter extends FileFilter {
        public boolean accept(File file) {
            return file.isDirectory();
        }

        public String getDescription() {
            return "directory";
        }

    }

}

