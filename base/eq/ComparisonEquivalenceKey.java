package base.eq;

import base.EquivalenceKey;
import base.PersonProperty;
import base.TaskProperty;

public class ComparisonEquivalenceKey<T extends Comparable<T>> implements EquivalenceKey {
    private static final String PROP_NAME = "comparison";
    private static final String PROP_DESC = "an equivalence key which compares two values";
    
    private T value;
    private final Comparison comp;
    
    public ComparisonEquivalenceKey(final T value, Comparison comp) {
        this.value = value;
        this.comp = comp;
    }
    
    public T getValue() {
        return value;
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
        if(other != null && getClass() == other.getClass()) {
            final ComparisonEquivalenceKey<?> o = (ComparisonEquivalenceKey<?>)other;
            if(value.getClass() == o.value.getClass() && comp == o.comp.anti()) {
                return comp((T)o.value); //TODO: evtl. noch besser machen, falls moeglich
            }
        }
        return false;
    }
    
    private boolean comp(T otherValue) {
        final int comparison = this.value.compareTo(otherValue);
        switch(comp) {
            case GR: return comparison > 0;
            case SM: return comparison < 0;
            case GREQ: return comparison >= 0;
            case SMEQ: return comparison <= 0;
            default: return comparison == 0;
        }
    }
}
