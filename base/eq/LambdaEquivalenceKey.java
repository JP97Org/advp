package base.eq;

import java.util.Objects;
import java.util.function.BiPredicate;

import base.EquivalenceKey;
import base.PersonProperty;
import base.TaskProperty;

/**
 * Represents an equivalence key for equivalent values according to a lambda BiPredicate.
 * 
 * @author jojo
 * @version 0.9
 *
 * @param <P> - the person's LEK part type
 * @param <T> - the task's LEK part type
 */
public class LambdaEquivalenceKey<P,T> implements EquivalenceKey {
    private static final String PROP_NAME = "lambda";
    private static final String PROP_DESC = "an equivalence key for equivalent values according to a lambda BiPredicate";
    
    private final int id;
    
    private final P valueP;
    private final T valueT;
    
    private final Class<?> oValClass;
    
    private final BiPredicate<P,T> biPredicate;
    
    private final boolean ofPerson;
    
    private LambdaEquivalenceKey(final int id, final P valueP, final T valueT, final BiPredicate<P,T> biPredicate, final Class<?> oValClass) {
        this.id = id;
        this.valueP = valueP;
        this.valueT = valueT;
        this.ofPerson = valueP != null;
        this.biPredicate = biPredicate;
        this.oValClass = Objects.requireNonNull(oValClass);
    }
    
    /**
     * Creates a new person's LEK with the given id, the person's LEK's value and the bi-predicate.
     * 
     * @param id
     * @param valueP
     * @param biPredicate
     * @param oValClass - the other val's class
     */
    public LambdaEquivalenceKey(final int id, final P valueP, final BiPredicate<P,T> biPredicate, final Class<?> oValClass) {
        this(id, Objects.requireNonNull(valueP), null, biPredicate, oValClass);
    }
    
    /**
     * Creates a new tasks's LEK with the task's LEK's value, the given id and the bi-predicate.
     * 
     * @param valueT
     * @param id
     * @param biPredicate
     * @param oValClass - the other val's class
     */
    public LambdaEquivalenceKey(final T valueT, final int id, final BiPredicate<P,T> biPredicate, final Class<?> oValClass) {
        this(id, null, Objects.requireNonNull(valueT), biPredicate, oValClass);
    }
    
    @Override
    public PersonProperty getPersonProperty() {
        if (ofPerson) {
            return new PersonProperty(PROP_NAME, PROP_DESC, this);
        }
        else {
            throw new UnsupportedOperationException("This equivalence key is only for tasks!");
        }
    }

    @Override
    public TaskProperty getTaskProperty() {
        if(!ofPerson) {
            return new TaskProperty(PROP_NAME, PROP_DESC, this);
        }
        else {
            throw new UnsupportedOperationException("This equivalence key is only for persons!");
        }
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public boolean isEquivalent(final EquivalenceKey other) {
        if (other != null && getClass().equals(other.getClass()) && getID() == other.getID()) {
            final LambdaEquivalenceKey<?,?> o = (LambdaEquivalenceKey<?,?>) other;
            final boolean correct = this.ofPerson != o.ofPerson;
            if (correct) {
                final P valP;
                final T valT;
                if (this.ofPerson) {
                    valP = this.valueP;
                    if (this.oValClass.isInstance(o.valueT) && o.oValClass.isInstance(this.valueP)) {
                        valT = (T) o.valueT;
                    } else {
                        return false;
                    }
                } else {
                    if (this.oValClass.isInstance(o.valueP) && o.oValClass.isInstance(this.valueT)) {
                        valP = (P) o.valueP;
                    } else {
                        return false;
                    }
                    valT = this.valueT;
                }
                
                return biPredicate.test(valP, valT);
            } else {
                throw new IllegalArgumentException("Wrong comparison p<-->p or t<-->t !");
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return PROP_NAME + " | id= " + id + " | value= " + (ofPerson ? valueP : valueT) + " |biPredicate= " + biPredicate; 
    }
}
