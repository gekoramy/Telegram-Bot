package gekoramy.telegram.bot;

import gekoramy.telegram.bot.model.Monitor;
import gekoramy.telegram.bot.timed.Chats;
import gekoramy.telegram.bot.timed.TimedTelegramLongPollingBot;
import gekoramy.telegram.bot.util.UselessMonitor;
import gekoramy.telegram.bot.exception.NotHandledCommandException;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;

/**
 * Handle all the bot updates, which could be:
 * - Messages
 * - CallbackQuery
 * - InlineQuery
 * <p>
 * It understand which type of update is and gives it to the commandRegistry
 *
 * @author Luca Mosetti
 * @since 2017
 */
public abstract class UseCaseBot extends TimedTelegramLongPollingBot {
    private final String botName;
    private final String botToken;
    private final CommandRegistry commandRegistry;
    private final Monitor monitor;

    /**
     * Here should be registered all the UseCaseCommand
     *
     * @param botName      bot's name
     * @param botToken     bot's token
     * @param msgPerMinute max messages per minute
     * @param monitor      monitor / analyzer of the traffic
     */
    protected UseCaseBot(String botName, String botToken, long msgPerMinute, Monitor monitor) {
        super(msgPerMinute);

        this.botName = botName;
        this.botToken = botToken;
        this.monitor = monitor == null ? new UselessMonitor() : monitor;
        this.commandRegistry = new CommandRegistry(this.botName, this.monitor);
    }

    protected CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    /**
     * @param update update received, which could be:
     *               - Messages
     *               - CallbackQuery
     *               - InlineQuery
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            onMessageUpdate(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            onCallbackQueryUpdate(update.getCallbackQuery());
        } else if (update.hasInlineQuery()) {
            onInlineQueryUpdate(update.getInlineQuery());
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Once understood that the update is a message
     * distinguishes command message, simply-text message, location.
     * It gives the update to the commandRegistry, which will
     * understand which UseCaseCommand should respond
     *
     * @param message message update received, which could be:
     *                - text
     *                - location
     *                - ...
     */
    protected void onMessageUpdate(Message message) {
        try {
            if (message.isCommand()) {
                commandRegistry.respondCommand(this, message);
            } else {
                commandRegistry.respondMessage(this, message, Chats.getCommand(message.getChatId()));
            }

        } catch (NotHandledCommandException e) {
            monitor.received(System.currentTimeMillis(), e.getCommand(), true, message);
        } catch (IllegalArgumentException e) {
            onFailure(e);
        }
    }

    /**
     * Once understood that the update is a callbackQuery
     * it gives it to the commandRegistry, which will
     * understand which UseCaseCommand should respond
     *
     * @param callbackQuery callbackQuery update received
     */
    protected void onCallbackQueryUpdate(CallbackQuery callbackQuery) {
        commandRegistry.respondCallbackQuery(this, callbackQuery);
    }

    /**
     * Once understood that the update is an inlineQuery
     * it gives it to the commandRegistry, which will
     * understand which UseCaseCommand should respond
     *
     * @param inlineQuery inlineQuery update received
     */
    protected void onInlineQueryUpdate(InlineQuery inlineQuery) {
        commandRegistry.respondInlineQuery(this, inlineQuery);
    }
}
