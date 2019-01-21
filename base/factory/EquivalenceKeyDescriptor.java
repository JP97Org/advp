package base.factory;

import java.lang.reflect.InvocationTargetException;

import base.EquivalenceKey;
import base.eq.EquivalenceKeyDescription;

public class EquivalenceKeyDescriptor {
    private final Class<? extends EquivalenceKey> keyClass;
    private EquivalenceKey key;

    public EquivalenceKeyDescriptor(final Class<? extends EquivalenceKey> keyClass, final Class<?>[] parameterTypes,
            final Object... initargs) throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.keyClass = keyClass;
        this.key = keyClass.getConstructor(parameterTypes).newInstance(initargs);
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
    }
    
    public EquivalenceKeyDescriptor(final EquivalenceKey key) {
        this.keyClass = key.getClass();
        this.key = key;
    }

    public EquivalenceKey getKey() {
        return this.key;
    }
}
