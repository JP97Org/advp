package org.jojo.advp.base.eq;

import java.util.Objects;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.PersonProperty;
import org.jojo.advp.base.TaskProperty;

/**
 * Represents an equivalence key for the de-jure gender of a person.
 * 
 * @author jojo
 * @version 0.9
 */
public enum GenderEquivalenceKey implements EquivalenceKey {
    FEMALE, MALE, OTHER;
    
    public static final String FEMALE_STR = "f";
    public static final String MALE_STR = "m";
    public static final String OTHER_STR = "o";
    
    private static final String PROP_NAME = "gender";
    private static final String PROP_DESC = "the de-jure gender of a person";
    
    /**
     * Gets the respective key.
     *
     * @param genderStr - the gender string
     * @return the respective key
     */
    public static GenderEquivalenceKey of(final String genderStr) {
        Objects.requireNonNull(genderStr);
        switch(genderStr) {
            case FEMALE_STR: return FEMALE;
            case MALE_STR: return MALE;
            case OTHER_STR: return OTHER;
            default: throw new IllegalArgumentException("only f,m,o are allowed!");
        }
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
    public boolean isEquivalent(EquivalenceKey other) {
        return equals(other);
    }

    @Override
    public int getID() {
        return IDs.GENDER;
    }
}
