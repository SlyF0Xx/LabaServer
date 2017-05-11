package Laba2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * Created by SlyFox on 28.02.2017.
 */
public class People {
    //static volatile private Map<String, Person> persons;
    private static Connection con;
    private static Statement st;
    public static Map<String, Person> GetPersons()
    {
        Map<String,Person> answer = new HashMap<String, Person>();
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            String sql = "SELECT * FROM laba7;";
            ResultSet result = st.executeQuery(sql);


            while(result.next())
            {
                String name =result.getString(1);
                String LocationPosition =result.getString(5);
                String ClassName = result.getString(4);
                Boolean IsCame = result.getBoolean(2);
                Boolean IsWait = result.getBoolean(3);

                sql = "SELECT * FROM leg WHERE owner='"  + name+ "';";
                Statement st2 = con.createStatement();
                ResultSet resultLeg = st2.executeQuery(sql);

                List<Leg> tmpLegs = new LinkedList<Leg>();
                while(resultLeg.next())
                {
                    tmpLegs.add(new Leg(resultLeg.getBoolean(2),resultLeg.getBoolean(3), Leg.Size.valueOf(resultLeg.getString(4))));
                }
                Leg LegAr[] = new Leg[tmpLegs.size()];
                LegAr = tmpLegs.toArray(LegAr);
                resultLeg.close();

                sql = "SELECT * FROM location WHERE name='"  + LocationPosition + "';";
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
    public static void AddPerson(Person person) {
        String sql;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            sql = "INSERT INTO location VALUES ('"+person.GetPlace().GetPosition()+"');";
            st.execute(sql);
            //return Person
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            sql = "INSERT INTO laba7 VALUES ('" + person.GetName() +
                    "', " + person.IsCame() + ", " + person.IsWait() + ", '" + person.getClass().getName() + "', '" + person.GetPlace().GetPosition() + "');";
            st.execute(sql);

            for(int i=0;i<person.GetLegCount();i++)
            {
                sql = "INSERT INTO leg VALUES (DEFAULT, '"+
                        person.GetLegs()[i].IsWashed() +"', '"+
                        person.GetLegs()[i].IsBarefoot()+"', '"+
                        person.GetLegs()[i].GetSize() +"','"+
                        person.GetName()+"', "+
                        i+") ;";
                st.execute(sql);
            }
        }
            catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void RemovePerson(String name)
    {
        String sql;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            sql = "DELETE FROM laba7 WHERE name='"+name+"';";
            st.execute(sql);
            //return Person
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void EditPerson(String name, Person newValue)
    {
        String sql;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            sql = "UPDATE laba7 SET (iscame, iswait, location) = ("+newValue.IsCame()+", "+newValue.IsWait()+", '"+newValue.GetPlace().GetPosition()+"') WHERE name = '"+name+"';";
            st.execute(sql);


            sql = "SELECT * FROM leg WHERE owner ='"+name+"';";
            ResultSet result =  st.executeQuery(sql);

            int i=0;
            while(result.next())
            {
                sql = "UPDATE leg SET (iswashed, isbarefoot, legsize) = ("+newValue.GetLegs()[i].IsWashed()+", "+newValue.GetLegs()[i].IsBarefoot()+", '"+newValue.GetLegs()[i].GetSize()+"') " +
                        "WHERE (owner = '"+name+"') AND (index = "+i+");";
                st.execute(sql);
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get element by Person name (key)
     *
     * @param name name of Person (for more information see
     *             {@link Person#GetName()})
     * @return element of collection with this name
     * @author SlyFox
     * @see Person
     */
    public static Person GetByName(String name) {
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            String sql = "SELECT * FROM laba7 WHERE name='"  + name + "';";
            ResultSet result = st.executeQuery(sql);

            result.next();

            String LocationPosition =result.getString(5);
            String ClassName = result.getString(4);
            Boolean IsCame = result.getBoolean(2);
            Boolean IsWait = result.getBoolean(3);

            sql = "SELECT * FROM leg WHERE owner='"  + name+ "';";
            Statement st2 = con.createStatement();
            ResultSet resultLeg = st2.executeQuery(sql);

            List<Leg> tmpLegs = new LinkedList<Leg>();
            while(resultLeg.next())
            {
                tmpLegs.add(new Leg(resultLeg.getBoolean(2),resultLeg.getBoolean(3), Leg.Size.valueOf(resultLeg.getString(4))));
            }
            Leg LegAr[] = new Leg[tmpLegs.size()];
            LegAr = tmpLegs.toArray(LegAr);
            resultLeg.close();

            sql = "SELECT * FROM location WHERE name='"  + LocationPosition + "';";
            ResultSet result2 = st2.executeQuery(sql);

            result2.next();

            Location Place = new Location(result2.getString(1));

            Class pers = Class.forName(ClassName);
            Constructor[] constructors = pers.getConstructors();
            for (Constructor constructor : constructors) {
                Class[] paramTypes = constructor.getParameterTypes();

                if(paramTypes.length  == (pers.getDeclaredFields().length+ pers.getSuperclass().getDeclaredFields().length)-2)//TODO магическое число неплохо бы убрать
                {
                    return (Person) constructor.newInstance(LegAr, Place ,name,IsCame, IsWait);
                }
            }
            result2.close();
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
        return null;
        //return persons.get(name);
    }


    public People() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres" +
                    "", "postgres", "vbntkmigbkm1");
            st = con.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //persons = new TreeMap<String, Person>();
    }

}
