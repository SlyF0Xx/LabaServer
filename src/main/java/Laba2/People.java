package Laba2;

import java.util.*;

/**
 * Created by SlyFox on 28.02.2017.
 */
public class People {
    static volatile private Map<String, Person> persons;

    public static Map<String, Person> GetPersons()
    {
        return  persons;
    }

    public static String read(Scanner in)
    {
        while(true)
        {
            if(in.hasNext())
            {
                return in.next();
            }
        }
    }

    /**
     * Add new element to the collection
     *
     * @param person element to add
     * @author SlyFox
     * @see Person
     */
    public static void AddPerson(Person person) {
        persons.put(person.GetName(), person);
    }

    /**
     * Get element by Person name (key)
     *
     * @param name name of Person (for more information see
     *             {@link Person#GetName()})
     * @return element of collection with this name
     * @author SlyFox
     * @see Person
     */
    public static Person GetByName(String name) {
        return persons.get(name);
    }


    public People() {
        persons = new TreeMap<String, Person>();
    }


    /**
     * Add element if parametr is less than values with minimal key in this collection (for more information about comparate see
     * {@link Person#compareTo(Object)})
     * @author SlyFox
     * @param person value which will campare
     * @see Person
     */
    public void add_if_min_extended(Person person)
    {
        Person pers = (Person)((TreeMap) persons).firstEntry().getValue();

        if(pers.compareTo(person)==1)
        {
            AddPerson(person);
        }
    }
}
