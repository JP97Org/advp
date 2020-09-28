package org.jojo.advp.interactive.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
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
import org.jojo.advp.base.eq.TimeInterval;
import org.jojo.advp.base.factory.GeneralFactory;
import org.jojo.advp.base.factory.KeyPairFactory;
import org.jojo.advp.base.factory.PropertySetFactory;
import org.jojo.advp.base.factory.TaskDescriptor;

public class InteractiveCore {
    private World world;
    private final List<KeyPairFactory> personsPreparation;
    private final List<KeyPairFactory> tasksPreparation;
    
    private String[] personNames;
    private String[] taskDescriptors;
    
    private boolean solved;
    
    public InteractiveCore() {
        this.personsPreparation = new ArrayList<>();
        this.tasksPreparation = new ArrayList<>();
        reset();
    }
    
    public void load(final String serializedPersonsPreparation, final String serializedTasksPreparation) throws ClassNotFoundException, IOException {
        reset();
        this.personsPreparation.addAll(deserialize(serializedPersonsPreparation));
        this.tasksPreparation.addAll(deserialize(serializedTasksPreparation));
    }
    
    public void loadPersonNames(final String[] personNames) {
        this.personNames = personNames;
    }
    
    public String[] finishLoadingPersonNames() {
        final String[] ret = this.personNames;
        this.personNames = null;
        return ret;
    }
    
    public void loadTaskDescriptors(final String[] taskDescriptors) {
        this.taskDescriptors = taskDescriptors;
    }
    
    public String[] finishLoadingTaskDescriptors() {
        final String[] ret = this.taskDescriptors;
        this.taskDescriptors = null;
        return ret;
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

    public boolean isSolved() {
        return isStarted() && solved;
    }
    
    public boolean isFullySolved() {
        return isStarted() && this.world.isCompletelyMapped();
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
    
    public void removePersonKey(int index, int innerIndex) {
        this.personsPreparation.get(index).remove(true, innerIndex);
    }
    
    public void removeTaskKey(int index, int innerIndex) {
        this.tasksPreparation.get(index).remove(false, innerIndex);
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
    
    public synchronized boolean solve() {
        this.solved = true;
        return this.world.solve();
    }
    
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

    public void reset() {
        this.world = null;
        this.solved = false;
        clearPreparations();
    }

    public boolean isPreparable(final int countPersons, final int countTasks) {
        return countPersons == personsPreparation.size() && countTasks == tasksPreparation.size();
    }
    
    public String serializePersonsPreparation() throws IOException, ClassNotFoundException {
        return serialize(personsPreparation);
    }
    
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
            return (List<KeyPairFactory>) ois.readObject();
        }
    }

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

    public Set<Task> getWorldTasks() {
        return this.world.getTasks();
    }
}
