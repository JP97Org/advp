package base.eq;

import base.EquivalenceKey;
import base.PersonProperty;
import base.TaskProperty;

public class AgeEquivalenceKey implements EquivalenceKey {
    private ComparisonEquivalenceKey<Integer> key;
    
    public AgeEquivalenceKey(int age, Comparison comparison) {
        this.key = new ComparisonEquivalenceKey<Integer>(IDs.INTERNAL, age, comparison);
    }
    
    @Override
    public PersonProperty getPersonProperty() {
        return key.getPersonProperty();
    }

    @Override
    public TaskProperty getTaskProperty() {
        return key.getTaskProperty();
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
