package base.eq;

import base.EquivalenceKey;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

public enum EquivalenceKeyDescription {
    AGE (AgeEquivalenceKey.class, int.class, Comparison.class),
    //TODO bei den generics noch schauen wie das besser geht, evtl. dann eh mit hints machen
    COMPARISON (ComparisonEquivalenceKey.class, int.class, Comparable.class, Comparison.class),
    CONTAINER (ContainerEquivalenceKey.class, int.class, List.class, Operation.class),
    EQUAL (EqualEquivalenceKey.class, int.class, Object.class),
    LAMBDA_PERSON (LambdaEquivalenceKey.class, int.class, Object.class, BiPredicate.class, Class.class),
    LAMBDA_TASK (LambdaEquivalenceKey.class, Object.class, int.class, BiPredicate.class, Class.class),
    TIME_PERSON (TimeEquivalenceKey.class, Set.class),
    TIME_TASK (TimeEquivalenceKey.class, TimeInterval.class)
    ;
    
    private static final String DELIM_LIST_OR_SET = ","; //TODO: better constant and delim management
    private static final String DELIM_TI = "-";
    
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
                ret.creationHints[k - 1] = hint;
            }
        }
        return ret;
    }

    public Object[] createInitArgs(final String[] argsStr) {
        if (argsStr.length == this.creationHints.length) {
            final int length = argsStr.length;
            final Object[] ret = new Object[length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = this.creationHints[i].transform(argsStr[i]);
            }
            if (Arrays.stream(ret).noneMatch(x -> x == null)) {
                boolean correct = ret.length == parameterTypes.length;
                for (int i = 0;i < ret.length && correct; i++) {
                    correct = parameterTypes[i].isInstance(ret[i]) || primitiveCheck(parameterTypes[i], ret[i]);
                }
                if (correct) {
                    return ret; 
                }
            }
        } else if (this.creationHints.length == 0){
            //creation without hints
            switch(this) {
            case AGE: this.creationHints = new CreationHint[]{CreationHint.INT,CreationHint.COMP}; break;
            case COMPARISON: this.creationHints = new CreationHint[]{CreationHint.INT, CreationHint.INT,CreationHint.COMP}; break; //assuming int comparison
            case TIME_PERSON: this.creationHints = new CreationHint[] {CreationHint.HASH_SET_TI};
            case TIME_TASK: this.creationHints = new CreationHint[] {CreationHint.TI};
            default: break;
            }
            if (argsStr.length == this.creationHints.length) {
                return createInitArgs(argsStr);
            }
        }
        return null;
    }
    
    private static boolean primitiveCheck(Class<?> cls, Object obj) {
        boolean ret = cls.isPrimitive();
        
        if (ret) {
            Class<?> checkClass = null;
            switch (cls.getName()) {
            case "boolean": checkClass = Boolean.class; break;
            case "char": checkClass = Character.class; break;
            case "byte": checkClass = Byte.class; break;
            case "short": checkClass = Short.class; break;
            case "int": checkClass = Integer.class; break;
            case "long": checkClass = Long.class; break;
            case "float": checkClass = Float.class; break;
            case "double": checkClass = Double.class; break;
            default: checkClass = null; assert false; break;
            }
            ret = checkClass != null && checkClass.isInstance(obj);
        }
        
        return ret;
    }

    private enum CreationHint {
        STR (s -> s),
        INT (s -> {
            try {
                return Integer.parseInt(s); 
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
            }
            }),
        COMP (s -> Comparison.of(s)),
        LIST_STR (s -> Arrays.asList(s.split(DELIM_LIST_OR_SET))),
        LIST_INT (s -> Arrays.asList(s.split(DELIM_LIST_OR_SET)).stream().mapToInt(x -> {
            try {
                return Integer.parseInt(x); 
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
            }
            }).toArray()),
        OP (s -> Operation.of(s)),
        LAMBDA (s -> null), //TODO: lambdas unterstuetzen! --> wahrscheins mit eigenem abgespeckten compiler oder so, oder lib laden mit den entsprechenden objekten und dem vergleichslamda
        HASH_SET_TI (s -> new HashSet<TimeInterval>(
                Arrays.asList(
                        Arrays.stream(s.split(DELIM_LIST_OR_SET)).map(x -> {
                            try {
                                return tiSplit(x);
                            } catch (ParseException e) {
                                throw new IllegalArgumentException(e.getMessage());
                            }
                        }).toArray(TimeInterval[]::new)))),
        TI(s -> {
            try {
                return tiSplit(s);
            } catch (ParseException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        });
        
        private final Function<String, Object> transform;
        
        CreationHint(final Function<String, Object> transform) {
            this.transform = transform;
        }
        
        Object transform(final String input) {
            return this.transform.apply(input);
        }
        
        private static TimeInterval tiSplit(final String s) throws ParseException {
            final String[] splt = s.split(DELIM_TI);
            final String fromStr = splt[0];
            final String toStr = splt[1];
            final Date from = DateFormat.getDateInstance().parse(fromStr);
            final Date to = DateFormat.getDateInstance().parse(toStr);
            return new TimeInterval(from, to);
        }
    }
}
