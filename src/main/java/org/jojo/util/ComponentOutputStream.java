package org.jojo.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

public class ComponentOutputStream extends OutputStream {
    private JTextComponent t;
    private JLabel l;
    private String s;
    private boolean isJLabel;

    public ComponentOutputStream(JTextComponent t) {
        this.t = t;
        this.s = "";
        this.isJLabel = false;
    }

    public ComponentOutputStream(JLabel l) {
        this.l = l;
        this.s = "";
        this.isJLabel = true;
    }

    @Override
    public void write(int arg0) throws IOException {
        s = s + (char) ((byte) arg0);
    }

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
        try {
            super.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
