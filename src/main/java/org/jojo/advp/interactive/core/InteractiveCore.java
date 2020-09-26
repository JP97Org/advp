package org.jojo.advp.interactive.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.jojo.advp.base.Person;
import org.jojo.advp.base.Solver;
import org.jojo.advp.base.Task;
import org.jojo.advp.base.World;
import org.jojo.advp.base.factory.GeneralFactory;
import org.jojo.advp.base.factory.KeyPairFactory;
import org.jojo.advp.base.factory.PropertySetFactory;
import org.jojo.advp.base.factory.TaskDescriptor;

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
    
    public Person getPersonOfTaskInstance (final String taskName, int instanceNum) {
        final Task task = this.world.getTasks()
                .stream().filter(t -> t.getName().equals(taskName)).findFirst().orElse(null);
        if (task != null) {
            final Person ret = this.world.getPersonOfTaskInstance(task, instanceNum);
            if (ret != null) {
                return ret;
            } else {
                throw new IllegalArgumentException("no person mapped or task instance number not found");
            }
        } else {
            throw new IllegalArgumentException("task not found");
        }
    }
    
    /* TODO not supported at the moment for interactive
    public Set<TaskInstance> getTaskInstancesOfPerson(Person person) {
        return this.world.getTaskInstancesOfPerson(person);
    }*/

    public void setSolver(final Solver solver) {
        this.world.setSolver(solver);
    }
    
    public boolean solve() {
        return this.world.solve();
    }
    
    public String getPrintResult() {
        final StringBuilder ret = new StringBuilder();
        
        ret.append("Preparation persons:\n");
        this.personsPreparation.forEach(x -> ret.append(x.toString(true)).append("\n"));
        ret.append("\nPreparation tasks:\n");
        this.tasksPreparation.forEach(x -> ret.append(x.toString(true)).append("\n"));
        
        ret.append("\nPersons:\n");
        final List<Person> persons = this.world.getPersons().stream().sorted(new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.getName().compareTo(o2.getName());
            }}).collect(Collectors.toList());
        persons.forEach(p -> ret.append(p.getName()).append("\n"));
        
        ret.append("\nTasks:\n");
        final List<Task> tasks = this.world.getTasks().stream().sorted(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getName().compareTo(o2.getName());
            }}).collect(Collectors.toList());
        tasks.forEach(t -> ret.append(t.getName()).append("\n"));
        
        ret.append("\nMappings:\n");
        for (Task t : tasks) {
            Person person;
            int num = 0;
            do {
                person = this.world.getPersonOfTaskInstance(t, num);
                if (person != null) {
                    ret.append(t.getName()).append("[").append(num)
                        .append("] -> ").append(person).append("\n");
                    num++;
                }
            } while (person != null);
        }
        
        return ret.toString();
    }
    
    public void reset() {
        this.world = null;
        clearPreparations();
    }
}
