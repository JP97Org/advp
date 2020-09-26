package org.jojo.advp.testApp.natz;

import static org.jojo.advp.base.eq.IDs.nextID;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jojo.advp.base.Person;
import org.jojo.advp.base.PersonProperty;
import org.jojo.advp.base.eq.EqualEquivalenceKey;
import org.jojo.advp.base.eq.GenderEquivalenceKey;
import org.jojo.advp.base.eq.TimeEquivalenceKey;
import org.jojo.advp.base.eq.TimeInterval;

public class PersonFactory {
    public static final int ID_NEW = nextID();
    
    private Set<Person> persons;
    
    public PersonFactory() {
        this.persons = new HashSet<>();
    }
    
    public Person getPerson(final String name, final boolean female, final boolean newPerson, final List<TimeInterval> dates) {
        if(this.persons.stream().map(p -> p.getName()).anyMatch(p -> p.equals(name))) {
            return this.persons.stream().filter(p -> p.getName().equals(name)).iterator().next();
        } 
        
        final GenderEquivalenceKey genderKey = female ? GenderEquivalenceKey.FEMALE : GenderEquivalenceKey.MALE;
        final EqualEquivalenceKey<Boolean> newPersonKey = new EqualEquivalenceKey<>(ID_NEW, newPerson);
        
        Set<PersonProperty> properties = new HashSet<>();
        properties.add(genderKey.getPersonProperty());
        properties.add(newPersonKey.getPersonProperty());
        
        final HashSet<TimeInterval> datesSet = new HashSet<TimeInterval>();
        dates.forEach(x -> datesSet.add(new TimeInterval(x)));
        final TimeEquivalenceKey tek = new TimeEquivalenceKey(datesSet);
        properties.add(tek.getPersonProperty());
        
        final Person person = new Person(name, properties);
        this.persons.add(person);
        return person;
    }
}
