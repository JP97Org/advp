package org.jojo.advp.interactive.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.StringJoiner;

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

import org.jojo.advp.interactive.ui.CommandLineInterface;

import org.jojo.util.BufferedPrintStream;
import org.jojo.util.ComponentOutputStream;
import org.jojo.util.ComponentPrintStream;
import org.jojo.util.JDO;
import org.jojo.util.ValidPrintStream;

/**
 * Represents the main frame of the GUI.
 * 
 * @author jojo
 * @version 0.9
 */
public class GuiFrame extends JFrame implements TableModelListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1446027899237228791L;

    private static final int TABLE_ROWS = 20;
    
    private final JTextField in = new JTextField();
    private final JTextPane out = new JTextPane();
    private final JTextPane err = new JTextPane();
    
    private final Settings settings = new Settings();
    
    private final BufferedPrintStream outB;
    private final BufferedPrintStream errB;
    private final CommandLineInterface cli;
    
    private TablePanel personTable;
    private TablePanel taskTable;
    
    /**
     * Creates a new GUI main frame.
     */
    public GuiFrame() {
        setTitle("ADVP | Allgemeines Dienstverteiler Programm | General-Purpose Task-Person-Matcher");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension fs = Toolkit.getDefaultToolkit().getScreenSize();
        fs.width = (int) fs.getWidth() - 70;
        fs.height = (int) fs.getHeight() - 100;
        setPreferredSize(fs);
        
        ComponentOutputStream outS = new ComponentOutputStream(out);
        ValidPrintStream outP = new ComponentPrintStream(outS);
        outB = new BufferedPrintStream(outP);
        ComponentOutputStream errS = new ComponentOutputStream(err);
        ValidPrintStream errP = new ComponentPrintStream(errS);
        errB = new BufferedPrintStream(errP);
        this.cli = new CommandLineInterface(System.in, outB, errB);
        
        Container ct = getContentPane();
        ct.setLayout(new BoxLayout(ct, BoxLayout.Y_AXIS));
        
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.X_AXIS));
        north.add(createPersonPanel());
        north.add(createTaskPanel());
        north.add(createSolverPanel());
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
        ct.add(new JLabel("OUT:"));
        ct.add(oscr);
        ct.add(new JLabel("ERR:"));
        ct.add(escr);
        
        setMenuBar(new GuiMenuBar(this).getMenuBar());
    }

    private JPanel createPersonPanel() {
        this.personTable = new TablePanel(TABLE_ROWS, "Add Person", cli, "Person", "Key List", "Add Key");
        this.personTable.addModelListener(this);
        return this.personTable;
    }
    
    private JPanel createTaskPanel() {
        this.taskTable = new TablePanel(TABLE_ROWS, "Add Task", cli, "Task", "#Instances" ,"Key List", "Add Key");
        this.taskTable.addModelListener(this);
        return this.taskTable;
    }
    
    private JPanel createSolverPanel() {
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

    /**
     * Creates an action listener for solving.
     * 
     * @return an action listener for solving
     */
    protected ActionListener solve() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final StringJoiner persons = new StringJoiner(";");
                final Object[] personsArr = personTable.getPersons();
                boolean ok = true;
                boolean end = false;
                for (int i = 0; i < personsArr.length && ok; i++) {
                    final Object now = personsArr[i];
                    if (now == null) {
                        end = true;
                    } else {
                        if (end) {
                            ok = false;
                        }
                        persons.add(now.toString());
                    }
                }
                final StringJoiner tasks = new StringJoiner(";");
                final Object[] tasksArr = taskTable.getTasks();
                end = false;
                for (int i = 0; i < tasksArr.length && ok; i++) {
                    final Object now = tasksArr[i];
                    if (now == null) {
                        end = true;
                    } else {
                        if (end) {
                            ok = false;
                        }
                        tasks.add(now.toString());
                    }
                }
                final int countPersons = (int) Arrays.stream(personsArr)
                        .filter(x -> x != null)
                        .filter(x -> !x.toString().isEmpty())
                        .count();
                final int countTasks = (int) Arrays.stream(tasksArr)
                        .filter(x -> x != null)
                        .filter(x -> !x.toString().isEmpty())
                        .count();
                ok &= cli.getCore().isPreparable(countPersons, countTasks);
                if (ok) {
                    final String[] cmds = {"start", 
                        "completePrepPersons " + persons, 
                        "completePrepTasks " + tasks,
                        settings.getSolver(),
                        "solve"};
                    for (final String cmd : cmds) {
                        al(cmd).actionPerformed(null);
                    }
                } else {
                    JDO errDiag = new JDO("Error", "Check if all relevant person and task names and #Instances are given.");
                    errDiag.open();
                }
            }
        };
    }

    /**
     * Creates an action listener for showing the solution.
     * 
     * @return an action listener for showing the solution
     */
    protected ActionListener showSolution() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                al("print").actionPerformed(null);
            }
        };
    }
    
    /**
     * Finishes loading, i.e. sets the persons and the tasks from the core's buffer to the table.
     * @see {@link org.jojo.advp.interactive.core.InteractiveCore#finishLoadingPersonNames}
     * @see {@link org.jojo.advp.interactive.core.InteractiveCore#finishLoadingTaskDescriptors}
     */
    protected void finishLoading() {
        final String[] persons = cli.getCore().finishLoadingPersonNames();
        if (persons != null) {
            personTable.setToModel("Person", persons);
        }
        final String[] taskDescriptors = cli.getCore().finishLoadingTaskDescriptors();
        if (taskDescriptors != null) {
            final String[] taskNames = Arrays.stream(taskDescriptors)
                    .map(x -> x == null ? null : x.replaceAll(",.*", ""))
                    .toArray(String[]::new);
            taskTable.setToModel("Task", taskNames);
            final String[] instances = Arrays.stream(taskDescriptors)
                    .map(x -> x == null ? null : x.replaceAll(".*,", ""))
                    .toArray(String[]::new);
            taskTable.setToModel("#Instances", instances);
        }
    }
    
    private ActionListener al() {
        return al(null);
    }
    
    /**
     * An action listener for executing commands.
     * 
     * @param cmd - the given command
     * @return an action listener for executing commands
     */
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
            } else if (cmd.startsWith("info ")) {
                final String output = cmd.replaceFirst("info\\s", "");
                outB.begin();
                outB.println(output);
                outB.suspend();
            } else if (cmd.startsWith("error ")) {
                final String output = cmd.replaceFirst("error\\s", "");
                errB.begin();
                errB.println(output);
                errB.suspend();
            } else {
                outB.begin();
                errB.begin();
                cli.executeCommand(cmd);
                personTable.setKeysToModel(cli.getCore().getPersonKeyPairFactoryList());
                taskTable.setKeysToModel(cli.getCore().getTaskKeyPairFactoryList());
                if (cmd.equals("print")) {
                    final String resultStr = outB.getBuffer();
                    JDO result = new JDO("Result Overview", resultStr);
                    result.open();
                } else if (cmd.equals("reset")) {
                    SpecialUtil.reset();
                    personTable.reset();
                    taskTable.reset();
                }
                outB.suspend();
                errB.suspend();
            }
            in.setText("");
        }
       };
       return ret;
    }
    
    /**
     * 
     * @return the person table panel
     */
    protected TablePanel getPersonTable() {
        return this.personTable;
    }
    
    /**
     * 
     * @return the task table panel
     */
    protected TablePanel getTaskTable() {
        return this.taskTable;
    }
    
    /**
     * 
     * @return the command line interface
     */
    protected CommandLineInterface getCli() {
        return this.cli;
    }

    @Override
    public void tableChanged(final TableModelEvent e) {
        DefaultTableModel model = (DefaultTableModel)e.getSource();
        final boolean bPerson = model.getColumnCount() == 3;
        final TablePanel table = bPerson ? personTable : taskTable;
        if (table.isUpdateNecessary()) {
            final int removeIndex = table.getKeyRemoveIndex();
            if (removeIndex >= 0) {
                al("keyRemove " + (bPerson ? "person" : "task") + " " + removeIndex).actionPerformed(null);
            } else {
                al("update").actionPerformed(null);
            }
        }
    }
}
