package org.jojo.advp.interactive.gui;

import java.awt.event.MouseEvent;

import javax.swing.JTable;

import org.jojo.util.TextUtil;

/**
 * An utility class for the GUI.
 * 
 * @author jojo
 * @version 0.9
 */
public final class GUIUtil {
    private GUIUtil() {
        
    }
    
    /**
     * Gets a tool tip text for a table cell.
     * 
     * @param table - the table
     * @param e - the mouse event
     * @return  a tool tip text for the table cell defined by the table and the mouse event 
     *          or null if mouse is not over a table cell
     */
    public static String getToolTipText(JTable table, MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = table.rowAtPoint(p);
        int colIndex = table.columnAtPoint(p);

        Object value = table.getValueAt(rowIndex, colIndex);
        if (value != null && !value.toString().isEmpty()) {
            tip = TextUtil.toHTML(TextUtil.toStringWithLineFeeds(value, 200));
        }
        return tip;
    }
}
