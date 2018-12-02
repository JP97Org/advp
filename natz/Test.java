package natz;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import base.Person;
import base.Task;
import base.eq.TimeInterval;

public class Test {
    private static File realInput = /*null;*/  new File("/home/jojo/Dokumente/in.csv"); /**/
    private static TestInputLoader til;
    
    public static void main(String[] args) throws FileNotFoundException {
        PersonFactory pf = new PersonFactory();
        if(realInput != null) til = new TestInputLoader(realInput, pf);
        
        final String[] dStrs = getDateStrings();
        TaskFactory tf = new TaskFactory(dStrs);
        final List<TimeInterval> dates = tf.getDates();
        
        final Set<Person> persons = getPersons(pf, dates);
        
        for(final Person person : persons) {
            System.out.println(person.getName());
        }

        final Set<Task> tasks = tf.getAllTasks();

        NatzControl nc = new NatzControl(persons, tasks, dates);
        final int iterations = 2;
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
            System.out.println(outputStr.replaceAll("null", "NUUUULL") + "\n");
            
            //resetting pf, tf and (nc and world)
            pf = new PersonFactory();
            if(til != null) til.load(pf);
            
            tf = new TaskFactory(getDateStrings());
            nc = new NatzControl(getPersons(pf, tf.getDates()), tf.getAllTasks(), tf.getDates());
        }
        System.out.println(cntMapped);
    }
    
    private static final String[] getDateStrings() {
        final String[] ret;
        if(realInput == null) {
            ret = new String[21];
            for(int i = 0;i < ret.length;i++) {
                final String s = (i < 10 ? "0" : "") + i;
                ret[i] = s + ".01.2018";
            }
        } else {
            ret = til.getDateStrs();
        }
        return ret;
    }
    
    private static final Set<Person> getPersons(final PersonFactory pf, final List<TimeInterval> dates) {
        final Set<Person> persons = new HashSet<Person>();
        if(realInput == null) {
            /*persons.add(pf.getPerson("Felix|m|o", false, false, dates));
            persons.add(pf.getPerson("Jonathan|m|n", false, true, dates));
            persons.add(pf.getPerson("Anna|f|o", true, false, dates));
            persons.add(pf.getPerson("Berta|f|n", true, true, dates));*/
            final int cnt = 24;
            for(int i = 0;i < cnt;i++) {
                final boolean female = Math.random() < 0.5;
                final boolean newK = Math.random() < 0.5;
                persons.add(pf.getPerson("" + i + "|" + (female ? "f" : "m") + "|" + (newK ? "n" : "o"), female, newK, dates));
            }
        } else {
            persons.addAll(til.getPersons());
        }
        return persons;
    }

}
