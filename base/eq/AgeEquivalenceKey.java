package base.eq;

import base.EquivalenceKey;
import base.PersonProperty;
import base.TaskProperty;

public class AgeEquivalenceKey implements EquivalenceKey {
    private static final String PROP_NAME = "age";
    private static final String PROP_DESC = "a comparison equivalence key for age values";
    
    private ComparisonEquivalenceKey<Integer> key;
    
    public AgeEquivalenceKey(int age, Comparison comparison) {
        this.key = new ComparisonEquivalenceKey<Integer>(IDs.INTERNAL, age, comparison);
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
        return getClass() == other.getClass() && key.isEquivalent(((AgeEquivalenceKey)other).key);
    }

}
