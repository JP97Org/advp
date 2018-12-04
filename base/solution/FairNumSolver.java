package base.solution;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import base.Person;
import base.Solver;
import base.Task;
import base.World;

import base.eq.TimeEquivalenceKey;

/**
 * Represents a solver which solves the problem as fairly as possible, i.e. every person should do ca.
 * the same amount of task instances.
 * 
 * @author jojo
 * @version 0.9
 */
public abstract class FairNumSolver implements Solver {
    //////////////////////////////////////////////////////
    //TODO DEBUG entfernen
    //private static int cntDb = 0;
    //////////////////////////////////////////////////////
    
    private World world;
    private boolean computePartialSolutionWhenSolvingNotPossible;
    
    /**
     * Constructor of FairNumSolver without partial calculation.
     */
    public FairNumSolver() {
        this.world = null;
        this.computePartialSolutionWhenSolvingNotPossible = false;
    }
    
    /**
     * Constructor of FairNumSolver with partial calculation settable.
     * @param computePartialSolutionWhenSolvingNotPossible - the partial solution boolean
     */
    public FairNumSolver(final boolean computePartialSolutionWhenSolvingNotPossible) {
        this.world = null;
        this.computePartialSolutionWhenSolvingNotPossible = computePartialSolutionWhenSolvingNotPossible;
    }
    
    /**
     * Determines whether the solver also calculates a partial solution.
     * 
     * @return whether the solver also calculates a partial solution
     */
    public boolean doesComputePartialSolutionWhenSolvingNotPossible() {
        return this.computePartialSolutionWhenSolvingNotPossible;
    }
    
    /**
     * Sets the partial solution boolean.
     * 
     * @param computePartialSolutionWhenSolvingNotPossible - the partial solution boolean
     */
    public void setComputePartialSolutionWhenSolvingNotPossible(boolean computePartialSolutionWhenSolvingNotPossible) {
        this.computePartialSolutionWhenSolvingNotPossible = computePartialSolutionWhenSolvingNotPossible;
    }
    
    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public boolean solve() {
        if(world != null) {
            //time-complexity: O(|Task| * |Person|^2 * (max |TaskProperties|) * (max |PersonProperties|) )
            //so if all values are ca. of the same length n: O(n^5)
            
            final List<Task> tasks = new ArrayList<>(world.getTasks());
            final List<Person> persons = new ArrayList<>(world.getPersons());
            final Map<Person, Integer> personToNumMap = new HashMap<>();
            persons.forEach(x -> personToNumMap.put(x, 0));
            
            //////////////////////////////
            //DEBUG TODO: entfernen sobald fehler behoben
            /*
            cntDb++;
            boolean jumpedIn = false;
            */
            //////////////////////////////
            
            for(final Task task : tasks) {
                boolean taskMapped = false;
                final Set<Person> alreadyTried = new HashSet<>();
                final Set<Person> alreadyMapped = new HashSet<>();
                Person person = getNextPerson(personToNumMap, alreadyTried);
                while(person != null && !taskMapped) {
                    boolean mapped = alreadyMapped.contains(person) ? false : world.mapTaskInstanceToPerson(task, person);
                    
                    //////////////////////////////////////////////////////////////
                    
                    //TODO: DEBUG-Einspringpunkt entfernen sobald fehler gefunden
                    
                    /*
                    try {
                        if(!jumpedIn && task.getName().equals("Floor 2")
                                &&
                                task.getProperties()
                                .stream()
                                .filter(x -> x.getEquivalenceKey().getClass().equals(TimeEquivalenceKey.class))
                                .map(x -> TimeEquivalenceKey.class.cast(x.getEquivalenceKey()))
                                .map(x -> x.getTimeIntervals())
                                .iterator().next().iterator().next()
                                .getFrom().equals(DateFormat.getDateInstance().parse("10.08.2018"))) {
                            if(cntDb == 2)
                                jumpedIn = true;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    */
                    
                    
                    //////////////////////////////////////////////////////////////
                    
                    if(mapped) {
                        personToNumMap.put(person, personToNumMap.get(person) + 1);
                        alreadyMapped.add(person);
                        //alreadyTried.clear(); //TODO: eigentlich nicht mehr benoetigt, da alternation das ueberprueft!
                        //double task-instance mapping to one person not possible atm with this solver!
                        //alreadyTried.addAll(alreadyMapped);
                    }/* else {*/
                    alreadyTried.add(person);
                    /*}*/
                    
                    if(mapped && task.getNumberOfInstances() == 0) {
                        taskMapped = true;
                    }
                    
                    person = getNextPerson(personToNumMap, alreadyTried);
                }
                
                if(!taskMapped && !this.computePartialSolutionWhenSolvingNotPossible) {
                    break;
                }
            }   
            
            System.out.println(personToNumMap + "\n" + edge(personToNumMap, true) + "|" + edge(personToNumMap, false)); //TODO: DEBUG-Ausgabe entfernen!
            
            boolean fullMapped = world.isCompletelyMapped();
            return fullMapped;
        }
        throw new IllegalStateException("World was not set!");
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

    /**
     * Gets the next person.
     * 
     * @param map - the persons to num map
     * @param alreadyTried - the persons already tried
     * @return the next person
     */
    protected abstract Person getNextPerson(Map<Person, Integer> map, Set<Person> alreadyTried);
}
