package org.jojo.advp.interactive.gui;

import javax.swing.JFrame;

/**
 * The main class of the GUI of ADVP.
 * 
 * @author jojo
 * @version 0.9
 */
public class Main {
    
    /**
     * Starts the GUI.
     * 
     * @param args - not used
     */
    public static void main(String[] args) {
        final JFrame frame = new GuiFrame();
        frame.pack();
        frame.setVisible(true);
    }
}
