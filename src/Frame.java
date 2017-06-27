//package Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.*;


/**
 * Created by Chrissi on 31.05.2017.
 * jajaj ich schreib ja schon was
 */
public class Frame extends JFrame implements ActionListener {
    private Container c = getContentPane();

    /***************************************************************************

     bei SourcheDir bitte die eigene Adresse eingeben!!!
     Dann kommt auch der entsprechende Ordner gleich beim start...


     *****************************************************************************/

    private File sourceDir = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Pictures");
    String username = System.getProperty("user.name");

    //System.out.println(username);


    //private ArrayList<JLabel> listed = new ArrayList<>();
    // private JLabel[] labels; // = new JLabel[listed.size()];

    private JScrollPane treeScrollPane;
    //content panel
    private JPanel cPanel;
    private JScrollPane contentPanel;
    private JList listContent;
    private ArrayList<ImageIcon> thumbnails;
    private ArrayList<File> content;
    private ListModel listModel;

    private File selected = null;
    private FileTree fileTree;


    public Frame() {

        super("Foto Administration Tool");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension screenSize = getToolkit().getScreenSize();
        setSize(screenSize.getSize());
        setBounds(0, 0, screenSize.width, screenSize.height);

//        c.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());

        c.setLayout(new BorderLayout());


        buildToolbar();
        buildFileTree();
        //if (fileTree.getSelected() != null)
        buildContentViewer(null);
        //buildProgressBar();


        pack();
        setVisible(true);
    }

    private void buildProgressBar() {
        JPanel progressPanel = new JPanel();

        c.add(progressPanel, BorderLayout.SOUTH);

    }
    //end of constructor

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
    private void buildContentViewer(File selected) {
        //ArrayList<JButton> labels = new ArrayList<>();
        //cPanel = new JPanel();
        //cPanel.setLayout(new FlowLayout());


        //returnms the last selected item of the fileTree
        if (selected != null) buildContent(selected);


        listContent = new JList<>();
        //listContent.setListData();
        listModel = new DefaultListModel();

        //the call to setLayoutOrientation, invoking setVisibleRowCount(-1)
        // makes the list display the maximum number of items possible in the available space
        listContent.setVisibleRowCount(-1);
        ListSelectionModel listSelectionModel = listContent.getSelectionModel();
        //listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());


        //setup for selection mode: Multiple Intervall means you can select one or more items
        // no matter if there is one non-selected between them
        listContent.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listContent.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        // listContent.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        // listContent.addListSelectionListener((ListSelectionListener) this);

        //the call to setLayoutOrientation, invoking setVisibleRowCount(-1)
        // makes the list display the maximum number of items possible in the available space
        //listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());

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
                setIcon(thumbnails.get(index));
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
        listContent.setBackground(Color.DARK_GRAY);
        listContent.setForeground(Color.WHITE);
        //listContent.setCellRenderer(new MyCellRenderer());


        contentPanel = new JScrollPane(listContent);
        //---- change appearance of contentPanel
        contentPanel.setPreferredSize(new Dimension(900, 800));

        contentPanel.setBackground(Color.DARK_GRAY);
        contentPanel.setForeground(Color.WHITE);
        contentPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        pack();
        contentPanel.updateUI();
        //contentPanel.validate();
        //contentPanel.repaint();

        c.add(contentPanel, BorderLayout.CENTER);
    }


    // class Content
    private void buildContent(File selected) {
        listContent.removeAll();
        listContent.setBackground(Color.DARK_GRAY);
        listContent.setForeground(Color.WHITE);
        // cPanel.removeAll();
        content = new ArrayList<>(); //new File[(int)selected.length()];
        thumbnails = new ArrayList<>();//new ImageIcon[(int)selected.length()];


        for (File file : selected.listFiles()) {
            //creates a File and puts it  in the array of content-files
            content.add(new File(file.getAbsolutePath()));

            //if file is directory, an image of a directory will be pushed in the array of thumbnails on the same position as the file is
            if (file.isDirectory()) {
                thumbnails.add(new ImageIcon(getClass().getResource("folder.png")));
            }
            //if file is an image (jpg) it creates a Foto, gets the thumbnail of it an pushes it into the array of thumbnails
            else if (file.isFile() && (file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("jpeg"))) {
                Foto foto = new Foto(file.getAbsolutePath());
                thumbnails.add(foto.getThumbnail());
            } else {
                thumbnails.add(new ImageIcon(getClass().getResource("picture.png")));
            }
        }


        // listContent = new JList<>();
        listContent.setListData(content.toArray());
        listContent.updateUI();
        //listContent.setListData();


        contentPanel.add(listContent);
        pack();
        contentPanel.validate();
        contentPanel.repaint();
        contentPanel.updateUI();

    }









