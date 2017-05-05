package IO;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.List;


public class Parser {
    public static int func(Object MyObject, List<CSVRecord> s, int current_num)
    {
        try
        {
            Field[] publicFields = MyObject.getClass().getDeclaredFields();
            for (int i = 0; i < 2; i++) {
                for (Field field : publicFields) {
                    field.setAccessible(true);
                    if(field.isAnnotationPresent(NotParse.class) || (s.get(0).get(current_num).equals("null")) || s.get(0).get(current_num).equals(""))
                    {
                       //field.set(MyObject,null);
                    }
                    else
                    {
                        if(s.get(0).get(current_num).charAt(0) != '\\' )
                        {
                            if(field.getType().isArray())
                            {
                                int num = Integer.valueOf(s.get(0).get(current_num++));

                                if(field.getType().toString().equals("class [I"))
                                {
                                    Integer [] mass = new Integer[num];
                                    for(int j = 0; j< num;j++)
                                    {
                                        mass[j] = Integer.valueOf(s.get(0).get(current_num));
                                        current_num++;
                                    }
                                    field.set(MyObject, mass);
                                } else if(field.getType().toString().equals("class [Z"))
                                {
                                    Boolean [] mass = new Boolean[num];
                                    for(int j = 0; j< num;j++)
                                    {
                                        mass[j] = Boolean.valueOf(s.get(0).get(current_num++));
                                        current_num++;
                                    }
                                    field.set(MyObject, mass);
                                }else if(field.getType().toString().equals("class [S"))
                                {
                                    Short [] mass = new Short[num];
                                    for(int j = 0; j< num;j++)
                                    {
                                        mass[j] = Short.valueOf(s.get(0).get(current_num));
                                        current_num++;
                                    }
                                    field.set(MyObject, mass);
                                }else if(field.getType().toString().equals("class [J"))
                                {
                                    Long [] mass = new Long[num];
                                    for(int j = 0; j< num;j++)
                                    {
                                        mass[j] = Long.valueOf(s.get(0).get(current_num));
                                        current_num++;
                                    }
                                    field.set(MyObject, mass);
                                }else if(field.getType().toString().equals("class [B"))
                                {
                                    Byte [] mass = new Byte[num];
                                    for(int j = 0; j< num;j++)
                                    {
                                        mass[j] = Byte.valueOf(s.get(0).get(current_num));
                                        current_num++;
                                    }
                                    field.set(MyObject, mass);
                                }else if(field.getType().toString().equals("class [F"))
                                {
                                    Float[] mass = new Float[num];
                                    for(int j = 0; j< num;j++)
                                    {
                                        mass[j] = Float.valueOf(s.get(0).get(current_num));
                                        current_num++;
                                    }
                                    field.set(MyObject, mass);
                                }else if(field.getType().toString().equals("class [D"))
                                {
                                    Double[] mass = new Double[num];
                                    for(int j = 0; j< num;j++)
                                    {
                                        mass[j] = Double.valueOf(s.get(0).get(current_num));
                                        current_num++;
                                    }
                                    field.set(MyObject, mass);
                                }else if(field.getType().toString().equals("class [C"))
                                {
                                    Character[] mass = new Character[num];
                                    for(int j = 0; j< num;j++)
                                    {
                                        field.set(MyObject, s.get(0).get(current_num).charAt(0));
                                        current_num++;
                                    }
                                    field.set(MyObject, mass);
                                }else
                                {
                                    String ClassName = field.getType().toString().substring(8,field.getType().toString().length()-1);

                                    Object objects = Array.newInstance(Class.forName(ClassName),num);

                                    for(int j = 0; j< num;j++)
                                    {
                                        Object MyObj = Class.forName(ClassName).newInstance();
                                        current_num = func(MyObj, s, current_num);

                                        Array.set(objects, j, MyObj);
                                    }
                                    field.set(MyObject, objects);
                                }
                            }
                            else
                            {
                                if ((field.getType() == Byte.class) || (field.getType() == Byte.TYPE)) {
                                    field.set(MyObject, Byte.valueOf(s.get(0).get(current_num)));
                                } else if ((field.getType() == Short.class) || (field.getType() == Short.TYPE)) {
                                    field.set(MyObject, Short.valueOf(s.get(0).get(current_num)));
                                } else if ((field.getType() == Character.class) || (field.getType() == Character.TYPE)) {
                                    field.set(MyObject, s.get(0).get(current_num).charAt(0));//!!!!!!!!!!!!!!!
                                } else if ((field.getType() == Integer.class) || (field.getType() == Integer.TYPE)) {
                                    field.set(MyObject, Integer.valueOf(s.get(0).get(current_num)));
                                } else if ((field.getType() == Long.class) || (field.getType() == Long.TYPE)) {
                                    field.setLong(null, Long.valueOf(s.get(0).get(current_num)));
                                } else if ((field.getType() == Float.class) || (field.getType() == Float.TYPE)) {
                                    field.set(MyObject, Float.valueOf(s.get(0).get(current_num)));
                                } else if ((field.getType() == Double.class) || (field.getType() == Double.TYPE)) {
                                    field.set(MyObject, Double.valueOf(s.get(0).get(current_num)));
                                } else if ((field.getType() == Boolean.class) || (field.getType() == Boolean.TYPE)) {
                                    field.set(MyObject, Boolean.valueOf(s.get(0).get(current_num)));
                                } else if (field.getType() == String.class) {
                                    field.set(MyObject, s.get(0).get(current_num));
                                } else if (field.getType().isEnum()) {
                                    field.set(MyObject, Enum.valueOf((Class)field.getType(),s.get(0).get(current_num)));
                                }
                                else
                                {
                                    Object temp = field.getType().newInstance();
                                    current_num = func(temp
                                            //field.getClass().newInstance()
                                            , s, current_num);
                                    field.set(MyObject, temp);
                                    current_num--;
                                }

                                current_num++;
                            }
                        }
                        else {
                            String ClassName = s.get(0).get(1).substring(1);

                            Object MyObj = Class.forName(ClassName).newInstance();
                            current_num = func(MyObj, s, ++current_num);
                            field.set(MyObject, MyObj);
                        }
                    }

                }

                publicFields = MyObject.getClass().getSuperclass().getDeclaredFields();
            }
        }

        catch (InstantiationException ex)
        {
            System.out.println("Бредовое поле, сворачиваемся");
        }
        catch(IllegalAccessException e)
        {
            System.out.println("Поля некорректны");
        }
        catch (ClassNotFoundException ex)
        {
            System.out.println("Класс не найден");
        }
        return current_num;
    }
/*
    public static void print(CSVPrinter Printer, Object object) {
        try {
            if(object != null) {
                Field[] publicFields = object.getClass().getDeclaredFields();
                for (Field field : publicFields) {
                    field.setAccessible(true);
                    if (field.getType().isPrimitive() || field.getType() == String.class ) {
                        Printer.print(field.get(object));
                    } else {
                        System.out.println(field.get(object));

                        if(field.getType().isArray())
                        {
                            try
                            {
                                Object[] objs = (Object[]) field.get(object);
                                for (Object obj : objs) {
                                    print(Printer, obj);
                                }
                            }
                            catch (Exception e) {
                                try{
                                    int[] ob  = (int[])field.get(object);
                                    for (Object obj : ob) {
                                        Printer.print(field.get(object));
                                    }
                                }
                                catch (Exception ex)
                                {
                                    try{
                                        float[] ob = (float[])field.get(object);
                                        for (Object obj : ob) {
                                            Printer.print(field.get(object));
                                        }                                    }
                                    catch (Exception exc)
                                    {
                                        try {
                                            double[] ob = (double[])field.get(object);
                                            for (Object obj : ob) {
                                                Printer.print(field.get(object));
                                            }
                                        }
                                        catch (Exception exce)
                                        {
                                            try{
                                                boolean[] ob = (boolean[])field.get(object);
                                                for (Object obj : ob) {
                                                    Printer.print(field.get(object));
                                                }
                                            }
                                            catch (Exception excep)
                                            {
                                                try{
                                                    byte[] ob = (byte[])field.get(object);
                                                    for (Object obj : ob) {
                                                        Printer.print(field.get(object));
                                                    }
                                                }
                                                catch (Exception except)
                                                {
                                                    try{
                                                        short[] ob = (short[])field.get(object);
                                                        for (Object obj : ob) {
                                                            Printer.print(field.get(object));
                                                        }
                                                    }
                                                    catch (Exception excepti)
                                                    {
                                                        try{
                                                            long[] ob = (long[])field.get(object);
                                                            for (Object obj : ob) {
                                                                Printer.print(field.get(object));
                                                            }
                                                        }
                                                        catch (Exception exceptio)
                                                        {
                                                            try{
                                                                char[] ob = (char [])field.get(object);
                                                                for (Object obj : ob) {
                                                                    Printer.print(field.get(object));
                                                                }
                                                            }
                                                            catch (Exception exception)
                                                            {
                                                                System.out.println("WTF");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            print(Printer, field.get(object));
                        }

                    }
                }
            }
            else
            {
                Printer.print("null");
            }
        } catch (Exception e) {
            System.out.println("Wtf");

        }
    }
*/

