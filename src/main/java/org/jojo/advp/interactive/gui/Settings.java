package org.jojo.advp.interactive.gui;

/**
 * Represents the settings.
 * 
 * @author jojo
 * @version 0.9
 */
public class Settings {
    
    /**
     * An enum for solver types.
     * 
     * @author jojo
     * @version 0.9
     */
    public enum SolverType {
        NAIVE, FAIRNUM;
    }
    
    private String solver;
    
    /**
     * Creates a new settings instance.
     */
    public Settings() {
        
    }
    
    /**
     * Sets a solver.
     * 
     * @param type - the solver type
     * @param partial - whether the solver should also allow partial solving
     * @param randomizeLevel - the level of randomization
     */
    public void setSolver(final SolverType type, final boolean partial, final int randomizeLevel) {
        switch(type) {
            case NAIVE: this.solver = "setSolver naive " + (partial ? "part" : "full") + " " + randomizeLevel; break;
            case FAIRNUM: this.solver = "setSolver fairNum " + (partial ? "part" : "full") + " " + randomizeLevel; break;
            default: this.solver = null;
        }
    }
    
    /**
     * 
     * @return the command string for setting the solver
     */
    public String getSolver() {
        return this.solver;
    }
}
