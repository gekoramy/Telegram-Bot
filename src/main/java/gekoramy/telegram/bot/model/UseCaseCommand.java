package gekoramy.telegram.bot.model;

import gekoramy.telegram.bot.model.query.Query;
import gekoramy.telegram.bot.responder.CallbackQueryResponder;
import gekoramy.telegram.bot.responder.InlineCallbackQueryResponder;
import gekoramy.telegram.bot.responder.InlineQueryResponder;
import gekoramy.telegram.bot.responder.MessageResponder;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

/**
 * All the registered UseCaseCommand have to extend this class
 *
 * @author Luca Mosetti
 * @since 2017
 */
public class UseCaseCommand {

    /**
     * Every UseCaseCommand has a unique Command
     */
    private final Command command;

    private final boolean isHandlingInline;

    public UseCaseCommand(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("command cannot be null");
        }

        boolean tmp;

        try {
            tmp = this.getClass().getMethod("respondInlineQuery", InlineQueryResponder.class, User.class, String.class).getDeclaringClass() != UseCaseCommand.class;
        } catch (NoSuchMethodException ignored) {
            tmp = false;
        }

        this.isHandlingInline = tmp;
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public boolean isHandlingInline() {
        return isHandlingInline;
    }

    public void init() {
    }

    /**
     * Responds to an incomplete command
     * /command [null / empty]
     *
     * @param absSender used to toComplete the respond(s)
     * @param chat      client's chat
     * @param user      client
     * @param arguments possible [arguments]
     */
    public void respondCommand(MessageResponder absSender, Chat chat, User user, String arguments) {
    }

    /**
     * Responds to a complete command / arguments
     * /command [arguments]
     *
     * @param absSender used to toComplete the respond(s)
     * @param message   message to respond
     */
    public void respondMessage(MessageResponder absSender, Message message) {
    }

    /**
     * Respond to a CallbackQuery sent by a message
     *
     * @param absSender used to toComplete the respond(s)
     * @param query     callBackQuery
     * @param user      client who sent the CallbackQuery
     * @param message   message which sent the CallbackQuery
     */
    public void respondCallbackQuery(CallbackQueryResponder absSender, Query query, User user, Message message) {
    }

    /**
     * Respond to a CallbackQuery sent by an inline message
     * It will occur only if the bot supports inline query
     *
     * @param absSender used to toComplete the respond(s)
     * @param query     callBackQuery
     * @param user      client who sent the CallbackQuery
     */
    public void respondCallbackQuery(InlineCallbackQueryResponder absSender, Query query, User user) {
    }

    /**
     * Respond to an InlineQuery sent by a client
     * command [arguments]
     *
     * @param absSender used to toComplete the respond(s)
     * @param from      client
     * @param arguments arguments of a complete command
     */
    public void respondInlineQuery(InlineQueryResponder absSender, User from, String arguments) {
    }

    @Override
    public String toString() {
        return command.toString();
    }
}

