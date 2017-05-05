package Cmd;

import Laba2.People;
import Laba2.Person;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by SlyFox on 05.03.2017.
 */
public class RemoveAll implements Command {

    /**
     * Remove all elements, which have values equals parametr (for more information see
     * {@link Person#equals(Object)})
     * @author SlyFox
     * @param objects person value which will compare
     * @return false
     * @see Person
     */
    @Override
    public boolean execute(Object... objects) {
        synchronized (Command.class)
        {
            Object [] set = People.GetPersons().keySet().toArray();

            for(int i=0;i<set.length;i++)
            {
                if(People.GetPersons().get(set[i]).equals((Person)objects[0]))
                {
                    People.GetPersons().remove(set[i]);
                }
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return "remove_all {element in json}";
    }

    @Override
    public Object[] read(String string) throws IOException, ClassNotFoundException
    {
        String[] strings = string.split(" ");

        for(String str: strings)
        {
            if(!str.equals(""))
            {
                ObjectMapper mapper = new ObjectMapper();
                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

                return new Object[]{mapper.readValue(string.split(str,2)[1], Class.forName(str))};
            }
        }
        throw new IOException("не найдено класса объекта");
    }
}
