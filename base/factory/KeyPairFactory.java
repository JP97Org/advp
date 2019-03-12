package base.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import base.EquivalenceKey;

public class KeyPairFactory {
    //TODO: this is a key-pair- factory for ONE person-task pair!
    
    private final List<EquivalenceKeyDescriptor> ofPersonKeys;
    private final List<EquivalenceKeyDescriptor> ofTaskKeys;
    
    public KeyPairFactory() {
        this.ofPersonKeys = new ArrayList<>();
        this.ofTaskKeys = new ArrayList<>();
    }
    
    public KeyPairFactory(final KeyPairFactory toCopy) {
        this.ofPersonKeys = Arrays.asList(toCopy.ofPersonKeys.stream().map(x -> new EquivalenceKeyDescriptor(x)).toArray(EquivalenceKeyDescriptor[]::new));
        this.ofTaskKeys = Arrays.asList(toCopy.ofTaskKeys.stream().map(x -> new EquivalenceKeyDescriptor(x)).toArray(EquivalenceKeyDescriptor[]::new));
    }
    
    public List<EquivalenceKey> getOfPersonKeys() {
        return Arrays.asList(this.ofPersonKeys.stream().map(x -> x.getKey()).toArray(EquivalenceKey[]::new));
    }
    
    public List<EquivalenceKey> getOfTaskKeys() {
        return Arrays.asList(this.ofTaskKeys.stream().map(x -> x.getKey()).toArray(EquivalenceKey[]::new));
    }
    
    public void generateKeyPair(final EquivalenceKeyDescriptor personKeyDesc, 
            final EquivalenceKeyDescriptor taskKeyDesc) {
        this.ofPersonKeys.add(personKeyDesc);
        this.ofTaskKeys.add(taskKeyDesc);
    }
    
    public String toString(final boolean bPerson) {
        return bPerson ? this.ofPersonKeys.toString() : this.ofTaskKeys.toString();
    }
    
    @Override
    public String toString() {
        return "ofPerson= " + toString(true) + "\nofTask= " + toString(false);
    }

    public void addKeyPairs(final KeyPairFactory other) {
        this.ofPersonKeys.addAll(other.ofPersonKeys);
        this.ofTaskKeys.addAll(other.ofTaskKeys);
    }
}
