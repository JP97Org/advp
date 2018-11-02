package base.eq;

import java.util.Date;
import java.util.Objects;

public class TimeInterval {
    private final Date from;
    private final Date to;
    
    public TimeInterval(Date from, Date to) {
        this.from = from;
        this.to = to;
    }
    
    public Date getFrom() {
        return from;
    }
    
    public Date getTo() {
        return to;
    }
    
    //for all: inclusive both interval-borders
    
    public boolean contains(Date time) {
        return time.getTime() >= from.getTime() && time.getTime() <= to.getTime();
    }
    
    public boolean contains(TimeInterval other) {
        return contains(other.from) && contains(other.to);
    }
    
    public boolean isContainedIn(TimeInterval other) {
        return other.contains(this);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
    
    @Override
    public boolean equals(Object other) {
        return getClass() == other.getClass() && from.equals(((TimeInterval)other).from)
                && to.equals(((TimeInterval)other).to);
    }
}
