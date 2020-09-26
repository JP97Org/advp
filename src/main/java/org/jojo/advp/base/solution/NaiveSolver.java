package org.jojo.advp.base.solution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jojo.advp.base.Person;
import org.jojo.advp.base.Solver;
import org.jojo.advp.base.Task;
import org.jojo.advp.base.World;

/**
 * Represents a solver which solves the problem naively.
 * 
 * @author jojo
 * @version 0.9
 */
public class NaiveSolver implements Solver {

    private World world;
    private boolean computePartialSolutionWhenSolvingNotPossible;
    
    /**
     * Creates a new naive solver without partial calculation.
     */
    public NaiveSolver() {
        this.world = null;
        this.computePartialSolutionWhenSolvingNotPossible = false;
    }
    
    /**
     * Creates a new naive solver with the given setting.
     * 
     * @param computePartialSolutionWhenSolvingNotPossible
     */
    public NaiveSolver(boolean computePartialSolutionWhenSolvingNotPossible) {
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
            //time-complexity: O( |Task| * |Person|^2 * (max |TaskProperties|) * (max |PersonProperties|) )
            //so if all values are ca. of the same length n: O(n^5)
            final List<Task> tasks = new ArrayList<>(world.getTasks());
            final List<Person> persons = new ArrayList<>(world.getPersons());
            
            outer:
            for(final Task task : tasks) {
                boolean mapped = false;
                final Set<Person> alreadyMappedPersons = new HashSet<Person>();
                Iterator<Person> iter = persons.iterator();
                while (iter.hasNext()) {
                    final Person person = iter.next();
                    mapped = alreadyMappedPersons.contains(person) ? false : world.mapTaskInstanceToPerson(task, person);
                    // at the moment no two task-instances of the same task can be mapped to 
                    // the same person with this solver
                    if(mapped) {
                        alreadyMappedPersons.add(person);
                        iter = persons.iterator(); //beginning again since other persons could now be mappable 
                    }
                    
                    if(mapped && task.getNumberOfInstances() == 0) {
                        continue outer;
                    }
                }
                if(task.getNumberOfInstances() > 0 && !this.computePartialSolutionWhenSolvingNotPossible) {
                    break outer; //failed to map this task --> no solution!
                }
            }
            
            boolean fullMapped = world.isCompletelyMapped();
            /*if(!fullMapped && !this.computePartialSolutionWhenSolvingNotPossible) {
                world.resetMapping();
            }*/
            return fullMapped;
        }
        throw new IllegalStateException("World was not set!");
    }
}
