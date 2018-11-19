package natz;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private static final int COUNT_FLOORS = 3;
    
    private static final int FLOOR_NUM_INSTANCE = 2;
    private static final int POOL_NUM_INSTANCE = 6;
    
    private List<TimeInterval> dates;
    
    public TaskFactory(String... datesAsStr) {
        ArrayList<String> datesClean = new ArrayList<String>();
        for(String date : datesAsStr) {
            if(!date.trim().equals("")) {
                datesClean.add(date.trim());
            }
        }
        dates = new ArrayList<TimeInterval>();
        for(String str : datesClean) {
            try {
                Date from = DateFormat.getDateInstance().parse(str);
                //System.out.println(from);
                Date to = new Date(from.getTime() + 24L * 60L * 60L * 1000L - 1L);
                //System.out.println(to);
                dates.add(new TimeInterval(from,to));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    
    public Task getFloorK(int k, TimeInterval date) {
        if(k < 0) throw new IllegalArgumentException("k must be >= 0");
        if(k >= COUNT_FLOORS) throw new IllegalArgumentException("k must be less than " + COUNT_FLOORS);
        
        Task ret = new Task("Floor " + k, FLOOR_NUM_INSTANCE);
            
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
        
        return ret;
    }
    
    public Task getPool(TimeInterval date) {
        Task ret = new Task("Pool", POOL_NUM_INSTANCE);
        //TODO: evtl. properties noch setzen! (wenn es welche gibt, hab jetzt erstmal keine gesehen)
        
        ret.addProperty(new TimeEquivalenceKey(date).getTaskProperty());
        
        return ret;
    }
    
    public List<TimeInterval> getDates() {
        return dates;
    }
}
