package Laba2;

import java.net.SocketAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by SlyFox on 12.05.2017.
 */
public class OutputThread extends Thread {
    List<Object> out;
    String temp;
    SocketAddress address;
    HostCommands main;

    OutputThread(String temp, SocketAddress address, HostCommands main)
    {
        out = new LinkedList<Object>();
        this.temp = temp;
        this.address = address;
        this.main = main;
    }

    public void run()
    {
        synchronized (OutputThread.class)
        {
            switch (temp)
            {
                case "GetPerson":
                {
                    main.SendObject(out.get(0),address);
                    break;
                }
                case "GetPersons":
                {
                    main.SendObject(out.get(0),address);
                    break;
                }
                case "GetCommandNames":
                {
                    main.SendObject(out.get(0),address);
                    break;
                }
                case "ExecuteCommand":
                {
                    try {
                        main.SendObject(out.get(0),address);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

}
