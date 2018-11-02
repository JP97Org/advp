package base.solution;

import java.util.Set;

import base.Person;
import base.Solver;
import base.Task;
import base.World;

public class NaiveSolver implements Solver {

    private World world;
    private boolean computePartialSolutionWhenSolvingNotPossible;
    
    public NaiveSolver() {
        this.world = null;
        this.computePartialSolutionWhenSolvingNotPossible = false;
    }
    
    public NaiveSolver(boolean computePartialSolutionWhenSolvingNotPossible) {
        this.world = null;
        this.computePartialSolutionWhenSolvingNotPossible = computePartialSolutionWhenSolvingNotPossible;
    }
    
    public boolean doesComputePartialSolutionWhenSolvingNotPossible() {
        return this.computePartialSolutionWhenSolvingNotPossible;
    }
    
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
            //time-complexity: O( |Task| * |Person| * (max |TaskProperties|) * (max |PersonProperties|) )
            //so if all values are ca. of the same length n: O(n^4)
            Set<Task> tasks = world.getTasks();
            Set<Person> persons = world.getPersons();
            
            outer:
            for(Task task : tasks) {
                boolean mapped = false;
                for(Person person : persons) {
                    mapped = world.mapTaskInstanceToPerson(task, person);
                    // at the moment no two task-instances of the same task can be mapped to 
                    // the same person with this solver
                    if(mapped && task.getNumberOfInstances() == 0) {
                        continue outer;
                    }
                }
                if(task.getNumberOfInstances() > 0 && !this.computePartialSolutionWhenSolvingNotPossible) {
                    break outer; //failed to map this task --> no solution!
                }
            }
            
            boolean fullMapped = world.isCompletelyMapped();
            if(!fullMapped && !this.computePartialSolutionWhenSolvingNotPossible) {
                world.resetMapping();
            }
            return fullMapped;
        }
        throw new IllegalStateException("World was not set!");
    }
}
