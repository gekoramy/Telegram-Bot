package bot;

import bot.exception.NotHandledCommandException;
import bot.timed.Chats;
import bot.timed.TimedTelegramLongPollingBot;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;

import java.io.InvalidClassException;

/**
 * Created by Luca Mosetti on 2017
 * <p>
 * Handle all the bot updates, which could be:
 * - Messages
 * - CallbackQuery
 * - InlineQuery
 * <p>
 * It understand which type of update is and gives it to the commandRegistry
 */
public abstract class BotHandler extends TimedTelegramLongPollingBot {
    private final String BOT_USERNAME;
    private final String BOT_TOKEN;
    private final CommandRegistry commandRegistry;

    /**
     * Here should be registered all the UseCaseCommand
     */
    protected BotHandler(String botName, String botToken, long maxMessagesPerMinute) {
        super(maxMessagesPerMinute);

        BOT_USERNAME = botName;
        BOT_TOKEN = botToken;
        commandRegistry = new CommandRegistry(BOT_USERNAME);
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
        if (update.hasMessage())
            onMessageUpdate(update.getMessage());

        if (update.hasCallbackQuery())
            onCallbackQueryUpdate(update.getCallbackQuery());

        if (update.hasInlineQuery())
            onInlineQueryUpdate(update.getInlineQuery());
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
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
            if (message.hasText()) {
                if (message.isCommand())
                    commandRegistry.respondCommand(this, message);
                else
                    commandRegistry.respondMessage(this, message, Chats.getCommand(message.getChatId()));
            }

            if (message.hasLocation()) {
                commandRegistry.respondLocation(this, message, Chats.getCommand(message.getChatId()));
            }
        } catch (NotHandledCommandException | IllegalArgumentException | InvalidClassException e) {
            e.printStackTrace();
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
