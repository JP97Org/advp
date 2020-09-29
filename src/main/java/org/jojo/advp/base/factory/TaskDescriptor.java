package org.jojo.advp.base.factory;

import java.util.Objects;

/**
 * Describes a task.
 * 
 * @author jojo
 * @version 0.9
 */
public class TaskDescriptor {
    private final String name;
    private final int numInstances;
    
    /**
     * Creates a new task descriptor.
     * 
     * @param name - the name of the task
     * @param numInstances - the number of instances of the task
     */
    public TaskDescriptor(final String name, final int numInstances) {
        if(numInstances < 0) throw new IllegalArgumentException("NumberInstances must be >= 0.");
        
        this.name = Objects.requireNonNull(name);
        this.numInstances = numInstances;
    }
    
    /**
     * 
     * @return the name of the task
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     * @return the number of instances of the task
     */
    public int getNumInstances() {
        return numInstances;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.numInstances);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            final TaskDescriptor o = (TaskDescriptor) other;
            return this.name.equals(o.name) && this.numInstances == o.numInstances;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.name + "|" + this.numInstances;
    }
}
