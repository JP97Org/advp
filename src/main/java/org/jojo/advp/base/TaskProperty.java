package org.jojo.advp.base;

import java.util.Objects;

/**
 * Represents a task's property.
 * 
 * @author jojo
 * @version 0.9
 */
public class TaskProperty extends Property {
    
    /**
     * Creates a new TaskProperty with the given name, description and equivalence key.
     * 
     * @param name
     * @param description
     * @param equivalenceKey
     */
    public TaskProperty(String name, String description, EquivalenceKey equivalenceKey) {
        super(name,description,equivalenceKey);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), getName(), getDescription(), getEquivalenceKey());
    }
    
    @Override
    public boolean equals(Object other) {
        if(other != null && getClass().equals(other.getClass())) {
            final TaskProperty o = (TaskProperty)other;
            return  getName().equals(o.getName()) && 
                    getDescription().equals(o.getDescription()) &&
                    getEquivalenceKey().equals(o.getEquivalenceKey());
        }
        return false;
    }
}
