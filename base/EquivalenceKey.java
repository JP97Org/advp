package base;

public interface EquivalenceKey {
    public PersonProperty getPersonProperty();
    public TaskProperty getTaskProperty();
    
    public boolean isEquivalent(EquivalenceKey other);
}
