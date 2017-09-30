package bot.model;

/**
 * Created by Luca Mosetti on 2017
 * <p>
 * An indissoluble combination between command and its description
 */
public class Command {

    private static final int COMMAND_MAX_LENGTH = 32;
    private final String commandIdentifier;
    private final String description;

    /**
     * @param commandIdentifier command between 1 and 32 chars
     * @param description       command description
     */
    public Command(String commandIdentifier, String description) {
        if (commandIdentifier == null || commandIdentifier.isEmpty())
            throw new IllegalArgumentException("commandIdentifier cannot be null or empty");

        if (commandIdentifier.length() + 1 > COMMAND_MAX_LENGTH)
            throw new IllegalArgumentException("commandIdentifier cannot be longer than " + COMMAND_MAX_LENGTH + " (including /)");

        this.commandIdentifier = commandIdentifier.toLowerCase();
        this.description = description;
    }

    public String getCommandIdentifier() {
        return commandIdentifier;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "/" + getCommandIdentifier() + "\n" + getDescription();
    }

    @Override
    public int hashCode() {
        return this.getCommandIdentifier().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && this.getCommandIdentifier().equals(((Command) obj).getCommandIdentifier());
    }
}