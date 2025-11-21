package FileBrowser.util;

import java.io.File;

public class MyFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            return file.isDirectory();
        }

        public String getDescription() {
            return "directory";
        }
}
