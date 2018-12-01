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
        final String[] dStrs = getDateStrings();
        TaskFactory tf = new TaskFactory(dStrs);
        final List<TimeInterval> dates = tf.getDates();
        PersonFactory pf = new PersonFactory();
        final Set<Person> persons = getPersons(pf, dates);
        
        for(final Person person : persons) {
            System.out.println(person.getName());
        }

        final Set<Task> tasks = tf.getAllTasks();

        NatzControl nc = new NatzControl(persons, tasks, dates);
        final int iterations = 100;
        int cntMapped = 0;
        for(int i = 0;i < iterations;i++) {
            final String[][] output = nc.calculateTable();
            cntMapped = nc.isFullyMapped() ? (cntMapped + 1) : cntMapped;
            final String outputStr = Arrays
                .stream(output)
                .map(x -> Arrays.stream(x).reduce("", (a, b) -> a + ";" + b))
                .reduce("", (c, d) -> c + "\n" + d)
                .replaceAll("\\n;", "\n")
                .replaceFirst("\\n", "");
            System.out.println(outputStr + "\n");
            
            //resetting pf, tf and (nc and world)
            pf = new PersonFactory();
            tf = new TaskFactory(dStrs);
            nc = new NatzControl(getPersons(pf, tf.getDates()), tf.getAllTasks(), tf.getDates()); 
        }
        System.out.println(cntMapped);
    }
    
    private static final String[] getDateStrings() {
        String[] ret = new String[21];
        
        for(int i = 0;i < ret.length;i++) {
            final String s = (i < 10 ? "0" : "") + i;
            ret[i] = s + ".01.2018";
        }
        
        return ret;
    }
    
    private static final Set<Person> getPersons(final PersonFactory pf, final List<TimeInterval> dates) {
        final Set<Person> persons = new HashSet<Person>();
        /*persons.add(pf.getPerson("Felix|m|o", false, false, dates));
        persons.add(pf.getPerson("Jonathan|m|n", false, true, dates));
        persons.add(pf.getPerson("Anna|f|o", true, false, dates));
        persons.add(pf.getPerson("Berta|f|n", true, true, dates));*/
        final int cnt = 32;
        for(int i = 0;i < cnt;i++) {
            final boolean female = Math.random() < 0.5;
            final boolean newK = Math.random() < 0.5;
            persons.add(pf.getPerson("" + i + "|" + (female ? "f" : "m") + "|" + (newK ? "n" : "o"), female, newK, dates));
        }
        return persons;
    }

}
