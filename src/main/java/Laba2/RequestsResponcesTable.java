package Laba2;

import java.util.HashMap;

/**
 * Created by SlyFox on 30.04.2017.
 */
public class RequestsResponcesTable {
    private static HashMap<Byte, String> RequestByValue;
    private static HashMap<String, Byte> RequestByName;


    public static byte getRequestByName(String name)
    {
        return RequestByName.get(name);
    }
    public static String getRequestByValue(byte value)
    {
        return RequestByValue.get(value);
    }

    public static void AddRequest(Byte value, String Name)
    {
        RequestByValue.put(value, Name);
        RequestByName.put(Name, value);
    }

    public RequestsResponcesTable()
    {
        RequestByValue = new HashMap<Byte, String>();
        RequestByName = new HashMap<String, Byte>();

        ResponceByValue = new HashMap<Byte, String>();
        ResponceByName = new HashMap<String, Byte>();

        AddRequest((byte)1, "GetPerson");
        AddRequest((byte)2, "GetPersons");
        AddRequest((byte)3, "EditPerson");
        AddRequest((byte)4, "EditPersons");
        AddRequest((byte)5, "AddPerson");
        AddRequest((byte)6, "DeletePerson");
        AddRequest((byte)7, "GetCommandNames");
        AddRequest((byte)8, "ExecuteCommand");

        AddResponce((byte)1, "Announcement");
        AddResponce((byte)2, "Value");
    }

    private static HashMap<Byte, String> ResponceByValue;
    private static HashMap<String, Byte> ResponceByName;


    public static byte getResponceByName(String name)
    {
        return ResponceByName.get(name);
    }
    public static String getResponceByValue(byte value)
    {
        return ResponceByValue.get(value);
    }

    public static void AddResponce(Byte value, String Name)
    {
        ResponceByValue.put(value, Name);
        ResponceByName.put(Name, value);
    }
}
