package org.jojo.advp.base.eq;

import pl.joegreen.lambdaFromString.LambdaCreationException;
import pl.joegreen.lambdaFromString.LambdaFactory;
import pl.joegreen.lambdaFromString.TypeReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.jojo.advp.base.EquivalenceKey;

/**
 * An enum for describing equivalence keys in order to ease dynamic creation.
 * 
 * @author jojo
 * @version 0.9
 */
public enum EquivalenceKeyDescription {
    AGE (AgeEquivalenceKey.class, int.class, Comparison.class),
    COMPARISON (ComparisonEquivalenceKey.class, int.class, Comparable.class, Comparison.class),
    CONTAINER (ContainerEquivalenceKey.class, int.class, List.class, Operation.class),
    EQUAL (EqualEquivalenceKey.class, int.class, Object.class),
    GENDER (GenderEquivalenceKey.class, String.class),
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
        this.keyClass = Objects.requireNonNull(keyClass);
        this.parameterTypes = Objects.requireNonNull(parameterTypes);
    }
    
    /**
     * 
     * @return the class of the key
     */
    public Class<? extends EquivalenceKey> getEqKeyClass() {
        return this.keyClass;
    }
    
    /**
     * 
     * @return the parameter types of the key
     */
    public Class<?>[] getParamTypes() {
        return this.parameterTypes;
    }
    
    /**
     * 
     * @return the delim for the settings name and the default value
     */
    public static String getDelim() {
        return "|";
    }
    
    /**
     * 
     * @return the settings names of the key
     */
    public String[] getSettingsNames() {
        final String[] ret = new String[this.creationHints.length];
        switch(this) {
            case AGE: ret[0] = "Age"; ret[1] = "Comparison"; break;
            case COMPARISON: ret[0] = getIdString(); ret[1] = "Int. Value"; ret[2] = "Comparison"; break;
            case CONTAINER: ret[2] = getIdString(); break;
            case EQUAL: ret[0] = getIdString(); ret[1] = "Str. Value"; break;
            case GENDER: ret[0] = "Gender"; break;
            case LAMBDA_PERSON: ret[0] = getIdString(); ret[1] = "P Str. Value"; ret[2] = "Expression"; ret[3] = "Other Class" + getDelim() + "String"; break;
            case LAMBDA_TASK: ret[0] = "T Str. Value"; ret[1] = getIdString(); ret[2] = "Expression"; ret[3] = "Other Class" + getDelim() + "String"; break;
            case TIME_PERSON: ret[0] = "Set of Time-Intervals"; break;
            case TIME_TASK: ret[0] = "Time-Interval"; break;
            default: break;
        }

        return ret;
    }
    
    private static String getIdString() {
        final int id = IDs.nextID();
        return "ID" + getDelim() + id;
    }
    
    /**
     * 
     * @param args - the arguments
     * @param delim - the delim for splitting the arguments
     * @return the equivalence key description referenced by the given arguments or null if none is found
     */
    public static EquivalenceKeyDescription ofArgs(final String args, final String delim) {
        Objects.requireNonNull(args);
        Objects.requireNonNull(delim);
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
                final CreationHint hint = Arrays.stream(CreationHint.values()).filter(x -> x.name().equals(splitted[k])).findFirst().orElse(null);
                if (hint == null) {
                    throw new IllegalArgumentException("unknown hint \"" + splitted[k] + "\"!");
                }
                ret.creationHints[k - 1] = hint;
            }
        }
        return ret;
    }

    /**
     * Creates the initialization arguments for this key and the given arguments string.
     * 
     * @param argsStr - the given arguments string
     * @return the initialization arguments for this key and the given arguments string
     */
    public Object[] createInitArgs(final String[] argsStr) {
        Objects.requireNonNull(argsStr);
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
            initializeDefaultCreationHints();
            if (argsStr.length == this.creationHints.length) {
                return createInitArgs(argsStr);
            }
        }
        return null;
    }
    
    /**
     * Initializes the default creation hints for this key description.
     */
    public void initializeDefaultCreationHints() {
        switch(this) {
        case AGE: this.creationHints = new CreationHint[] {CreationHint.INT, CreationHint.COMP}; break;
        case COMPARISON: this.creationHints = new CreationHint[] {CreationHint.INT, CreationHint.INT,CreationHint.COMP}; break; //assuming int comparison
        case CONTAINER: this.creationHints = new CreationHint[3]; break;
        case EQUAL: this.creationHints = new CreationHint[] {CreationHint.INT, CreationHint.STR}; break;
        case GENDER: this.creationHints = new CreationHint[] {CreationHint.STR}; break;
        case LAMBDA_PERSON: this.creationHints = new CreationHint[] {CreationHint.INT, CreationHint.STR, CreationHint.LAMBDA, CreationHint.CLASS}; break;
        case LAMBDA_TASK: this.creationHints = new CreationHint[] {CreationHint.STR, CreationHint.INT, CreationHint.LAMBDA, CreationHint.CLASS}; break;
        case TIME_PERSON: this.creationHints = new CreationHint[] {CreationHint.HASH_SET_TI}; break;
        case TIME_TASK: this.creationHints = new CreationHint[] {CreationHint.TI}; break;
        default: break;
        }
    }
    
    /**
     * 
     * @return the creation hints
     */
    public CreationHint[] getCreationHints() {
        CreationHint[] ret = new CreationHint[this.creationHints.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = this.creationHints[i];
        }
        return ret;
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

    /**
     * An enum representing hints for creation of arguments of a key.
     * 
     * @author jojo
     * @version 0.9
     */
    public enum CreationHint {
        CLASS(s -> {
            switch(s) {
                case "String": return String.class;
                case "Integer": return Integer.class;
                case "Double": return Double.class;
                case "Boolean": return Boolean.class;
                case "TimeInterval": return TimeInterval.class;
                default: return Object.class;
            }
        }),
        STR (s -> s),
        INT (s -> {
            try {
                return Integer.parseInt(s); 
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
            }
            }),
        DOUBLE (s -> {
            try {
                return Double.parseDouble(s); 
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
            }
            }),
        BOOL (s -> {
            try {
                return Boolean.parseBoolean(s); 
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
        LAMBDA (s -> createBiPredicateFromString(s)),
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
        
        //TODO: also allow dynamic creation of lambdas which are not <String, String>
        /**
         * Creates a bi-predicate from the given lambdaExpression
         * @param lambdaExpression -    the lambda expression of the form "(p,t) -> expr(p,t)"
         *                              where p and t are variables with names in ([a-z]+)
         *                              and expr(p,t) is a function returning boolean
         * @return a bi-predicate from the given lambdaExpression
         */
        private static BiPredicate<String, String> createBiPredicateFromString(final String lambdaExpression) throws IllegalArgumentException {
            final String matchRegex = "\\([a-z]+,[a-z]+\\)\\s->\\s.*";
            if (lambdaExpression != null && lambdaExpression.matches(matchRegex)) {
                LambdaFactory lambdaFactory = LambdaFactory.get();
                BiPredicate<String, String> ret;
                try {
                    ret = lambdaFactory.createLambda(lambdaExpression, new TypeReference<BiPredicate<String, String>>(){});
                } catch (LambdaCreationException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
                return ret;
            } else {
                throw new IllegalArgumentException("Lambda expression must match (" + matchRegex + ")");
            }
        }

        Object transform(final String input) {
            return this.transform.apply(input);
        }
        
        /**
         * 
         * @param input - input
         * @return whether format of input is ok
         */
        public boolean isFormatOk(final String input) {
            try {
                final boolean ok = transform(input) != null;
                return ok;
            } catch (IllegalArgumentException e) {
                return false;
            }
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
