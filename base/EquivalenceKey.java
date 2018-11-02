package base;

public interface EquivalenceKey {
    public PersonProperty getPersonProperty();
    public TaskProperty getTaskProperty();
    public int getID();
    
    public boolean isEquivalent(EquivalenceKey other);
}
