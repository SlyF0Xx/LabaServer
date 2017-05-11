/**
 * Created by SlyFox on 05.11.2016.
 */
package Laba2;

import Exceptions.ExceptionWrongName;

public class LitleBoy extends Person {
    @Override
    public void See(Person Who)
    {
        System.out.print(this.GetName() + " увидел в " + Who.GetPlace().GetPosition());
        if(Who.GetLegs().length ==2)
        {
            System.out.println(
                    (Who.GetLegs()[0].GetSize() == Who.GetLegs()[1].GetSize()? (Who.GetLegs()[1].GetSize() == Leg.Size.Big ? " большие " : (Who.GetLegs()[1].GetSize() == Leg.Size.Medium ? " средние ": " маленькие ")):"разные по размеру")+
                            (Who.GetLegs()[0].IsWashed() == Who.GetLegs()[1].IsWashed()? (Who.GetLegs()[0].IsWashed()? "вымытые " : "не вымытые ") : "вымытую и не вымытую ")+
                            (Who.GetLegs()[0].IsBarefoot()== Who.GetLegs()[1].IsBarefoot()? (Who.GetLegs()[0].IsBarefoot()? "босые " : "обутые ") :"обутую и необутую ")+ "ноги");
        }
        else
        {
            System.out.println(Who.GetLegs()[0].GetSize()+
                    (Who.GetLegs()[0].IsWashed()? " вымытую " : " не вымытую ")+
                    (Who.GetLegs()[0].IsBarefoot()? "босую ": "обутую ")+"ногу");
        }

    }

    public void Reaction(Person Who)
    {
        if(Who.GetLegs().length==1)
        {
            System.out.print(this.GetName()+" затрепетал");
        }
        else
        {
            if(Who.GetLegs()[0].GetSize()==Leg.Size.Big && Who.GetLegs()[0].IsBarefoot() && Who.GetLegs()[0].IsWashed()
                    &&Who.GetLegs()[1].GetSize()==Leg.Size.Big && Who.GetLegs()[1].IsBarefoot() && Who.GetLegs()[1].IsWashed())
            {
                System.out.print(this.GetName()+" затрепетал");
            }
            else
            {
                System.out.print(this.GetName()+" не затрепетал");
            }
        }

    }

    public void SillyBoy(Person Who)
    {
        if(this.IsWait())
        {
            Reaction(Who);
            System.out.println(" хотя ждал");
        }
        else
        {
            Reaction(Who);
            System.out.println(" т.к. не ждал");
        }
    }

    @Override
    public void Notifyed(Person Who)
    {
        See(Who);
        SillyBoy(Who);
    }
    public LitleBoy(Leg[] Legs,Location Place,String Name)throws ExceptionWrongName
    {
        super(Legs,Place,Name);
    }

    public LitleBoy(Leg[] Legs,Location Place, String Name, Boolean IsCame, Boolean IsWait)throws ExceptionWrongName
    {
        super(Legs,Place,Name, IsCame, IsWait);
    };

    public LitleBoy()
    {
        super();
    }
}

