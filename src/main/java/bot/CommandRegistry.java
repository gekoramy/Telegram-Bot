package bot;

import bot.exception.NotHandledCommandException;
import bot.exception.TwoCommandException;
import bot.model.Command;
import bot.model.UseCaseCommand;
import bot.model.handling.HandleCallbackQuery;
import bot.model.handling.HandleInlineQuery;
import bot.model.handling.HandleLocation;
import bot.model.query.Query;
import bot.model.query.QueryParser;
import bot.timed.TimedAbsSender;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;

import java.io.InvalidClassException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Luca Mosetti on 2017
 * <p>
 * Associate Commands to UseCaseCommands
 */
public class CommandRegistry {
    private final Map<String, UseCaseCommand> commandRegistryMap = new HashMap<>();
    private final QueryParser queryParser = new QueryParser();

    private final String botUsername;
    private UseCaseCommand defaultCommand;
    private UseCaseCommand defaultInlineCommand;

    /*package*/ CommandRegistry(String botUsername) {
        this.botUsername = botUsername;
    }

    public Collection<UseCaseCommand> getRegisteredCommands() {
        return this.commandRegistryMap.values();
    }

    /**
     * Remove the bot username from the viaggia.command
     * (used only in the group chats)
     *
     * @param command viaggia.command which may have the bot username
     * @return unique viaggia.command without bot username
     */
    private String removeUsernameFromCommandIfNeeded(String command) {
        return command.replace("@" + this.botUsername, "").trim();
    }

    /**
     * Add an association
     *
     * @param useCaseCommand UseCaseCommand
     * @throws TwoCommandException if there is already a defined Command-UseCaseCommand association for this Command
     */
    public final void register(UseCaseCommand useCaseCommand) throws TwoCommandException {
        if (this.commandRegistryMap.containsKey(useCaseCommand.getCommand().getCommandIdentifier()))
            throw new TwoCommandException(useCaseCommand.getCommand());

        useCaseCommand.init();
        this.commandRegistryMap.put(useCaseCommand.getCommand().getCommandIdentifier(), useCaseCommand);
    }

    /**
     * Optional UseCaseCommand used when a request doesn't use registered Commands
     *
     * @param defaultCommand UseCaseCommand
     */
    public void setDefaultCommand(UseCaseCommand defaultCommand) {
        this.defaultCommand = defaultCommand;
    }

    /**
     * Optional UseCaseCommand used when an inline request doesn't use registered Commands
     *
     * @param defaultInlineCommand UseCaseCommand
     */
    public void setDefaultInlineCommand(UseCaseCommand defaultInlineCommand) throws InvalidClassException {
        if (!(defaultInlineCommand instanceof HandleCallbackQuery))
            throw new InvalidClassException(defaultInlineCommand.getCommand().getCommandIdentifier() + " doesn't handle inline queries");

        this.defaultInlineCommand = defaultInlineCommand;
    }

    /**
     * Execute the 'respondCommand' method of the corrected UseCaseCommand
     * interpreted by extracting the viaggia.command from 'message'
     *
     * @param absSender used to send the respond(s)
     * @param message   message to respond
     * @return true if there's an association or it's defined a defaultCommand
     */
    /*package*/ final boolean respondCommand(TimedAbsSender absSender, Message message) {
        if (!message.hasText() || !message.isCommand())
            return false;

        String command;
        String parameters = null;

        // /viaggia.command [optional] [argument]
        String text = message.getText();
        String commandMessage = text.substring(1);

        if (commandMessage.contains(" ")) {
            command = commandMessage.substring(0, commandMessage.indexOf(" "));
            parameters = commandMessage.substring(commandMessage.indexOf(" ") + 1);
        } else {
            command = commandMessage;
        }

        command = removeUsernameFromCommandIfNeeded(command);

        if (this.commandRegistryMap.containsKey(command)) {
            this.commandRegistryMap.get(command).respondCommand(absSender, message.getFrom(), message.getChat(), parameters);
            return true;
        }

        if (this.defaultCommand != null) {
            this.defaultCommand.respondCommand(absSender, message.getFrom(), message.getChat(), parameters);
            return true;
        }

        return false;
    }

