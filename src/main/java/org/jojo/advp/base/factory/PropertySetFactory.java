package org.jojo.advp.base.factory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.PersonProperty;
import org.jojo.advp.base.TaskProperty;

/**
 * Represents a factory for creation of property sets.
 * 
 * @author jojo
 * @version 0.9
 */
public class PropertySetFactory {
    private final KeyPairFactory keyPairFactory;
    
    /**
     * Creates a new property set factory.
     * 
     * @param keyPairFactory - the key pair factory from which the keys for the properties are taken
     */
    public PropertySetFactory(final KeyPairFactory keyPairFactory) {
        this.keyPairFactory = keyPairFactory;
    }
    
    /**
     * 
     * @return the person's properties
     */
    public Set<PersonProperty> getPersonProperties() {
        final Set<PersonProperty> ret = new HashSet<>();
        
        this.keyPairFactory.getOfPersonKeys().forEach(x -> ret.add(x.getPersonProperty()));
        
        return ret;
    }
    
    /**
     * 
     * @param keys - the set of keys
     * @return the person's properties containing the respective keys
     */
    public static Set<PersonProperty> getPersonProperties(final Set<EquivalenceKey> keys) {
        Objects.requireNonNull(keys);
        final Set<PersonProperty> ret = new HashSet<>();
        
        keys.forEach(x -> ret.add(x.getPersonProperty()));
        
        return ret;
    }
    
    /**
     * 
     * @return the task's properties
     */
    public Set<TaskProperty> getTaskProperties() {
        final Set<TaskProperty> ret = new HashSet<>();
        
        this.keyPairFactory.getOfTaskKeys().forEach(x -> ret.add(x.getTaskProperty()));
        
        return ret;
    }
    
    /**
     * 
     * @param keys - the set of keys
     * @return the task's properties containing the respective keys
     */
    public static Set<TaskProperty> getTaskProperties(final Set<EquivalenceKey> keys) {
        Objects.requireNonNull(keys);
        final Set<TaskProperty> ret = new HashSet<>();
        
        keys.forEach(x -> ret.add(x.getTaskProperty()));
        
        return ret;
    }
}
