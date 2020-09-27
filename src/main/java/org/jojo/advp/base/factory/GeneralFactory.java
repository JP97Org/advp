package org.jojo.advp.base.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jojo.advp.base.Person;
import org.jojo.advp.base.PersonProperty;
import org.jojo.advp.base.Task;
import org.jojo.advp.base.TaskProperty;

public class GeneralFactory {
    public GeneralFactory() {
        
    }
    
    public Person createPerson(final String name, final Set<PersonProperty> properties) {
        return new Person(Objects.requireNonNull(name), Objects.requireNonNull(properties));
    }
    
    public Task createTask(final String name, int numInstances, final Set<TaskProperty> properties) {
        return new Task(Objects.requireNonNull(name), numInstances, Objects.requireNonNull(properties));
    }
    
    public Task createTask(final TaskDescriptor taskDesc, final Set<TaskProperty> properties) {
        Objects.requireNonNull(taskDesc);
        Objects.requireNonNull(properties);
        return createTask(taskDesc.getName(), taskDesc.getNumInstances(), properties);
    }
    
    public Set<Person> createPersons(final Map<String, Set<PersonProperty>> personMap) {
        Objects.requireNonNull(personMap);
        final Set<Person> ret = new HashSet<>();
        
        personMap.entrySet().forEach(x -> ret.add(createPerson(x.getKey(), x.getValue())));
        
        return ret;
    }
    
    public Set<Task> createTasks(final Map<TaskDescriptor, Set<TaskProperty>> taskMap) {
        Objects.requireNonNull(taskMap);
        final Set<Task> ret = new HashSet<>();
        
        taskMap.entrySet().forEach(x -> ret.add(createTask(x.getKey(), x.getValue())));
        
        return ret;
    }
}