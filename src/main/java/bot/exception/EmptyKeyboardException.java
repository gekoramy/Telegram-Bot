package bot.exception;

/**
 * Created by Luca Mosetti on 2017
 */
public class EmptyKeyboardException extends Exception {
    private static final String DESCRIPTION = "Empty keyboard";

    public EmptyKeyboardException() {
        super(DESCRIPTION);
    }
}
