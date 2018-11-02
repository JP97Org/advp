package base.eq;

import java.util.HashMap;
import java.util.Map;

import base.EquivalenceKey;

public class EqIDMap {
    private Map<EquivalenceKey, Integer> map;
    
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
