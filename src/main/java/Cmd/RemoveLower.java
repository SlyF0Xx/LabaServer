package Cmd;


import Laba2.People;

/**
 * Created by SlyFox on 05.03.2017.
 */
public class RemoveLower implements Command {


    /**
     * Remove elements which key is lower than parametr (for more information see {@link  String#compareTo(String)})
     * @author SlyFox
     * @param objects String which will comapre
     * @return false
     * @see String
     */
    @Override
    public boolean execute(Object... objects) {
        synchronized (Command.class)
        {
            Object [] set = People.GetPersons().keySet().toArray();

            for(int i=0;i<set.length;i++)
            {
                if(((String)set[i]).compareTo((String) objects[0])<0)
                {
                    //TODO exception на пустую коллекцию
                    People.RemovePerson((String) set[i]);
                    //People.GetPersons().remove(set[i]);
                }
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return "remove_lower String";
    }
}