    public static void bad_print(CSVPrinter Printer, Object object)
    {
        try{
            Field[] fields = object.getClass().getDeclaredFields() ;

            for(int i=0;i<2;i++)
            {
                if (fields.length>0)
                {
                    //Field field = fields[0];
                    for(Field field: fields)
                    {
                        field.setAccessible(true);
                        if(field.isAnnotationPresent(NotParse.class))
                        {
                            continue;
                        }
                        else if(field.get(object) == null)
                        {
                            Printer.print("null");
                        }
                        else if((!(field.get(object).getClass().equals(field.getType()))) && !field.getType().isPrimitive())
                            Printer.print("\\"+field.get(object).getClass());
                        if(field.getType().isArray())
                        {
                            if(field.getType().toString().equals("class [I"))
                            {
                                Printer.print(((int[]) field.get(object)).length);
                                for(int obj: (int[]) field.get(object)) {
                                    Printer.print(obj);
                                }
                            } else if(field.getType().toString().equals("class [Z"))
                            {
                                Printer.print(((boolean[]) field.get(object)).length);
                                for(boolean obj: (boolean[]) field.get(object)) {
                                    Printer.print(obj);
                                }
                            }else if(field.getType().toString().equals("class [S"))
                            {
                                Printer.print(((short[]) field.get(object)).length);
                                for(short obj: (short[]) field.get(object)) {
                                    Printer.print(obj);
                                }
                            }else if(field.getType().toString().equals("class [J"))
                            {
                                Printer.print(((long[]) field.get(object)).length);
                                for(long obj: (long[]) field.get(object)) {
                                    Printer.print(obj);
                                }
                            }else if(field.getType().toString().equals("class [B"))
                            {
                                Printer.print(((byte[]) field.get(object)).length);
                                for(byte obj: (byte[]) field.get(object)) {
                                    Printer.print(obj);
                                }
                            }else if(field.getType().toString().equals("class [F"))
                            {
                                Printer.print(((float[]) field.get(object)).length);
                                for(float obj: (float[]) field.get(object)) {
                                    Printer.print(obj);
                                }
                            }else if(field.getType().toString().equals("class [D"))
                            {
                                Printer.print(((double[]) field.get(object)).length);
                                for(double obj: (double[]) field.get(object)) {
                                    Printer.print(obj);
                                }
                            }else if(field.getType().toString().equals("class [C")) {
                                Printer.print(((char[]) field.get(object)).length);
                                for (char obj : (char[]) field.get(object)) {
                                    Printer.print(obj);
                                }
                            }else if (field.getType().toString().equals("class [Ljava.lang.String"))
                            {
                                Printer.print(((String[]) field.get(object)).length);
                                for(String obj: (String[]) field.get(object)) {
                                    Printer.print(obj);
                                }
                            }else
                            {
                                //bad_print(Printer, ((Object[]) fields[0].get(object))[0]);
                                Printer.print(((Object[]) field.get(object)).length);
                                for(Object obj: (Object[]) field.get(object)) {
                                    bad_print(Printer, obj);
                                }
                            }
                        }
                        else if(field.getType().isPrimitive() || field.getType().isEnum() || field.getType() == String.class)
                        {
                            Printer.print(field.get(object));
                        }
                        else
                        {
                            bad_print(Printer, field.get(object));
                        }
                    }
                }
                fields = object.getClass().getSuperclass().getDeclaredFields() ;
            }

        } catch (IllegalAccessException e) {
            System.out.println("Невозможно получить доступ у полю");
        }
        catch (IOException e){
            System.out.println("Ошибка ввода/вывода. Убедитесь в наличии соотвествующего файла и установки переменной среды окружения ReadFileDir");
        }


    }
}
