package org.jojo.advp.base;

import java.util.Objects;

/**
 * Represents a property.
 * 
 * @author jojo
 * @version 0.9
 */
public abstract class Property {
    private final String name;
    private final String description;
    private final EquivalenceKey equivalenceKey;
    
    /**
     * Constructor of property.
     * 
     * @param name - the property's name
     * @param description - the property's description
     * @param equivalenceKey - the property's equivalence key
     */
    public Property(String name, String description, EquivalenceKey equivalenceKey) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.equivalenceKey = Objects.requireNonNull(equivalenceKey);
    }
    
    /**
     * 
     * @return the property's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     * @return the property's description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 
     * @return the property's equivalence key
     */
    public EquivalenceKey getEquivalenceKey() {
        return equivalenceKey;
    }
    
    /**
     * 
     * @param otherEqKey - the given other key
     * @return whether this property's key is equivalent to the given other key
     */
    public final boolean fulfills(EquivalenceKey otherEqKey) {
        return this.equivalenceKey.isEquivalent(Objects.requireNonNull(otherEqKey));
    }
    
    /**
     * Calls mapped on the underlying equivalence key.
     */
    public void mapped() {
        getEquivalenceKey().mapped();
    }
    
    @Override
    public String toString() {
        return this.name    
                + " | " 
                + this.description
                + " | " 
                + this.equivalenceKey;
    }
}
