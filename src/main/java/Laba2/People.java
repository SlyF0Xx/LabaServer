package Laba2;

import IO.NotParse;
import ORM.Atribute;
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
    private static Statement st;
    public static  Map<String, Person> GetPersons()
    {
        Map<String,Person> answer = new HashMap<String, Person>();
        List<String> temp = new LinkedList<>();
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            String sql = "SELECT * FROM person;";
            ResultSet result = st.executeQuery(sql);


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
                    temp.add(String.valueOf(resultLeg.getBoolean(2)));
                    temp.add(String.valueOf(resultLeg.getBoolean(3)));
                    temp.add(resultLeg.getString(4));
                }

                resultLeg.close();

                sql = "SELECT * FROM place WHERE position='"  + LocationPosition + "';";
                ResultSet result2 = st2.executeQuery(sql);

                result2.next();

                temp.add(0, String.valueOf(LegAr.length));
                temp.add(name);
                temp.add(IsCame.toString());
                temp.add(IsWait.toString());
                temp.add(result2.getString(1));


                Person pers = (Person) Class.forName(ClassName).newInstance();

                IO.Parser.podfunc(pers, temp, 0);
                answer.put(name, pers);
                result2.close();
                temp.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return answer;
    }

    @Deprecated
    public static Map<String, Person> OldGetPersons()
    {
        Map<String,Person> answer = new HashMap<String, Person>();
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            String sql = "SELECT * FROM person;";
            ResultSet result = st.executeQuery(sql);


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
    public static void AddPerson(Person person) {
        String sql;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            sql = "INSERT INTO place VALUES ('"+person.GetPlace().GetPosition()+"');";
            st.execute(sql);
            //return Person
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            sql = "INSERT INTO person VALUES ('" + person.GetName() +
                    "', " + person.IsCame() + ", " + person.IsWait() + ", '" + person.getClass().getName() + "', '" + person.GetPlace().GetPosition() + "');";
            st.execute(sql);

            for(int i=0;i<person.GetLegCount();i++)
            {
                sql = "INSERT INTO legs VALUES (DEFAULT, '"+
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

            sql = "DELETE FROM person WHERE name='"+name+"';";
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

            sql = "SELECT position FROM place WHERE position ='"+newValue.GetPlace().GetPosition()+"';";
            ResultSet result1 = st.executeQuery(sql);

            if(!result1.next())
            {
                sql = "INSERT INTO place VALUES('"+newValue.GetPlace().GetPosition()+"');";
                st.execute(sql);
            }

            sql = "UPDATE person SET (iscame, iswait, place) = ("+newValue.IsCame()+", "+newValue.IsWait()+", '"+newValue.GetPlace().GetPosition()+"') WHERE name = '"+name+"';";
            st.execute(sql);


            sql = "SELECT * FROM legs WHERE owner ='"+name+"';";
            ResultSet result =  st.executeQuery(sql);

            Statement st2 = con.createStatement();


            int i=0;

            while(result.next());
            {
                sql = "UPDATE legs SET (iswashed, isbarefoot, legsize) = ("+newValue.GetLegs()[i].IsWashed()+", "+newValue.GetLegs()[i].IsBarefoot()+", '"+newValue.GetLegs()[i].GetSize()+"') " +
                        "WHERE (owner = '"+name+"') AND (index = "+i+");";
                st2.execute(sql);
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
        List<String> temp = new LinkedList<>();
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            String sql = "SELECT * FROM person WHERE name='"  + name + "';";
            ResultSet result = st.executeQuery(sql);

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
                temp.add(String.valueOf(resultLeg.getBoolean(2)));
                temp.add(String.valueOf(resultLeg.getBoolean(3)));
                temp.add(resultLeg.getString(4));
            }

            resultLeg.close();

            sql = "SELECT * FROM place WHERE position='"  + LocationPosition + "';";
            ResultSet result2 = st2.executeQuery(sql);

            result2.next();

            temp.add(result2.getString(1));

            temp.add(0, String.valueOf(LegAr.length));
            temp.add(name);
            temp.add(IsCame.toString());
            temp.add(IsWait.toString());

            Person pers = (Person) Class.forName(ClassName).newInstance();

            IO.Parser.podfunc(pers, temp, 0);
            return  pers;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void CreateDataTable(Class target)
    {
        try {
            String sql;
            String atributes= "";
            String primaryKey = "";

            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            Field[] publicFields = target.getDeclaredFields();
            for (int i = 0; i < 2; i++) {
                for (Field field : publicFields) {
                    field.setAccessible(true);

                    if(field.isAnnotationPresent(Atribute.class))
                    {
                        atributes += ((Atribute) field.getAnnotation(Atribute.class)).name() + " " +
                                ((Atribute) field.getAnnotation(Atribute.class)).type() + " " +
                                (((Atribute) field.getAnnotation(Atribute.class)).Reference().equals("")? "": ("REFERENCES " +
                                        ((Atribute) field.getAnnotation(Atribute.class)).Reference() )) +",";

                        primaryKey += ((Atribute) field.getAnnotation(Atribute.class)).isPrimaryKey()? ((Atribute) field.getAnnotation(Atribute.class)).name()+ ",": "";
                    }
                    publicFields = target.getSuperclass().getDeclaredFields();
                }
            }

            if(primaryKey.equals(""))
            {
                atributes = "ID SERIAL, " + atributes;
                primaryKey = "ID";
            }
            else
            {
                primaryKey = primaryKey.substring(0, primaryKey.length()-1);
            }

                sql = "CREATE TABLE "+
                    ((Entity)target.getAnnotation(Entity.class)).name()+
                    " (" + atributes +
                    "PRIMARY KEY ( " + primaryKey +
                    ")  );";
            st.execute(sql);
        } catch (SQLException e) {
            System.out.print("Не пытайся вставить лишнего!");
        }

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
