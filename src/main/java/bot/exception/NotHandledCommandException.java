package bot.exception;

import bot.model.Command;

/**
 * Created by Luca Mosetti on 2017
 */
public class NotHandledCommandException extends Exception {
    private static final String DESCRIPTION = "Not handled command: ";

    public NotHandledCommandException(Command command) {
        super(DESCRIPTION + command.toString());
    }
}
