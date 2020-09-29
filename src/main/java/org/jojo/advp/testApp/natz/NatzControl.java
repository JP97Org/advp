package org.jojo.advp.testApp.natz;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jojo.advp.base.Person;
import org.jojo.advp.base.Task;
import org.jojo.advp.base.World;
import org.jojo.advp.base.eq.TimeEquivalenceKey;
import org.jojo.advp.base.eq.TimeInterval;
import org.jojo.advp.base.solution.FairNumSolver;
import org.jojo.advp.base.solution.NaiveFairNumSolver;
import org.jojo.advp.base.solution.OptimizedFairNumSolver;

/**
 * Represents the controller for the natz.
 * 
 * @author jojo
 * @version 0.9
 */
public class NatzControl {
    private World world;
    private List<TimeInterval> dates;
    
    /**
     * Creates a new controller for the natz.
     * 
     * @param persons - the persons
     * @param tasks - the tasks
     * @param dates - the dates
     * @param partial - whether partial solving should be accepted
     * @param randomizeLvl - the randomization level
     * @param optimize - whether optimization should take place (experimental)
     * @param origSolver - whether the original solver should be used
     * @param tf - the task factory
     */
    public NatzControl(final Set<Person> persons, final Set<Task> tasks, 
            final List<TimeInterval> dates, final boolean partial, final int randomizeLvl,
            final boolean optimize, final boolean origSolver, final TaskFactory tf) {
        if(randomizeLvl < 0) throw new IllegalArgumentException("randomizeLvl must be >= 0");
        this.world = new World("natz");
        persons.forEach(p -> this.world.addPerson(p));
        tasks.forEach(t -> this.world.addTask(t));

        this.dates = dates;

        FairNumSolver solver = optimize ? new OptimizedFairNumSolver(partial, randomizeLvl) 
                                              : new NaiveFairNumSolver(partial, randomizeLvl);
        
        solver = origSolver ? new WorseOriginalNatzSolver(partial, randomizeLvl, tf) : solver;
        
        this.world.setSolver(solver);
    }

    /**
     * Calculates the output table.
     * 
     * @return the output table
     */
    public String[][] calculateTable() {
        boolean solved = this.world.isCompletelyMapped();
        if (!solved) {
            solved = this.world.solve();
        }
        return generateOutput();
    }

    private String[][] generateOutput() {
        // TODO: DEBUG-Ausgaben auskommentieren
        System.out.println("partly mapped");
        System.out.println("fully mapped ?: " + this.world.isCompletelyMapped());

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
                final Task task = this.world.getTasks()
                        .stream()
                        .filter(x -> x.getName().equals(header[index]))
                        .filter(x -> dateFilterMethod(x, odex))
                        .iterator().next();
                final Person person = this.world.getPersonOfTaskInstance(task, getTaskIndex(header, i));
                ret[o][i] = person != null ? person.getName() : "null";
            }
        }

        return ret;
    }

    private boolean dateFilterMethod(final Task x, final int odex) {
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

    /**
     * 
     * @return whether the world is fully mapped
     */
    public boolean isFullyMapped() {
        return world.isCompletelyMapped();
    }
}