    /**
     * Builds a toolbar that is not movable. if you want it movable, use setFloatable(true)
     * adds the toolbar to the JFrame on top of the frame
     * creates a button to set up the location of the folder you want to sort.
     */
    private void buildToolbar() {
       // JToolBar bar = new JToolBar();


        //---


        // Erstellung einer Menüleiste
        JMenuBar menu = new JMenuBar();
        // Menü wird hinzugefügt
        menu.add(new JMenu("Datei"));





        //Toolbar wird erstellt
        JToolBar bar = new JToolBar();
        //Größe der Toolbar wird gesetzt
        bar.setSize(230, 20);

        bar.setBackground(Color.DARK_GRAY);
        bar.setForeground(Color.WHITE);

        //----

        bar.setFloatable(false);
        c.add(bar, BorderLayout.NORTH);
        //new JButton, which image is located in the src-folder

        //---
        JButton chooseFolder = new JButton(new ImageIcon(getClass().getResource("folder.png")));
        JButton bearbeiten = new JButton("Bearbeiten");
        JButton hilfe = new JButton("Hilfe");

        chooseFolder.setBackground(Color.DARK_GRAY);
        chooseFolder.setForeground(Color.WHITE);

        bearbeiten.setBackground(Color.DARK_GRAY);
        bearbeiten.setForeground(Color.WHITE);

        hilfe.setBackground(Color.DARK_GRAY);
        hilfe.setForeground(Color.WHITE);



        chooseFolder.setToolTipText("set the location of the folder you want to sort");

        //if you use setActionCommand you don't have to write one actionListener for each actionEvent.
        chooseFolder.setActionCommand("chooseFolder");
        bearbeiten.setActionCommand("Bearbeiten");
        hilfe.setActionCommand("Hilfe");

        chooseFolder.addActionListener(this);
        bar.add(chooseFolder);

        bearbeiten.addActionListener(this);
        bar.add(bearbeiten);

        hilfe.addActionListener(this);
        bar.add(hilfe);
    }
    //----

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String cmd = e.getActionCommand();
        /*
        folder-Button:
         opens filechooser to choose directory to sort
         save chosen directory in sourceDir
         */
        if (obj instanceof JButton) {
            //toolbar
            //Button to choose the folder which should be sorted
            if (cmd.equals("chooseFolder")) {
                System.out.println("chooseFolder clicked");
                //opens the pop up window to search through the local stored folders.
                JFileChooser fc = new JFileChooser();
                //first directory shown when the file chooser window is opened
                fc.setCurrentDirectory(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Pictures"));
                //implements a new FileFilter (function below)
                fc.setFileFilter(new MyFileFilter());
                //directories_only important because else you would choose files instead of dirs
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    //if a valid choice is made, the selected folder of the filechooser will be saved in sourceDir
                    selected = fc.getSelectedFile();
                    // sourcheDirSet = true;
                    //show the new content in the treeScrollPane

                    fileTree.setSelectedTreeNode(selected.getAbsolutePath());
                    buildContent(selected);
                }
            }
            //labels in contentpanel
            if (cmd.equals("folder")) {
                System.out.println("klicked on folder");
            }
            if (cmd.equals("picture")) {
                System.out.println("klicked on picture");
            }


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

    //Creates a JScrollPane, creates a new FileTree instance with given directory saved in sourceDir
    //adds the FileTree to the treeScrollPane on left side
    private void buildFileTree() {
        treeScrollPane = new JScrollPane();
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

        FileTree(File dir) {
            setLayout(new BorderLayout());


            // Make a tree list with all the nodes, and make it a JTree
            tree = new JTree(addNodes(null, dir));
            //  tree.addItemListener
            ToolTipManager.sharedInstance().registerComponent(tree);

            tree.setBackground(Color.DARK_GRAY);
            tree.setForeground(Color.WHITE);

            renderer = new DefaultTreeCellRenderer();
            renderer.setBackground(new Color(0, 0, 0, 0));
            renderer.setBackgroundNonSelectionColor(new Color(0, 0, 0, 0));
            // renderer.setToolTipText(tree.get);
            tree.setCellRenderer(renderer);

            // Add a listener
            tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                            .getPath().getLastPathComponent();
                    System.out.println("You selected " + node);
                    selected = new File(node.toString());
                    // cPanel.removeAll();
                    if (node.getChildCount() > 0) buildContent(selected);
                    //printAll(getGraphics());
                }
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

            if (curTop != null) { // should only be null at root
                curTop.add(curDir);
            }
            Vector<String> ol = new Vector<>();
            String[] tmp = dir.list();
            for (int i = 0; i < tmp.length; i++)
                ol.addElement(tmp[i]);
            Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
            File f;
            Vector<String> files = new Vector<String>();
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
                }
                else
                    files.addElement(thisObject);
            }
            // Pass two: for files.
            for (int fnum = 0; fnum < files.size(); fnum++)
                curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
            return curDir;
        }

        public Dimension getMinimumSize() {
            return new Dimension(300, 400);
        }

        public Dimension getPreferredSize() {
            return new Dimension(300, 800);
        }

        public void setSelectedTreeNode(String path) {
            // Search forward from first visible row looking for any visible node
            // whose name starts with prefix.
            int startRow = 0;
            String prefix = path;
            TreePath tPath = tree.getNextMatch(prefix, startRow, Position.Bias.Forward);
            tree.setSelectionPath(tPath);
            tree.expandPath(tPath);
        }



    }

}
