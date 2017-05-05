package Cmd;

/**
 * Created by SlyFox on 13.03.2017.
 */
public class Exit implements Command {
    /**
     * @param objects - nothing
     * @return true
     * @throws Exception - do not throw anything-
     */
    @Override
    public boolean execute(Object... objects) throws Exception {
        return true;
    }

    @Override
    public String toString() {
        return "exit";
    }
}
