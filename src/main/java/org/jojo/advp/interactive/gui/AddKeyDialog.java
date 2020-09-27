package org.jojo.advp.interactive.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.Property;
import org.jojo.advp.base.eq.EquivalenceKeyDescription;
import org.jojo.advp.base.factory.KeyPairFactory;
import org.jojo.advp.interactive.ui.CommandLineInterface;
import org.jojo.util.JDO;

public class AddKeyDialog extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = -5595352886989883120L;
    private final boolean bPerson;
    private final int index;
    private final KeyPairFactory key;
    private final CommandLineInterface cli;
    private final int maxIndex;
    
    private final String[] heads;
    private final DefaultTableModel model;
    private final JTable table;
    
    private final Map<EquivalenceKeyDescription, JPanel> editMap;
    
    private final JTextField removeIndicesTextField;
    
    public AddKeyDialog(final boolean bPerson, final int index, final KeyPairFactory key, 
            final CommandLineInterface cli, final int maxIndex) {
        this.bPerson = bPerson;
        this.index = index;
        this.key = Objects.requireNonNull(key);
        this.cli = Objects.requireNonNull(cli);
        this.maxIndex = maxIndex;
        setTitle("Add key for " + (bPerson ? "person" : "task") + " key factory at index " + index);
        final Dimension d = this.getToolkit().getScreenSize();
        Dimension fs = new Dimension(d.width - 200, 600);
        setPreferredSize(fs);
        // central location
        this.setLocation((int) ((d.getWidth() - this.getWidth()) / 2 - fs.width / 2), (int) ((d.getHeight() - this.getHeight()) / 2 - fs.height / 2));
        
        this.heads = new String[]{"External Index ", "Internal Index", "Key ID", "Key Name", "Key Settings"};
        this.model = new DefaultTableModel();
        this.table = new JTable(this.model) {
            /**
             * 
             */
            private static final long serialVersionUID = -2087753205803446376L;

            public String getToolTipText(MouseEvent e) {
                return GUIUtil.getToolTipText(this, e);
            }
        };
        reload();
        JScrollPane sPane = new JScrollPane(this.table);
        
        this.editMap = new HashMap<EquivalenceKeyDescription, JPanel>();
        Container ct = getContentPane();
        ct.setLayout(new BoxLayout(ct, BoxLayout.Y_AXIS));
        ct.add(sPane);
        for (EquivalenceKeyDescription eqDesc : EquivalenceKeyDescription.values()) {
            eqDesc.initializeDefaultCreationHints();
            ct.add(createPanelFor(eqDesc));
            ct.add(new JSeparator());
        }
        
        final JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
        south.add(new JLabel("Remove Indices:"));
        this.removeIndicesTextField = new JTextField();
        this.removeIndicesTextField.setPreferredSize(new Dimension(100,30));
        south.add(this.removeIndicesTextField);
        JButton removeButton = new JButton("Remove keys at given indices");
        removeButton.addActionListener(removeAl());
        south.add(removeButton);
        
        ct.add(south);
    }
    
    
    private void reload() {
        for (String h : heads) {
            model.addColumn(h);
        }
        reloadData();
        table.setCellEditor(new TableCellEditor() {
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
    }
    
    private void reloadData() {
        final List<EquivalenceKey> keys = getKeys();
        model.setRowCount(keys.size());
        for (int i = 0; i < keys.size();i++) {
            final EquivalenceKey localKey = keys.get(i);
            model.setValueAt(index, i, 0);
            model.setValueAt(i, i, 1);
            Property property = bPerson ? localKey.getPersonProperty() : localKey.getTaskProperty();
            model.setValueAt(localKey.getID(), i, 2);
            model.setValueAt(property.getName(), i, 3);
            final String settings = localKey.toString();
            model.setValueAt(settings, i, 4);
        }
    }


    private JPanel createPanelFor(EquivalenceKeyDescription eqDesc) {
        JPanel ret = new JPanel();
        ret.setLayout(new BoxLayout(ret, BoxLayout.X_AXIS));
        
        ret.add(new JLabel("Add " + eqDesc + " Key"));
        ret.add(new JSeparator(JSeparator.VERTICAL));
        
        if (eqDesc == EquivalenceKeyDescription.CONTAINER) {
            ret.add(new JLabel("Indices to add:"));
            final JTextField indicesField = new JTextField();
            indicesField.setPreferredSize(new Dimension(200,30));
            ret.add(indicesField);
            ret.add(new JSeparator(JSeparator.VERTICAL));
            ret.add(new JLabel("Operator:"));
            final JTextField opField = new JTextField();
            opField.setPreferredSize(new Dimension(50,30));
            ret.add(opField);
            ret.add(new JSeparator(JSeparator.VERTICAL));
            ret.add(new JLabel("ID:"));
            final String idString = eqDesc.getSettingsNames()[2]
                    .replaceAll(".*" + enquote(EquivalenceKeyDescription.getDelim()), "");
            final JTextField idField = new JTextField(idString);
            idField.setPreferredSize(new Dimension(50,30));
            ret.add(idField);
            ret.add(new JSeparator(JSeparator.VERTICAL));
        } else {
            final String[] settingsNames = eqDesc.getSettingsNames();
            final String delim = EquivalenceKeyDescription.getDelim();
            for (final String settingsName : settingsNames) {
                final String labelText = (settingsName.contains(delim) ?
                        settingsName.replaceAll(enquote(delim) + ".*", "") : settingsName) 
                        + ":";
                ret.add(new JLabel(labelText));
                final JTextField textField = new JTextField();
                setDefaultValue(textField, settingsName);
                textField.setPreferredSize(new Dimension(200,30));
                ret.add(textField);
                ret.add(new JSeparator(JSeparator.VERTICAL));
            }
        }
        final JButton addButton = new JButton("Add");
        addButton.addActionListener(addAl(eqDesc));
        ret.add(addButton);
        
        this.editMap.put(eqDesc, ret);
        return ret;
    }
    
    private static String enquote(String toQuote) {
        return Pattern.quote(toQuote);
    }
    
    private static void setDefaultValue(final JTextField textField, final String settingsName) {
        final String delim = EquivalenceKeyDescription.getDelim();
        final String stdValue = settingsName.contains(delim) ? 
                settingsName.replaceAll(".*" + enquote(delim), "") : "";
        textField.setText(stdValue);
    }
    
    private ActionListener addAl(final EquivalenceKeyDescription eqDesc) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JPanel respectivePanel = editMap.get(eqDesc);
                final int compCount = respectivePanel.getComponentCount();
                final JTextField[] textFields = new  JTextField[(compCount - 3) / 3];
                for (int i = 3; i < compCount - 1; i++) {
                    final Component localComponent = respectivePanel.getComponent(i);
                    if (i % 3 == 0) {
                        final int localIndex = (i / 3) - 1;
                        final JTextField field = (JTextField)localComponent;
                        textFields[localIndex] = field;
                    }
                }

                final int indexOfAddedKey = maxIndex + 1;
                final String cmd;
                if (eqDesc == EquivalenceKeyDescription.CONTAINER) {
                    cmd = "keyContainerInternal " + (bPerson ? "person" : "task")
                            + " " + index + " " + getTexts(textFields, " ");
                } else {
                    cmd = "key" + (bPerson ? "Person" : "Task")
                            + " " + eqDesc.toString() 
                            + ";"
                            + getTexts(textFields, EquivalenceKeyDescription.getDelim())
                            + "\n" + "keyAddAll " + (bPerson ? "person" : "task")
                            + " " + index + " " + indexOfAddedKey;
                }
                changeKey(cmd);
                for (int i = 0;i < textFields.length;i++) {
                    final String settingsName = eqDesc.getSettingsNames()[i];
                    if (settingsName != null) {
                        setDefaultValue(textFields[i], settingsName);
                    }
                }
            }

            private String getTexts(final JTextField[] textFields, final String delim) {
                StringJoiner ret = new StringJoiner(delim);
                for (JTextField tf : textFields) {
                    ret.add(tf.getText());
                }
                return ret.toString();
            }
        };
    }
    
    private ActionListener removeAl() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String indicesStr = removeIndicesTextField.getText();
                if (indicesStr != null && !indicesStr.isEmpty()) {
                    try {
                        final int[] indices = Arrays.stream(splitAtSpace(indicesStr))
                            .mapToInt(x -> Integer.parseInt(x)).toArray();
                        StringJoiner cmd = new StringJoiner("\n");
                        for (int i : indices) {
                            cmd.add("keyRemoveInternal " + (bPerson ? "person" : "task") + " " + index + " " + i);
                        }
                        changeKey(cmd.toString());
                    } catch (NumberFormatException exc) {
                        JDO errDiag = new JDO("Error", "Wrong Number Format: " + exc.getMessage());
                        errDiag.open();
                    }
                }
            }
        };
    }
    
    private static String[] splitAtSpace(String in) {
        return in.trim().contains(" ") ? in.trim().split("\\s") : new String[] {in};
    }

    private List<EquivalenceKey> getKeys() {
        return bPerson ? key.getOfPersonKeys() : key.getOfTaskKeys();
    }
    
    private void changeKey(final String commands) {
        String[] lines = commands.contains("\n") ? commands.split("\n") : new String[] {commands};
        for (String line : lines) {
            cli.executeCommand(line);
        }
        reloadData();
    }

    public void addTableModelListener(final TableModelListener l) {
        this.model.addTableModelListener(l);
    }
}
