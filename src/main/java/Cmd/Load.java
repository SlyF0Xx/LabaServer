package Cmd;

import IO.Parser;
import Laba2.People;
import Laba2.Person;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by SlyFox on 10.03.2017.
 */
public class Load implements Command {

    /**
     * Load elements (they must be describe CSV) and fill collection from file
     * @return false
     * @param objects nothing
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @author SlyFox
     */
    @Override
    public boolean execute(Object... objects) throws FileNotFoundException, IOException,ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        synchronized (Command.class)
        {
            if (System.getenv().containsKey("ReadFileDir")) {
                File file = new File(System.getenv("ReadFileDir"));
                if (!file.exists() || !file.canRead()) {
                    System.out.println("Не существует или нет прав доступа");
                } else {
                    Scanner in = new Scanner(new File(System.getenv("ReadFileDir")));
                    //Scanner in = new Scanner(new File("out.txt"));
                    while (in.hasNextLine()) {
                        CSVParser parser = CSVParser.parse(in.nextLine(), CSVFormat.RFC4180);
                        List<CSVRecord> list = parser.getRecords();

                        Object MyObject = Class.forName(list.get(0).get(0)).newInstance();

                        Parser.func(MyObject, list, 1);

                        People.AddPerson((Person) MyObject);
                    }
                    in.close();
                }
                return false;
            } else {
                System.out.println("Переменная окружения ReadFileDir отсутствует");
                throw new FileNotFoundException();
            }
        }
    }

    @Override
    public String toString() {
        return "load";
    }
}
