package base.solution;

import java.util.Set;

import base.Person;
import base.Solver;
import base.Task;
import base.World;

public class NaiveSolver implements Solver {

    private World world;
    
    public NaiveSolver() {
        this.world = null;
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
                    if(mapped && task.getNumberOfInstances() == 0) continue outer;
                }
                if(task.getNumberOfInstances() > 0) break outer; //failed to map this task --> no solution!
            }
            
            return world.isCompletelyMapped();
        }
        throw new IllegalStateException("World was not set!");
    }
}
