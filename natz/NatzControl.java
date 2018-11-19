package natz;

import java.util.Set;

import base.Person;
import base.Solver;
import base.Task;
import base.World;
import base.solution.NaiveSolver;

public class NatzControl {
    private World world;
    
    public NatzControl(Set<Person> persons, Set<Task> tasks) {
        this.world = new World("natz");
        persons.forEach(p -> this.world.addPerson(p));
        tasks.forEach(t -> this.world.addTask(t));
        
        Solver solver = new NaiveSolver(); //TODO other solver
        this.world.setSolver(solver);
    }
    
    public String[][] calculateTable() {
        boolean solved = this.world.isCompletelyMapped();
        if(!solved) {
            solved = this.world.solve();
        }
        return generateOutput();
    }
    
    private String[][] generateOutput() {
        String[][] ret = null;
        
        if(this.world.isCompletelyMapped()) {
            
        }
        
        return ret;
    }
    
    public void resetMapping() {
        this.world.resetMapping();
    }
}
