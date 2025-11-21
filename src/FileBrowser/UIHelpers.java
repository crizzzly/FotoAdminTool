package FileBrowser;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;


public class UIHelpers {
    public static Icon getSystemIcon(String iconName) {
        // Try to get system icon first
        Icon icon = UIManager.getIcon(iconName);
        if (icon == null) {
            // Fallback to common system icons
            return switch (iconName) {
                case "folder" ->
                        FileSystemView.getFileSystemView().getSystemIcon(new File(System.getProperty("user.home")));
                case "sort" -> UIManager.getIcon("Table.ascendingSortIcon");
                case "undo" -> UIManager.getIcon("OptionPane.errorIcon");
                default -> UIManager.getIcon("OptionPane.informationIcon");
            };
        }
        return icon;
    }

}

