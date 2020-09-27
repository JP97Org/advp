package org.jojo.advp.base.eq;

import java.util.List;
import java.util.Objects;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.PersonProperty;
import org.jojo.advp.base.TaskProperty;

/**
 * Represents a container equivalence key.
 * 
 * @author jojo
 * @version 0.9
 */
public class ContainerEquivalenceKey<T extends EquivalenceKey> implements EquivalenceKey {
    /**
     * 
     */
    private static final long serialVersionUID = 7853144961238607739L;
    private static final String PROP_NAME = "container";
    private static final String PROP_DESC = "a container equivalence key";
    
    private int id;
    private List<T> keys;
    private Operation operation;
    private int alternationIndex;
    private boolean freshlyInitialized;
    
    /**
     * Creates a new ContainerEquivalenceKey with the given id, keys and operation.
     * 
     * @param id - id must be the same as the id of T
     * @param keys - list of equivalence keys
     * @param operation
     */
    public ContainerEquivalenceKey(int id, List<T> keys, Operation operation) {
        //id must be the same as the id of T
        this.id = id;
        this.keys = Objects.requireNonNull(keys);
        this.operation = Objects.requireNonNull(operation);
        
        this.alternationIndex = 0;
        this.freshlyInitialized = true;
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
        return id;
    }

    @Override
    public boolean isEquivalent(EquivalenceKey other) {
        return isEquivalent(other, true);
    }
    
    private boolean isEquivalent(EquivalenceKey other, boolean isCheckingID) {
        if(other != null && (!isCheckingID || id == other.getID())) {
            boolean ret = true;
            
            if(getClass().equals(other.getClass())) { //other type is also a container
                if (id == other.getID()) {
                    final ContainerEquivalenceKey<?> o = (ContainerEquivalenceKey<?>)other;
                    switch(o.operation) {
                    case OR:
                        ret = o.keys.stream().map(k -> this.isEquivalent(k, false)).reduce(false, (a,b) -> a || b);
                        break;
                    case ALTERNATE:
                        throw new UnsupportedOperationException("Alternate on container-container comparison is not allowed");
                    case AND: //AND and default same case
                    default:
                        ret = o.keys.stream().map(k -> this.isEquivalent(k, false)).reduce(true, (a,b) -> a && b);
                        break;
                    }
                } else {
                    ret = false;
                }
            } else { //other type is a singular eq-key
                switch(operation) {
                case OR:
                    ret = keys.stream().map(k -> k.isEquivalent(other)).reduce(false, (a,b) -> a || b);
                    break;
                case ALTERNATE:
                    ret = alternateEquivalence(other);
                    break;
                case AND: //AND and default same case
                default:
                    ret = keys.stream().map(k -> k.isEquivalent(other)).reduce(true, (a,b) -> a && b);
                    break;
                }
            }
            
            return ret;
        }
        return false;
    }
    
    private boolean alternateEquivalence(final EquivalenceKey other) {
        if(this.freshlyInitialized) {
            final Boolean[] boolArr = this.keys.stream().map(k -> k.isEquivalent(other)).toArray(Boolean[]::new);
            for(int i = 0;i < boolArr.length;i++) {
                if(boolArr[i]) {
                    this.alternationIndex = i;
                    break;
                }
            }
            return this.keys.stream().map(k -> k.isEquivalent(other)).reduce(false, (a,b) -> a || b);
        } else {
            return this.keys.get(alternationIndex).isEquivalent(other);
        }
    }

    @Override
    public void mapped() {
        this.keys.forEach(x -> x.mapped());
        if(this.operation == Operation.ALTERNATE) {
            this.alternationIndex++;
            if(this.alternationIndex >= this.keys.size()) {
                this.alternationIndex -= this.keys.size();
            }
        }
        this.freshlyInitialized = false;
    }
    
    @Override
    public String toString() {
        return PROP_NAME + " | id= " + id + " | keys= " + keys + " | operation= " + operation; 
    }
}
