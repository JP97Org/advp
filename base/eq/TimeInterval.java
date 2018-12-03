package base.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TimeInterval {
    private final Date from;
    private final Date to;
    
    public TimeInterval(Date from, Date to) {
        if(to.getTime() < from.getTime()) throw new IllegalArgumentException("to time must be >= from time!");
        
        this.from = from;
        this.to = to;
    }
    
    public TimeInterval(final TimeInterval toCopy) {
        this(new Date(toCopy.from.getTime()), new Date(toCopy.to.getTime()));
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
    
    /**
     * 
     * @param toCut - must be disjunct and ordered asceding and all contained in this time interval!
     * @return ordered ascending list of time intervals containing all time except for the cut out open intervals
     */
    public List<TimeInterval> cut(List<TimeInterval> toCut) {
        List<TimeInterval> ret = new ArrayList<TimeInterval>();
        
        final int size = toCut.size();
        if(size > 0) {
            final TimeInterval interval = toCut.get(0);
            final TimeInterval before = new TimeInterval(this.from, interval.from);
            final TimeInterval after = new TimeInterval(interval.to, this.to);
            ret.add(before);
            if(size > 1) {
                final List<TimeInterval> remainingList = new ArrayList<TimeInterval>();
                remainingList.addAll(toCut);
                remainingList.remove(0);
                ret.addAll(after.cut(remainingList));
            } else {
                ret.add(after);
            }
        }
        
        return ret;
    }
    
    /**
     * 
     * @param toCut - must be contained in this time interval!
     * @return
     */
    public List<TimeInterval> cut(TimeInterval toCut) {
        return cut(Arrays.asList(toCut));
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
    
    @Override
    public boolean equals(Object other) {
        return getClass().equals(other.getClass()) && from.equals(((TimeInterval)other).from)
                && to.equals(((TimeInterval)other).to);
    }
}
