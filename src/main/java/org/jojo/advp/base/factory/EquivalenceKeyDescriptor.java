package org.jojo.advp.base.factory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.eq.EquivalenceKeyDescription;
import org.jojo.advp.base.eq.GenderEquivalenceKey;

/**
 * Represents a descriptor of an equivalence key so that a key can be directly extracted from an 
 * instance of this class.
 * 
 * @see {@link EquivalenceKeyDescription}
 * 
 * @author jojo
 * @version 0.9
 */
public class EquivalenceKeyDescriptor implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1354795253848716875L;
    private final Class<? extends EquivalenceKey> keyClass;
    private EquivalenceKey key;
    private final Object[] initargs;

    /**
     * Creates a new EquivalenceKeyDescriptor.
     * 
     * @param keyClass - the class of the key
     * @param parameterTypes - the parameter types
     * @param initargs - the initial arguments
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public EquivalenceKeyDescriptor(final Class<? extends EquivalenceKey> keyClass, final Class<?>[] parameterTypes,
            final Object... initargs) throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.keyClass = Objects.requireNonNull(keyClass);
        this.key = keyClass.getConstructor(Objects.requireNonNull(parameterTypes)).newInstance(Objects.requireNonNull(initargs));
        this.initargs = Objects.requireNonNull(initargs);
    }

    /**
     * Creates a new EquivalenceKeyDescriptor.
     * 
     * @param desc - an equivalence key description
     * @param initargs - initial arguments for the key
     * @see {@link EquivalenceKeyDescription}
     */
    public EquivalenceKeyDescriptor(final EquivalenceKeyDescription desc, final Object... initargs) {
        Objects.requireNonNull(desc);
        Objects.requireNonNull(initargs);
        this.keyClass = desc.getEqKeyClass();
        if (desc == EquivalenceKeyDescription.GENDER) {
            final String gStr = initargs[0].toString();
            this.key = GenderEquivalenceKey.of(gStr);
        } else {
            try {
                this.key = this.keyClass.getConstructor(desc.getParamTypes()).newInstance(initargs);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
                // should not happen, this would be an internal error!
            }
        }
        this.initargs = initargs;
    }
    
    /**
     * Creates a new EquivalenceKeyDescriptor.
     * 
     * @param key - an equivalence key
     */
    public EquivalenceKeyDescriptor(final EquivalenceKey key) {
        Objects.requireNonNull(key);
        this.keyClass = key.getClass();
        this.key = key;
        this.initargs = null;
    }
    
    /**
     * Creates a new EquivalenceKeyDescriptor.
     * 
     * @param toCopy - the key descriptor which should be copied
     */
    public EquivalenceKeyDescriptor(final EquivalenceKeyDescriptor toCopy) {
        Objects.requireNonNull(toCopy);
        this.keyClass = toCopy.keyClass;
        if (toCopy.initargs == null) {
            throw new IllegalArgumentException("not copiable descriptor!");
        }
        this.initargs = toCopy.initargs;
        if (this.keyClass.equals(GenderEquivalenceKey.class)) {
            final String gStr = initargs[0].toString();
            this.key = GenderEquivalenceKey.of(gStr);
        } else {
            try {
                final Iterator<?> iter = Arrays.stream(this.initargs).map(x -> x.getClass()).iterator();
                final List<Class<?>> paramTypes = new ArrayList<>();
                while (iter.hasNext()) {
                    final Class<?> next = (Class<?>)iter.next();
                    paramTypes.add(primitiveReplace(next));
                }
                this.key = this.keyClass.getConstructor(paramTypes.toArray(new Class<?>[paramTypes.size()])).newInstance(initargs);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
                // should not happen, this would be an internal error!
            }
        }
    }

    private static Class<?> primitiveReplace(final Class<?> cls) {
        if (cls.equals(Boolean.class)) {
            return boolean.class;
        }
        if (cls.equals(Character.class)) {
            return char.class;
        }
        if (cls.equals(Byte.class)) {
            return byte.class;
        }
        if (cls.equals(Short.class)) {
            return short.class;
        }
        if (cls.equals(Integer.class)) {
            return int.class;
        }
        if (cls.equals(Long.class)) {
            return long.class;
        }
        if (cls.equals(Float.class)) {
            return float.class;
        }
        if (cls.equals(Double.class)) {
            return double.class;
        }
        
        return cls;
    }

    /**
     * 
     * @return the key
     */
    public EquivalenceKey getKey() {
        return this.key;
    }
    
    @Override
    public String toString() {
        return this.key.toString();
    }
}
