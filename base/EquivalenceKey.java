package base;

public interface EquivalenceKey {
    public PersonProperty getPersonProperty();
    public TaskProperty getTaskProperty();
    public int getID();
    
    public boolean isEquivalent(EquivalenceKey other);
    
    /**
     * Can be overwritten by Properties which change behavior after mapping.
     */
    public default void mapped() {
        
    }
}
