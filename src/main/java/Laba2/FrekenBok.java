package Laba2;

import Exceptions.ExceptionWrongName;

/**
 * Created by SlyFox on 05.11.2016.
 */
public class FrekenBok extends Person {
    @Override
    public void See(Person Who)
    {
        System.out.println(this.GetName() + " грозно посмотрела на "+ Who.GetName()+"a");
    }

    public FrekenBok(Leg[] Legs,Location Place, String Name)throws ExceptionWrongName
    {
        super(Legs,Place,Name);
    };
    public FrekenBok()
    {
        super();
    };
}
