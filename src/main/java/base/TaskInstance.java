package base;

import java.util.AbstractMap;

//only a TaskInstance "typedef"
public class TaskInstance extends AbstractMap.SimpleEntry<Task,Integer> {
    /**
     * 
     */
    private static final long serialVersionUID = 6223874625241305964L;

    public TaskInstance(Task key, Integer value) {
        super(key, value);
    }
}