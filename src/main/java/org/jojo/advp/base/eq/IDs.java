package org.jojo.advp.base.eq;

/**
 * Utility class which contains constant IDs for some equivalence keys. <br />
 * It also can be used for getting a next unique ID.
 * 
 * @author jojo
 * @version 0.9
 */
public class IDs {
    private static int id = 0;
    
    
    //////////////////////////////////////////////////////////////////////////
    //fixed IDs (have 9 digits)
    
    //for internal equivalence keys (see e.g. Age#key)
    public static final int INTERNAL = 100000000;
   
    public static final int TIME     = 100000010;
    
    public static final int AGE      = 100000020;
    public static final int GENDER   = 100000030;
    
    //////////////////////////////////////////////////////////////////////////
    
    /**
     * Gets the next unused ID.
     * 
     * @return the next unused ID.
     */
    public static int nextID() {
        final int ret = id;
        id++;
        return ret;
    }
    
    private IDs() {
        throw new AssertionError("Utility classes must not be instantiated!");
    }
}
