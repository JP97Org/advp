package base;

import java.util.Objects;

public abstract class Property {
    private final String name;
    private final String description;
    private final EquivalenceKey equivalenceKey;
    
    public Property(String name, String description, EquivalenceKey equivalenceKey) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.equivalenceKey = Objects.requireNonNull(equivalenceKey);
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public EquivalenceKey getEquivalenceKey() {
        return equivalenceKey;
    }
    
    public final boolean fulfills(EquivalenceKey otherEqKey) {
        return this.equivalenceKey.isEquivalent(otherEqKey);
    }
    
    public void mapped() {
        getEquivalenceKey().mapped();
    }
    
    @Override
    public String toString() {
        return this.name    
                + " | " 
                + this.description
                + " | " 
                + this.equivalenceKey;
    }
}
