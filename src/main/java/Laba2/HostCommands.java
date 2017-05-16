package Laba2;

import Cmd.*;
import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by SlyFox on 02.05.2017.
 */
public class HostCommands {
    private static DatagramSocket serverSocket;
    private static SocketAddress tempAddr;
    private static Commands commands;

    public static DatagramSocket getServerSocket() {
        return serverSocket;
    }

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
            serverSocket = new DatagramSocket(new InetSocketAddress(2222));
            //serverSocket = new DatagramSocket(new InetSocketAddress("192.168.1.1", 2222));
            System.out.println("Сервер присоединился");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String ReceiveCommand()
    {
        byte[] receiveData = new byte[1];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length, new InetSocketAddress("88.201.205.92", 2222));
        try {
            serverSocket.receive(receivePacket);
            serverSocket.connect(receivePacket.getSocketAddress());
            tempAddr = receivePacket.getSocketAddress();

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
        //Set<SocketAddress> subscribers = new LinkedHashSet<SocketAddress>();
        while (true)
        {
            /*SocketAddress socketAddress = Receive();
            if(!subscribers.contains(socketAddress))
            {
                subscribers.add(socketAddress);
            }*/
            String temp = ReceiveCommand();
            System.out.println("Сервер получил команду!");

            ExecuteThread executeThread = new ExecuteThread(temp,tempAddr);
            switch (temp)
            {
                case "GetPerson":
                {
                    executeThread.params.add(HostCommands.ReceiveString());
                    break;
                }
                case "AddPerson":
                {
                    executeThread.params.add((Person) HostCommands.ReceiveObject());
                    break;
                }
                case "DeletePerson":
                {
                    executeThread.params.add(HostCommands.ReceiveString());
                    break;
                }
                case "ExecuteCommand":
                {
                    executeThread.params.add(HostCommands.ReceiveString());
                    executeThread.params.add(HostCommands.ReceiveString());
                    break;
                }
                case "EditPerson":
                {
                    executeThread.params.add(HostCommands.ReceiveString());
                    executeThread.params.add((Person) HostCommands.ReceiveObject());
                }
            }
            serverSocket.disconnect();
            executeThread.start();


//            InputThread inputThread = new InputThread(temp,tempAddr);
//            inputThread.start();

        }


        /*while(true)
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
        }*/
    }


    public static void SendObject(Object object, SocketAddress address)
    {
        try {
            if(object instanceof String)
            {
                ByteBuffer tmp = ByteBuffer.allocate(4).putInt(((String) object).getBytes().length);
                serverSocket.send(new DatagramPacket(tmp.array(), 4, address));
                serverSocket.send(new DatagramPacket(((String) object).getBytes(), ((String) object).getBytes().length, address));
            }
            else if(object instanceof Integer)
            {
                ByteBuffer tmp = ByteBuffer.allocate(4).putInt((Integer) object);
                serverSocket.send(new DatagramPacket(tmp.array(), 4, address));
            }
            else if (object instanceof Boolean)
            {
                ByteBuffer tmp = ByteBuffer.allocate(1).put((byte)((Boolean)object==true?1:0));
                serverSocket.send(new DatagramPacket(tmp.array(), 1, address));
            }
            else
            {
                ByteArrayOutputStream t = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(t);
                oos.writeObject(object);

                ByteBuffer tmp = ByteBuffer.allocate(4).putInt(t.toByteArray().length);
                serverSocket.send(new DatagramPacket(tmp.array(), 4, address));
                serverSocket.send(new DatagramPacket(t.toByteArray(), t.toByteArray().length, address));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
