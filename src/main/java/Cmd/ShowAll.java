package Cmd;

import Laba2.People;
import Laba2.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by SlyFox on 07.03.2017.
 */
public class ShowAll implements Command {

    /**
     * Save elements in CSV in file
     * @param objects nothing
     * @return false
     * @throws Exception
     */
    @Override
    public boolean execute(Object... objects) throws Exception
    {
        synchronized (Command.class)
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            for(Person person: People.GetPersons().values())
            {
                System.out.println(mapper.writeValueAsString(person));
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return "show_all";
    }
}
