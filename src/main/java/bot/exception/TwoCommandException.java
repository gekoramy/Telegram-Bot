package bot.exception;

import bot.model.Command;

/**
 * Created by Luca Mosetti on 2017
 */
public class TwoCommandException extends Exception {
    private static final String DESCRIPTION = "Already defined command: ";

    public TwoCommandException(Command command) {
        super(DESCRIPTION + command.getCommandIdentifier());
    }
}