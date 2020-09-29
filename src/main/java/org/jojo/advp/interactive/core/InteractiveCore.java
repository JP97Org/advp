package org.jojo.advp.interactive.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jojo.advp.base.EquivalenceKey;
import org.jojo.advp.base.Person;
import org.jojo.advp.base.Solver;
import org.jojo.advp.base.Task;
import org.jojo.advp.base.World;
import org.jojo.advp.base.factory.GeneralFactory;
import org.jojo.advp.base.factory.KeyPairFactory;
import org.jojo.advp.base.factory.PropertySetFactory;
import org.jojo.advp.base.factory.TaskDescriptor;

/**
 * Represents an interactive core for communication between user interface and the ADVP base API.
 * 
 * @author jojo
 * @version 0.9
 */
public class InteractiveCore {
    private World world;
    private final List<KeyPairFactory> personsPreparation;
    private final List<KeyPairFactory> tasksPreparation;
    
    private String[] personNames;
    private String[] taskDescriptors;
    
    private boolean solved;
    
    /**
     * Creates a new empty interactive core.
     */
    public InteractiveCore() {
        this.personsPreparation = new ArrayList<>();
        this.tasksPreparation = new ArrayList<>();
        reset();
    }
    
    /**
     * Resets this core and then loads the given serialized data into this core.
     * 
     * @param serializedPersonsPreparation - the serialized preparations (key pair factories) for the persons
     * @param serializedTasksPreparation - the serialized preparations (key pair factories) for the tasks
     * @throws ClassNotFoundException if class is not found
     * @throws IOException if an I/O failure occurs
     */
    public void load(final String serializedPersonsPreparation, final String serializedTasksPreparation) throws ClassNotFoundException, IOException {
        reset();
        this.personsPreparation.addAll(deserialize(serializedPersonsPreparation));
        this.tasksPreparation.addAll(deserialize(serializedTasksPreparation));
    }
    
    /**
     * Loads the given names of persons into a buffer.
     * 
     * @param personNames - the given names of persons
     * @see {@link InteractiveCore#finishLoadingPersonNames}
     */
    public void loadPersonNames(final String[] personNames) {
        this.personNames = personNames;
    }
    
    /**
     * Gets the names of the persons from the respective buffer and clears the buffer afterwards.
     * 
     * @return the names of the persons from the buffer
     * @see {@link InteractiveCore#loadPersonNames}
     */
    public String[] finishLoadingPersonNames() {
        final String[] ret = this.personNames;
        this.personNames = null;
        return ret;
    }
    
    /**
     * Loads the given descriptors of tasks into a buffer.
     * 
     * @param taskDescriptors - the given descriptors of tasks
     * @see {@link InteractiveCore#finishLoadingTaskDescriptors}
     */
    public void loadTaskDescriptors(final String[] taskDescriptors) {
        this.taskDescriptors = taskDescriptors;
    }
    
    /**
     * Gets the descriptors of tasks from the respective buffer and clears the buffer afterwards.
     * 
     * @return the descriptors of tasks from the buffer
     * @see {@link InteractiveCore#loadTaskDescriptors}
     */
    public String[] finishLoadingTaskDescriptors() {
        final String[] ret = this.taskDescriptors;
        this.taskDescriptors = null;
        return ret;
    }

    /**
     * Starts the core, i.e. sets a new empty world.
     * @see {@link World}
     */
    public void start() {
        this.world = new World();
    }
    
    /**
     * 
     * @return whether the core is started
     * @see {@link InteractiveCore#start}
     */
    public boolean isStarted() {
        return this.world != null;
    }
    
    /**
     * 
     * @return whether the world has a solver
     */
    public boolean hasSolver() {
        return isStarted() && this.world.getSolver() != null;
    }

    /**
     * 
     * @return whether the set solver of the world has run at least once
     * @see {@link InteractiveCore#isFullySolved}
     */
    public boolean isSolved() {
        return isStarted() && solved;
    }
    
