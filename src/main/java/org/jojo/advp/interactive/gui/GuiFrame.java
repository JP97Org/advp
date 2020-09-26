package org.jojo.advp.interactive.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jojo.advp.base.factory.KeyPairFactory;
import org.jojo.advp.interactive.ui.CommandLineInterface;

import org.jojo.util.BufferedPrintStream;
import org.jojo.util.ComponentOutputStream;
import org.jojo.util.ComponentPrintStream;
import org.jojo.util.ValidPrintStream;

public class GuiFrame extends JFrame implements TableModelListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1446027899237228791L;

    private static final int TABLE_ROWS = 20;
    
    private TablePanel personTable;
    private TablePanel taskTable;
    
    private JTextField in = new JTextField();
    private JTextPane out = new JTextPane();
    private JTextPane err = new JTextPane();
    
    private Settings settings = new Settings();
    
    BufferedPrintStream outB;
    BufferedPrintStream errB;
    private CommandLineInterface cli;
    
    public GuiFrame() {
        setTitle("ADVP - Allgemeines Dienstverteiler Programm");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension fs = Toolkit.getDefaultToolkit().getScreenSize();
        fs.width = (int) fs.getWidth() - 70;
        fs.height = (int) fs.getHeight() - 100;
        setPreferredSize(fs);
        createMenuBar();
        
        Container ct = getContentPane();
        ct.setLayout(new BoxLayout(ct, BoxLayout.Y_AXIS));
        
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.X_AXIS));
        north.add(getPersonPanel());
        north.add(getTaskPanel());
        north.add(getSolverPanel());
        ct.add(north);
        
        Dimension preferredSize = new Dimension(300,300);
        out.setPreferredSize(preferredSize);
        out.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
        out.setEditable(false);
        err.setForeground(Color.RED);
        err.setPreferredSize(preferredSize);
        err.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
        err.setEditable(false);
        JScrollPane oscr = new JScrollPane(out);
        JScrollPane escr = new JScrollPane(err); 
        
        oscr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        oscr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        escr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        escr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
        in.addActionListener(al());
        in.setPreferredSize(new Dimension(100,30));
        south.add(new JLabel("IN:"));
        south.add(in);
        
        ct.add(south);
        //ct.add(new JSeparator());
        ct.add(new JLabel("OUT:"));
        ct.add(oscr);
        //ct.add(new JSeparator());
        ct.add(new JLabel("ERR:"));
        ct.add(escr);

        ComponentOutputStream outS = new ComponentOutputStream(out);
        ValidPrintStream outP = new ComponentPrintStream(outS);
        outB = new BufferedPrintStream(outP);
        ComponentOutputStream errS = new ComponentOutputStream(err);
        ValidPrintStream errP = new ComponentPrintStream(errS);
        errB = new BufferedPrintStream(errP);
        this.cli = new CommandLineInterface(System.in, outB, errB);
    }

    private JPanel getPersonPanel() {
        this.personTable = new TablePanel(TABLE_ROWS, "Add Person", "Person", "Key List", "Add Key");
        this.personTable.addModelListener(this);
        return this.personTable;
    }
    
    private JPanel getTaskPanel() {
        this.taskTable = new TablePanel(TABLE_ROWS, "Add Task", "Task", "#Instances" ,"Key List", "Add Key");
        this.taskTable.addModelListener(this);
        return this.taskTable;
    }
    
    private JPanel getSolverPanel() {
        JPanel ret = new JPanel();
        ret.setLayout(new BoxLayout(ret, BoxLayout.Y_AXIS));
        ret.add(new JLabel("Solver:"));
        ButtonGroup solvers = new ButtonGroup();
        JRadioButton naive = new JRadioButton("naive");
        naive.addActionListener(solverSettingsListener());
        JRadioButton fairNum = new JRadioButton("fairNum");
        fairNum.addActionListener(solverSettingsListener());
        fairNum.setSelected(true);
        solvers.add(naive);
        solvers.add(fairNum);
        ret.add(naive);
        ret.add(fairNum);
        ret.add(new JSeparator());
        ret.add(new JLabel("Compute Partial Solution?:"));
        ButtonGroup partial = new ButtonGroup();
        JRadioButton part = new JRadioButton("partial");
        part.addActionListener(solverSettingsListener());
        part.setSelected(true);
        JRadioButton full = new JRadioButton("full");
        full.addActionListener(solverSettingsListener());
        partial.add(part);
        partial.add(full);
        ret.add(part);
        ret.add(full);
        ret.add(new JSeparator());
        ret.add(new JLabel("Randomize Level:"));
        JSlider randLvl = new JSlider(JSlider.HORIZONTAL,
                0, 20, 0);
        randLvl.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateSettings(randLvl);
            }});
        
        randLvl.setMajorTickSpacing(10);
        randLvl.setMinorTickSpacing(1);
        randLvl.setPaintTicks(true);
        randLvl.setPaintLabels(true);
        randLvl.setMaximumSize(new Dimension(300,100));
        ret.add(randLvl);
        
        ret.add(new JSeparator());
        final JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(solve());
        ret.add(solveButton);
        ret.add(new JSeparator());
        final JButton showSolution = new JButton("Show Solution");
        showSolution.addActionListener(showSolution());
        ret.add(showSolution);
        
        part.doClick();
        ret.setMaximumSize(new Dimension (200,300));
        return ret;
    }
    
    private ActionListener solverSettingsListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton source = (JRadioButton)e.getSource();
                updateSettings(source);
            }
            
        };
    }
    
    private void updateSettings(final Component source) {
        JRadioButton[] buttons = Arrays.stream(source.getParent().getComponents())
                .filter(x -> x.getClass().isAssignableFrom(JRadioButton.class))
                .map(x -> JRadioButton.class.cast(x))
                .toArray(JRadioButton[]::new);
        Settings.SolverType solverType = null;
        boolean partial = false;
        for (JRadioButton button : buttons) {
            if (button.isSelected()) {
                final String text = button.getText();
                if (text.equals("naive")) {
                    solverType = Settings.SolverType.NAIVE;
                } else if (text.equals("fairNum")) {
                    solverType = Settings.SolverType.FAIRNUM;
                } else if (text.equals("partial")) {
                    partial = true;
                }
            }
        }
        int randLvl = Arrays.stream(source.getParent().getComponents())
                .filter(x -> x.getClass().isAssignableFrom(JSlider.class))
                .map(x -> JSlider.class.cast(x).getModel().getValue()).findFirst().orElse(0);
        this.settings.setSolver(solverType, partial, randLvl);
    }

    private void createMenuBar() {
        MenuBar mb = new MenuBar();
        
        Menu file = new Menu("File");
        MenuItem exitM = new MenuItem("Exit");
        exitM.addActionListener(al("quit"));
        file.add(exitM);
        
        Menu edit = new Menu("Edit");
        MenuItem reset = new MenuItem("Reset");
        reset.addActionListener(al("reset"));
        MenuItem clear = new MenuItem("Clear Screens");
        clear.addActionListener(al("cls"));
        edit.add(clear);
        edit.add(reset);
        
        Menu solve = new Menu("Solve");
        MenuItem solveMi = new MenuItem("Solve");
        solveMi.addActionListener(solve());
        MenuItem show = new MenuItem("Show Solution");
        show.addActionListener(showSolution());
        solve.add(solveMi);
        solve.add(show);
        
        mb.add(file);
        mb.add(edit);
        mb.add(solve);
        setMenuBar(mb);
    }
    
    private ActionListener solve() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO do all the things necessary for solving
            }
        };
    }
    
    private ActionListener showSolution() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO do all the things necessary for showing solution
            }
        };
    }
    
    private ActionListener al() {
        return al(null);
    }
    
    protected ActionListener al(final String cmd) {
       ActionListener ret = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
           if (cmd != null) {
               in.setText(cmd);
           }
           prcs();
        }
        
        private void prcs() {
            final String cmd = in.getText();
            out.setText(out.getText() + (out.getText().isEmpty() ? ">" : "\n>") + cmd + "\n");
            if (cmd.equals("quit")) {
                System.exit(0);
            } else if (cmd.equals("cls")) {
                out.setText("");
                err.setText("");
            } else if (cmd.equals("update")) {
                personTable.setKeysToModel(cli.getCore().getPersonKeyPairFactoryList());
                taskTable.setKeysToModel(cli.getCore().getTaskKeyPairFactoryList());
            } else {
                outB.begin();
                errB.begin();
                cli.executeCommand(cmd);
                personTable.setKeysToModel(cli.getCore().getPersonKeyPairFactoryList());
                taskTable.setKeysToModel(cli.getCore().getTaskKeyPairFactoryList());
                outB.suspend();
                errB.suspend();
            }
            in.setText("");
        }
       };
       return ret;
    }

    @Override
    public void tableChanged(final TableModelEvent e) {
        DefaultTableModel model = (DefaultTableModel)e.getSource();
        if (e.getColumn() == model.findColumn("Key List")) {
            final boolean bPerson = model.getColumnCount() == 3;
            final TablePanel table = bPerson ? personTable : taskTable;
            final int removeIndex = table.getKeyRemoveIndex();
            if (removeIndex >= 0) {
                al("keyRemove " + (bPerson ? "person" : "task") + " " + removeIndex).actionPerformed(null);
            } else {
                final KeyPairFactory newKey = table.getNewKey();
                if (bPerson) {
                    cli.getCore().addPersonKeyPairFactory(newKey);
                } else {
                    cli.getCore().addTaskKeyPairFactory(newKey);
                }
                al("update").actionPerformed(null);
            }
        }
    }
}
