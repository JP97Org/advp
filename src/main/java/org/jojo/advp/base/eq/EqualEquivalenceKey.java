package org.jojo.advp.base.eq;

import java.util.Objects;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.PersonProperty;
import org.jojo.advp.base.TaskProperty;

/**
 * Represents an equivalence key for equivalent values.
 * 
 * @author jojo
 * @version 0.9
 */
public class EqualEquivalenceKey<T> implements EquivalenceKey {
    /**
     * 
     */
    private static final long serialVersionUID = 5061019802084351161L;
    private static final String PROP_NAME = "equal";
    private static final String PROP_DESC = "an equivalence key for equivalent values";
    
    private int id;
    
    private final T value;
    
    /**
     * Creates a new EqualEquivalenceKey with the given id and the given constant.
     * 
     * @param id
     * @param constant
     */
    public EqualEquivalenceKey(final int id, final T constant) {
        this.id = id;
        this.value = Objects.requireNonNull(constant);
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
    public int hashCode() {
        return Objects.hash(id, value.getClass(), value.hashCode());
    }
    
    @Override
    public boolean equals(Object other) {
        if(other != null && getClass().equals(other.getClass())) {
            final EqualEquivalenceKey<?> o = (EqualEquivalenceKey<?>)other;
            return value.getClass().equals(o.value.getClass()) && id == o.id && value.equals(o.value);
        }
        return false;
    }

    @Override
    public int getID() {
        return id;
    }
    
    @Override
    public boolean isEquivalent(EquivalenceKey other) {
        return equals(other);
    }
    
    @Override
    public String toString() {
        return PROP_NAME + " | id= " + id + " | value= " + value; 
    }
}
