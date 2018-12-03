package base;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Task {
    private final String name;
    private int numberInstances;
    private final Set<TaskProperty> properties;
    
    public Task(String name, int numberInstances) {
        if(numberInstances < 0) throw new IllegalArgumentException("NumberInstances must be >= 0.");
        this.name = name;
        this.numberInstances = numberInstances;
        this.properties = new HashSet<>();
    }
    
    public Task(String name, int numberInstances, Set<TaskProperty> properties) {
        this(name, numberInstances);
        this.properties.addAll(properties);
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getNumberOfInstances() {
        return this.numberInstances;
    }
    
    public boolean decrementNumberOfInstances() {
        if(this.numberInstances > 0) {
            this.numberInstances--;
            return true;
        }
        return false;
    }
    
    public Set<TaskProperty> getProperties() {
        return properties;
    }
    
    public boolean hasProperty(TaskProperty property) {
        return this.properties.contains(property);
    }
    
    public boolean addProperty(TaskProperty property) {
        return this.properties.add(property);
    }
    
    public void mapped() {
        properties.stream().forEach(x -> x.mapped());
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
