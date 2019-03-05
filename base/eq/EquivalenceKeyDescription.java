package base.eq;

import base.EquivalenceKey;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public enum EquivalenceKeyDescription {
    AGE (AgeEquivalenceKey.class, int.class, Comparison.class),
    //TODO bei den generics noch schauen wie das besser geht
    COMPARISON (ComparisonEquivalenceKey.class, int.class, Comparable.class, Comparison.class),
    CONTAINER (ContainerEquivalenceKey.class, int.class, List.class, Operation.class),
    EQUAL (EqualEquivalenceKey.class, int.class, Object.class),
    LAMBDA_PERSON (LambdaEquivalenceKey.class, int.class, Object.class, BiPredicate.class, Class.class),
    LAMBDA_TASK (LambdaEquivalenceKey.class, Object.class, int.class, BiPredicate.class, Class.class),
    TIME_PERSON (TimeEquivalenceKey.class, Set.class),
    TIME_TASK (TimeEquivalenceKey.class, TimeInterval.class)
    ;
    
    private final Class<? extends EquivalenceKey> keyClass;
    private final Class<?>[] parameterTypes;
    
    private CreationHint[] creationHints;
    
    EquivalenceKeyDescription(final Class<? extends EquivalenceKey> keyClass, 
            final Class<?>... parameterTypes) {
        this.keyClass = keyClass;
        this.parameterTypes = parameterTypes;
    }
    
    public Class<? extends EquivalenceKey> getEqKeyClass() {
        return this.keyClass;
    }
    
    public Class<?>[] getParamTypes() {
        return this.parameterTypes;
    }
    
    public static EquivalenceKeyDescription ofArgs(final String args, final String delim) {
        final String[] splitted = args.split(delim);
        final String descName = splitted[0];
        EquivalenceKeyDescription ret = null;
        for (final EquivalenceKeyDescription desc : EquivalenceKeyDescription.values()) {
            if (desc.name().equals(descName)) {
                ret = desc;
                break;
            }
        }
        if (ret != null) {
            ret.creationHints = new CreationHint[splitted.length - 1];
            for (int i = 1; i < splitted.length; i++) {
                final int k = i;
                final CreationHint hint = Arrays.stream(CreationHint.values()).filter(x -> x.name().equals(splitted[k])).iterator().next();
                if (hint == null) {
                    throw new IllegalArgumentException("unknown hint \"" + splitted[k] + "\"!");
                }
                ret.creationHints[i - 1] = hint;
            }
        }
        return ret;
    }

    public Object[] createInitArgs(final String[] argsStr) {
        // TODO Auto-generated method stub
        return null;
    }
    
    private enum CreationHint {
        STR,
        INT,
        COMP,
        LIST_STR,
        LIST_INT,
        OP,
        LAMBDA, //TODO: evtl. genauere hints
        HASH_SET_TI,
        TI;
    }
}
