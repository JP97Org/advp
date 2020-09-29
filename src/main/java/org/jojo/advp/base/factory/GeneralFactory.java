package org.jojo.advp.base.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jojo.advp.base.Person;
import org.jojo.advp.base.PersonProperty;
import org.jojo.advp.base.Task;
import org.jojo.advp.base.TaskProperty;

/**
 * Represents a general factory for person and task creation.
 * 
 * @author jojo
 * @version 0.9
 */
public class GeneralFactory {
    public GeneralFactory() {
        
    }
    
    /**
     * Creates a new person with the given name and properties.
     * 
     * @param name - the name
     * @param properties - the set of properties
     * @return a new person with the given name and properties
     */
    public Person createPerson(final String name, final Set<PersonProperty> properties) {
        return new Person(Objects.requireNonNull(name), Objects.requireNonNull(properties));
    }
    
    /**
     * Creates a new task with the given name, number of instances and properties.
     * 
     * @param name - the name
     * @param numInstances - the number of instances
     * @param properties - the set of properties
     * @return a new task with the given name, number of instances and properties
     */
    public Task createTask(final String name, int numInstances, final Set<TaskProperty> properties) {
        return new Task(Objects.requireNonNull(name), numInstances, Objects.requireNonNull(properties));
    }
    
    /**
     * Creates a new task with the given name, number of instances and properties.
     * 
     * @param taskDesc - the task descriptor consisting of name and number of instances
     * @param properties - the set of properties
     * @return a new task with the given name, number of instances and properties
     * @see {@link TaskDescriptor}
     */
    public Task createTask(final TaskDescriptor taskDesc, final Set<TaskProperty> properties) {
        Objects.requireNonNull(taskDesc);
        Objects.requireNonNull(properties);
        return createTask(taskDesc.getName(), taskDesc.getNumInstances(), properties);
    }
    
    /**
     * Creates a set of persons with the given properties.
     * 
     * @param personMap - a map from names to a set of properties
     * @return a set of persons with the given properties
     */
    public Set<Person> createPersons(final Map<String, Set<PersonProperty>> personMap) {
        Objects.requireNonNull(personMap);
        final Set<Person> ret = new HashSet<>();
        
        personMap.entrySet().forEach(x -> ret.add(createPerson(x.getKey(), x.getValue())));
        
        return ret;
    }
    
    /**
     * Creates a set of tasks with the given properties.
     * 
     * @param taskMap - a map from task descriptors to properties
     * @return a set of tasks with the given properties
     * @see {@link TaskDescriptor}
     */
    public Set<Task> createTasks(final Map<TaskDescriptor, Set<TaskProperty>> taskMap) {
        Objects.requireNonNull(taskMap);
        final Set<Task> ret = new HashSet<>();
        
        taskMap.entrySet().forEach(x -> ret.add(createTask(x.getKey(), x.getValue())));
        
        return ret;
    }
}
