package natz;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import base.Person;
import base.eq.TimeInterval;

public class TestInputLoader {
    private static final String DELIM = ",";
    
    private final File inputFile;
    private PersonFactory pf;
    private final Set<Person> persons;
    private final ArrayList<StringBuilder> dateStrs;
    
    public TestInputLoader(final File inputFile, final PersonFactory pf) throws FileNotFoundException {
        this.inputFile = inputFile;
        this.persons = new HashSet<>();
        this.dateStrs = new ArrayList<>();
        load(pf);
    }
    
    protected void load(final PersonFactory pf) throws FileNotFoundException {
        this.pf = pf;
        final Scanner sc = new Scanner(this.inputFile);
        
        final String datesStr = sc.nextLine();
        this.dateStrs.addAll(
                Arrays
                .asList(Arrays
                        .stream(datesStr.split(DELIM))
                        .map(x -> new StringBuilder(x))
                        .toArray(StringBuilder[]::new)));
        
        while(sc.hasNextLine()) {
            final String readPersonData = sc.nextLine();
            final String[] personDataArr = readPersonData.split(DELIM);
            final String name = personDataArr[0];
            final boolean f = Boolean.parseBoolean(personDataArr[1]);
            final boolean n = Boolean.parseBoolean(personDataArr[2]);
            //ignore further input in this line if there is any!
            
            final List<TimeInterval> dates = getDates();
            final Person person = this.pf.getPerson(name, f, n, dates);
            this.persons.add(person);
        }
        
        sc.close();
    }
    
    private List<TimeInterval> getDates() {
        //Preparing the input for correct parsing
        this.dateStrs.forEach(x -> x.append(".2018"));
        
        //Parsing dates with help of TaskFactory
        TaskFactory tf = new TaskFactory(getDateStrs());
        return tf.getDates();
    }
    
    public Set<Person> getPersons() {
        return this.persons;
    }
    
    public String[] getDateStrs() {
        return this.dateStrs.stream().map(x -> x.toString()).toArray(String[]::new);
    }
}
