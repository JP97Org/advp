package org.jojo.advp.interactive.gui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jojo.advp.interactive.ui.CommandLineInterface;
import org.jojo.util.DataSaverAndLoader;
import org.jojo.util.JDO;
import org.jojo.util.JFC;

public class GuiMenuBar {
    private final GuiFrame frame;
    private final TablePanel personTable;
    private final TablePanel taskTable;
    private final CommandLineInterface cli;
    
    private final MenuBar mb;
    
    public GuiMenuBar(final GuiFrame frame) {
        this.frame = frame;
        this.personTable = frame.getPersonTable();
        this.taskTable = frame.getTaskTable();
        this.cli = frame.getCli();
        this.mb = new MenuBar();
        
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
        exitM.addActionListener(frame.al("quit"));
        file.add(loadM);
        file.add(saveM);
        file.add(loadCommands);
        file.add(saveCommands);
        file.add(exitM);
        
        Menu edit = new Menu("Edit");
        MenuItem reset = new MenuItem("Reset");
        reset.addActionListener(frame.al("reset"));
        MenuItem clear = new MenuItem("Clear Screens");
        clear.addActionListener(frame.al("cls"));
        edit.add(clear);
        edit.add(reset);
        
        Menu solve = new Menu("Solve");
        MenuItem solveMi = new MenuItem("Solve");
        solveMi.addActionListener(frame.solve());
        MenuItem show = new MenuItem("Show Solution");
        show.addActionListener(frame.showSolution());
        MenuItem saveSolution = new MenuItem("Save Solution");
        saveSolution.addActionListener(saveSolution());
        solve.add(solveMi);
        solve.add(show);
        solve.add(saveSolution);
        
        Menu special = new Menu("Special");
        MenuItem loadRandomNatz = new MenuItem("Load Random Natz");
        loadRandomNatz.addActionListener(SpecialUtil.loadRandomNatz(frame, cli));
        MenuItem loadNatz = new MenuItem("Load Natz");
        loadNatz.addActionListener(SpecialUtil.loadNatz(frame, cli));
        MenuItem saveNatzResult = new MenuItem("Save Natz Result");
        saveNatzResult.addActionListener(SpecialUtil.saveNatzResult(frame, cli));
        special.add(loadRandomNatz);
        special.add(loadNatz);
        special.add(saveNatzResult);
        
        Menu help = new Menu("Help");
        MenuItem helpM = new MenuItem("Help");
        helpM.addActionListener(help());
        MenuItem aboutM = new MenuItem("About");
        aboutM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                (new JDO("About", "ADVP Version 0.9\n"
                        + "by org.jojo (Lolalol97)\n"
                        + "Free Software")).open();
            }
        });
        help.add(helpM);
        help.add(aboutM);
        
        mb.add(file);
        mb.add(edit);
        mb.add(solve);
        mb.add(special);
        mb.add(help);
    }
    
    public MenuBar getMenuBar() {
        return mb;
    }
    
    private ActionListener saveSolution() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cli.getCore().isSolved()) {
                    if (!cli.getCore().isFullySolved()) {
                        frame.al("info solution to be saved is not fully solved").actionPerformed(null);
                    }
                    save();
                } else {
                    JDO solveDiag = new JDO("Solve?", 
                            "The problem is not solved, would you like it to be solved?", 
                            "OK");
                    solveDiag.getB1().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.solve().actionPerformed(e);
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
                        frame.al("info saved solution to " + file.getAbsolutePath()).actionPerformed(null);
                    } catch (IOException e) {
                        frame.al("error " + e.getMessage()).actionPerformed(null);
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
                    frame.al("load " + file.getAbsolutePath()).actionPerformed(null);
                    frame.al("update").actionPerformed(null);
                    frame.finishLoading();
                }
            }
        };
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
                    frame.al("save " + file.getAbsolutePath()).actionPerformed(null);
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
                        frame.al("error " + e1.getMessage()).actionPerformed(null);
                        return;
                    }
                    for (int i = 0; i < data.length; i++) {
                        cli.executeCommand(data[i][0]);
                    }
                    frame.al("update").actionPerformed(null);
                    frame.finishLoading();
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
                        frame.al("info saved commands to " + file.getAbsolutePath()).actionPerformed(e);
                    } catch (IOException e1) {
                        frame.al("error " + e1.getMessage()).actionPerformed(e);
                    }
                }
            }
        };
    }
    
    private ActionListener help() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream out;
                try {
                    out = new PrintStream(baos, true, java.nio.charset.StandardCharsets.UTF_8.name());
                    CommandLineInterface localCli = new CommandLineInterface(System.in, out, System.err);
                    localCli.executeCommand("help");
                    final String helpStr = "Add Keys with the Add Key Dialog for the different rows.\n"
                            + "The changes you made get saved to the table as soon as the dialog is closed.\n"
                            + "After you have set the keys and the person's and task's name and number of instances the system can try to solve the problem when you press the Solve button or menu item.\n"
                            + "Furthermore, there are some saving and loading options in the File menu and a Save Solution menu item in the Solve menu for saving the solution, i.e. the mappings of task instances to persons.\n"
                            + "\n\n" 
                            + baos.toString(java.nio.charset.StandardCharsets.UTF_8.name());
                    (new JDO("Help", helpStr)).open();
                } catch (UnsupportedEncodingException e1) {
                    frame.al("error an exception occured: " + e1.getMessage());
                    return;
                }
            }};
    }
}
