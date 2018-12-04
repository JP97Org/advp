package base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import base.eq.TimeEquivalenceKey;
import base.eq.TimeInterval;

/**
 * Represents a world of a task-person-mapping optimization problem.
 * 
 * @author jojo
 * @version 0.9
 */
public class World {
    private static final String STD_NAME = "std-world";
    
    private final String name;
    private final Set<WorldProperty> properties; //TODO: noch unused, evtl. auch nicht benoetigt
    private final Set<Person> persons;
    private final Set<Task> tasks;
    
    private final Map<TaskInstance, Person> taskInstanceToPersonMap;
    
    private Solver solver;
    
    /**
     * Creates a new World with the given name.
     * 
     * @param name - the given name
     */
    public World(String name) {
        this.name = name;
        this.properties = new HashSet<>();
        this.persons = new HashSet<>();
        this.tasks = new HashSet<>();
        this.taskInstanceToPersonMap = new HashMap<>();
        this.solver = null;
    }
    
    /**
     * Creates a new World with the given name and properties.
     * 
     * @param name
     * @param properties
     * @param persons
     * @param tasks
     */
    public World(String name, Set<WorldProperty> properties, Set<Person> persons, Set<Task> tasks) {
        this(name);
        this.properties.addAll(properties);
        this.persons.addAll(persons);
        this.tasks.addAll(tasks);
    }
    
    /**
     * Creates an empty standard world.
     */
    public World() {
        this(STD_NAME);
    }
    
    /**
     * Creates a new standard World with the given properties.
     * 
     * @param name
     * @param properties
     * @param persons
     * @param tasks
     */
    public World(Set<WorldProperty> properties, Set<Person> persons, Set<Task> tasks) {
        this(STD_NAME, properties, persons, tasks);
    }

    /**
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * TODO
     * 
     * @param property - 
     * @return
     */
    public boolean hasProperty(WorldProperty property) {
        return this.properties.contains(property);
    }
    
    /**
     * 
     * @return the persons
     */
    public Set<Person> getPersons() {
        return persons;
    }
    
    /**
     * 
     * @return the tasks
     */
    public Set<Task> getTasks() {
        return tasks;
    }
    
    /**
     * Adds the given property.
     * 
     * @param property - the given property
     * @return whether it was added
     */
    public boolean addProperty(WorldProperty property) {
        return this.properties.add(property);
    }
    
    /**
     * Adds the given person.
     * 
     * @param person - the given person
     * @return whether it was added
     */
    public boolean addPerson(Person person) {
        return this.persons.add(person);
    }
    
    /**
     * Adds the given task.
     * 
     * @param task - the given task
     * @return whether it was added
     */
    public boolean addTask(Task task) {
        return this.tasks.add(task);
    }
    
    /**
     * Gets the person mapped to the given task instance.
     * 
     * @param task - the task
     * @param instanceNum - the instance index
     * @return the person mapped to the given task instance or null if no person is mapped to the instance
     */
    public Person getPersonOfTaskInstance(Task task, int instanceNum) {
        return this.taskInstanceToPersonMap.get(pairGenerate(task, instanceNum));
    }
    
    /**
     * Gets all task instances of the given person.
     * 
     * @param person - the given person
     * @return a set of TaskInstance which are mapped to the given person
     */
    public Set<TaskInstance> getTaskInstancesOfPerson(Person person) {
        Set<TaskInstance> ret = new HashSet<>();
        
        if(this.taskInstanceToPersonMap.containsValue(person)) {
            Set<TaskInstance> keySet = this.taskInstanceToPersonMap.keySet();
            for(TaskInstance key : keySet) {
                if(this.taskInstanceToPersonMap.get(key).equals(person)) {
                    ret.add(key);
                }
            }
        }
        
        return ret;
    }
    
    /**
     * Sets the given solver and sets the solver's world to this one.
     * 
     * @param solver - the given solver
     */
    public void setSolver(Solver solver) {
        this.solver = solver;
        this.solver.setWorld(this);
    }
    
    /**
     * Gets the solver.
     * 
     * @return the solver or null if none is set
     */
    public Solver getSolver() {
        return this.solver;
    }
    
