package org.jojo.advp.base.eq;

import java.util.HashMap;
import java.util.Map;

import org.jojo.advp.base.EquivalenceKey;

/**
 * Represents a map for equivalence keys. It can be used to store ids, so that there are unique.
 * 
 * @author jojo
 * @version 0.9
 */
public class EqIDMap {
    private final Map<EquivalenceKey, Integer> map;
    
    public EqIDMap() {
        map = new HashMap<>();
    }
    
    public Integer put(EquivalenceKey key) {
        return map.put(key, key.getID());
    }
    
    public Integer get(EquivalenceKey key) {
        return map.get(key);
    }
    
    public boolean contains(EquivalenceKey key) {
        return map.containsKey(key);
    }
    
    public boolean contains(int id) {
        return map.containsValue(id);
    }
}
