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
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.jojo.advp.interactive.ui.CommandLineInterface;

import org.jojo.util.BufferedPrintStream;
import org.jojo.util.ComponentOutputStream;
import org.jojo.util.ComponentPrintStream;
import org.jojo.util.DataSaverAndLoader;
import org.jojo.util.JDO;
import org.jojo.util.JFC;
import org.jojo.util.TextUtil;
import org.jojo.util.ValidPrintStream;

public class GuiFrame extends JFrame implements TableModelListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1446027899237228791L;

    private static final int TABLE_ROWS = 20;//1;//2;//
    
    private TablePanel personTable;
    private TablePanel taskTable;
    
    private JTextField in = new JTextField();
    private JTextPane out = new JTextPane();
    private JTextPane err = new JTextPane();
    
    private Settings settings = new Settings();
    
    private BufferedPrintStream outB;
    private BufferedPrintStream errB;
    private CommandLineInterface cli;
    
    public GuiFrame() {
        setTitle("ADVP | Allgemeines Dienstverteiler Programm | General-Purpose Task-Person-Matcher");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension fs = Toolkit.getDefaultToolkit().getScreenSize();
        fs.width = (int) fs.getWidth() - 70;
        fs.height = (int) fs.getHeight() - 100;
        setPreferredSize(fs);
        createMenuBar();
        
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
        MenuItem loadM = new MenuItem("Load");
        loadM.addActionListener(loadAl());
        MenuItem saveM = new MenuItem("Save");
        saveM.addActionListener(saveAl());
        MenuItem loadCommands = new MenuItem("Load Commands");
        loadCommands.addActionListener(loadCommands());
        MenuItem saveCommands = new MenuItem("Save Commands");
        saveCommands.addActionListener(saveCommands());
        MenuItem exitM = new MenuItem("Exit");
        exitM.addActionListener(al("quit"));
        file.add(loadM);
        file.add(saveM);
        file.add(loadCommands);
        file.add(saveCommands);
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
        MenuItem saveSolution = new MenuItem("Save Solution");
        saveSolution.addActionListener(saveSolution());
        solve.add(solveMi);
        solve.add(show);
        solve.add(saveSolution);
        
        mb.add(file);
        mb.add(edit);
        mb.add(solve);
        setMenuBar(mb);
    }

    private ActionListener solve() {
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
    
    private ActionListener showSolution() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                al("print").actionPerformed(null);
            }
        };
    }
    
    private ActionListener saveSolution() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cli.getCore().isSolved()) {
                    if (!cli.getCore().isFullySolved()) {
                        al("info solution to be saved is not fully solved").actionPerformed(null);
                    }
                    save();
                } else {
                    JDO solveDiag = new JDO("Solve?", 
                            "The problem is not solved, would you like it to be solved?", 
                            "OK");
                    solveDiag.getB1().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            solve().actionPerformed(e);
                            solveDiag.setVisible(false);
                            solveDiag.dispose();
                            save();
                        }
                    });
                    solveDiag.open();
                }
            }
            
            private void save() {
                final JFC jfc = new JFC();
                final FileFilter fileFilter = new FileNameExtensionFilter("CSV File","csv");
                final File file = jfc.open("Save solution mappings to .csv file", fileFilter);
                if (file != null) {
                    final DataSaverAndLoader sal = new DataSaverAndLoader(java.nio.charset.StandardCharsets.UTF_8.name());
                    try {
                        sal.saveData(file, cli.getCore().getMappings());
                        al("info saved solution to " + file.getAbsolutePath()).actionPerformed(null);
                    } catch (IOException e) {
                        al("error " + e.getMessage()).actionPerformed(null);
                    }
                }
            }
        };
    }
    
    private ActionListener loadAl() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFC jfc = new JFC();
                final FileFilter fileFilter = new FileNameExtensionFilter("ADVP CSV File","csv");
                final File file = jfc.open("Load ADVP .csv file", fileFilter); 
                if (file != null) {
                    al("load " + file.getAbsolutePath()).actionPerformed(null);
                    al("update").actionPerformed(null);
                    finishLoading();
                }
            }
        };
    }
    
    private void finishLoading() {
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
    
    private ActionListener saveAl() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFC jfc = new JFC();
                final FileFilter fileFilter = new FileNameExtensionFilter("ADVP CSV File","csv");
                final File file = jfc.open("Save ADVP .csv file", fileFilter); 
                if (file != null) {
                    String[] persons = Arrays.stream(personTable.getPersons())
                            .map(x -> x == null ? null : x.toString())
                            .toArray(String[]::new);
                    cli.getCore().loadPersonNames(persons);
                    String[] taskDescriptors = Arrays.stream(taskTable.getTasks())
                            .map(x -> x == null ? null : x.toString())
                            .toArray(String[]::new);
                    cli.getCore().loadTaskDescriptors(taskDescriptors);
                    al("save " + file.getAbsolutePath()).actionPerformed(null);
                }
            }
        };
    }
    

    private ActionListener loadCommands() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFC jfc = new JFC();
                final FileFilter fileFilter = new FileNameExtensionFilter("ADVP CSV File","csv");
                final File file = jfc.open("Load ADVP Commands .csv file", fileFilter); 
                if (file != null) {
                    DataSaverAndLoader sal = new DataSaverAndLoader(java.nio.charset.StandardCharsets.UTF_8.name());
                    final String[][] data;
                    try {
                        data = sal.allDataValues(file);
                    } catch (IOException e1) {
                        al("error " + e1.getMessage()).actionPerformed(null);
                        return;
                    }
                    for (int i = 0; i < data.length; i++) {
                        cli.executeCommand(data[i][0]);
                    }
                    al("update").actionPerformed(null);
                    finishLoading();
                }
            }
        };
    }
    
    private ActionListener saveCommands() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFC jfc = new JFC();
                final FileFilter fileFilter = new FileNameExtensionFilter("ADVP CSV File","csv");
                final File file = jfc.open("Save ADVP Commands .csv file", fileFilter); 
                if (file != null) {
                    final List<String> commands = cli.getHistory();
                    final String[][] data = new String[commands.size()][1];
                    for (int i = 0; i < commands.size(); i++) {
                        data[i][0] = commands.get(i);
                    }
                    DataSaverAndLoader sal = new DataSaverAndLoader(java.nio.charset.StandardCharsets.UTF_8.name());
                    try {
                        sal.saveData(file, data);
                    } catch (IOException e1) {
                        al("error " + e1.getMessage()).actionPerformed(null);
                    }
                }
            }
        };
    }
    
    private ActionListener al() {
        return al(null);
    }
    
    private ActionListener al(final String cmd) {
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
                    JDO result = new JDO("Result Overview", TextUtil.toHTML(resultStr));
                    result.open();
                } else if(cmd.equals("reset")) {
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
