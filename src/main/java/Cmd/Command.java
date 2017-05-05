package Cmd;

import java.io.IOException;

/**
 * Created by SlyFox on 05.03.2017.
 */
public interface Command {

    public boolean execute(Object... objects) throws Exception;

    default Object[] read(String string) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        return new String[]{string.trim()};
    }
}
