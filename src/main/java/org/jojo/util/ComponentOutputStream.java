package org.jojo.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

/**
 * Represents an output stream for writing to a JTextComponent or a JLabel.
 * 
 * @author jojo
 * @version 0.9
 */
public class ComponentOutputStream extends OutputStream {
    private JTextComponent t;
    private JLabel l;
    private String s;
    private boolean isJLabel;

    /**
     * Creates a new component output stream.
     * 
     * @param t - the JTextComponent to which should be written
     */
    public ComponentOutputStream(JTextComponent t) {
        this.t = t;
        this.s = "";
        this.isJLabel = false;
    }

    /**
     * Creates a new component output stream.
     * 
     * @param l - the JLabel to which should be written
     */
    public ComponentOutputStream(JLabel l) {
        this.l = l;
        this.s = "";
        this.isJLabel = true;
    }

    @Override
    public void write(int arg0) throws IOException {
        s = s + (char) ((byte) arg0);
    }

    @Override
    public void flush() {
        if (!isJLabel) {
            t.setText(t.getText() + s);
            s = "";
        } else {
            boolean html = false;
            if (!l.getText().startsWith("<html>")) {
                l.setText("<html>");
                html = true;
            }
            l.setText(l.getText() + s.replaceAll("\\n", "<br />"));
            if (html) {
                l.setText(l.getText() + "</html>");
            }
            s = "";
        }
    }

}
