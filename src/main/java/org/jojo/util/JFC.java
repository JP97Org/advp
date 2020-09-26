package org.jojo.util;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

@SuppressWarnings("serial")
public class JFC extends JDialog {
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