    /**
     * Calls solve on the set solver.
     * 
     * @return whether solver's solve returned true
     * @throws UnsupportedOperationException - if no solver is set
     */
    public boolean solve() throws UnsupportedOperationException {
        if(this.solver != null) {
            return solver.solve();
        }
        throw new UnsupportedOperationException("No solver specified!");
    }
    
    /**
     * Tries to map one instance of the given task to the given person. <br />
     * Does nothing and returns false if the task or the person does not exist in this world. <br />
     * Also returns false if the task has no further instances which could be mapped. <br /> <br />
     * Returns true and maps only if this is valid: <br />
     * - for all task properties must exist at least one matching person property. <br />
     * - Two properties match, if one of them fulfills the other.
     * 
     * @param taskArg - the given task
     * @param personArg - the given person
     * @return whether the mapping succeeded
     */
    public boolean mapTaskInstanceToPerson(final Task taskArg, final Person personArg) {
        if(tasks.contains(taskArg) && persons.contains(personArg)) {
            final Task task = tasks.stream().filter(x -> x.equals(taskArg)).iterator().next();
            final Person person = persons.stream().filter(x -> x.equals(personArg)).iterator().next();
            
            final boolean ok = task.getNumberOfInstances() > 0 && check(task, person);
            
            if(ok) {
                final boolean b = task.decrementNumberOfInstances();
                assert b;
                final TaskInstance key = pairGenerate(task);
                taskInstanceToPersonMap.put(key, person);
                task.mapped();
                final TimeInterval ti = task.getProperties()
                        .stream()
                        .filter(x -> x.getEquivalenceKey().getClass().equals(TimeEquivalenceKey.class))
                        .map(x -> TimeEquivalenceKey.class.cast(x.getEquivalenceKey()))
                        .map(x -> x.getTimeIntervals())
                        .iterator().next().iterator().next();
                person.mapped(ti);
                return true;
            }
            return false;
        }
        return false; //TODO: evtl. auch EXC, wenn sowas versucht wird (person || task nicht da)
    }
    
    private boolean check(final Task task, final Person person) {
        boolean ret = true;
        
        outer: 
        for(TaskProperty taskProp : task.getProperties()) {
            // For every task property there must exist at least one matching person property.
            // Two properties match, if one of them fulfills the other.
            boolean preRet = false;
            inner: 
            for(PersonProperty personProp : person.getProperties()) {
                EquivalenceKey[] keys = new EquivalenceKey[]
                        {taskProp.getEquivalenceKey(), personProp.getEquivalenceKey()};
                preRet =    keys[0].getID() == keys[1].getID()
                         && (personProp.fulfills(keys[0])
                             || taskProp.fulfills(keys[1]));
                if(preRet) break inner;
            }
            ret &= preRet;
            if(!ret) break outer;
        }
        
        return ret;
    }
    
    private static TaskInstance pairGenerate(Task task) {
        return pairGenerate(task, task.getNumberOfInstances());
    }
    
    private static TaskInstance pairGenerate(Task task, int num) {
        return new TaskInstance(task, num);
    }
    
    /**
     * Determines whether this world is completely mapped.
     * 
     * @return whether this world is completely mapped
     */
    public boolean isCompletelyMapped() {
        return tasks.stream().map(x -> x.getNumberOfInstances()).reduce(0, (x,y) -> x + y).intValue() == 0; 
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, properties, persons, tasks);
    }
    
    @Override
    public boolean equals(Object other) {
        if(other != null && getClass().equals(other.getClass())) {
            final World o = (World)other;
            return  name.equals(o.name) && 
                    properties.equals(o.properties) &&
                    persons.equals(o.persons) &&
                    tasks.equals(o.tasks);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.name
                + " | " 
                + setStr(this.properties)
                + " | "
                + setStr(this.persons)
                + " | "
                + setStr(this.tasks);
    }
    
    private static <T> String setStr(Set<T> set) {
        return set.stream().map(p -> p.toString()).reduce("", (s,t) -> s + " | " + t);
    }
}
