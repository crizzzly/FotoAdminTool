//package Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;


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

    private File selected = null;
    FileTree fileTree;


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
        buildProgressBar();


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
        cPanel = new JPanel();
        cPanel.setLayout(new FlowLayout());
        cPanel.setPreferredSize(new Dimension(900, 800));

        //returnms the last selected item of the fileTree
        if (selected != null) buildContent(selected);

        pack();
        cPanel.validate();
        cPanel.repaint();
        JScrollPane contentPanel = new JScrollPane(cPanel);
        contentPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        c.add(contentPanel, BorderLayout.CENTER);
    }

    private void buildContent(File selected) {
        //if(selected != null) {
        cPanel.removeAll();
        File[] content = selected.listFiles();
        System.out.println("number of files/folders in selected item: " + (content != null ? content.length : 0));
        assert content != null;
        for (File aContent : content) {
            System.out.println(aContent.getAbsolutePath());
            ImageIcon icon = null;
            Image img = null;
            JButton button = null;
            if (aContent.isDirectory()) {
                icon = new ImageIcon(getClass().getResource("folder.png"));
                button = new JButton(aContent.getName(), icon);

                button.setActionCommand("folder");
                button.addActionListener(this);
                //labels.add(button);

            }
            //use thumbnail instead:
            else {
                if (aContent.getName().toLowerCase().endsWith("jpg") || aContent.getName().toLowerCase().endsWith("jpeg")) {
                    img = Toolkit.getDefaultToolkit().getImage(aContent.getAbsolutePath());
                    ImageIcon imageIcon = new ImageIcon(aContent.getAbsolutePath());
                    //ImageIO.read(new File(aContent.getCanonicalPath()));//.getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH);
                    //BufferedImage bi =     ImageIO.read(new File(aContent.getCanonicalPath()));
                    Image scaledInstance = img.getScaledInstance((int) ((imageIcon.getIconWidth() * 1.5) / 100) + 1, (int) ((imageIcon.getIconHeight() * 1.5) / 100) + 1, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaledInstance);

                } else {
                    icon = new ImageIcon(getClass().getResource("picture.png"));
                }
                button = new JButton(aContent.getName(), icon);
                button.setHorizontalTextPosition(AbstractButton.CENTER);
                button.setVerticalTextPosition(AbstractButton.BOTTOM);
                button.setActionCommand("picture");
                button.addActionListener(this);
                //labels.add(button);
            }

            //    labels[i] = new JLabel(content[i].getPath(), icon, LEFT);
            //  contentPanel.add(labels[i]);
            //JLabel label = new JLabel(aContent.getName(), icon, SwingConstants.CENTER);
            //  label.setHorizontalAlignment(SwingConstants.CENTER);
            cPanel.add(button);
            System.out.println("added imageicon in contentpanel");

        }
        pack();
        cPanel.validate();
        cPanel.repaint();
        // }
    }


    /**
     * Builds a toolbar that is not movable. if you want it movable, use setFloatable(true)
     * adds the toolbar to the JFrame on top of the frame
     * creates a button to set up the location of the folder you want to sort.
     */
    private void buildToolbar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        c.add(bar, BorderLayout.NORTH);
        //new JButton, which image is located in the src-folder
        JButton chooseFolder = new JButton(new ImageIcon(getClass().getResource("folder.png")));
        chooseFolder.setToolTipText("set the location of the folder you want to sort");
        //if you use setActionCommand you don't have to write one actionListener for each actionEvent.
        chooseFolder.setActionCommand("chooseFolder");
        chooseFolder.addActionListener(this);
        bar.add(chooseFolder);
    }

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
                fc.setCurrentDirectory(new File("C:\\Users\\USERNAME\\Pictures"));
                //implements a new FileFilter (function below)
                fc.setFileFilter(new MyFileFilter());
                //directories_only important because else you would choose files instead of dirs
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    //if a valid choice is made, the selected folder of the filechooser will be saved in sourceDir
                    selected = fc.getSelectedFile();
                    // sourcheDirSet = true;
                    //show the new content in the treeScrollPane

                    //fileTree.setSelectedTreeNode(selected.toString());
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

        FileTree(File dir) {
            setLayout(new BorderLayout());

            // Make a tree list with all the nodes, and make it a JTree
            tree = new JTree(addNodes(null, dir));
            //  tree.addItemListener

            //tree.addTreeExpansionListener(new );
            DefaultTreeCellRenderer treeRenderer;
            treeRenderer = new DefaultTreeCellRenderer();

            //treeRenderer.firePropertyChange("getText", );
            tree.setCellRenderer(new MyTreeCellRenderer());

            // Add a listener
            tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                            .getPath().getLastPathComponent();
                    System.out.println("You selected " + node);
                    selected = new File(node.toString());
                    cPanel.removeAll();
                    if (node.getChildCount() > 0) buildContent(selected);
                    //printAll(getGraphics());
                }
            });

            // Lastly, put the JTree into a JScrollPane.
            //JScrollPane scrollPane = new JScrollPane();
            //scrollPane.getViewport().add(tree);
            //add(BorderLayout.CENTER, scrollPane);
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
            Vector ol = new Vector();
            String[] tmp = dir.list();
            for (int i = 0; i < tmp.length; i++)
                ol.addElement(tmp[i]);
            Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
            File f;
            Vector files = new Vector();
            // Make two passes, one for Dirs and one for Files. This is #1.
            for (int i = 0; i < ol.size(); i++) {
                String thisObject = (String) ol.elementAt(i);
                String newPath;
                if (curPath.equals("."))
                    newPath = thisObject;
                else
                    newPath = curPath + File.separator + thisObject;
                if ((f = new File(newPath)).isDirectory())
                    addNodes(curDir, f);
                else
                    files.addElement(thisObject);
            }
            // Pass two: for files.
            for (int fnum = 0; fnum < files.size(); fnum++)
                curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
            return curDir;
        }

        public Dimension getMinimumSize() {
            return new Dimension(200, 400);
        }

        public Dimension getPreferredSize() {
            return new Dimension(200, 800);
        }

        public void setSelectedTreeNode(String path) {
            tree.setSelectionPath(new TreePath(path));
        }


        public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

            private FileSystemView fsv = FileSystemView.getFileSystemView();

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                //System.out.println(value);
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    Object userValue = node.getUserObject();

                /*value = ((DefaultMutableTreeNode) value).getUserObject();
                if (value instanceof File) {
                File file = (File) value;
                /*
                if (file.isDirectory()) {
                    setIcon(fsv.getSystemIcon(file));
                    setText(file.getName());
                } else {
                    setIcon(fsv.getSystemIcon(file));
                    setText(file.getName());
                }
                    setIcon(fsv.getSystemIcon(file));
                    setText(file.getName());
                    //setText(((File) value).getName());
                }*/

                }
                return this;
            }
        }
    }

}
