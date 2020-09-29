package org.jojo.util;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jojo.util.TextUtil;

/**
 * Represents a JDialog with zero, one or two buttons.
 * 
 * @author jojo
 * @version 0.9
 */
public class JDO extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 5417971841359640787L;
    private JButton b1;
    private JButton b2;

    /**
     * Creates a new JDO with a title and a text.
     * 
     * @param title - a title
     * @param text - a text
     */
    public JDO(String title, String text) {
        b1 = null;
        b2 = null;
        setAlwaysOnTop(true);
        setTitle(title);
        final JLabel label = new JLabel(TextUtil.toHTML(text));
        JScrollPane scr = new JScrollPane(label);
        scr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        Container c = getContentPane();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(scr);
        Dimension fs = getSize();
        final Dimension d = getToolkit().getScreenSize();
        setLocation((int) ((d.getWidth() - getWidth()) / 2 - fs.width / 2),
                (int) ((d.getHeight() - getHeight()) / 2 - fs.height / 2));
    }

    /**
     * Creates a new JDO with a title, a text and a button.
     * 
     * @param title - a title
     * @param text - a text
     * @param button - text of the button
     */
    public JDO(String title, String text, String button) {
        this(title, text);
        b1 = new JButton(button);
        b2 = null;
        
        Container c = getContentPane();
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(b1);
        c.add(buttons, "Center");
    }

    /**
     * Creates a new JDO with a title, a text and two buttons.
     * 
     * @param title - a title
     * @param text - a text
     * @param button1 - text for the first button
     * @param button2 - text for the second button
     */
    public JDO(String title, String text, String button1, String button2) {
        this(title, text, button1);
        b2 = new JButton(button2);
        
        Container c = getContentPane();
        
        JPanel buttons = (JPanel) c.getComponent(1);
        buttons.add(b2);
    }

    /**
     * Opens the dialog.
     */
    public void open() {
        pack();
        setVisible(true);
    }

    /**
     * 
     * @return the first button
     */
    public JButton getB1() {
        return b1;
    }

    /**
     * 
     * @return the second button
     */
    public JButton getB2() {
        return b2;
    }
}
