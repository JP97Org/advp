package org.jojo.advp.base.factory;

import java.util.Objects;

public class TaskDescriptor {
    private final String name;
    private final int numInstances;
    
    public TaskDescriptor(final String name, final int numInstances) {
        if(numInstances < 0) throw new IllegalArgumentException("NumberInstances must be >= 0.");
        
        this.name = Objects.requireNonNull(name);
        this.numInstances = numInstances;
    }
    
    public String getName() {
        return name;
    }
    
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
}
