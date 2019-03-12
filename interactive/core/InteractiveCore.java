package interactive.core;

import java.util.ArrayList;
import java.util.List;
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
    private final List<KeyPairFactory> personsPreparation;
    private final List<KeyPairFactory> tasksPreparation;
    
    public InteractiveCore() {
        this.personsPreparation = new ArrayList<>();
        this.tasksPreparation = new ArrayList<>();
        reset();
    }
    
    public void start() {
        this.world = new World();
    }
    
    public boolean isStarted() {
        return this.world != null;
    }
    
    public boolean hasSolver() {
        return isStarted() && this.world.getSolver() != null;
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
    
    public void addPersonKeyPairFactory(final KeyPairFactory keyPairFactory) {
        this.personsPreparation.add(keyPairFactory);
    }
    
    public void addTaskKeyPairFactory(final KeyPairFactory keyPairFactory) {
        this.tasksPreparation.add(keyPairFactory);
    }
    
    public void removePersonKeyPairFactory(final int index) {
        this.personsPreparation.remove(index);
    }
    
    public void removeTaskKeyPairFactory(final int index) {
        this.tasksPreparation.remove(index);
    }
    
    public List<KeyPairFactory> getPersonKeyPairFactoryList() {
        return new ArrayList<>(this.personsPreparation);
    }
    
    public List<KeyPairFactory> getTaskKeyPairFactoryList() {
        return new ArrayList<>(this.tasksPreparation);
    }
    
    public boolean completePersonsCreation(final List<String> names) {
        final int size = names.size();
        final boolean correct = size == this.personsPreparation.size() 
                && size == names.stream().distinct().count();
        
        if (correct) {
            for (int i = 0; i < size; i++) {
                final String name = names.get(i);
                final KeyPairFactory factory = this.personsPreparation.get(i);
                addPerson(name, factory);
            }
        }
        
        return correct;
    }
    
    public boolean completeTasksCreation(final List<TaskDescriptor> descriptors) {
        final int size = descriptors.size();
        final boolean correct = size == this.tasksPreparation.size() 
                && size == descriptors.stream().distinct().count();
        
        if (correct) {
            for (int i = 0; i < size; i++) {
                final TaskDescriptor desc = descriptors.get(i);
                final KeyPairFactory factory = this.tasksPreparation.get(i);
                addTask(desc, factory);
            }
        }
        
        return correct;
    }
    
    public void clearPreparations() {
        this.personsPreparation.clear();
        this.tasksPreparation.clear();
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
        clearPreparations();
    }
}
