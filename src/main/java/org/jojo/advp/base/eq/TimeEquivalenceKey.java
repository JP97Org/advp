package org.jojo.advp.base.eq;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.PersonProperty;
import org.jojo.advp.base.Task;
import org.jojo.advp.base.TaskProperty;

/**
 * Represents an equivalence key for time-interval values.
 * 
 * @author jojo
 * @version 0.9
 */
public class TimeEquivalenceKey implements EquivalenceKey {
    /**
     * 
     */
    private static final long serialVersionUID = -4901838592752690045L;
    private static final String PROP_NAME = "time";
    private static final String PROP_DESC = "an equivalence key for time-interval values";
    
    private boolean ofPerson;
    
    private Set<TimeInterval> personsPossibleTimeIntervals;
    
    private TimeInterval taskFromTo;
    
    private TimeEquivalenceKey(boolean ofPerson, Collection<TimeInterval> timeIntervalOrTimeIntervals) {
        this.ofPerson = ofPerson;
        if(ofPerson) {
            this.personsPossibleTimeIntervals = new HashSet<>();
            this.personsPossibleTimeIntervals.addAll(Objects.requireNonNull(timeIntervalOrTimeIntervals));
        } else {
            if(timeIntervalOrTimeIntervals.size() != 1) {
                throw new IllegalArgumentException("task interval must be only one!");
            }
            this.taskFromTo = timeIntervalOrTimeIntervals.iterator().next();
        }
    }
    
    /**
     * Creates a time equivalence key for a person with a set of time intervals.
     * 
     * @param personTimeIntervals - the set of time intervals
     */
    public TimeEquivalenceKey(Set<TimeInterval> personTimeIntervals) {
        this(true, Objects.requireNonNull(personTimeIntervals));
    }
    
    /**
     * Creates a time equivalence key for a task with one time interval.
     * 
     * @param taskTimeInterval - the task's time interval
     */
    public TimeEquivalenceKey(TimeInterval taskTimeInterval) {
        this(false, Arrays.asList(Objects.requireNonNull(taskTimeInterval)));
    }
    
    /**
     * Removes the time interval from this person's TEK's possible time intervals.
     * 
     * @param ti - the time interval which was mapped
     * @throws UnsupportedOperationException - if called on a task's TEK
     */
    public void mapped(final TimeInterval ti) throws UnsupportedOperationException{
        Objects.requireNonNull(ti);
        if(ofPerson) {
            this.personsPossibleTimeIntervals.remove(ti);
        } else {
            throw new UnsupportedOperationException("Only on person's TEK mapped(ti) can be called!");
        }
    }
    
    /**
     * Gets the time intervals for reading.
     * 
     * @return the time intervals
     */
    public Set<TimeInterval> getTimeIntervals() {
        return new HashSet<TimeInterval>(ofPerson ? this.personsPossibleTimeIntervals : Arrays.asList(taskFromTo));
    }
    
    @Override
    public PersonProperty getPersonProperty() {
        if (ofPerson) {
            return new PersonProperty(PROP_NAME, PROP_DESC, this);
        }
        else {
            throw new UnsupportedOperationException("This equivalence key is only for tasks!");
        }
    }
    
    @Override
    public TaskProperty getTaskProperty() {
        if(!ofPerson) {
            return new TaskProperty(PROP_NAME, PROP_DESC, this);
        }
        else {
            throw new UnsupportedOperationException("This equivalence key is only for persons!");
        }
    }
    
    @Override
    public int getID() {
        return IDs.TIME;
    }
    
    @Override
    public boolean isEquivalent(EquivalenceKey other) {
        if(other != null && getClass().equals(other.getClass())) {
            final TimeEquivalenceKey o = (TimeEquivalenceKey)other;
            
            boolean taskIntervalContained = false;
            if(ofPerson && !o.ofPerson) {
                for(TimeInterval interval : personsPossibleTimeIntervals) {
                    taskIntervalContained = interval.contains(o.taskFromTo);
                    if(taskIntervalContained) {
                        break;
                    }
                }
            } else if(!ofPerson && o.ofPerson) {
                taskIntervalContained = o.isEquivalent(this);
            }
            
            return getID() == o.getID() && taskIntervalContained;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return PROP_NAME + " | value= " + (ofPerson ? personsPossibleTimeIntervals : taskFromTo); 
    }
    
    /**
     * Extracts the taks's TEK.
     * 
     * @param task - the task
     * @return the task's TEK
     * @throws IllegalArgumentException - when the task does not have a TEK
     */
    public static TimeEquivalenceKey extract(final Task task) {
        Objects.requireNonNull(task);
        final Iterator<TimeEquivalenceKey> ret = task.getProperties()
            .stream()
            .filter(x -> x.getEquivalenceKey().getClass().equals(TimeEquivalenceKey.class))
            .map(x -> TimeEquivalenceKey.class.cast(x.getEquivalenceKey())).iterator();
        if(ret.hasNext()) {
            return ret.next();
        } else {
            throw new IllegalArgumentException("Task must have a TEK to extract it!");
        }
    }
}