    /**
     * 
     * @return whether a complete solution was found, i.e. all tasks have been mapped
     * @see {@link World#isCompletelyMapped}
     */
    public boolean isFullySolved() {
        return isStarted() && this.world.isCompletelyMapped();
    }
    
    /**
     * 
     * @return a new key pair factory
     */
    public KeyPairFactory getNewKeyPairFactory() {
        return new KeyPairFactory();
    }
    
    /**
     * 
     * @param keyPairFactory - the given key pair factory
     * @return a property set factory for the given key pair factory
     */
    public PropertySetFactory getNewPropertySetFactory(final KeyPairFactory keyPairFactory) {
        return new PropertySetFactory(keyPairFactory);
    }
    
    /**
     * 
     * @return a new general factory
     */
    public GeneralFactory getNewGeneralFactory() {
        return new GeneralFactory();
    }
    
    /**
     * Adds the given person's key pair factory.
     * 
     * @param keyPairFactory - the given factory
     */
    public void addPersonKeyPairFactory(final KeyPairFactory keyPairFactory) {
        this.personsPreparation.add(keyPairFactory);
    }
    
    /**
     * Adds the given task's key pair factory.
     * 
     * @param keyPairFactory - the given factory
     */
    public void addTaskKeyPairFactory(final KeyPairFactory keyPairFactory) {
        this.tasksPreparation.add(keyPairFactory);
    }
    
    /**
     * Removes the person's key pair factory at the given index.
     * 
     * @param index - the given index
     */
    public void removePersonKeyPairFactory(final int index) {
        this.personsPreparation.remove(index);
    }
    
    /**
     * Removes the task's key pair factory at the given index.
     * 
     * @param index - the given index
     */
    public void removeTaskKeyPairFactory(final int index) {
        this.tasksPreparation.remove(index);
    }
    
    /**
     * Removes the person's key at the given inner index of the factory at the given outer index.
     * 
     * @param index - the given outer index
     * @param innerIndex - the given inner index
     */
    public void removePersonKey(int index, int innerIndex) {
        this.personsPreparation.get(index).remove(true, innerIndex);
    }
    
    /**
     * Removes the task's key at the given inner index of the factory at the given outer index.
     * 
     * @param index - the given outer index
     * @param innerIndex - the given inner index
     */
    public void removeTaskKey(int index, int innerIndex) {
        this.tasksPreparation.get(index).remove(false, innerIndex);
    }
    
    /**
     * 
     * @return a list of the keys preparation for the persons
     */
    public List<KeyPairFactory> getPersonKeyPairFactoryList() {
        return new ArrayList<>(this.personsPreparation);
    }
    
    /**
     * 
     * @return a list of the keys preparation for the tasks
     */
    public List<KeyPairFactory> getTaskKeyPairFactoryList() {
        return new ArrayList<>(this.tasksPreparation);
    }
    
    /**
     * Completes the creation of persons from the keys preparation and the given names.
     * 
     * @param names - the given names
     * @return whether the persons creation was successfully completed
     */
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
    
    /**
     * Completes the creation of tasks from the keys preparation and the given task descriptors.
     * 
     * @param descriptors - the given task descriptors
     * @return whether the tasks creation was successfully completed
     */
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
    
    /**
     * Clears all preparations, i.e. removes all the key pair factories from the preparations list.
     */
    public void clearPreparations() {
        this.personsPreparation.clear();
        this.tasksPreparation.clear();
    }
    
    /**
     * Adds a person to the world.
     * 
     * @param name - the person's name
     * @param keyPairFactory - the person's key pair factory
     * @return whether the person was successfully added
     */
    public boolean addPerson(final String name, final KeyPairFactory keyPairFactory) {
        final PropertySetFactory propFactory = new PropertySetFactory(keyPairFactory);
        return addPerson(new Person(name, propFactory.getPersonProperties()));
    }
    
