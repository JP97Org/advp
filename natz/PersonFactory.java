package natz;

import java.util.HashSet;
import java.util.Set;

import base.Person;
import base.PersonProperty;
import base.eq.EqualEquivalenceKey;
import base.eq.GenderEquivalenceKey;

public class PersonFactory {
    public static final int ID_NEW = 1; //TODO: better ID-management
    
    private Set<Person> persons;
    
    public PersonFactory() {
        this.persons = new HashSet<>();
    }
    
    public Person getPerson(final String name, final boolean female, final boolean newPerson) {
        if(this.persons.stream().map(p -> p.getName()).anyMatch(p -> p.equals(name))) {
            return this.persons.stream().filter(p -> p.getName().equals(name)).iterator().next();
        } 
        
        final GenderEquivalenceKey genderKey = female ? GenderEquivalenceKey.FEMALE : GenderEquivalenceKey.MALE;
        final EqualEquivalenceKey<Boolean> newPersonKey = new EqualEquivalenceKey<>(ID_NEW, newPerson);
        
        Set<PersonProperty> properties = new HashSet<>();
        properties.add(genderKey.getPersonProperty());
        properties.add(newPersonKey.getPersonProperty());
        
        final Person person = new Person(name, properties);
        this.persons.add(person);
        return person;
    }
}
