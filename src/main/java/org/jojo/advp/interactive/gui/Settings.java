package org.jojo.advp.interactive.gui;

public class Settings {
    public enum SolverType {
        NAIVE, FAIRNUM;
    }
    
    private String solver;
    
    public Settings() {
        
    }
    
    public void setSolver(final SolverType type, final boolean partial, final int randomizeLevel) {
        switch(type) {
            case NAIVE: this.solver = "setSolver naive " + (partial ? "part" : "full") + " " + randomizeLevel; break;
            case FAIRNUM: this.solver = "setSolver fairNum " + (partial ? "part" : "full") + " " + randomizeLevel; break;
            default: this.solver = null;
        }
    }
    
    public String getSolver() {
        return this.solver;
    }
}
