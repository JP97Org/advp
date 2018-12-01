package base.eq;

import java.util.List;
import java.util.Objects;

import base.EquivalenceKey;
import base.PersonProperty;
import base.TaskProperty;

public class ContainerEquivalenceKey<T extends EquivalenceKey> implements EquivalenceKey {
    private static final String PROP_NAME = "container";
    private static final String PROP_DESC = "a container equivalence key";
    
    private int id;
    private List<T> keys;
    private Operation operation;
    private int alternationIndex;
    
    public ContainerEquivalenceKey(int id, List<T> keys, Operation operation) {
        //id must be the same as the id of T
        this.id = id;
        this.keys = Objects.requireNonNull(keys);
        this.operation = operation;
        
        this.alternationIndex = 0;
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
        if(id == other.getID()) {
            boolean ret = true;
            
            if(getClass() == other.getClass()) { //other type is also a container
                //TODO: evtl. auch nicht akzeptieren
                final ContainerEquivalenceKey<?> o = (ContainerEquivalenceKey<?>)other;
                ret = o.keys.stream().map(k -> this.isEquivalent(k)).reduce(false, (a,b) -> a || b);
            } else { //other type is a singular eq-key
                switch(operation) {
                case OR:
                    ret = keys.stream().map(k -> k.isEquivalent(other)).reduce(false, (a,b) -> a || b);
                    break;
                case ALTERNATE:
                    ret = keys.get(alternationIndex).isEquivalent(other);
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
    
    @Override
    public void mapped() {
        if(operation == Operation.ALTERNATE) {
            alternationIndex++;
            if(alternationIndex >= keys.size()) {
                alternationIndex -= keys.size();
            }
        }
    }

}
