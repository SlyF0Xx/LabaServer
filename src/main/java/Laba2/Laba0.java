/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Laba2;
import Cmd.Load;
import Cmd.Save;
import Exceptions.ExceptionWrongName;

import java.io.FileNotFoundException;
import java.io.IOException;


public class Laba0
{
    public boolean equals(Object obj)
    {
         return true;
    }

    public String toString()
    {
        return "main  class";
    }

    public int hashCode()
    {
        return 1;
    }

    public static void main(String[] args) {
        new RequestsResponcesTable();
        HostCommands.SetInetAddress();


        Location Home = new Location("doors");

        final People people = new People();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    System.out.println("Спасаем наши души");

                    new Save().execute();
                    //people.save();
                } catch (IOException e) {
                    System.out.println("Попытка сохранить файл без указания пути к файлу. Устаносите корректное значение переменной окружения ReadFileDir");
                } catch (NullPointerException e) {
                    System.out.println("Попытка сохранить файл, однако данные не успели считаться. Завершение работы без сохранения");
                }
            }
        });

        try {

        //    people.AddPerson(new LitleBoy(new Leg[] {new Leg(true,true, Leg.Size.Small),new Leg(true, true, Leg.Size.Big)}, Home, "Mal"));
        //    people.AddPerson(new FrekenBok(new Leg[] {new Leg(true,true, Leg.Size.Big),new Leg(true, true, Leg.Size.Big)}, new Location("Near the home"), "Freken Bok"));
        //            people.save();

            new Load().execute();

            HostCommands.Observe();

            people.GetByName("Mal").Waiting(people.GetByName("Freken Bok"));
            people.GetByName("Mal").SetWait(false);


            people.GetByName("Freken Bok").Come(Home);
            people.GetByName("Freken Bok").See(people.GetByName("Mal"));

            Person.Info.ToDo();

              //people.save();
            new Save().execute();

            people.AddPerson(new Person(new Leg[]{new Leg(true, true, Leg.Size.Small), new Leg(true, true, Leg.Size.Small)}
                    , Home, "_Папа_") {
                @Override
                public void See(Person Who) {
                    System.out.print("Никого не вижу");
                }
            });

        }
        catch(ExceptionWrongName a)
        {
            System.out.println("Имя введено неверно");
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода. Убедитесь в наличии соотвествующего файла и установки переменной среды окружения ReadFileDir");
        } catch (InstantiationException e) {
            System.out.println("Невозможно создать объект класса. Возмжно, класс не имеет конструктора без параметров. Убедитесь в корректности файла");
        } catch (IllegalAccessException e) {
            System.out.println("Ошибка доступа.");
        } catch (ClassNotFoundException e) {
            System.out.println("Класс не найден. Объекты данного класа не поддерживаются программой");
        } catch (Exception e)
        {
            System.out.println("Неопознанная ошибка");
        }
    }
}
