package base.eq;

import base.EquivalenceKey;
import base.PersonProperty;
import base.TaskProperty;

public enum Gender implements EquivalenceKey {
    FEMALE, MALE, OTHER;
    
    public static final String FEMALE_STR = "f";
    public static final String MALE_STR = "m";
    public static final String OTHER_STR = "o";
    
    private static final int ID = 123456789;
    private static final String PROP_NAME = "gender";
    private static final String PROP_DESC = "the de-jure gender of a person";
    
    public static Gender of(final String genderStr) {
        switch(genderStr) {
            case FEMALE_STR: return FEMALE;
            case MALE_STR: return MALE;
            case OTHER_STR: return OTHER;
            default: return null; //TODO: evtl. noch EXC anstatt null zurueckgeben
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
        return ID;
    }
}
