package natz;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import base.Person;
import base.Task;
import base.eq.TimeInterval;

public class Test {
    public static void main(String[] args) {
        final TaskFactory tf = new TaskFactory("14.05.2018", "15.05.2018");
        final List<TimeInterval> dates = tf.getDates();
        final PersonFactory pf = new PersonFactory();
        final Set<Person> persons = new HashSet<Person>();
        persons.add(pf.getPerson("Felix|m|o", false, false, dates));
        persons.add(pf.getPerson("Jonathan|m|n", false, true, dates));
        persons.add(pf.getPerson("Anna|f|o", true, false, dates));
        persons.add(pf.getPerson("Berta|f|n", true, true, dates));
        final int cnt = 16;
        for(int i = 0;i < cnt;i++) {
            persons.add(pf.getPerson("" + i + "|" + (i % 2 == 0 ? "f" : "m") + "|" + (i % 2 == 0 ? "n" : "o"), i % 2 == 0, i % 2 == 0, dates));
        }

        final Set<Task> tasks = tf.getAllTasks();

        NatzControl nc = new NatzControl(persons, tasks, dates);
        final String[][] output = nc.calculateTable();
        if(output != null) {
            final String outputStr = Arrays
                .stream(output)
                .map(x -> Arrays.stream(x).reduce("", (a, b) -> a + ";" + b))
                .reduce("", (c, d) -> c + "\n" + d)
                .replaceAll("\\n;", "\n")
                .replaceFirst("\\n", "");
            System.out.println(outputStr);
        } else {
            System.err.println("Mapping failed!");
        }
    }

}
