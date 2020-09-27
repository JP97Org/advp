package org.jojo.advp.base.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.eq.EquivalenceKeyDescription;
import org.jojo.advp.base.eq.Operation;

public class KeyPairFactory {
    //TODO: this is a key-pair- factory for ONE person-task pair!
    
    private final List<EquivalenceKeyDescriptor> ofPersonKeys;
    private final List<EquivalenceKeyDescriptor> ofTaskKeys;
    
    public KeyPairFactory() {
        this.ofPersonKeys = new ArrayList<>();
        this.ofTaskKeys = new ArrayList<>();
    }
    
    public KeyPairFactory(final KeyPairFactory toCopy) {
        Objects.requireNonNull(toCopy);
        this.ofPersonKeys = Arrays.asList(toCopy.ofPersonKeys.stream().map(x -> new EquivalenceKeyDescriptor(x)).toArray(EquivalenceKeyDescriptor[]::new));
        this.ofTaskKeys = Arrays.asList(toCopy.ofTaskKeys.stream().map(x -> new EquivalenceKeyDescriptor(x)).toArray(EquivalenceKeyDescriptor[]::new));
    }
    
    public KeyPairFactory(final boolean bPerson, final List<EquivalenceKey> keys) {
        this();
        if (bPerson) {
            this.ofPersonKeys.addAll(keys.stream()
                    .map(x -> new EquivalenceKeyDescriptor(x))
                    .collect(Collectors.toList()));
        } else {
            this.ofTaskKeys.addAll(keys.stream()
                    .map(x -> new EquivalenceKeyDescriptor(x))
                    .collect(Collectors.toList()));
        }
    }
    
    public List<EquivalenceKey> getOfPersonKeys() {
        return Arrays.asList(this.ofPersonKeys.stream().map(x -> x.getKey()).toArray(EquivalenceKey[]::new));
    }
    
    public List<EquivalenceKey> getOfTaskKeys() {
        return Arrays.asList(this.ofTaskKeys.stream().map(x -> x.getKey()).toArray(EquivalenceKey[]::new));
    }
    
    public void generateKeyPair(final EquivalenceKeyDescriptor personKeyDesc, 
            final EquivalenceKeyDescriptor taskKeyDesc) {
        this.ofPersonKeys.add(Objects.requireNonNull(personKeyDesc));
        this.ofTaskKeys.add(Objects.requireNonNull(taskKeyDesc));
    }
    
    public String toString(final boolean bPerson) {
        return bPerson ? this.ofPersonKeys.toString() : this.ofTaskKeys.toString();
    }
    
    @Override
    public String toString() {
        return "ofPerson= " + toString(true) + "\nofTask= " + toString(false);
    }

    public void addKeyPairs(final KeyPairFactory other) {
        Objects.requireNonNull(other);
        this.ofPersonKeys.addAll(other.ofPersonKeys);
        this.ofTaskKeys.addAll(other.ofTaskKeys);
    }

    public void container(int id, final boolean bPerson, final Operation op) {
        Objects.requireNonNull(op);
        if (bPerson) {
            container(id, this.ofPersonKeys, op);
        } else {
            container(id, this.ofTaskKeys, op);
        }
    }

    private void container(final int id, final List<EquivalenceKeyDescriptor> keysDesc, final Operation op) {
        final List<EquivalenceKey> keys = Arrays.asList(keysDesc.stream().map(x -> x.getKey()).toArray(EquivalenceKey[]::new));
        keysDesc.clear();
        final Object[] initargs = {id, keys, op};
        final EquivalenceKeyDescriptor containerDesc = new EquivalenceKeyDescriptor(EquivalenceKeyDescription.CONTAINER, initargs);
        keysDesc.add(containerDesc);
    }

    public void remove(final boolean bPerson, int innerIndex) {
        if (bPerson) {
            if(innerIndex >= this.ofPersonKeys.size()) {
                throw new IllegalArgumentException("indexOutOfBounds with index(inner)= " + innerIndex + " | size= " + this.ofPersonKeys.size());
            }
            this.ofPersonKeys.remove(innerIndex);
        } else {
            if(innerIndex >= this.ofTaskKeys.size()) {
                throw new IllegalArgumentException("indexOutOfBounds with index(inner)= " + innerIndex + " | size= " + this.ofTaskKeys.size());
            }
            this.ofTaskKeys.remove(innerIndex);
        }
    }
}
