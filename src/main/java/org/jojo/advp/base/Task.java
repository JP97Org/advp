package org.jojo.advp.base;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jojo.advp.base.eq.TimeEquivalenceKey;
import org.jojo.advp.base.factory.TaskDescriptor;

/**
 * Represents a Task.
 * 
 * @author jojo
 * @version 0.9
 */
public class Task {
    private final String name;
    private int numberInstances;
    private final Set<TaskProperty> properties;
    
    /**
     * Creates a new Task.
     * 
     * @param name - the task's name
     * @param numberInstances - the number of instances of the task
     */
    public Task(String name, int numberInstances) {
        if(numberInstances < 0) throw new IllegalArgumentException("NumberInstances must be >= 0.");
        this.name = Objects.requireNonNull(name);
        this.numberInstances = numberInstances;
        this.properties = new HashSet<>();
    }
    
    /**
     * Creates a new Task.
     * 
     * @param name - the task's name
     * @param numberInstances - the number of instances of the task
     * @param properties - the task's properties
     */
    public Task(String name, int numberInstances, Set<TaskProperty> properties) {
        this(name, numberInstances);
        this.properties.addAll(properties);
    }
    
    public Task(TaskDescriptor taskDesc, Set<TaskProperty> properties) {
        this(Objects.requireNonNull(taskDesc).getName(), taskDesc.getNumInstances());
        this.properties.addAll(Objects.requireNonNull(properties));
    }

    /**
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * 
     * @return the number of instances
     */
    public int getNumberOfInstances() {
        return this.numberInstances;
    }
    
    /**
     * Decrements the number of instances if it is >0.
     * 
     * @return whether the number of instances was decremented
     */
    public boolean decrementNumberOfInstances() {
        if(this.numberInstances > 0) {
            this.numberInstances--;
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @return the task's properties
     */
    public Set<TaskProperty> getProperties() {
        return properties;
    }
    
    /**
     * 
     * @param property - the given property
     * @return whether the task contains the given property
     */
    public boolean hasProperty(TaskProperty property) {
        return this.properties.contains(Objects.requireNonNull(property));
    }
    
    /**
     * Adds the given property.
     * 
     * @param property - the given property
     * @return whether it was added
     */
    public boolean addProperty(TaskProperty property) {
        return this.properties.add(Objects.requireNonNull(property));
    }
    
    /**
     * Calls mapped on every property of this task.
     */
    public void mapped() {
        properties.stream().forEach(x -> x.mapped());
    }
    
    /**
     * Determines whether this task has at least one TEK property.
     * 
     * @return whether this task has at least one TEK property
     */
    public boolean hasTEK() {
        return this.properties.stream()
            .map(x -> x.getEquivalenceKey())
            .filter(x -> x.getClass().equals(TimeEquivalenceKey.class)).iterator().hasNext();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, properties);
    }
    
    @Override
    public boolean equals(Object other) {
        if(other != null && getClass().equals(other.getClass())) {
            final Task o = (Task)other;
            return  name.equals(o.name) && 
                    properties.equals(o.properties);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.name    
                + " | " 
                + this.numberInstances 
                + " | " 
                + this.properties.stream().map(p -> p.toString()).reduce("", (s,t) -> s + " | " + t);
    }
}
