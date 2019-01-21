package base.factory;

import java.util.ArrayList;
import java.util.List;

import base.EquivalenceKey;

public class KeyPairFactory {
    //TODO: this is a key-pair- factory for ONE person-task pair!
    
    private final List<EquivalenceKey> ofPersonKeys;
    private final List<EquivalenceKey> ofTaskKeys;
    
    public KeyPairFactory() {
        this.ofPersonKeys = new ArrayList<>();
        this.ofTaskKeys = new ArrayList<>();
    }
    
    public List<EquivalenceKey> getOfPersonKeys() {
        return new ArrayList<>(ofPersonKeys);
    }
    
    public List<EquivalenceKey> getOfTaskKeys() {
        return new ArrayList<>(ofTaskKeys);
    }
    
    public void generateKeyPair(final EquivalenceKeyDescriptor personKeyDesc, 
            final EquivalenceKeyDescriptor taskKeyDesc) {
        this.ofPersonKeys.add(personKeyDesc.getKey());
        this.ofTaskKeys.add(taskKeyDesc.getKey());
    }
}
