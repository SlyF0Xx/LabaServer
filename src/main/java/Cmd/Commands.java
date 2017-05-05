package Cmd;

import Cmd.Command;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Commands {
    private Map<String, Command> commands;

    public void Reading() {
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            try {
                boolean exit = false;

                Command temp = commands.get(in.next());
                if (temp.execute(temp.read(in.nextLine()))) {
                    break;
                }

                System.out.println("Команда выполнена");
                System.out.println("Пожалуйста введите следующую команду");

            } catch (JsonParseException e) {
                System.out.println(e.getMessage());
                System.out.println("Ошибка Jackson. Обратитесь в службу поддержки Jackson");
            } catch (JsonMappingException e) {
                System.out.println(e.getMessage());
                System.out.println("Ошибка Jackson. Обратитесь в службу поддержки Jackson");
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
                System.out.println("Ошибка Jackson. Обратитесь в службу поддержки Jackson");
            } catch (InstantiationException e) {
                System.out.println("Невозможно создать объект класса. Возмжно, класс не имеет конструктора без параметров. Убедитесь в корректности файла");
            } catch (IllegalAccessException e) {
                System.out.println("Ошибка доступа.");
            } catch (ClassNotFoundException e) {
                System.out.println("Класс не найден. Объекты данного класа не поддерживаются программой");
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода. Убедитесь в наличии соотвествующего файла и установки переменной среды окружения ReadFileDir");
            } catch (Exception e) {
                System.out.println("Некоректный ввод. Попробуйте снова");
            }
        }

    }

    public void SetCommand(String string, Command command)
    {
        commands.put(string, command);
    }

    public Commands()
    {
        commands = new HashMap<>();
    }

    public Map<String, Command> GetCommands()
    {
        return commands;
    }

    public void info()
    {
        commands.forEach((n,i)->System.out.println(i.toString()));
    }
}
