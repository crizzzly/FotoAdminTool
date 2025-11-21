package FileBrowser.ui.components;
import FileBrowser.util.UIConstants;
import org.apache.commons.imaging.formats.xbm.AbstractXbmTest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Utility class for creating and managing UI components with consistent theming.
 */
public class UIComponents {
    
    /**
     * Creates a styled toolbar button with consistent theming
     * @param iconName Name of the icon to use (must be in the resources folder)
     * @param tooltip Tooltip text to show on hover
     * @return A styled JButton
     */
    public static JButton createToolbarButton(String iconName, String tooltip) {
        JButton button = new JButton();
        button.setName(iconName);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(UIConstants.PANEL_BACKGROUND);
        button.setForeground(UIConstants.TEXT_LIGHT);
        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // Make the button react to ActionEvents
//        button.addActionListener(e -> {
//            if (e.getSource() instanceof JButton) {
//                // This will be handled by the MainFrame's actionPerformed method
//                ActionEvent newEvent = new ActionEvent(
//                    e.getSource(),
//                    e.getID(),
//                    ((JButton)e.getSource()).getText()
//                );
//                ActionListener[] listeners = button.getActionListeners();
//                for (ActionListener listener : listeners) {
//                    listener.actionPerformed(newEvent);
//                }
//            }
//        });
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.SELECTION_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.PANEL_BACKGROUND);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.SELECTION_COLOR.darker());
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.SELECTION_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Shows a folder selection dialog
     * @param parent The parent component for the dialog
     * @param title The title of the dialog
     * @param currentDirectory The initial directory to show
     * @return The selected directory, or null if cancelled
     */
    public static File selectFolder(Component parent, String title, File currentDirectory) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(title);
        if (currentDirectory != null) {
            fileChooser.setCurrentDirectory(currentDirectory);
        }
        
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    
    /**
     * Creates a toolbar with consistent theming
     * @return A configured JToolBar
     */
    @org.jetbrains.annotations.NotNull
    public static JToolBar createToolbar(ActionListener listener) {
        JToolBar toolbar = new JToolBar();

        // Configure the toolbar's appearance
        toolbar.setFloatable(false);
        toolbar.setOpaque(true);
        toolbar.setBackground(UIConstants.PANEL_BACKGROUND);
        toolbar.setForeground(UIConstants.TEXT_LIGHT);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.SELECTION_COLOR),
            new EmptyBorder(5, 5, 5, 5)
        ));

        // Set a preferred size to ensure visibility
        toolbar.setPreferredSize(new Dimension(toolbar.getWidth(), 50));

        // Create a panel to hold the buttons (this helps with theming)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setOpaque(false);
        
        // Create and add buttons
        JButton chooseFolder = createToolbarButton("folder", UIConstants.Buttons.Tooltips.CHOOSE_FOLDER);
        chooseFolder.setActionCommand(UIConstants.Buttons.CHOOSE_FOLDER);
        chooseFolder.setText(UIConstants.Buttons.CHOOSE_FOLDER);
        chooseFolder.addActionListener(listener);
        
        JButton sortButton = createToolbarButton("sort", UIConstants.Buttons.Tooltips.SORT_FILES);
        sortButton.setActionCommand(UIConstants.Buttons.SORT_FILES);
        sortButton.setText(UIConstants.Buttons.SORT_FILES);

        JButton undoButton = createToolbarButton("undo", UIConstants.Buttons.Tooltips.UNDO_CHANGES);
        undoButton.setActionCommand(UIConstants.Buttons.UNDO_CHANGES);
        undoButton.setText(UIConstants.Buttons.UNDO_CHANGES);
        
        // Add buttons to the panel
        buttonPanel.add(chooseFolder);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(sortButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(undoButton);
        
        // Add the panel to the toolbar
        toolbar.add(buttonPanel);
        
        // Add some spacing at the end
        toolbar.add(Box.createHorizontalGlue());
        
        return toolbar;
    }
}
