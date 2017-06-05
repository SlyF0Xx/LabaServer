package Laba2;

import IO.NotParse;
import ORM.Atribute;
import ORM.DataWraper;
import ORM.Entity;

import javax.naming.Name;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * Created by SlyFox on 28.02.2017.
 */
public class People {
    //static volatile private Map<String, Person> persons;
    private static Connection con;
   // private static Statement st;

    public static  Map<String, Person> GetPersons()
    {
        List<Person> persons = dataWraper.getAll(FrekenBok.class);
        Map<String, Person> personMap = new HashMap<>();

        persons.forEach( (i)->
                personMap.put(i.GetName(), i)
        );

        persons = dataWraper.getAll(LitleBoy.class);
        persons.forEach( (i)->
                personMap.put(i.GetName(), i)
        );

        return personMap;
    }

    @Deprecated
    public static Map<String, Person> OldGetPersons()
    {
        Map<String,Person> answer = new HashMap<String, Person>();
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            String sql = "SELECT * FROM person;";
            Statement stl = con.createStatement();
            ResultSet result = stl.executeQuery(sql);


            while(result.next())
            {
                String name =result.getString(1);
                String LocationPosition =result.getString(5);
                String ClassName = result.getString(4);
                Boolean IsCame = result.getBoolean(2);
                Boolean IsWait = result.getBoolean(3);

                sql = "SELECT count(*) FROM legs WHERE owner='"  + name+ "';";
                Statement st2 = con.createStatement();
                ResultSet resultLeg = st2.executeQuery(sql);

                resultLeg.next();
                Leg LegAr[] = new Leg[resultLeg.getInt(1)];

                for(int i = 0; i<LegAr.length;i++)
                {
                    sql = "SELECT * FROM legs WHERE owner='"  + name+ "' AND index = '"+ i+"';";
                    st2 = con.createStatement();
                    resultLeg = st2.executeQuery(sql);
                    resultLeg.next();
                    LegAr[i] = new Leg(resultLeg.getBoolean(2),resultLeg.getBoolean(3), Leg.Size.valueOf(resultLeg.getString(4)));
                }

                resultLeg.close();

                sql = "SELECT * FROM place WHERE name='"  + LocationPosition + "';";
                ResultSet result2 = st2.executeQuery(sql);

                result2.next();

                Location Place = new Location(result2.getString(1));

                Class pers = Class.forName(ClassName);
                Constructor[] constructors = pers.getConstructors();
                for (Constructor constructor : constructors) {
                    Class[] paramTypes = constructor.getParameterTypes();

                    if(paramTypes.length  == (pers.getDeclaredFields().length+ pers.getSuperclass().getDeclaredFields().length)-2)//TODO магическое число неплохо бы убрать
                    {
                        answer.put(name,(Person) constructor.newInstance(LegAr, Place ,name,IsCame, IsWait));
                        break;
                    }
                }
                result2.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return answer;

        //return  persons;
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
    public static void AddPerson(Person person)
    {
        dataWraper.addRecordTrans(person, dataWraper.tables.get(Person.class.getAnnotation(Entity.class).name()), null);
    }

    static DataWraper dataWraper;

    public static void RemovePerson(String name)
    {
        List<String> keys = new LinkedList<>();
        keys.add(name);
        dataWraper.deleteRecrdTrans(FrekenBok.class, keys);
        dataWraper.deleteRecrdTrans(LitleBoy.class, keys);
    }


    public static void EditPerson(String name, Person newValue)
    {
        List<String> keys = new LinkedList<>();
        keys.add(name);
        dataWraper.deleteRecrdTrans(FrekenBok.class, keys);

        dataWraper.deleteRecrdTrans(LitleBoy.class, keys);
        dataWraper.addRecordTrans(newValue, dataWraper.tables.get(Person.class.getAnnotation(Entity.class).name()), null);
    }

    /*
    public static void CheckTables()
    {
        Statement stl = null;
        try {
            stl = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            String sql = "SELECT * FROM place;";
            stl.execute(sql);
        } catch (SQLException e) {
            CreateDataTable(Location.class);
        }

        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            String sql = "SELECT * FROM legs;";
            stl.execute(sql);
        } catch (SQLException e) {
            CreateDataTable(Leg.class);
        }

        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            String sql = "SELECT * FROM person;";
            stl.execute(sql);
        } catch (SQLException e) {
            CreateDataTable(Person.class);
        }
    }
    */

    /**
     * Get element by Person name (key)
     *
     * @param name name of Person (for more information see
     *             {@link Person#GetName()})
     * @return element of collection with this name
     * @author SlyFox
     * @see Person
     */
    public static Person GetByName(String name)
    {
        List<String> keys = new LinkedList<>();
        keys.add(name);
        try {
            return (Person) dataWraper.getRecordTrans(FrekenBok.class, keys);
        }catch (Exception e) {
            return (Person) dataWraper.getRecordTrans(LitleBoy.class, keys);
        }
    }

    public static void CreateDataTable()
    {
        dataWraper.createDataTableTrns(Person.class, null, null);
    }

    public People() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres" +
                    "", "postgres", "vbntkmigbkm1");
            dataWraper = new DataWraper();
            dataWraper .Delete();
            dataWraper.createDataTableTrns(Person.class, null,null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //persons = new TreeMap<String, Person>();
    }

}
