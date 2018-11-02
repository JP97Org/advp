package base;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Person {
    private final String name;
    private final Set<PersonProperty> properties;
    
    public Person(String name) {
        this.name = name;
        this.properties = new HashSet<PersonProperty>();
    }
    
    public Person(String name, Set<PersonProperty> properties) {
        this(name);
        this.properties.addAll(properties);
    }
    
    public String getName() {
        return name;
    }
    
    public Set<PersonProperty> getProperties() {
        return properties;
    }
    
    public boolean hasProperty(PersonProperty property) {
        return this.properties.contains(property);
    }
    
    public boolean addProperty(PersonProperty property) {
        return this.properties.add(property);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, properties);
    }
    
    @Override
    public boolean equals(Object other) {
        if(other != null && getClass() == other.getClass()) {
            final Person o = (Person)other;
            return  name.equals(o.name) && 
                    properties.equals(o.properties);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.name
                + " | " 
                + this.properties.stream().map(p -> p.toString()).reduce("", (s,t) -> s + " | " + t);
    }
}
