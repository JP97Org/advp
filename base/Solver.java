package base;

/**
 * The Solver interface should be implemented by all solvers of the optimization problem.
 * 
 * @author jojo
 * @version 0.9
 */
public interface Solver {
    
    /**
     * Sets the World which should be solved to the given world.
     * @param world - the given world
     */
    public void setWorld(World world);
    
    /**
     * Solves the optimization problem.
     * 
     * @return whether the problem is solved
     */
    public boolean solve();
}
