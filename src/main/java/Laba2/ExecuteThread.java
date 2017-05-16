package Laba2;

import Cmd.*;

import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

import static Laba2.HostCommands.ReceiveObject;
import static Laba2.HostCommands.SendObject;

/**
 * Created by SlyFox on 11.05.2017.
 */
public class ExecuteThread extends Thread {
    List<Object> params;
    SocketAddress address;
    String temp;

    ExecuteThread(String temp, SocketAddress address)
    {
        params = new LinkedList<>();
        this.temp = temp;
        this.address = address;
    }


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


    public void run()
    {
        OutputThread outputThread = new OutputThread(temp,address);
        switch (temp)
        {
            case "GetPerson":
            {
                outputThread.out.add(People.GetByName((String) params.get(0)));
                break;
            }
            case "GetPersons":
            {
                outputThread.out.add(People.GetPersons());
                break;
            }
            case "AddPerson":
            {
                People.AddPerson((Person) params.get(0));
                break;
            }
            case "DeletePerson":
            {
                People.RemovePerson((String) params.get(0));
                break;
            }
            case "GetCommandNames":
            {
                outputThread.out.add(commands.GetCommands().keySet().toArray());
                break;
            }
            case "ExecuteCommand":
            {
                try {
                    outputThread.out.add(commands.GetCommands().get(params.get(0)).execute(commands.GetCommands().get(params.get(0)).read((String) params.get(1))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "EditPerson":
            {
                People.EditPerson((String) params.get(0), (Person) params.get(1));
            }
        }
        outputThread.start();
    }
}
