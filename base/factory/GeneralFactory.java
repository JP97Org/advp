package base.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import base.Person;
import base.PersonProperty;
import base.Task;
import base.TaskProperty;

public class GeneralFactory {
    public GeneralFactory() {
        
    }
    
    public Person createPerson(final String name, final Set<PersonProperty> properties) {
        return new Person(name, properties);
    }
    
    public Task createTask(final String name, int numInstances, final Set<TaskProperty> properties) {
        return new Task(name, numInstances, properties);
    }
    
    public Task createTask(final TaskDescriptor taskDesc, final Set<TaskProperty> properties) {
        return createTask(taskDesc.getName(), taskDesc.getNumInstances(), properties);
    }
    
    public Set<Person> createPersons(final Map<String, Set<PersonProperty>> personMap) {
        final Set<Person> ret = new HashSet<>();
        
        personMap.entrySet().forEach(x -> ret.add(createPerson(x.getKey(), x.getValue())));
        
        return ret;
    }
    
    public Set<Task> createTasks(final Map<TaskDescriptor, Set<TaskProperty>> taskMap) {
        final Set<Task> ret = new HashSet<>();
        
        taskMap.entrySet().forEach(x -> ret.add(createTask(x.getKey(), x.getValue())));
        
        return ret;
    }
}
