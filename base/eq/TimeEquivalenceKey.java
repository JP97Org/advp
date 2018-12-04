package base.eq;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import base.EquivalenceKey;
import base.PersonProperty;
import base.TaskProperty;

/**
 * Represents an equivalence key for time-interval values.
 * 
 * @author jojo
 * @version 0.9
 */
public class TimeEquivalenceKey implements EquivalenceKey {
    private static final String PROP_NAME = "time";
    private static final String PROP_DESC = "an equivalence key for time-interval values";
    
    private boolean ofPerson;
    
    private Set<TimeInterval> personsPossibleTimeIntervals;
    
    private TimeInterval taskFromTo;
    
    private TimeEquivalenceKey(boolean ofPerson, Collection<TimeInterval> timeIntervalOrTimeIntervals) {
        this.ofPerson = ofPerson;
        if(ofPerson) {
            this.personsPossibleTimeIntervals = new HashSet<>();
            this.personsPossibleTimeIntervals.addAll(timeIntervalOrTimeIntervals);
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
        this(true, personTimeIntervals);
    }
    
    /**
     * Creates a time equivalence key for a task with one time interval.
     * 
     * @param taskTimeInterval - the task's time interval
     */
    public TimeEquivalenceKey(TimeInterval taskTimeInterval) {
        this(false, Arrays.asList(taskTimeInterval));
    }
    
    /**
     * Removes the time interval from this person's TEK's possible time intervals.
     * 
     * @param ti - the time interval which was mapped
     * @throws UnsupportedOperationException - if called on a task's TEK
     */
    public void mapped(final TimeInterval ti) throws UnsupportedOperationException{
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
}
