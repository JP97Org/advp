package base.eq;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import base.EquivalenceKey;
import base.PersonProperty;
import base.TaskProperty;

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
    
    public TimeEquivalenceKey(Set<TimeInterval> personTimeIntervals) {
        this(true, personTimeIntervals);
    }
    
    public TimeEquivalenceKey(TimeInterval taskTimeIntervals) {
        this(false, Arrays.asList(taskTimeIntervals));
    }
    
    public void mapped(final TimeInterval ti) {
        this.personsPossibleTimeIntervals.remove(ti);
    }
    
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
        if(other != null && getClass() == other.getClass()) {
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
