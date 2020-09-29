package org.jojo.advp.base.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.eq.EquivalenceKeyDescription;
import org.jojo.advp.base.eq.Operation;

/**
 * Represents a factory for creation of a key pair for one person-task pair.
 * 
 * @author jojo
 * @version 0.9
 */
public class KeyPairFactory implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -61897893286443638L;
    private final List<EquivalenceKeyDescriptor> ofPersonKeys;
    private final List<EquivalenceKeyDescriptor> ofTaskKeys;
    
    /**
     * Creates a new empty key pair factory.
     */
    public KeyPairFactory() {
        this.ofPersonKeys = new ArrayList<>();
        this.ofTaskKeys = new ArrayList<>();
    }
    
    /**
     * Copies the given key pair factory.
     * 
     * @param toCopy - the factory to copy
     */
    public KeyPairFactory(final KeyPairFactory toCopy) {
        Objects.requireNonNull(toCopy);
        this.ofPersonKeys = Arrays.asList(toCopy.ofPersonKeys.stream().map(x -> new EquivalenceKeyDescriptor(x)).toArray(EquivalenceKeyDescriptor[]::new));
        this.ofTaskKeys = Arrays.asList(toCopy.ofTaskKeys.stream().map(x -> new EquivalenceKeyDescriptor(x)).toArray(EquivalenceKeyDescriptor[]::new));
    }
    
    /**
     * Creates a key pair factory and adds the given keys to the person's key list or to the task's key list.
     * 
     * @param bPerson - whether the keys should be added to the person's key list
     * @param keys - the given keys
     */
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
    
    /**
     * 
     * @return the person's keys
     */
    public List<EquivalenceKey> getOfPersonKeys() {
        return Arrays.asList(this.ofPersonKeys.stream().map(x -> x.getKey()).toArray(EquivalenceKey[]::new));
    }
    
    /**
     * 
     * @return the task's keys
     */
    public List<EquivalenceKey> getOfTaskKeys() {
        return Arrays.asList(this.ofTaskKeys.stream().map(x -> x.getKey()).toArray(EquivalenceKey[]::new));
    }
    
    /**
     * Generates a key pair from the given key descriptors.
     * 
     * @param personKeyDesc - the person's key's descriptor
     * @param taskKeyDesc - the task's key's descriptor
     * @see {@link EquivalenceKeyDescriptor}
     */
    public void generateKeyPair(final EquivalenceKeyDescriptor personKeyDesc, 
            final EquivalenceKeyDescriptor taskKeyDesc) {
        this.ofPersonKeys.add(Objects.requireNonNull(personKeyDesc));
        this.ofTaskKeys.add(Objects.requireNonNull(taskKeyDesc));
    }
    
    /**
     * 
     * @param bPerson - whether the representation should show the person's keys
     * @return a string representation of the person's or the task's keys
     */
    public String toString(final boolean bPerson) {
        return bPerson ? this.ofPersonKeys.toString() : this.ofTaskKeys.toString();
    }
    
    @Override
    public String toString() {
        return "ofPerson= " + toString(true) + "\nofTask= " + toString(false);
    }

    /**
     * Adds the key pairs of the other factory to this one.
     * 
     * @param other - the other factory
     */
    public void addKeyPairs(final KeyPairFactory other) {
        Objects.requireNonNull(other);
        this.ofPersonKeys.addAll(other.ofPersonKeys);
        this.ofTaskKeys.addAll(other.ofTaskKeys);
    }

    /**
     * Creates a container key of the keys of this factory with the given operation.
     * 
     * @param id - the id of the resulting container key
     * @param bPerson - whether the person's key list should be containered
     * @param op - the given operation
     */
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

    /**
     * Removes the key at the given index from the respective key list.
     * 
     * @param bPerson - whether the key should be removed from the person's key list
     * @param innerIndex - the given index
     */
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
