package FileBrowser;

import javax.swing.*;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.Vector;

public class FileTree extends JPanel {
      /** Display a file system in a JTree view
     *
             * @author Ian Darwin
     * @version $Id: FileBrowser.FileTree.java,v 1.9 2004/02/23 03:39:22 ian Exp $
     **/
    //final Object[] selectedFile = new File[1];
    final JTree tree;
    private File selected;
    private int minHeight;
    private int minWidth;
    private Dimension screenSize;

    FileTree(File dir, MainFrame mainFrame) {
        // Reference to the parent FileBrowser.MainFrame
        this.screenSize = getToolkit().getScreenSize();
        setLayout(new BorderLayout());

        // Make a tree list with all the nodes, and make it a JTree
        tree = new JTree(addNodes(null, dir));
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
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                        boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, sel, leaf, row, hasFocus);
                
                // Set background and foreground based on selection
                if (sel) {
                    setBackground(UIConstants.SELECTION_COLOR);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(UIConstants.PANEL_BACKGROUND);
                    setForeground(UIConstants.TEXT_LIGHT);
                }
                
                // Set icon based on node type
                if (leaf) {
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                } else {
                    setIcon(expanded ? UIManager.getIcon("Tree.openIcon") : UIManager.getIcon("Tree.closedIcon"));
                }
                
                return this;
            }
        };
        
        // Configure renderer colors
        renderer.setBackgroundNonSelectionColor(UIConstants.PANEL_BACKGROUND);
        renderer.setTextNonSelectionColor(UIConstants.TEXT_LIGHT);
        renderer.setBackgroundSelectionColor(UIConstants.SELECTION_COLOR);
        renderer.setTextSelectionColor(Color.WHITE);
        renderer.setOpaque(true);
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
                        System.err.println("buildscrollPane didn't successfully complete");
                        ex.printStackTrace();
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
