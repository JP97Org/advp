package org.jojo.advp.base.factory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.PersonProperty;
import org.jojo.advp.base.TaskProperty;

public class PropertySetFactory {
    private final KeyPairFactory keyPairFactory;
    
    public PropertySetFactory(final KeyPairFactory keyPairFactory) {
        this.keyPairFactory = keyPairFactory;
    }
    
    public Set<PersonProperty> getPersonProperties() {
        final Set<PersonProperty> ret = new HashSet<>();
        
        this.keyPairFactory.getOfPersonKeys().forEach(x -> ret.add(x.getPersonProperty()));
        
        return ret;
    }
    
    public static Set<PersonProperty> getPersonProperties(final Set<EquivalenceKey> keys) {
        Objects.requireNonNull(keys);
        final Set<PersonProperty> ret = new HashSet<>();
        
        keys.forEach(x -> ret.add(x.getPersonProperty()));
        
        return ret;
    }
    
    public Set<TaskProperty> getTaskProperties() {
        final Set<TaskProperty> ret = new HashSet<>();
        
        this.keyPairFactory.getOfTaskKeys().forEach(x -> ret.add(x.getTaskProperty()));
        
        return ret;
    }
    
    public static Set<TaskProperty> getTaskProperties(final Set<EquivalenceKey> keys) {
        Objects.requireNonNull(keys);
        final Set<TaskProperty> ret = new HashSet<>();
        
        keys.forEach(x -> ret.add(x.getTaskProperty()));
        
        return ret;
    }
}
