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
public class AddIfMin implements Command {

    /**
     * Add element if parametr is less than all values of this collection (for more information about comparate see
     * {@link Person#compareTo(Object)}
     * @author SlyFox
     * @param objects person value which will compare
     * @return false
     * @see Person
     */
    @Override
    public boolean execute(Object... objects) throws Exception {
        synchronized (Command.class)
        {
            Object [] set = People.GetPersons().keySet().toArray();

            boolean flag = true;
            for(int i=0;i<set.length;i++)
            {
                if(People.GetPersons().get(set[i]).compareTo(((Person)objects[0]))==0)
                {
                    flag = false;
                }
            }
            if(flag)
            {
                People.AddPerson((Person)objects[0]);
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return "add_if_min {element in json}";
    }

    @Override
    public Object[] read(String string) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        String[] strings = string.split(" ");

        for(String str: strings)
        {
            if(!str.equals(""))
            {
                ObjectMapper mapper = new ObjectMapper();
                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

                Object temp;

                try
                {
                    temp = mapper.readValue(string.split(str,2)[1], Class.forName(str));
                }
                catch (Exception e)
                {
                    temp = Class.forName(str).newInstance();
                }

                return new Object[]{temp};
            }
        }

        //TODO возможно свой exception
        throw new IOException("не найдено класса объекта");
    }
}
