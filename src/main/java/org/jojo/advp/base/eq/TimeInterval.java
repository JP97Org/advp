package org.jojo.advp.base.eq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents a time interval with millisecond precision including beginning and end.
 * 
 * @author jojo
 * @version 0.9
 */
public class TimeInterval implements Comparable<TimeInterval>, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2772353962838666894L;
    private final Date from;
    private final Date to;
    
    /**
     * Creates a new TimeInterval.
     * 
     * @param from - the beginning of the interval
     * @param to - the end of the interval
     */
    public TimeInterval(Date from, Date to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        if(to.getTime() < from.getTime()) throw new IllegalArgumentException("to time must be >= from time!");
        
        this.from = from;
        this.to = to;
    }
    
    /**
     * Copies the given TimeInterval.
     * 
     * @param toCopy - the given time interval
     */
    public TimeInterval(final TimeInterval toCopy) {
        this(new Date(Objects.requireNonNull(toCopy).from.getTime()), new Date(toCopy.to.getTime()));
    }
    
    /**
     * Gets the beginning of this interval.
     * 
     * @return the beginning of this interval
     */
    public Date getFrom() {
        return from;
    }
    
    /**
     * Gets the end of this interval.
     * 
     * @return the end of this interval
     */
    public Date getTo() {
        return to;
    }
    
    /**
     * Determines whether the given time is included, including both border, in this interval.
     * 
     * @param time - the given time
     * @return whether the given time is included
     */
    public boolean contains(Date time) {
        Objects.requireNonNull(time);
        return time.getTime() >= from.getTime() && time.getTime() <= to.getTime();
    }
    
    /**
     * Determines whether the other TimeInterval is completely included, including both border, in this interval.
     * 
     * @param other - the given time interval
     * @return whether the given time interval is completely included
     */
    public boolean contains(TimeInterval other) {
        return other != null ? (contains(other.from) && contains(other.to)) : false;
    }
    
    /**
     * Determines whether this TimeInterval is completely included, including both border, in the other one.
     * 
     * @param other - the given time interval
     * @return whether this TimeInterval is completely included in the other one
     */
    public boolean isContainedIn(TimeInterval other) {
        return other != null ? other.contains(this) : false;
    }
    
    /**
     * TODO experimental method
     * 
     * @param toCut - must be disjunct and ordered asceding and all contained in this time interval!
     * @return ordered ascending list of time intervals containing all time except for the cut out open intervals
     */
    public List<TimeInterval> cut(List<TimeInterval> toCut) {
        Objects.requireNonNull(toCut);
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
     * TODO experimental method
     * 
     * @param toCut - must be contained in this time interval!
     * @return
     */
    public List<TimeInterval> cut(TimeInterval toCut) {
        Objects.requireNonNull(toCut);
        return cut(Arrays.asList(toCut));
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
    
    @Override
    public boolean equals(Object other) {
        return other != null && getClass().equals(other.getClass()) && from.equals(((TimeInterval)other).from)
                && to.equals(((TimeInterval)other).to);
    }

    /**
     * getFrom compare!!!!!!!!
     */
    @Override
    public int compareTo(TimeInterval o) {
        return this.getFrom().compareTo(o.getFrom());
    }
}
