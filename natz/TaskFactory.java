package natz;

import java.util.Arrays;

import base.Task;
import base.TaskProperty;
import base.eq.ContainerEquivalenceKey;
import base.eq.EqualEquivalenceKey;
import base.eq.GenderEquivalenceKey;
import base.eq.IDs;
import base.eq.Operation;

public class TaskFactory {
    private static final int COUNT_FLOORS = 3;
    
    private static final int FLOOR_NUM_INSTANCE = 2;
    private static final int POOL_NUM_INSTANCE = 6;
    
    private final Task[] floors;
    private Task pool;
    
    public TaskFactory() {
        floors = new Task[COUNT_FLOORS];
        pool = null;
    }
    
    public Task getFloorK(int k) {
        if(k < 0) throw new IllegalArgumentException("k must be greater than 0");
        if(k >= COUNT_FLOORS) throw new IllegalArgumentException("k must be less than " + COUNT_FLOORS);
        
        if(floors[k] == null) {
            floors[k] = new Task("Floor " + k, FLOOR_NUM_INSTANCE);
            
            final Operation alternate = Operation.ALTERNATE;
            
            final GenderEquivalenceKey female = GenderEquivalenceKey.FEMALE;
            final GenderEquivalenceKey male = GenderEquivalenceKey.MALE;

            final ContainerEquivalenceKey<GenderEquivalenceKey> oneMaleOneFemaleKey 
                = new ContainerEquivalenceKey<>(IDs.GENDER, Arrays.asList(female, male), alternate);
            final TaskProperty oneMaleOneFemale = oneMaleOneFemaleKey.getTaskProperty();
            floors[k].addProperty(oneMaleOneFemale);
            
            final EqualEquivalenceKey<Boolean> newKey 
                = new EqualEquivalenceKey<>(PersonFactory.ID_NEW, true);
            final EqualEquivalenceKey<Boolean> oldKey 
                = new EqualEquivalenceKey<>(PersonFactory.ID_NEW, false);
            
            final ContainerEquivalenceKey<EqualEquivalenceKey<Boolean>> oneNewOneOldKey
                = new ContainerEquivalenceKey<>(PersonFactory.ID_NEW, Arrays.asList(oldKey, newKey), alternate);
            final TaskProperty oneNewOneOld = oneNewOneOldKey.getTaskProperty();
            floors[k].addProperty(oneNewOneOld);
        }
        
        return floors[k];
    }
    
    public Task getPool() {
        if(pool == null) {
            pool = new Task("Pool", POOL_NUM_INSTANCE);
            //TODO: evtl. properties noch setzen! (wenn es welche gibt, hab jetzt erstmal keine gesehen)
        }
        
        return pool;
    }
}
