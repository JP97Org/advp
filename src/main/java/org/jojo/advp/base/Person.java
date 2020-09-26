package org.jojo.advp.base;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jojo.advp.base.eq.TimeEquivalenceKey;
import org.jojo.advp.base.eq.TimeInterval;

/**
 * Represents a person in the broadest sense of the word, i.e. an (sub/ob)ject with a name and
 * a set of properties.
 * 
 * @author jojo
 * @version 0.9
 */
public class Person {
    private final String name;
    private final Set<PersonProperty> properties;
    
    /**
     * Creates a new Person without properties.
     * 
     * @param name - the person's name
     */
    public Person(final String name) {
        this.name = Objects.requireNonNull(name);
        this.properties = new HashSet<PersonProperty>();
    }
    
    /**
     * Creates a new Person with the given properties.
     * 
     * @param name - the person's name
     * @param properties - the person's properties
     */
    public Person(final String name, final Set<PersonProperty> properties) {
        this(name);
        this.properties.addAll(Objects.requireNonNull(properties));
    }
    
    /**
     * 
     * @return person's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     * @return person's properties
     */
    public Set<PersonProperty> getProperties() {
        return properties;
    }
    
    /**
     * 
     * @param property - the given property
     * @return whether the person has the given property
     */
    public boolean hasProperty(PersonProperty property) {
        return this.properties.contains(Objects.requireNonNull(property));
    }
    
    /**
     * Adds the given property to this person's properties.
     * 
     * @param property - the given properties
     * @return whether the adding was successful, i.e. the property was not formerly contained
     */
    public boolean addProperty(PersonProperty property) {
        return this.properties.add(Objects.requireNonNull(property));
    }
    
    /**
     * If ti != null and this person has an TimeEquivalenceKey's PersonProperty,
     * maps the given TimeInterval, i.e. calls TimeEquivalenceKey's mapped(TimeInterval ti) method.
     * 
     * @param ti - the given TimeInterval
     */
    public void mapped(final TimeInterval ti) {
        if(ti != null) {
            final TimeEquivalenceKey key = (TimeEquivalenceKey) this.properties.stream()
                .filter(x -> x.getEquivalenceKey().getClass().equals(TimeEquivalenceKey.class))
                .map(x -> x.getEquivalenceKey())
                .iterator().next();
            if(key != null) {
                key.mapped(ti);
            }
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, properties);
    }
    
    @Override
    public boolean equals(Object other) {
        if(other != null && getClass().equals(other.getClass())) {
            final Person o = (Person)other;
            return  name.equals(o.name) && 
                    properties.equals(o.properties);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.name;/*
                + " | " 
                + this.properties.stream().map(p -> p.toString()).reduce("", (s,t) -> s + " | " + t);*/
    }
}
