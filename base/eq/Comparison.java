package base.eq;

public enum Comparison {
    GR,SM,GREQ,SMEQ,EQ;

    public Comparison anti() {
        switch(this) {
            case GR: return SM;
            case SM: return GR;
            case GREQ: return SMEQ;
            case SMEQ: return GREQ;
            default: return this;
        }
    }
}