    /**
     * Adds a person to the world.
     * 
     * @param person - the person to be added
     * @return whether the person was successfully added
     */
    public boolean addPerson(final Person person) {
        return this.world.addPerson(person);
    }
    
    /**
     * Adds a task to the world.
     * 
     * @param taskDesc - the task descriptor of the task to be added
     * @param keyPairFactory - the task's key pair factory
     * @return whether the task was successfully added
     */
    public boolean addTask(final TaskDescriptor taskDesc, final KeyPairFactory keyPairFactory) {
        final PropertySetFactory propFactory = new PropertySetFactory(keyPairFactory);
        return addTask(new Task(taskDesc, propFactory.getTaskProperties()));
    }

    /**
     * Adds a task to the world.
     * 
     * @param task - the task to be added
     * @return whether the task was successfully added
     */
    public boolean addTask(final Task task) {
        return this.world.addTask(task);
    }
    
    /**
     * Gets the person which is mapped to the given task instance.
     * 
     * @param taskName - the name of the task
     * @param instanceNum - the instance index of the task instance
     * @return the mapped person
     * @throws IllegalArgumentException if the task instance is not found or no person is mapped to it
     */
    public Person getPersonOfTaskInstance (final String taskName, int instanceNum) throws IllegalArgumentException {
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

    /**
     * Sets the given solver as the solver of the world.
     * 
     * @param solver - the given solver
     */
    public synchronized void setSolver(final Solver solver) {
        this.world.setSolver(solver);
        this.solved = false;
    }
    
    /**
     * Solves the problem.
     * 
     * @return whether the problem is solved
     */
    public synchronized boolean solve() {
        this.solved = true;
        return this.world.solve();
    }
    
    /**
     * Gets the result for the print command summarizing this core.
     * 
     * @return the result for the print command summarizing this core
     */
    public String getPrintResult() {
        final StringBuilder ret = new StringBuilder();
        
        ret.append("Preparation persons:\n");
        this.personsPreparation.forEach(x -> ret.append(x.toString(true)).append("\n"));
        ret.append("\nPreparation tasks:\n");
        this.tasksPreparation.forEach(x -> ret.append(x.toString(false)).append("\n"));
        
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
    
    /**
     * 
     * @return the mappings from task instances to persons <br /><br />
     * {@code result[i][0]} is {@code task.getName() + "[" + instanceNum + "]"} <br />
     * and ({@code result[i][1]} is {@code person == null ? "" : person.toString()}
     */
    public String[][] getMappings() {
        final List<Task> tasks = this.world.getTasks().stream().sorted(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getName().compareTo(o2.getName());
            }}).collect(Collectors.toList());
        
        final String[][] ret = new String[getNumberOfTaskInstances(tasks)][2];
        for (int o = 0, count = 0; o < tasks.size(); o++) {
            final Task task = tasks.get(o);
            for (int i = 0; i < task.getInitialNumberOfInstances(); i++, count++) {
                final Person person = this.world.getPersonOfTaskInstance(task, i);
                final String pStr = person == null ? "" : person.toString();
                ret[count][0] = task.getName() + "[" + i + "]";
                ret[count][1] = pStr;
            }
        }
        return ret;
    }
    
    private static int getNumberOfTaskInstances(final List<Task> tasks) {
        return tasks.stream().mapToInt(t -> t.getInitialNumberOfInstances()).sum();
    }

    /**
     * Resets this core, i.e. removes the world and all preparations.
     */
    public synchronized void reset() {
        this.world = null;
        this.solved = false;
        clearPreparations();
    }

    /**
     * 
     * @param countPersons - the count of persons
     * @param countTasks - the count of tasks
     * @return whether the preparations are ok for the given counts
     */
    public boolean isPreparable(final int countPersons, final int countTasks) {
        return countPersons == personsPreparation.size() && countTasks == tasksPreparation.size();
    }
    
