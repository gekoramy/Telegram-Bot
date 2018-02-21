package gekoramy.telegram.bot.exception;

import gekoramy.telegram.bot.model.Command;

/**
 * @author Luca Mosetti
 * @since 2017
 */
public class TwoCommandException extends Exception {
    private static final String DESCRIPTION = "Already defined command: ";

    public TwoCommandException(Command command) {
        super(DESCRIPTION + command.getCommandIdentifier());
    }
}