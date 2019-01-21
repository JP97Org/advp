package base.eq;

import base.EquivalenceKey;

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
}