    /**
     * Serializes the preparations for the persons.
     * 
     * @return a serialization string which can be written to a text file
     * @throws IOException if an I/O failure occurs
     * @throws ClassNotFoundException if a class is not found
     */
    public String serializePersonsPreparation() throws IOException, ClassNotFoundException {
        return serialize(personsPreparation);
    }
    
    /**
     * Serializes the preparations for the tasks.
     * 
     * @return a serialization string which can be written to a text file
     * @throws IOException if an I/O failure occurs
     * @throws ClassNotFoundException if a class is not found
     */
    public String serializeTasksPreparation() throws IOException, ClassNotFoundException {
        return serialize(tasksPreparation);
    }
    
    private static final int TIMES = 10;
    private static <T> String serialize(T o) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        try (ObjectOutputStream oos = new ObjectOutputStream (baos)) {
            oos.writeObject(o);
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray())
                .replaceAll(Pattern.quote("\n"), times("newline", TIMES))
                .replaceAll(Pattern.quote("\""), times("quote", TIMES))
                .replaceAll(Pattern.quote(";"), times("semicolon", TIMES));
    }

    private static String times(final String in, int times) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(in);
        }
        return builder.toString();
    }
    
    private static List<KeyPairFactory> deserialize(final String serializedPersonsPreparation) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream (
                Base64.getDecoder().decode(serializedPersonsPreparation
                .replaceAll("(semicolon){"+TIMES+"}", ";")
                .replaceAll("(quote){"+TIMES+"}", "\"")
                .replaceAll("(newline){"+TIMES+"}", "\n")));
        try (ObjectInputStream ois = new ObjectInputStream (bais)) {
            Object o = ois.readObject();
            if (o instanceof List<?>) {
                try {
                    @SuppressWarnings("unchecked") // it is checked by the call of a method of KeyPairFactory
                    List<KeyPairFactory> ret = (List<KeyPairFactory>) o;
                    for (final KeyPairFactory k : ret) {
                        k.getOfPersonKeys();
                    }
                    return ret;
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("read object has not the correct class, it should be: List<KeyPairFactory> but is a list of other values.");
                }
            } else {
                throw new IllegalArgumentException("read object has not the correct class, it should be: List<KeyPairFactory> but is: " + o.getClass());
            }
        }
    }

    /**
     * Prepares the given world, i.e. adds all the contents of the given world to the respective
     * preparation lists and buffers.
     * 
     * @param localWorld - the given world
     */
    public void prepare(final World localWorld) {
        Objects.requireNonNull(localWorld);
        reset();
        final List<String> personNamesList = new ArrayList<String>();
        final List<String> taskDescriptorsList = new ArrayList<String>();
        
        for (final Person p : localWorld.getPersons()) {
            personNamesList.add(p.getName());
            final List<EquivalenceKey> keys = p.getProperties().stream()
                    .map(x -> x.getEquivalenceKey()).collect(Collectors.toList());
            final KeyPairFactory factory = new KeyPairFactory(true, keys);
            this.personsPreparation.add(factory);
        }
        
        for (final Task t : localWorld.getTasks()) {
            taskDescriptorsList.add(t.getName() + "," + t.getInitialNumberOfInstances());
            final List<EquivalenceKey> keys = t.getProperties().stream()
                    .map(x -> x.getEquivalenceKey()).collect(Collectors.toList());
            final KeyPairFactory factory = new KeyPairFactory(false, keys);
            this.tasksPreparation.add(factory);
        }
        
        loadPersonNames(personNamesList.toArray(new String[personNamesList.size()]));
        loadTaskDescriptors(taskDescriptorsList.toArray(new String[taskDescriptorsList.size()]));
    }

    /**
     * Gets the tasks of the world.
     * 
     * @return the tasks of the world
     */
    public Set<Task> getWorldTasks() {
        return this.world.getTasks();
    }
}
