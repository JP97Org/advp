package base;

import java.util.Objects;

public class PersonProperty extends Property {
    
    public PersonProperty(String name, String description, EquivalenceKey equivalenceKey) {
        super(name,description,equivalenceKey);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), getName(), getDescription(), getEquivalenceKey());
    }
    
    @Override
    public boolean equals(Object other) {
        if(other != null && getClass() == other.getClass()) {
            final PersonProperty o = (PersonProperty)other;
            return  getName().equals(o.getName()) && 
                    getDescription().equals(o.getDescription()) &&
                    getEquivalenceKey().equals(o.getEquivalenceKey());
        }
        return false;
    }
}
