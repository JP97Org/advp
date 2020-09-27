package org.jojo.advp.interactive.gui;

import java.awt.event.MouseEvent;

import javax.swing.JTable;

import org.jojo.util.TextUtil;

public final class GUIUtil {
    private GUIUtil() {
        
    }
    
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