    /**
     * Execute the 'respondMessage' method of the corrected UseCaseCommand
     * required by the argument 'viaggia.command'
     *
     * @param absSender used to send the respond(s)
     * @param message   message to respond
     * @param command   unique viaggia.command
     * @throws NotHandledCommandException there is no Command-UseCaseCommand association for this viaggia.command
     * @throws IllegalArgumentException   Message doesn't have text
     */
    /*package*/ final void respondMessage(TimedAbsSender absSender, Message message, Command command) throws NotHandledCommandException {
        if (!this.commandRegistryMap.containsKey(command.getCommandIdentifier()))
            throw new NotHandledCommandException(command);

        if (!message.hasText())
            throw new IllegalArgumentException("Message doesn't have text");

        this.commandRegistryMap.get(command.getCommandIdentifier()).respondMessage(absSender, message.getFrom(), message.getChat(), message.getText());
    }

    /**
     * @param absSender used to send the respond(s)
     * @param message   message to respond
     * @param command   unique viaggia.command
     * @throws NotHandledCommandException there is no Command-UseCaseCommand association for this viaggia.command
     * @throws IllegalArgumentException   Message doesn't have a location
     */
    /*package*/ final void respondLocation(TimedAbsSender absSender, Message message, Command command) throws NotHandledCommandException, InvalidClassException {
        if (!this.commandRegistryMap.containsKey(command.getCommandIdentifier()))
            throw new NotHandledCommandException(command);

        if (!message.hasLocation())
            throw new IllegalArgumentException("Message doesn't have a location");

        if (!(this.commandRegistryMap.get(command.getCommandIdentifier()) instanceof HandleLocation))
            throw new InvalidClassException(command.getCommandIdentifier() + " doesn't handle locations");

        ((HandleLocation) this.commandRegistryMap.get(command.getCommandIdentifier())).respondLocation(absSender, message.getFrom(), message.getChat(), message.getLocation());
    }

    /**
     * Execute the 'respondCallbackQuery' method of the corrected UseCaseCommand
     * interpreted by extracting the viaggia.command from 'cbq'
     * this extraction is made by the QueryParser
     *
     * @param absSender used to send the respond(s)
     * @param cbq       CallbackQuery
     * @return true if there's an association
     */
    /*package*/ final boolean respondCallbackQuery(TimedAbsSender absSender, CallbackQuery cbq) {
        Query query = queryParser.parse(cbq.getData());

        if (this.commandRegistryMap.containsKey(query.getCommandIdentifier()) && (this.commandRegistryMap.get(query.getCommandIdentifier()) instanceof HandleCallbackQuery)) {
            ((HandleCallbackQuery) this.commandRegistryMap.get(query.getCommandIdentifier())).respondCallbackQuery(absSender, cbq, query);
            return true;
        }

        return false;
    }

    /**
     * Execute the 'respondInlineQuery' method of the corrected UseCaseCommand
     * interpreted by extracting the viaggia.command from parsed 'inlineQuery'
     * this extraction is made by the QueryParser
     *
     * @param absSender   used to send the respond(s)
     * @param inlineQuery inlineQuery
     * @return true if there's an association or it's defined a defaultCommand able to reply to inlineQuery
     */
    /*package*/ final boolean respondInlineQuery(TimedAbsSender absSender, InlineQuery inlineQuery) {
        if (!inlineQuery.hasQuery() && inlineQuery.getQuery().isEmpty()) {
            if (this.defaultInlineCommand != null && this.defaultInlineCommand instanceof HandleInlineQuery) {
                ((HandleInlineQuery) this.defaultInlineCommand).respondInlineQuery(absSender, inlineQuery.getFrom(), inlineQuery.getId(), "");
                return true;
            }

            return false;
        }

        String command;
        String parameters = null;

        // viaggia.command [optional] [argument]
        String commandMessage = inlineQuery.getQuery().toLowerCase();

        if (commandMessage.contains(" ")) {
            command = commandMessage.substring(0, commandMessage.indexOf(" "));
            parameters = commandMessage.substring(commandMessage.indexOf(" ") + 1);
        } else {
            command = commandMessage;
        }

        if (this.commandRegistryMap.containsKey(command) && this.commandRegistryMap.get(command) instanceof HandleInlineQuery) {
            ((HandleInlineQuery) this.commandRegistryMap.get(command)).respondInlineQuery(absSender, inlineQuery.getFrom(), inlineQuery.getId(), parameters);
            return true;
        }

        if (this.defaultInlineCommand != null && this.defaultInlineCommand instanceof HandleInlineQuery) {
            ((HandleInlineQuery) this.defaultInlineCommand).respondInlineQuery(absSender, inlineQuery.getFrom(), inlineQuery.getId(), parameters);
            return true;
        }

        return false;
    }
}

