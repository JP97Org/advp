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
    private static final boolean PARTIAL = true;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    // SETTINGS
    
    private static final int RANDOMIZE_LVL = 100; 
    //private static final int ITERATIONS = 1;//100; //no longer used!
    private static final boolean OPTIMIZE = false; //TODO optimize more!
    private static final boolean ORIG_SOLVER = false;
    
    private static final File REAL_INPUT = /*null; */ new File("/home/jojo/Dokumente/in.csv"); /**/
    
    private static final int TRY_FIND_ITERATIONS = 10; //use not more than 10 for WorseOrigSolver!
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    
    private static TestInputLoader til;
    
    private static boolean solutionFound = false;
    private static String[][] lastRes = null;
    
    public static void main(String[] args) throws FileNotFoundException {
        long before;
        /*if(ITERATIONS > 0) {
            before = System.currentTimeMillis();
            System.out.println(calc(0,0, ITERATIONS));
            time(before);
        }*/
        
        if(TRY_FIND_ITERATIONS > 0) {
            double[] counts = new double[TRY_FIND_ITERATIONS];
            int maxCnt = 0;
            final long cmplBefore = System.currentTimeMillis();
            int checksum = 0;
            int doublationsDirect = 0;
            for(int i = 0;i < TRY_FIND_ITERATIONS;i++) {
                before = System.currentTimeMillis();
                int cnt = 0;
                do {
                    checksum += calc(0,0,1);
                    cnt++;
                } while(!solutionFound);
                doublationsDirect += doublationsDirect();
                time(before);
                counts[i] = cnt;
                maxCnt = cnt > maxCnt ? cnt : maxCnt;
            }
            time(cmplBefore);
            System.out.println(checksum == TRY_FIND_ITERATIONS);
            System.out.println("Average count until solution found: " + Arrays.stream(counts).reduce(0, (a,b) -> a + b) / (double)TRY_FIND_ITERATIONS);
            System.out.println("Maximum count until solution found: " + maxCnt);
            System.out.println("Doublations direct (avg): " + doublationsDirect / (double)TRY_FIND_ITERATIONS);
        }    
    }
    
    private static void time(final long before) {
        System.out.println((System.currentTimeMillis() - before) + "ms");
    }
    
    private static int doublationsDirect() {
        int ret = 0;
        
        for (int o = 1; o < lastRes.length; o++) {
            final String[] lineBefore = lastRes[o-1];
            final String[] line = lastRes[o];
            for (int i = 0; i < line.length; i++) {
                ret += line[i].equals(lineBefore[i]) ? 1 : 0;
            }
        }
        
        return ret;
    }
    
    private static int calc(final int cntMapped, final int iter, final int until) throws FileNotFoundException {
        if(iter == until) {
            return cntMapped;
        }
        
        PersonFactory pf = new PersonFactory();
        if(REAL_INPUT != null) til = new TestInputLoader(REAL_INPUT, pf);
        
        final String[] dStrs = getDateStrings();
        TaskFactory tf = new TaskFactory(dStrs);
        final List<TimeInterval> dates = tf.getDates();
        
        final Set<Person> persons = getPersons(pf, dates);

        final Set<Task> tasks = tf.getAllTasks();

        NatzControl nc = new NatzControl(persons, tasks, dates, PARTIAL, RANDOMIZE_LVL, OPTIMIZE, ORIG_SOLVER, tf);
        final String[][] output = nc.calculateTable();
        lastRes = output;
        final String outputStr = Arrays
                .stream(output)
                .map(x -> Arrays.stream(x).reduce("", (a, b) -> a + ";" + b))
                .reduce("", (c, d) -> c + "\n" + d)
                .replaceAll("\\n;", "\n")
                .replaceFirst("\\n", "");
        if(nc.isFullyMapped()) {
            for(final Person person : persons) {
                System.out.println(person.getName());
            }
            System.out.println(outputStr.replaceAll("null", "NUUUULL") + "\n");
        }
        if (nc.isFullyMapped()) {
           solutionFound = true; 
        } else {
           solutionFound = false;
        }
        return calc(cntMapped + (nc.isFullyMapped() ? 1 : 0), iter + 1, until);
    }
    
    private static String[] getDateStrings() {
        final String[] ret;
        if(REAL_INPUT == null) {
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
    
    private static Set<Person> getPersons(final PersonFactory pf, final List<TimeInterval> dates) {
        final Set<Person> persons = new HashSet<Person>();
        if(REAL_INPUT == null) {
            /*persons.add(pf.getPerson("Felix|m|o", false, false, dates));
            persons.add(pf.getPerson("Jonathan|m|n", false, true, dates));
            persons.add(pf.getPerson("Anna|f|o", true, false, dates));
            persons.add(pf.getPerson("Berta|f|n", true, true, dates));*/
            final int cnt = 48;
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
