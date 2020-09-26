package org.jojo.util;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class UneditableJTextField extends JTextField {
    public UneditableJTextField() {
        super();
        setEditable(false);
    }

    public UneditableJTextField(String text) {
        super(text);
        setEditable(false);
    }

    public UneditableJTextField(Image i) {
        setOpaque(false);
        setEditable(false);
        Graphics g = i.getGraphics();
        paintComponent(g);
        if (i != null)
            g.drawImage(i, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}
