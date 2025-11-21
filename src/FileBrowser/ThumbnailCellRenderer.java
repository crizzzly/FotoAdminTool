package FileBrowser;


import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ThumbnailCellRenderer extends JPanel implements ListCellRenderer<Object> {
    private final JLabel iconLabel = new JLabel("", JLabel.CENTER);
    private final JLabel textLabel = new JLabel("", JLabel.CENTER);
    private final int thumbWidth;
    private final Color selectionColor = new Color(7, 110, 246);
    private final Color defaultColor = Color.DARK_GRAY;
    private final ArrayList<Image> thumbnails;


    public ThumbnailCellRenderer(int thumbWidth, ArrayList<Image> thumbnails) {
        this.thumbWidth = thumbWidth;
        this.thumbnails = thumbnails;
        setLayout(new BorderLayout(0, 5)); // 5px vertical gap between icon and text
        setOpaque(true);
        setBackground(UIConstants.PANEL_BACKGROUND);

        // Configure icon label
        iconLabel.setOpaque(false);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);

        // Configure text label
        textLabel.setOpaque(false);
        textLabel.setForeground(UIConstants.TEXT_LIGHT);
        textLabel.setFont(getFont().deriveFont(Font.PLAIN, 12));

        // Add components to panel
        add(iconLabel, BorderLayout.CENTER);
        add(textLabel, BorderLayout.SOUTH);

        // Add padding around the cell
        setBorder(BorderFactory.createEmptyBorder(
                UIConstants.CELL_VERTICAL_PADDING,
                UIConstants.CELL_HORIZONTAL_PADDING,
                UIConstants.CELL_VERTICAL_PADDING,
                UIConstants.CELL_HORIZONTAL_PADDING
        ));
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
    int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof File file) {
            textLabel.setText(file.getName());

            // Set icon - use existing thumbnail or default icon
            ImageIcon icon = null;
            if (index >= 0 && index < thumbnails.size()) {
                Image img = thumbnails.get(index);
                if (img != null) {
                    // Scale image while maintaining aspect ratio
                    int width = thumbWidth;
                    int height = (int) (img.getHeight(null) * ((double) width / img.getWidth(null)));
                    icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
                }
            }

            if (icon == null) {
                // Use default folder or file icon from FileOperations utility
                Image iconImage = FileOperations.getDefaultSystemIcon(file.isDirectory());
                // Scale the default icon to match thumbnail size
                int width = Math.min(thumbWidth, iconImage.getWidth(null));
                int height = (int) (iconImage.getHeight(null) * ((double) width / iconImage.getWidth(null)));
                icon = new ImageIcon(iconImage.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            }
            iconLabel.setIcon(icon);
        }

        // Set background and foreground based on selection
        if (isSelected) {
            setBackground(UIConstants.SELECTION_COLOR);
            textLabel.setForeground(UIConstants.TEXT_LIGHT);
        } else {
            setBackground(UIConstants.PANEL_BACKGROUND);
            textLabel.setForeground(UIConstants.TEXT_MUTED);
        }
        return this;
    }
}