package base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class World {
    private static final String STD_NAME = "std-world";
    
    private final String name;
    private final Set<WorldProperty> properties; //TODO: noch unused, evtl. auch nicht benoetigt
    private final Set<Person> persons;
    private final Set<Task> tasks;
    
    private final Map<TaskInstance, Person> taskInstanceToPersonMap;
    
    public World(String name) {
        this.name = name;
        this.properties = new HashSet<>();
        this.persons = new HashSet<>();
        this.tasks = new HashSet<>();
        this.taskInstanceToPersonMap = new HashMap<>();
    }
    
    public World(String name, Set<WorldProperty> properties, Set<Person> persons, Set<Task> tasks) {
        this(name);
        this.properties.addAll(properties);
        this.persons.addAll(persons);
        this.tasks.addAll(tasks);
    }
    
    public World() {
        this(STD_NAME);
    }
    
    public World(Set<WorldProperty> properties, Set<Person> persons, Set<Task> tasks) {
        this(STD_NAME, properties, persons, tasks);
    }

    public String getName() {
        return name;
    }
    
    public boolean hasProperty(WorldProperty property) {
        return this.properties.contains(property);
    }
    
    public Set<Person> getPersons() {
        return persons;
    }
    
    public Set<Task> getTasks() {
        return tasks;
    }
    
    public boolean addProperty(WorldProperty property) {
        return this.properties.add(property);
    }
    
    public boolean addPerson(Person person) {
        return this.persons.add(person);
    }
    
    public boolean addTask(Task task) {
        return this.tasks.add(task);
    }
    
    public Person getPersonOfTaskInstance(Task task, int instanceNum) {
        return this.taskInstanceToPersonMap.get(pairGenerate(task, instanceNum));
    }
    
    public Set<Map.Entry<Task, Integer>> getTaskInstancesOfPerson(Person person) {
        Set<Map.Entry<Task, Integer>> ret = new HashSet<>();
        
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
    
    public boolean mapTaskInstanceToPerson(final Task taskArg, final Person personArg) {
        if(tasks.contains(taskArg) && persons.contains(personArg)) {
            final Task task = tasks.stream().filter(x -> x.equals(taskArg)).iterator().next();
            final Person person = persons.stream().filter(x -> x.equals(personArg)).iterator().next();
            
            final boolean ok = task.getNumberOfInstances() > 0 && check(task, person);
            
            if(ok) {
                final boolean b = task.decrementNumberOfInstances();
                if(!b) return false; //TODO: EXC in diesem Fall
                final TaskInstance key = pairGenerate(task);
                taskInstanceToPersonMap.put(key, person);
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
            // for every task property there must exist at least one matching person property
            boolean preRet = false;
            inner: 
            for(PersonProperty personProp : person.getProperties()) {
                preRet = taskProp.fulfills(personProp.getEquivalenceKey());
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
    
    public boolean isCompletelyMapped() {
        return tasks.stream().map(x -> x.getNumberOfInstances()).reduce(0, (x,y) -> x + y).intValue() == 0; 
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, properties, persons, tasks);
    }
    
    @Override
    public boolean equals(Object other) {
        if(other != null && getClass() == other.getClass()) {
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