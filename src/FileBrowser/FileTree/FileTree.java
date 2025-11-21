package FileBrowser.FileTree;

import FileBrowser.MainFrame;
import FileBrowser.UIConstants;
import static FileBrowser.FileTree.FileTreeHelper.getDefaultTreeCellRenderer;

import javax.swing.*;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.Objects;

import java.util.logging.Logger;


/** Display a file system in a JTree view
 * @author Ian Darwin
 * @version $Id: FileBrowser.FileTree.FileTree.java,v 1.9 2004/02/23 03:39:22 ian Exp $
 **/
public class FileTree extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(FileTree.class.getName());

    //final Object[] selectedFile = new File[1];
    final JTree tree;
    private File selected;
    private static int minHeight;
    private static int minWidth;
    private final Dimension screenSize;

    public FileTree(File dir, MainFrame mainFrame) {
        // Reference to the parent FileBrowser.MainFrame
        this.screenSize = getToolkit().getScreenSize();
        setLayout(new BorderLayout());

        // Make a tree list with all the nodes, and make it a JTree
        tree = new JTree(FileTreeHelper.addNodes(null, dir));
        //  tree.addItemListener
        ToolTipManager.sharedInstance().registerComponent(tree);

        // Set tree properties
        tree.setBackground(UIConstants.PANEL_BACKGROUND);
        tree.setForeground(UIConstants.TEXT_LIGHT);
        tree.setOpaque(true);
        
        // Configure tree UI properties for dark mode
        UIManager.put("Tree.background", UIConstants.PANEL_BACKGROUND);
        UIManager.put("Tree.textBackground", UIConstants.PANEL_BACKGROUND);
        UIManager.put("Tree.textForeground", UIConstants.TEXT_LIGHT);
        UIManager.put("Tree.selectionBackground", UIConstants.SELECTION_COLOR);
        UIManager.put("Tree.selectionForeground", Color.WHITE);
        
        // Create and configure the tree cell renderer
        DefaultTreeCellRenderer renderer =  getDefaultTreeCellRenderer();
        // renderer.setToolTipText(tree.get);
        tree.setCellRenderer(renderer);

        // Add a listener
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                    .getPath().getLastPathComponent();
            System.out.println("Filetree: You selected " + node);
            if (!Objects.equals(selected, new File(node.toString()))) {
                selected = new File(node.toString());
                if (node.getChildCount() > 0) {
                    try {
                        mainFrame.buildScrollPane();
                    } catch (Exception ex) {
                        LOGGER.severe("buildscrollPane didn't successfully complete\n" + ex.getMessage());
                    }
                }
            }
        });
        // Lastly, put the JTree into a JScrollPane.
        add(tree);
    }





    public static void setMinimumHeight(int height){
        minHeight = height;
    }

    public static void setMinimumWidth(int width){
        minWidth = width;
    }
    public Dimension getMinimumSize() {

        return new Dimension(minWidth, minHeight);
    }

    public Dimension getPreferredSize() {
        return new Dimension(350, screenSize.height);
    }

    public void clearSelection(){
        tree.clearSelection();
    }

    public void collapsePath(TreePath path){
        tree.collapsePath(path);
    }

    public void expandPath(TreePath path){
        tree.expandPath(path);
    }

    public TreePath getNextMatch(String prefix, int startingRow, Position.Bias bias){
        return tree.getNextMatch(prefix, startingRow, bias);
    }

    public void setSelectionPath(TreePath path){
        tree.setSelectionPath(path);
    }

    public void setSelectedTreeNode(String path) {
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
