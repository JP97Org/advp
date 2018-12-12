package natz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import base.Person;
import base.Task;
import base.eq.TimeEquivalenceKey;
import base.eq.TimeInterval;
import base.solution.FairNumSolver;

public class WorseOriginalNatzSolver extends FairNumSolver {
    private final TaskFactory tf;
    private int randomizeLvl;

    private Set<Person> alreadyTried;
    
    public WorseOriginalNatzSolver(final TaskFactory tf) {
        super();
        this.tf = tf;
        this.randomizeLvl = 0;
        this.alreadyTried = new HashSet<>();
    }
    
    public WorseOriginalNatzSolver(final boolean computePartialSolutionWhenSolvingNotPossible, final int randomizeLvl, final TaskFactory tf) {
        super(computePartialSolutionWhenSolvingNotPossible);
        if(randomizeLvl < 0) throw new IllegalArgumentException("randomizeLvl must be >= 0");
        this.tf = tf;
        this.randomizeLvl = randomizeLvl;
        this.alreadyTried = new HashSet<>();
    }

    @Override
    public boolean solve() {
        if(this.world == null) throw new IllegalStateException("World was not set!");
        
        final Map<Person,Integer> countMap = new HashMap<>(); 
        final List<Person> personsList = new ArrayList<Person>(this.world.getPersons());
        personsList.forEach(x -> countMap.put(x, 0));
        //TODO evtl. add counts fuer leute, die nur teilweise da sind
        
        final List<TimeInterval> dates = this.tf.getDates();
        Collections.sort(dates);
        
        TimeInterval secondLastDate = null;
        TimeInterval lastDate = null;
        
        //TODO evtl. secondLastTasksMap
        final Map<Person,Task> lastTaskMap = new HashMap<>();
        personsList.forEach(x -> lastTaskMap.put(x, null));
        
        outer:
        for (final TimeInterval date : dates) {
            final List<Task> tasks = this.tf.getTasksOfDate(date);
            for (final Task task : tasks) {
                boolean taskMapped = false;
                for (Person p = getNextPerson(countMap, personsList, lastDate, lastTaskMap, task); 
                        p != null && !taskMapped; 
                        p = getNextPerson(countMap, personsList, lastDate, lastTaskMap, task)) {
                    assert(task.getNumberOfInstances() > 0);
                    final boolean mapped = this.world.mapTaskInstanceToPerson(task, p);
                    this.alreadyTried.add(p);
                    if(mapped) {
                        countMap.put(p, countMap.get(p) + 1);
                        lastTaskMap.put(p, task);
                    }
                    if(mapped && task.getNumberOfInstances() == 0) {
                        taskMapped = true;
                    }
                }
                
                this.alreadyTried.clear();
                
                if((!taskMapped) && (!doesComputePartialSolutionWhenSolvingNotPossible())) {
                    break outer;
                }
            }
            
            secondLastDate = lastDate;
            lastDate = date;
        }
        
        System.out.println(countMap + "\n" + edge(countMap, true) + "|" + edge(countMap, false)); //TODO: DEBUG-Ausgabe entfernen!
        
        return this.world.isCompletelyMapped();
    }
    
    //TODO: DEBUG method entfernen
    private int edge(final Map<Person, Integer> personToNumMap, final boolean min) {
        int ret = min ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        
        for (final Entry<Person, Integer> entry : personToNumMap.entrySet()) {
            final int val = entry.getValue();
            if(min ? val < ret : val > ret) {
                ret = val;
            }
        }
        
        return ret;
    }

    private Person getNextPerson(Map<Person, Integer> map, List<Person> personsList, TimeInterval lastDate, Map<Person, Task> lastTaskMap, Task task) {
        for(int i = 0;i < this.randomizeLvl;i++) {
            Collections.shuffle(personsList);
        }
        
        int min = Integer.MAX_VALUE;
        Set<Person> pMin = new HashSet<>();
        
        for(Entry<Person, Integer> entry : map.entrySet()) {
            final Person person = entry.getKey();
            final int num = entry.getValue();
            final Task lastTaskOfPerson = lastTaskMap.get(person);
            final boolean lastDateMatch = lastTaskOfPerson == null ? false
                    : similar(lastTaskOfPerson, task) && lastTaskOfPerson.getProperties()
                    .stream()
                    .map(x -> x.getEquivalenceKey())
                    .filter(x -> x.getClass().equals(TimeEquivalenceKey.class))
                    .map(x -> TimeEquivalenceKey.class.cast(x))
                    .map(x -> x.getTimeIntervals()).iterator().next().iterator().next()
                    .equals(lastDate);
            if (!this.alreadyTried.contains(person) && !lastDateMatch && num <= min) {
                if(num < min) {
                    min = num;
                    pMin.clear();
                }
                pMin.add(person);
            }
        }
        
        if(pMin.size() > 0) {
            for(final Person p : personsList) {
                if (pMin.contains(p)) {
                    return p;
                }
            }
        }
            
        return null;
    }
    
    private boolean similar(Task task, Task taskTwo) {
        final String name = task.getName();
        final String nameTwo = taskTwo.getName();
        return (name.contains(TaskFactory.POOL_STR) && nameTwo.contains(TaskFactory.POOL_STR))
                || (name.contains(TaskFactory.FLOOR_STR) && nameTwo.contains(TaskFactory.FLOOR_STR));
    }

    @Override
    protected Person getNextPerson(Map<Person, Integer> map, Set<Person> alreadyTried) {
        // other method is used!
        return null;
    }

}
