package interactive.core;

import java.util.Set;

import base.Person;
import base.Solver;
import base.Task;
import base.TaskInstance;
import base.World;
import base.factory.GeneralFactory;
import base.factory.KeyPairFactory;
import base.factory.PropertySetFactory;
import base.factory.TaskDescriptor;

public class InteractiveCore {
    private World world;
    
    public InteractiveCore() {
        reset();
    }
    
    public void start() {
        this.world = new World();
    }
    
    public boolean isStarted() {
        return this.world != null;
    }
    
    public KeyPairFactory getNewKeyPairFactory() {
        return new KeyPairFactory();
    }
    
    public PropertySetFactory getNewPropertySetFactory(final KeyPairFactory keyPairFactory) {
        return new PropertySetFactory(keyPairFactory);
    }
    
    public GeneralFactory getNewGeneralFactory() {
        return new GeneralFactory();
    }
    
    public boolean addPerson(final String name, final KeyPairFactory keyPairFactory) {
        final PropertySetFactory propFactory = new PropertySetFactory(keyPairFactory);
        return addPerson(new Person(name, propFactory.getPersonProperties()));
    }
    
    public boolean addPerson(final Person person) {
        return this.world.addPerson(person);
    }
    
    public boolean addTask(final TaskDescriptor taskDesc, final KeyPairFactory keyPairFactory) {
        final PropertySetFactory propFactory = new PropertySetFactory(keyPairFactory);
        return addTask(new Task(taskDesc, propFactory.getTaskProperties()));
    }

    public boolean addTask(final Task task) {
        return this.world.addTask(task);
    }
    
    public Person getPersonOfTaskInstance (final Task task, int instanceNum) {
        return this.world.getPersonOfTaskInstance(task, instanceNum);
    }
    
    public Set<TaskInstance> getTaskInstancesOfPerson(Person person) {
        return this.world.getTaskInstancesOfPerson(person);
    }

    public void setSolver(final Solver solver) {
        this.world.setSolver(solver);
    }
    
    public boolean solve() {
        return this.world.solve();
    }
    
    public void reset() {
        this.world = null;
    }
}
