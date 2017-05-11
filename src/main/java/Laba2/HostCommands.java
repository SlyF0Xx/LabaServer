package Laba2;

import Cmd.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * Created by SlyFox on 02.05.2017.
 */
public class HostCommands {
    private static DatagramSocket serverSocket;

    private static Commands commands;

    static
    {
        commands = new Commands();
        commands.SetCommand("add_if_min", new AddIfMin());
        commands.SetCommand("remove_lower", new RemoveLower());
        commands.SetCommand("remove_all", new RemoveAll());
        commands.SetCommand("show_all", new ShowAll());
        commands.SetCommand("save", new Save());
        commands.SetCommand("load", new Load());
    }

    public static void SetInetAddress()
    {
        try {
            serverSocket = new DatagramSocket(2222);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String ReceiveCommand()
    {
        byte[] receiveData = new byte[1];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            serverSocket.receive(receivePacket);
            serverSocket.connect(receivePacket.getSocketAddress());
            return RequestsResponcesTable.getRequestByValue(receivePacket.getData()[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer ReceiveInteger()
    {
        byte[] receiveData = new byte[4];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            serverSocket.receive(receivePacket);

            return ByteBuffer.allocate(4).put(receivePacket.getData()).getInt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String ReceiveString()
    {
        byte[] receiveData = new byte[ReceiveInteger()];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            serverSocket.receive(receivePacket);

            return new String(receiveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object ReceiveObject()
    {
        byte[] receiveData = new byte[ReceiveInteger()];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            serverSocket.receive(receivePacket);

            ByteArrayInputStream t = new ByteArrayInputStream(receiveData);
            ObjectInputStream ois = new ObjectInputStream(t);

            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void Observe()
    {
        while(true)
        {
            String temp = ReceiveCommand();

            switch (temp)
            {
                case "GetPerson":
                {
                    String name = ReceiveString();
                    SendObject(People.GetByName(name));
                    break;
                }
                case "GetPersons":
                {
                    SendObject(People.GetPersons());
                    break;
                }
                case "AddPerson":
                {
                    People.AddPerson((Person) ReceiveObject());
                    break;
                }
                case "DeletePerson":
                {
                    People.RemovePerson(ReceiveString());
                    break;
                }
                case "GetCommandNames":
                {
                    SendObject(commands.GetCommands().keySet().toArray());
                    break;
                }
                case "ExecuteCommand":
                {
                    String name = ReceiveString();
                    String params = ReceiveString();
                    try {
                        SendObject(commands.GetCommands().get(name).execute(commands.GetCommands().get(name).read(params)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "EditPerson":
                {
                    String name = ReceiveString();
                    Person person = (Person) ReceiveObject();
                    People.EditPerson(name, person);
                }
            }
            serverSocket.disconnect();
        }
    }


    public static void SendObject(Object object)
    {
        try {
            if(object instanceof String)
            {
                ByteBuffer tmp = ByteBuffer.allocate(4).putInt(((String) object).getBytes().length);
                serverSocket.send(new DatagramPacket(tmp.array(), 4));
                serverSocket.send(new DatagramPacket(((String) object).getBytes(), ((String) object).getBytes().length));
            }
            else if(object instanceof Integer)
            {
                ByteBuffer tmp = ByteBuffer.allocate(4).putInt((Integer) object);
                serverSocket.send(new DatagramPacket(tmp.array(), 4));
            }
            else if (object instanceof Boolean)
            {
                ByteBuffer tmp = ByteBuffer.allocate(1).put((byte)((Boolean)object==true?1:0));
                serverSocket.send(new DatagramPacket(tmp.array(), 1));
            }
            else
            {
                ByteArrayOutputStream t = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(t);
                oos.writeObject(object);

                ByteBuffer tmp = ByteBuffer.allocate(4).putInt(t.toByteArray().length);
                serverSocket.send(new DatagramPacket(tmp.array(), 4));
                serverSocket.send(new DatagramPacket(t.toByteArray(), t.toByteArray().length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
