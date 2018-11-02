package base.eq;

public class IDs {
    private static int id = 0;
    
    
    //////////////////////////////////////////////////////////////////////////
    //fixed IDs (have 9 digits)
    
    //for internal equivalence keys (see e.g. Age#key)
    public static final int INTERNAL = 100000000;
   
    public static final int AGE      = 100000001;
    public static final int GENDER   = 100000002;
    
    //////////////////////////////////////////////////////////////////////////
    
    public static int nextID() {
        final int ret = id;
        id++;
        return ret;
    }
    
    private IDs() {
        throw new AssertionError("Utility classes must not be instantiated!");
    }
}
