package org.jojo.advp.base.eq;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * Represents an operation for the ContainerEquivalenceKey.
 * 
 * @author jojo
 * @version 0.9
 */
public enum Operation implements Serializable {
    OR,ALTERNATE,AND;
    
    public static Operation of(final String str) {
        Objects.requireNonNull(str);
        final Iterator<Operation> iter = Arrays.stream(values()).filter(x -> x.name().equals(str)).iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }
}
