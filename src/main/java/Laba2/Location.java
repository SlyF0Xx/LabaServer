/**
 * Created by SlyFox on 05.11.2016.
 */
package Laba2;

import ORM.Atribute;
import ORM.Entity;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@Entity(name = "Place")
public class Location implements Serializable {


    public boolean equals(Object obj)
    {
        if(Position.equals(((Location)obj).GetPosition()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        return Position.toString();
    }

    public int hashCode()
    {
        return Position.hashCode();
    }



    @Atribute(name = "Position", type = "TEXT", isPrimaryKey = true)
    @JsonProperty("Position")
    private String Position;

    public String GetPosition()
    {
        return Position;
    }

    public void SetPosition(String position)
    {
        Position = position;
    }

    public Location(String Position)
    {
        this.Position = Position;
    }

    public Location()
    {
        Position = "";
    }
}
