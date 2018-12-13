package base.eq;

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
    
    private final BiPredicate<P,T> biPredicate;
    
    private final boolean ofPerson;
    
    private LambdaEquivalenceKey(final int id, final P valueP, final T valueT, final BiPredicate<P,T> biPredicate) {
        this.id = id;
        this.valueP = valueP;
        this.valueT = valueT;
        this.ofPerson = valueP != null;
        this.biPredicate = biPredicate;
    }
    
    /**
     * Creates a new person's LEK with the given id, the person's LEK's value and the bi-predicate.
     * 
     * @param id
     * @param valueP
     * @param biPredicate
     */
    public LambdaEquivalenceKey(final int id, final P valueP, final BiPredicate<P,T> biPredicate) {
        this(id, valueP, null, biPredicate);
    }
    
    /**
     * Creates a new tasks's LEK with the task's LEK's value, the given id and the bi-predicate.
     * 
     * @param valueT
     * @param id
     * @param biPredicate
     */
    public LambdaEquivalenceKey(final T valueT, final int id, final BiPredicate<P,T> biPredicate) {
        this(id, null, valueT, biPredicate);
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
                    try {
                        valT = (T) o.valueT;
                    } catch(ClassCastException exc) {
                        return false; //TODO vllt. auch EXC oder sowieso ganz anders machen, das ist so sehr unschoen
                    }
                } else {
                    try {
                        valP = (P) o.valueP;
                    } catch(ClassCastException exc) {
                        return false; //TODO vllt. auch EXC oder sowieso ganz anders machen, das ist so sehr unschoen
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

}
