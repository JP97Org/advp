package natz;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import base.Person;
import base.Task;
import base.World;
import base.eq.TimeEquivalenceKey;
import base.eq.TimeInterval;
import base.solution.FairNumSolver;
import base.solution.NaiveFairNumSolver;
import base.solution.OptimizedFairNumSolver;

public class NatzControl {
    private World world;
    private List<TimeInterval> dates;

    public NatzControl(final Set<Person> persons, final Set<Task> tasks, 
            final List<TimeInterval> dates, final boolean partial, final int randomizeLvl,
            final boolean optimize) {
        if(randomizeLvl < 0) throw new IllegalArgumentException("randomizeLvl must be >= 0");
        this.world = new World("natz");
        persons.forEach(p -> this.world.addPerson(p));
        tasks.forEach(t -> this.world.addTask(t));

        this.dates = dates;

        final FairNumSolver solver = optimize ? new OptimizedFairNumSolver(partial, randomizeLvl) 
                                              : new NaiveFairNumSolver(partial, randomizeLvl);
        this.world.setSolver(solver);
    }

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
            ret[0][i] = "Pool"; // TODO: better constant management
        }
        int cnt = 0;
        for (int o = TaskFactory.POOL_NUM_INSTANCE; o < headerSize; o += TaskFactory.FLOOR_NUM_INSTANCE) {
            for (int i = o; i < o + TaskFactory.FLOOR_NUM_INSTANCE; i++) {
                ret[0][i] = "Floor " + cnt; // TODO: better constant management
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
        
        return x
                .getProperties()
                .stream()
                .filter(a -> a.getEquivalenceKey().getClass().equals(TimeEquivalenceKey.class))
                .map(a -> TimeEquivalenceKey.class.cast(a.getEquivalenceKey()))
                .map(a -> a.getTimeIntervals())
                .iterator().next().iterator().next().equals(date);
    }

    private static int getTaskIndex(final String[] header, final int i) {
        // TODO: better constant management
        final int numPool = (int) Arrays.stream(header).filter(x -> x.equals("Pool")).count();
        final String floor = "Floor ";
        final int numFloor = (int) Arrays.stream(header).filter(x -> x.startsWith(floor))
                .map(x -> x.substring(floor.length())).filter(x -> x.equals("0")).count();

        return (i < numPool) ? i : ((i - numPool) % numFloor);
    }

    public boolean isFullyMapped() {
        return world.isCompletelyMapped();
    }
}
