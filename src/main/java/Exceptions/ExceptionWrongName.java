package Exceptions;

/**
 * Created by SlyFox on 18.12.2016.
 */
public class ExceptionWrongName extends Exception {
    @Override
    public String getMessage() {
        return "Wrong name inputed. Name must content only Lat or Cyrillic letters";
    }
}
