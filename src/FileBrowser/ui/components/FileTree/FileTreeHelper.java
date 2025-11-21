package FileBrowser.ui.components.FileTree;

import FileBrowser.util.UIConstants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.Vector;

import static FileBrowser.ui.components.FileTree.FileTree.setMinimumHeight;
import static FileBrowser.ui.components.FileTree.FileTree.setMinimumWidth;

public class FileTreeHelper {

    /**
     * Add nodes from under "dir" into curTop. Highly recursive.
     */
    public static DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
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

    public static @NotNull DefaultTreeCellRenderer getDefaultTreeCellRenderer() {
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
        return renderer;
    }


}
