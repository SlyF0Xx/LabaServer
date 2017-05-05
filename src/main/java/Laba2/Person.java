/**
 * Created by SlyFox on 05.11.2016.
 */
package Laba2;

import Exceptions.ExceptionWrongName;
import IO.NotParse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

/**
 * @author SlyFox
 */
public abstract class Person  implements Waitable,Subscribable,Comparable, Serializable
{
    /**
     *  Smth
     * @param o Person which will campare
     * @return 1 if hashCode parametr is bigger than hashCode of this object
     * 0 if not =)
     */
    @Override
    public int compareTo(Object o) {
        if(o.hashCode()<hashCode())
        //if(o.hashCode()<=hashCode())
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public boolean equals(Object obj)
    {
        if(Legs.length == ((Person)obj).GetLegs().length)
        {
            for(int i=0;i<Legs.length;i++)
            {
                if(!(Legs[i].equals(((Person)obj).GetLegs()[i])))
                {
                    return false;
                }
            }
            return  Name.equals(((Person)obj).GetName());
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        return GetName();
    }

    public int hashCode()
    {
        return Legs.hashCode()+Name.hashCode();
    }

    static class Info
    {
        public static void ToDo()
        {
            System.out.println("ToDo ToDo ToDoToDoToDo ToDo ToDooooooo ToDo");
        }
    }

    @JsonProperty("Legs")
    private Leg Legs[];

    @JsonProperty("Place")
    private Location Place;

    @JsonProperty("Name")
    private String Name;

    @JsonProperty("Came")
    private boolean Came;

    @JsonProperty("wait")
    private boolean wait;

    @NotParse
    @JsonIgnore
    private Person SubjectForSubscribing;

    @NotParse
    @JsonIgnore
    private Map<String,Person> subscribers;


    public Person GetSubjectForSubscribing(){return SubjectForSubscribing;}

    public boolean IsWait()
    {
        return wait;
    }

    public void SetWait(boolean wait)
    {
        this.wait = wait;
    }

    public boolean IsCame()
    {
        return Came;
    }

    public StringProperty IsVisualCame(){return new SimpleStringProperty(String.valueOf(Came));}

    public String GetName()
    {
        return Name;
    }

    public StringProperty GetVisualName() {return new SimpleStringProperty(Name);}

    public Location GetPlace()
    {
        return Place;
    }

    public Leg[] GetLegs()
    {
        return Legs;
    }

    public int GetLegCount()
    {
        return Legs.length;
    }

    public abstract void See(Person Who);

    public void Notifyed(Person Who){}


    public void Waiting(Person subject)
    {
        wait = true;
        SubjectForSubscribing = subject;
        subject.Subscribe(this);
    }

    public void StopWaiting()
    {
        wait = false;
        SubjectForSubscribing.Unsubscribe(this.GetName());
        SubjectForSubscribing = null;
    }

    public void Come(Location Where)
    {
        System.out.println("Персонаж "+Name + " переместился из " + Place.GetPosition() + " в " + Where.GetPosition());

        Came = true;
        Place = Where;
        for(Map.Entry<String,Person> entry : subscribers.entrySet())
        {
            if(Where.equals(entry.getValue().GetPlace()))
            {
                entry.getValue().Notifyed(this);
            }
        }
    }

    public void Subscribe(Person subscriber)
    {
        subscribers.put(subscriber.Name, subscriber);
    }

    public void Unsubscribe(String name)
    {
        subscribers.remove(name);
    }

    public Map<String,Person> GetSubscribers(){return subscribers;}


    public Person(Leg[] Legs,Location Place,String Name) throws ExceptionWrongName
    {
        if(Legs.length==1)
        {
            this.Legs = new Leg[1];
            this.Legs = Legs;
        }
        else
        {
            this.Legs = new Leg[2];
            this.Legs[0] = Legs[0];
            this.Legs[1] = Legs[1];
        }

        //Pattern p = Pattern.compile("[a-z,A-Z,А-Я,а-я]+,' ',[a-z,A-Z,А-Я,а-я]+");
        Pattern p = Pattern.compile("[a-z,A-Z,А-Я,а-я]+\\s?[a-z,A-Z,А-Я,а-я]+");
        Matcher m = p.matcher(Name);
        if (m.find())
        {
            throw new ExceptionWrongName();
        }
        this.Name = Name;
        this.Place = Place;
        //subscribers = new HashMap<String,Person>();
        subscribers = new HashMap<String,Person>();
    }

    public Person()
    {
        Legs = new Leg[2];
        Legs[0] = new Leg();
        Legs[1] = new Leg();
        Name = "";
        Place = new Location();
        //subscribers = new HashMap<String,Person>();
        subscribers = new HashMap<String,Person>();
    }
}
