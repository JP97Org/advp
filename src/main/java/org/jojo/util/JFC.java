package org.jojo.util;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

/**
 * Represents a JDialog containing a JFileChooser.
 * 
 * @author jojo
 * @version 0.9
 */
public class JFC extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 6685293378994610903L;

    /**
     * Opens a JFC with the given title.
     * 
     * @param title - the given title
     * @return the selected file or null if none was selected
     */
    public File open(String title) {
        JDialog d = new JDialog(this);
        JFileChooser save = new JFileChooser();
        save.setDialogTitle(title);

        int ret = save.showOpenDialog(d);
        save.setVisible(true);

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = save.getSelectedFile();
            return file;
        } else {
            return null;
        }
    }

    /**
     * Opens a JFC with the given title and a javax.swing.filechooser.FileFilter.
     * 
     * @param title - the given title 
     * @param f - the given javax.swing.filechooser.FileFilter
     * @return the selected file or null if none was selected
     */
    public File open(String title, javax.swing.filechooser.FileFilter f) {
        JDialog d = new JDialog(this);
        JFileChooser save = new JFileChooser();
        save.setDialogTitle(title);

        save.addChoosableFileFilter(f);
        save.setFileFilter(f);

        int ret = save.showOpenDialog(d);
        save.setVisible(true);

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = save.getSelectedFile();
            return file;
        } else {
            return null;
        }
    }
}
