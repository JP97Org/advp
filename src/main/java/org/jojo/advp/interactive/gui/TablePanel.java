package org.jojo.advp.interactive.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jojo.advp.base.factory.KeyPairFactory;

public class TablePanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -6546301071275524214L;
    private final int initialRows;
    private final boolean bPerson;
    private final String[] heads;
    private final JTable table;
    private final DefaultTableModel model; 
    
    private KeyPairFactory newKey;
    private List<KeyPairFactory> keys;
    private int keyRemoveIndex = -1;
    
    public TablePanel(final int initialRows, final String buttonName, final String... heads) {
        this.initialRows = initialRows;
        this.bPerson = buttonName.contains("Person");
        this.heads = heads;
        this.model = new DefaultTableModel();
        this.table = new JTable(this.model);
        this.keys = new ArrayList<KeyPairFactory>();
        reload();
        JScrollPane sPane = new JScrollPane(this.table);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(sPane);
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        JButton addObject = new JButton(buttonName);
        addObject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addRow((Vector<Object>)null);
            }
        });
        buttons.add(addObject);
        JButton removeObject = new JButton(buttonName.replaceAll("Add", "Remove"));
        removeObject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int last = model.getRowCount() - 1;
                model.removeRow(last);
            }
        });
        buttons.add(removeObject);
        add(buttons);
    }
    
    public void addModelListener(TableModelListener l) {
        this.model.addTableModelListener(l);
    }
    
    private void reload() {
        final int lastColumn = heads.length - 1;
        for (String h : heads) {
            model.addColumn(h);
        }
        model.setRowCount(initialRows);
        table.getColumn("Key List").setCellEditor(new TableCellEditor() {
            @Override
            public Object getCellEditorValue() {
                return null;
            }

            @Override
            public boolean isCellEditable(EventObject anEvent) {
                return false;
            }

            @Override
            public boolean shouldSelectCell(EventObject anEvent) {
                return false;
            }

            @Override
            public boolean stopCellEditing() {
                return false;
            }

            @Override
            public void cancelCellEditing() {
                
            }

            @Override
            public void addCellEditorListener(CellEditorListener l) {
                
            }

            @Override
            public void removeCellEditorListener(CellEditorListener l) {
                
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                    int column) {
                return null;
            }
            
        });
        table.getColumn(table.getColumnName(lastColumn)).setCellRenderer(
                new JButtonRenderer());
        table.getColumn(table.getColumnName(lastColumn)).setCellEditor(
                new JButtonEditor());
    } 
    
    public void setKeysToModel(final List<KeyPairFactory> list) {
        this.keys = list;
        addKeysToModel();
    }
    
    private void addKeysToModel() {
        for (int row = 0; row < keys.size(); row++) {
            this.model.setValueAt(keys.get(row).toString(bPerson), row, model.findColumn("Key List"));
        }
    }
    
    public int getKeyRemoveIndex() {
        return this.keyRemoveIndex ;
    }
    
    public KeyPairFactory getNewKey() {
        return this.newKey;
    }
    
    private class JButtonRenderer implements TableCellRenderer {
        private JButton button = new JButton();

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            table.setShowGrid(true);
            table.setGridColor(Color.LIGHT_GRAY);
            button.setText("Add Key");
            button.setToolTipText("Opens a dialog for adding keys");
            return button;
        }
    }

    private class JButtonEditor extends AbstractCellEditor implements TableCellEditor {
        /**
         * 
         */
        private static final long serialVersionUID = -189576724227287762L;
        private JButton button;

        public JButtonEditor() {
            super();
            button = new JButton("Add Key");
            button.setOpaque(true);
        }

        public Object getCellEditorValue() {
            return null;
        }

        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return false;
        }

        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }

        public void cancelCellEditing() {
        }

        public void addCellEditorListener(CellEditorListener l) {
        }

        public void removeCellEditorListener(CellEditorListener l) {
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            for (ActionListener al : button.getActionListeners()) {
                button.removeActionListener(al);
            }
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //TODO open dialog for changing persons key here
                    System.out.println("Zeilenzahl: " + row);
                }
            });
            return button;
        }
    } 
}
