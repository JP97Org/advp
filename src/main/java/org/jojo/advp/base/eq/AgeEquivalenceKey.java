package org.jojo.advp.base.eq;

import java.util.Objects;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.PersonProperty;
import org.jojo.advp.base.TaskProperty;

/**
 * Represents a comparison equivalence key for age values.
 * 
 * @author jojo
 * @version 0.9
 */
public class AgeEquivalenceKey implements EquivalenceKey {
    /**
     * 
     */
    private static final long serialVersionUID = -9210263381806213149L;
    private static final String PROP_NAME = "age";
    private static final String PROP_DESC = "a comparison equivalence key for age values";
    
    private ComparisonEquivalenceKey<Integer> key;
    
    /**
     * Creates a new AgeEquivalenceKey with the given age and the given comparison.
     * 
     * @param age - the given age
     * @param comparison - the given comparison
     */
    public AgeEquivalenceKey(int age, Comparison comparison) {
        this.key = new ComparisonEquivalenceKey<Integer>(IDs.INTERNAL, Objects.requireNonNull(age), Objects.requireNonNull(comparison));
    }
    
    @Override
    public PersonProperty getPersonProperty() {
        return new PersonProperty(PROP_NAME, PROP_DESC, this);
    }

    @Override
    public TaskProperty getTaskProperty() {
        return new TaskProperty(PROP_NAME, PROP_DESC, this);
    }

    @Override
    public int getID() {
        return IDs.AGE;
    }

    @Override
    public boolean isEquivalent(EquivalenceKey other) {
        return other != null && getClass().equals(other.getClass()) && key.isEquivalent(((AgeEquivalenceKey)other).key);
    }
    
    @Override
    public String toString() {
        return PROP_NAME + " | " + key;
    }
}
