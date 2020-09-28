package org.jojo.advp.interactive.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;
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

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.factory.KeyPairFactory;
import org.jojo.advp.interactive.ui.CommandLineInterface;

public class TablePanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -6546301071275524214L;
    private final int initialRows;
    private final boolean bPerson;
    private final String[] heads;
    private final DefaultTableModel model;
    private final JTable table;
    
    private List<KeyPairFactory> keys;
    
    private boolean isUpdateNecessary;
    private int keyRemoveIndex = -1;
    
    private CommandLineInterface cli;
    
    public TablePanel(final int initialRows, final String buttonName, final CommandLineInterface cli
            , final String... heads) {
        this.initialRows = initialRows;
        this.bPerson = buttonName.contains("Person");
        this.cli = Objects.requireNonNull(cli);
        this.heads = heads;
        this.model = new DefaultTableModel();
        this.table = new JTable(this.model) {
            /**
             * 
             */
            private static final long serialVersionUID = 8263161517438294548L;
        
            public String getToolTipText(MouseEvent e) {
                return GUIUtil.getToolTipText(this, e);
            }
        };
        this.keys = new ArrayList<KeyPairFactory>();
        reload();
        JScrollPane sPane = new JScrollPane(this.table);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(sPane);
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        JButton addObject = new JButton(buttonName);
        JButton removeObject = new JButton(buttonName.replaceAll("Add", "Remove"));
        addObject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addRow((Vector<Object>)null);
                model.fireTableDataChanged();
                removeObject.setEnabled(true);
            }
        });
        buttons.add(addObject);
        removeObject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int last = model.getRowCount() - 1;
                model.removeRow(last);
                isUpdateNecessary = true;
                model.fireTableDataChanged();
                if (last == 0) {
                    removeObject.setEnabled(false);
                }
            }
        });
        buttons.add(removeObject);
        add(buttons);
        
        this.isUpdateNecessary = false;
    }
    
    public void addModelListener(TableModelListener l) {
        this.model.addTableModelListener(l);
    }
    
    private void reload() {
        for (String h : heads) {
            model.addColumn(h);
        }
        refresh(initialRows);
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
        final int lastColumn = heads.length - 1;
        
        table.getColumn(table.getColumnName(lastColumn)).setCellRenderer(
                new JButtonRenderer());
        table.getColumn(table.getColumnName(lastColumn)).setCellEditor(
                new JButtonEditor());
    } 
    
    private void refresh(final int rowCount) {
        this.model.setRowCount(rowCount);
    }
    
    public boolean isUpdateNecessary() {
        return this.isUpdateNecessary;
    }
    
    public void setKeysToModel(final List<KeyPairFactory> list) {
        this.keys = list;
        setKeysToModel();
    }
    
    private void setKeysToModel() {
        this.isUpdateNecessary = false;
        if (this.keys.size() > model.getRowCount()) {
            model.setRowCount(this.keys.size());
        }
        
        final int col = model.findColumn("Key List");
        final int rowCount = model.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            this.model.setValueAt(null, row, col);
        }
        for (int row = 0; row < keys.size(); row++) {
            this.model.setValueAt(keys.get(row).toString(bPerson), row, col);
        }
        model.fireTableDataChanged();
    }
    
    public void setToModel(String columnName, final String[] values) {
        model.setRowCount(Math.max(values.length, model.getRowCount()));
        final int col = model.findColumn(columnName);
        for (int i = 0; i < values.length; i++) {
            this.model.setValueAt(values[i], i, col);
        }
        model.fireTableDataChanged();
    }
    
    public void reset() {
        refresh(0);
        refresh(initialRows);
    }
    
    public int getKeyRemoveIndex() {
        return this.keyRemoveIndex;
    }
    
    public Object[] getPersons() {
        if (bPerson) {
            final Object[] ret = new Object[model.getRowCount()];
            final int colNr = model.findColumn("Person");
            for (int i = 0; i < ret.length; i++) {
                ret[i] = model.getValueAt(i, colNr);
                if(ret[i] != null && ret[i].toString().isEmpty()) {
                    ret[i] = null;
                }
            }
            return ret;
        }
        return null;
    }
    
    public Object[] getTasks() {
        if (!bPerson) {
            final Object[] ret = new Object[model.getRowCount()];
            final int taskColNr = model.findColumn("Task");
            final int instColNr = model.findColumn("#Instances");
            for (int i = 0; i < ret.length; i++) {
                ret[i] = model.getValueAt(i, taskColNr);
                if(ret[i] == null || ret[i].toString().isEmpty()) {
                    ret[i] = null;
                } else {
                    final Object num = model.getValueAt(i, instColNr);
                    if (num == null || num.toString().isEmpty()) {
                        ret[i] = null;
                    } else {
                        ret[i] = ret[i] + "," + num.toString();
                    }
                }
            }
            return ret;
        }
        return null;
    }
    
    private class JButtonRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = new JButton();
            table.setShowGrid(true);
            table.setGridColor(Color.LIGHT_GRAY);
            button.setText("Add Key");
            button.setToolTipText("Opens a dialog for adding keys");
            if (row > keys.size()) {
                button.setEnabled(false);
            }
            return button;
        }
    }

    private class JButtonEditor extends AbstractCellEditor implements TableCellEditor {
        /**
         * 
         */
        private static final long serialVersionUID = -189576724227287762L;

        public JButtonEditor() {
            super();
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
            final JButton button = new JButton("Add Key");
            button.setToolTipText("Opens a dialog for adding keys");
            for (ActionListener al : button.getActionListeners()) {
                button.removeActionListener(al);
            }
            if (row <= keys.size()) {
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        final KeyPairFactory key;
                        if (keys.size() > row) {
                            key = keys.get(row);
                        } else {
                            key = new KeyPairFactory();
                            keys.add(key);
                            if (bPerson) {
                                cli.getCore().addPersonKeyPairFactory(key);
                            } else {
                                cli.getCore().addTaskKeyPairFactory(key);
                            }
                        }
                        final List<EquivalenceKey> localKeys = bPerson ? key.getOfPersonKeys() : key.getOfTaskKeys();
                        final int sizeBefore = localKeys.size();
                        final int maxIndex = keys.size() - 1;
                        final AddKeyDialog addKey = new AddKeyDialog(bPerson, row, key, cli, maxIndex);
                        addKey.pack();
                        addKey.setVisible(true);
                        addKey.addWindowListener(new WindowListener() {
                            @Override
                            public void windowOpened(WindowEvent e) {
                            }

                            @Override
                            public void windowClosing(WindowEvent e) {
                                isUpdateNecessary = true;
                                if (localKeys.size() == 0 && sizeBefore != 0) {
                                    keyRemoveIndex = row;
                                }
                                model.fireTableDataChanged();
                            }
                            @Override
                            public void windowClosed(WindowEvent e) {
                            }
                            @Override
                            public void windowIconified(WindowEvent e) {
                            }
                            @Override
                            public void windowDeiconified(WindowEvent e) {
                            }
                            @Override
                            public void windowActivated(WindowEvent e) {
                            }
                            @Override
                            public void windowDeactivated(WindowEvent e) {
                            }
                        });
                    }
                });
            } else {
                button.setEnabled(false);
            }
            return button;
        }
    }
}
