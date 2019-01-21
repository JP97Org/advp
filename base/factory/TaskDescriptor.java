package base.factory;

public class TaskDescriptor {
    private final String name;
    private final int numInstances;
    
    public TaskDescriptor(final String name, final int numInstances) {
        this.name = name;
        this.numInstances = numInstances;
    }
    
    public String getName() {
        return name;
    }
    
    public int getNumInstances() {
        return numInstances;
    }
}
