package Laba2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by SlyFox on 16.05.2017.
 */
public class Reciver {

    public static String ReceiveCommand(HostCommands commands)
    {
        byte[] receiveData = new byte[1];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length, new InetSocketAddress("88.201.205.92", 2222));
        try {
            commands.getServerSocket().receive(receivePacket);
            commands.getServerSocket().connect(receivePacket.getSocketAddress());
            commands.AddClient(receivePacket.getSocketAddress());
            //tempAddr = receivePacket.getSocketAddress();

            return RequestsResponcesTable.getRequestByValue(receivePacket.getData()[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer ReceiveInteger(HostCommands commands)
    {
        byte[] receiveData = new byte[4];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            commands.getServerSocket().receive(receivePacket);

            return ByteBuffer.allocate(4).put(receivePacket.getData()).getInt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String ReceiveString(HostCommands commands)
    {
        byte[] receiveData = new byte[ReceiveInteger(commands)];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            commands.getServerSocket().receive(receivePacket);

            return new String(receiveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object ReceiveObject(HostCommands commands)
    {
        byte[] receiveData = new byte[ReceiveInteger(commands)];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            commands.getServerSocket().receive(receivePacket);

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

}
