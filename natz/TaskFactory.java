package natz;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import base.Task;
import base.TaskProperty;
import base.eq.ContainerEquivalenceKey;
import base.eq.EqualEquivalenceKey;
import base.eq.GenderEquivalenceKey;
import base.eq.IDs;
import base.eq.Operation;
import base.eq.TimeEquivalenceKey;
import base.eq.TimeInterval;

public class TaskFactory {
    protected static final int COUNT_FLOORS = 3;
    
    protected static final int FLOOR_NUM_INSTANCE = 2;
    protected static final int POOL_NUM_INSTANCE = 6;
    
    protected static final String POOL_STR = "Pool";
    protected static final String FLOOR_STR = "Floor ";
    
    private final List<TimeInterval> dates;
    private final List<Task> tasks;
    
    public TaskFactory(String... datesAsStr) {
        final ArrayList<String> datesClean = new ArrayList<String>();
        for(String date : datesAsStr) {
            if(!date.trim().equals("")) {
                datesClean.add(date.trim());
            }
        }
        this.dates = new ArrayList<TimeInterval>();
        for(String str : datesClean) {
            try {
                final Date from = DateFormat.getDateInstance().parse(str);
                //System.out.println(from);
                final Date to = new Date(from.getTime() + 24L * 60L * 60L * 1000L - 1L);
                //System.out.println(to);
                this.dates.add(new TimeInterval(from,to));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        this.tasks = new ArrayList<>();
    }
    
    public Task getFloorK(int k, TimeInterval date) {
        if(k < 0) throw new IllegalArgumentException("k must be >= 0");
        if(k >= COUNT_FLOORS) throw new IllegalArgumentException("k must be less than " + COUNT_FLOORS);
        
        final Task ret = new Task(FLOOR_STR + k, FLOOR_NUM_INSTANCE);
            
        final Operation alternate = Operation.ALTERNATE;
            
        final GenderEquivalenceKey female = GenderEquivalenceKey.FEMALE;
        final GenderEquivalenceKey male = GenderEquivalenceKey.MALE;

        final ContainerEquivalenceKey<GenderEquivalenceKey> oneMaleOneFemaleKey 
                = new ContainerEquivalenceKey<>(IDs.GENDER, Arrays.asList(female, male), alternate);
        final TaskProperty oneMaleOneFemale = oneMaleOneFemaleKey.getTaskProperty();
        ret.addProperty(oneMaleOneFemale);
            
        final EqualEquivalenceKey<Boolean> newKey 
                = new EqualEquivalenceKey<>(PersonFactory.ID_NEW, true);
        final EqualEquivalenceKey<Boolean> oldKey 
                = new EqualEquivalenceKey<>(PersonFactory.ID_NEW, false);
            
        final ContainerEquivalenceKey<EqualEquivalenceKey<Boolean>> oneNewOneOldKey
                = new ContainerEquivalenceKey<>(PersonFactory.ID_NEW, Arrays.asList(oldKey, newKey), alternate);
        final TaskProperty oneNewOneOld = oneNewOneOldKey.getTaskProperty();
        ret.addProperty(oneNewOneOld);
        
        ret.addProperty(new TimeEquivalenceKey(date).getTaskProperty());
        
        if(!tasks.contains(ret)) {
            tasks.add(ret);
        }
        
        return ret;
    }
    
    public Task getPool(TimeInterval date) {
        final Task ret = new Task(POOL_STR, POOL_NUM_INSTANCE);
        //TODO: evtl. properties noch setzen! (wenn es welche gibt, hab jetzt erstmal keine gesehen)
        
        final TimeEquivalenceKey tek = new TimeEquivalenceKey(date);
        ret.addProperty(tek.getTaskProperty());
        
        if(!tasks.contains(ret)) {
            tasks.add(ret);
        }
        
        return ret;
    }
    
    public Set<Task> getAllTasks() {
        final Set<Task> ret = new HashSet<Task>();
        
        for (final TimeInterval date : this.dates) {
            ret.add(getPool(date));
            for(int i = 0;i < COUNT_FLOORS;i++) {
                ret.add(getFloorK(i, date));
            }
        }
        
        return ret;
    }
    
    public List<TimeInterval> getDates() {
        return this.dates;
    }

    public List<Task> getTasksOfDate(final TimeInterval date) {
        return Arrays.asList(this.tasks.stream()
                .filter(x -> date.equals(TimeEquivalenceKey.extract(x).getTimeIntervals().iterator().next()))
                .toArray(Task[]::new));
    }
}
