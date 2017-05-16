package Laba2;

import java.net.SocketAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import static Laba2.HostCommands.SendObject;

/**
 * Created by SlyFox on 12.05.2017.
 */
public class OutputThread extends Thread {
    List<Object> out;
    String temp;
    SocketAddress address;

    OutputThread(String temp, SocketAddress address)
    {
        out = new LinkedList<Object>();
        this.temp = temp;
        this.address = address;
    }

    public void run()
    {
        synchronized (OutputThread.class)
        {
            switch (temp)
            {
                case "GetPerson":
                {
                    SendObject(out.get(0),address);
                    break;
                }
                case "GetPersons":
                {
                    SendObject(out.get(0),address);
                    break;
                }
                case "GetCommandNames":
                {
                    SendObject(out.get(0),address);
                    break;
                }
                case "ExecuteCommand":
                {
                    try {
                        SendObject(out.get(0),address);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

}
