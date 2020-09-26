package org.jojo.advp.base;

import java.util.Objects;

/**
 * Represents a person's property.
 * 
 * @author jojo
 * @version 0.9
 */
public class PersonProperty extends Property {
    
    /**
     * Creates a new PersonProperty with the given name, description and equivalence key.
     * 
     * @param name
     * @param description
     * @param equivalenceKey
     */
    public PersonProperty(String name, String description, EquivalenceKey equivalenceKey) {
        super(name,description,equivalenceKey);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), getName(), getDescription(), getEquivalenceKey());
    }
    
    @Override
    public boolean equals(Object other) {
        if(other != null && getClass().equals(other.getClass())) {
            final PersonProperty o = (PersonProperty)other;
            return  getName().equals(o.getName()) && 
                    getDescription().equals(o.getDescription()) &&
                    getEquivalenceKey().equals(o.getEquivalenceKey());
        }
        return false;
    }
}
