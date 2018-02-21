package gekoramy.telegram.bot.exception;

/**
 * @author Luca Mosetti
 * @since 2017
 */
public class NotHandledCommandException extends Exception {
    private static final String DESCRIPTION = "Not handled command: ";

    private final String command;

    public NotHandledCommandException(String command) {
        super(DESCRIPTION + command);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
