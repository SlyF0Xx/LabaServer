package Cmd;

import IO.FileOutpuStreamToWriter;
import IO.Parser;
import Laba2.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by SlyFox on 10.03.2017.
 */
public class Save implements Command {


    /**
     * Save fields information (except fields which mark @see @NotParse) about all elements of this collection (collections fields are unsupported) in CSV format
     * @author SlyFox
     * @param objects nothing
     * @return false
     * @throws IOException
     */
    @Override
    public boolean execute(Object... objects)throws IOException
    {
        synchronized (Command.class)
        {
            if(System.getenv().containsKey("ReadFileDir"))
            {
                File file = new File(System.getenv("ReadFileDir"));
                if (!file.canWrite()) {
                    System.out.println("Нет прав доступа");
                }
                else
                {
                    FileOutpuStreamToWriter writer = new FileOutpuStreamToWriter(System.getenv("ReadFileDir"));

                    //FileOutpuStreamToWriter writer = new FileOutpuStreamToWriter("out.txt");

                    CSVPrinter f = new CSVPrinter(writer, CSVFormat.RFC4180);


                    for(Person person: People.GetPersons().values())
                    {
                        f.print(People.GetByName(person.GetName()).getClass().getName());

                        Parser.bad_print(f,People.GetByName(person.GetName()));
                        f.println();
                    }

                    writer.close();
                }
                return false;
            }
            else
            {
                System.out.println("Переменная окружения ReadFileDir отсутствует");
                throw new FileNotFoundException();
            }
        }
    }

    @Override
    public String toString() {
        return "save";
    }
}
