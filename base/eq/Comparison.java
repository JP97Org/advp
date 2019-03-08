package base.eq;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents a Comparison.
 * 
 * @author jojo
 * @version 0.9
 */
public enum Comparison {
    GR,SM,GREQ,SMEQ,EQ;

    /**
     * Gets the anti-comparison of this comparison, i.e. the contrary comparison, e.g. for SM: GR.
     * @return the anti-comparison, i.e. the contrary comparison
     */
    public Comparison anti() {
        switch(this) {
            case GR: return SM;
            case SM: return GR;
            case GREQ: return SMEQ;
            case SMEQ: return GREQ;
            default: return this;
        }
    }
    
    public static Comparison of(final String str) {
        final Iterator<Comparison> iter = Arrays.stream(values()).filter(x -> x.name().equals(str)).iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }
}
