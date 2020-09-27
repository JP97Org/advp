package org.jojo.advp.base;

import java.io.Serializable;

/**
 * Represents an equivalence-key, i.e. a key for representing fitting/not-fitting task and person properties.
 * 
 * @author jojo
 * @version 0.9
 */
public interface EquivalenceKey extends Serializable {
    /**
     * 
     * @return the respective person property of this key.
     */
    public PersonProperty getPersonProperty();
    
    /**
     * 
     * @return the respective task property of this key
     */
    public TaskProperty getTaskProperty();
    
    /**
     * 
     * @return the unique id of this equivalence key (programmer must ensure its uniqueness)!
     */
    public int getID();
    
    /**
     * Note that this method is NOT an equivalence relation! 
     * It rather means equivalence in the sense of fitting together!
     * 
     * @param other - other equivalenceKey
     * @return whether this key fits to the other one
     */
    public boolean isEquivalent(EquivalenceKey other);
    
    /**
     * Can be overwritten by Properties which change behavior after mapping.
     */
    public default void mapped() {
        
    }
}
