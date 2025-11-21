package FileBrowser.ui.components;

import FileBrowser.util.UIConstants;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MenuBar {
    private void buildMenuBar(ActionListener listener) {
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


        bearbeiten.setBackground(UIConstants.PANEL_BACKGROUND);
        bearbeiten.setForeground(UIConstants.TEXT_LIGHT
        );

        hilfe.setBackground(UIConstants.PANEL_BACKGROUND);
        hilfe.setForeground(UIConstants.TEXT_LIGHT
        );

        bearbeiten.setActionCommand("Bearbeiten");
        hilfe.setActionCommand("Hilfe");

        bearbeiten.addActionListener(listener);
        hilfe.addActionListener(listener);

        menu.add(bearbeiten);
        menu.add(hilfe);
    }

}
