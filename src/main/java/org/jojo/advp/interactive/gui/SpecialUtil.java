package org.jojo.advp.interactive.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jojo.advp.base.Person;
import org.jojo.advp.base.Task;
import org.jojo.advp.base.World;
import org.jojo.advp.base.eq.TimeEquivalenceKey;
import org.jojo.advp.base.eq.TimeInterval;
import org.jojo.advp.interactive.ui.CommandLineInterface;
import org.jojo.advp.testApp.natz.PersonFactory;
import org.jojo.advp.testApp.natz.TaskFactory;
import org.jojo.advp.testApp.natz.Test;
import org.jojo.advp.testApp.natz.TestInputLoader;
import org.jojo.util.DataSaverAndLoader;
import org.jojo.util.JFC;

public final class SpecialUtil {
    private static List<TimeInterval> dates;
    
    private SpecialUtil() {
        
    }
    
    public static void reset() {
        dates = null;
    }

    public static ActionListener loadRandomNatz(GuiFrame guiFrame, CommandLineInterface cli) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Test.REAL_INPUT = null;
                final String[] dateStrs = Test.getDateStrings();
                final TaskFactory tf = new TaskFactory(true, dateStrs);
                dates = tf.getDates();
                final Set<Person> persons = Test.getPersons(new PersonFactory(), dates);
                final Set<Task> tasks = tf.getAllTasks();
                final World world = new World("natz");
                persons.forEach(p -> world.addPerson(p));
                tasks.forEach(t -> world.addTask(t));
                cli.getCore().prepare(world);
                guiFrame.al("update").actionPerformed(null);
                guiFrame.finishLoading();
            }
        };
    }

    public static ActionListener loadNatz(GuiFrame guiFrame, CommandLineInterface cli) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFC jfc = new JFC();
                final FileFilter fileFilter = new FileNameExtensionFilter("ADVP CSV File","csv");
                final File file = jfc.open("Load natz in .csv file", fileFilter); 
                if (file != null) {
                    try {
                        final TestInputLoader loader = new TestInputLoader(file, new PersonFactory());
                        final String[] dateStrs = loader.getDateStrs();
                        final TaskFactory tf = new TaskFactory(true, dateStrs);
                        dates = tf.getDates();
                        final Set<Person> persons = loader.getPersons();
                        final Set<Task> tasks = tf.getAllTasks();
                        final World world = new World("natz");
                        persons.forEach(p -> world.addPerson(p));
                        tasks.forEach(t -> world.addTask(t));
                        cli.getCore().prepare(world);
                        guiFrame.al("update").actionPerformed(null);
                        guiFrame.finishLoading();
                    } catch (Exception e1) {
                        guiFrame.al("error an exception occured: " + e1.getMessage()).actionPerformed(null);
                    }
                }
            }
        };
    }

    public static ActionListener saveNatzResult(GuiFrame guiFrame, CommandLineInterface cli) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFC jfc = new JFC();
                final FileFilter fileFilter = new FileNameExtensionFilter("ADVP CSV File","csv");
                final File file = jfc.open("Save natz out .csv file", fileFilter); 
                if (file != null) {
                    DataSaverAndLoader sal = new DataSaverAndLoader(java.nio.charset.StandardCharsets.UTF_8.name());
                    try {
                        final String[][] data = generateOutput(cli);
                        sal.saveData(file, data);
                        guiFrame.al("info saved natz solution to " + file.getAbsolutePath()).actionPerformed(e);
                    } catch (IllegalStateException e1) {
                        guiFrame.al("error " + e1.getMessage()).actionPerformed(e);
                    } catch (IOException|IllegalArgumentException e1) {
                        guiFrame.al("error an exception occured: " + e1.getMessage()).actionPerformed(e);
                    }
                }
            }
        };
    }
    
    private static String[][] generateOutput(CommandLineInterface cli) throws IllegalStateException {
        if (dates == null) {
            throw new IllegalStateException("you must first load a natz before you can save a natz result");
        }
        final int headerSize = TaskFactory.POOL_NUM_INSTANCE
                + TaskFactory.COUNT_FLOORS * TaskFactory.FLOOR_NUM_INSTANCE;
        String[][] ret = new String[dates.size() + 1][headerSize];
        // adding header
        for (int i = 0; i < TaskFactory.POOL_NUM_INSTANCE; i++) {
            ret[0][i] = TaskFactory.POOL_STR;
        }
        int cnt = 0;
        for (int o = TaskFactory.POOL_NUM_INSTANCE; o < headerSize; o += TaskFactory.FLOOR_NUM_INSTANCE) {
            for (int i = o; i < o + TaskFactory.FLOOR_NUM_INSTANCE; i++) {
                ret[0][i] = TaskFactory.FLOOR_STR + cnt;
            }
            cnt++;
        }
        final String[] header = ret[0];

        // adding body
        for (int o = 1; o < ret.length; o++) {
            final int odex = o - 1;
            for (int i = 0; i < ret[o].length; i++) {
                final int index = i;
                final Task task = cli.getCore().getWorldTasks()
                        .stream()
                        .filter(x -> x.getName().startsWith(header[index]))
                        .filter(x -> dateFilterMethod(x, odex))
                        .findFirst().orElse(null);
                if (task == null) {
                    throw new IllegalArgumentException("a task was not found (internal exception)");
                }
                final Person person = cli.getCore().getPersonOfTaskInstance(task.getName(), getTaskIndex(header, i));
                ret[o][i] = person != null ? person.getName() : "null";
            }
        }

        return ret;
    }

    private static boolean dateFilterMethod(final Task x, final int odex) {
        final TimeInterval date = dates.get(odex);
        
        return date.equals(TimeEquivalenceKey.extract(x).getTimeIntervals().iterator().next());
    }

    private static int getTaskIndex(final String[] header, final int i) {
        final int numPool = (int) Arrays.stream(header).filter(x -> x.equals(TaskFactory.POOL_STR)).count();
        final String floor = TaskFactory.FLOOR_STR;
        final int numFloor = (int) Arrays.stream(header).filter(x -> x.startsWith(floor))
                .map(x -> x.substring(floor.length())).filter(x -> x.equals("0")).count();

        return (i < numPool) ? i : ((i - numPool) % numFloor);
    }
}
