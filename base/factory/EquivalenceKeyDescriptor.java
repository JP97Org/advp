package base.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import base.EquivalenceKey;
import base.eq.EquivalenceKeyDescription;

public class EquivalenceKeyDescriptor {
    private final Class<? extends EquivalenceKey> keyClass;
    private EquivalenceKey key;
    private final Object[] initargs;

    public EquivalenceKeyDescriptor(final Class<? extends EquivalenceKey> keyClass, final Class<?>[] parameterTypes,
            final Object... initargs) throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.keyClass = keyClass;
        this.key = keyClass.getConstructor(parameterTypes).newInstance(initargs);
        this.initargs = initargs;
    }

    public EquivalenceKeyDescriptor(final EquivalenceKeyDescription desc, final Object... initargs) {
        this.keyClass = desc.getEqKeyClass();
        try {
            this.key = this.keyClass.getConstructor(desc.getParamTypes()).newInstance(initargs);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            // should not happen, this would be an internal error!
        }
        this.initargs = initargs;
    }
    
    public EquivalenceKeyDescriptor(final EquivalenceKey key) {
        this.keyClass = key.getClass();
        this.key = key;
        this.initargs = null;
    }
    
    public EquivalenceKeyDescriptor(final EquivalenceKeyDescriptor toCopy) {
        this.keyClass = toCopy.keyClass;
        if (toCopy.initargs == null) {
            throw new IllegalArgumentException("not copiable desriptor!");
        }
        this.initargs = toCopy.initargs;
        try {
            final Iterator<?> iter = Arrays.stream(this.initargs).map(x -> x.getClass()).iterator();
            final List<Class<?>> paramTypes = new ArrayList<>();
            while (iter.hasNext()) {
                paramTypes.add((Class<?>)iter.next());
            }
            this.key = this.keyClass.getConstructor(paramTypes.toArray(new Class<?>[paramTypes.size()])).newInstance(initargs);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            // should not happen, this would be an internal error!
        }
    }

    public EquivalenceKey getKey() {
        return this.key;
    }
    
    @Override
    public String toString() {
        return this.key.toString();
    }
}
