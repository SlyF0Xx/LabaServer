/**
 * Created by SlyFox on 05.11.2016.
 */
package Laba2;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Leg implements Serializable {


    public enum Size
    {
        Small,Medium,Big ;
    };


    public boolean equals(Object obj)
    {
        if(Washed = ((Leg)obj).IsWashed() &&
           Barefoot ==  ((Leg)obj).IsBarefoot() &&
           LegSize.equals(((Leg)obj).GetSize()))
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
        return LegSize.toString();
    }

    public int hashCode()
    {
        return (Washed?1:0) + (Barefoot?1:0) + LegSize.hashCode();
    }


    @JsonProperty("Washed")
    private boolean Washed;

    @JsonProperty("Barefoot")
    private boolean Barefoot;

    @JsonProperty("LegSize")
    private Size LegSize;

    public boolean IsWashed()
    {
        return Washed;
    };
    public boolean IsBarefoot()
    {
        return Barefoot;
    };
    public Size GetSize()
    {
        return LegSize;
    };
    public void SetWashed(boolean washed)
    {
        Washed = washed;
    }
    public void SetBarefoot(boolean barefoot)
    {
        Barefoot = barefoot;
    }
    public void SetSize(Size legSize)
    {
        LegSize=legSize;
    }
    public Leg(boolean Washed,boolean Barefoot,Size LegSize)
    {
        this.Washed = Washed;
        this.Barefoot = Barefoot;
        this.LegSize = LegSize;
    }
    public Leg()
    {
        Washed = false;
        Barefoot = false;
        LegSize = Size.Small;
    }
}
