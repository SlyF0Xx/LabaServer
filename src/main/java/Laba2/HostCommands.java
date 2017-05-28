package Laba2;

import Cmd.*;
import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by SlyFox on 02.05.201, 7.
 */
public class HostCommands {
    private DatagramSocket serverSocket;
    private Set<SocketAddress> clients;
    private Commands commands;

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

    HostCommands()
    {
        clients = new HashSet<>();

        commands = new Commands();
        commands.SetCommand("add_if_min", new AddIfMin());
        commands.SetCommand("remove_lower", new RemoveLower());
        commands.SetCommand("remove_all", new RemoveAll());
        commands.SetCommand("show_all", new ShowAll());
        commands.SetCommand("save", new Save());
        commands.SetCommand("load", new Load());
    }

    public void SetInetAddress()
    {
        try {
            serverSocket = new DatagramSocket(new InetSocketAddress("localhost", 2222));
            //serverSocket = new DatagramSocket(new InetSocketAddress("192.168.43.22", 2222));
            System.out.println("Сервер присоединился");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Announc()
    {
        clients.forEach(
                (i)->SendResponce("Announcement", i)
        );
    }

    public void AddClient(SocketAddress client)
    {
        clients.add(client);
    }

    public void Observe() {
        //Set<SocketAddress> subscribers = new LinkedHashSet<SocketAddress>();
        while (true) {
            /*SocketAddress socketAddress = Receive();
            if(!subscribers.contains(socketAddress))
            {
                subscribers.add(socketAddress);
            }*/
            String temp = Reciver.ReceiveCommand(this);
            System.out.println("Сервер получил команду!");

            ExecuteThread executeThread = new ExecuteThread(temp, serverSocket.getRemoteSocketAddress(), this);
            switch (temp) {
                case "GetPerson": {
                    executeThread.params.add(Reciver.ReceiveString(this));
                    break;
                }
                case "AddPerson": {
                    executeThread.params.add((Person) Reciver.ReceiveObject(this));
                    break;
                }
                case "DeletePerson": {
                    executeThread.params.add(Reciver.ReceiveString(this));
                    break;
                }
                case "ExecuteCommand": {
                    executeThread.params.add(Reciver.ReceiveString(this));
                    executeThread.params.add(Reciver.ReceiveString(this));
                    break;
                }
                case "EditPerson": {
                    executeThread.params.add(Reciver.ReceiveString(this));
                    executeThread.params.add((Person) Reciver.ReceiveObject(this));
                }
            }
            serverSocket.disconnect();
            executeThread.start();
        }
    }

    public void SendResponce(String name, SocketAddress address)
    {
        ByteBuffer tmp = ByteBuffer.allocate(1).put(RequestsResponcesTable.getResponceByName(name));
        try {
            SocketAddress oldAddres =  serverSocket.getRemoteSocketAddress();

            if(oldAddres!=null && !oldAddres.equals(address))
            {
                serverSocket.disconnect();
            }

            //serverSocket.connect(address);
            serverSocket.send(new DatagramPacket(tmp.array(), 1, address));

            if(oldAddres!=null && !oldAddres.equals(address))
            {
                serverSocket.connect(oldAddres);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendObject(Object object, SocketAddress address)
    {
        SendResponce("Value",address);
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
