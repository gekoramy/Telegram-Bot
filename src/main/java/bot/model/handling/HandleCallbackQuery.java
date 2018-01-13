package bot.model.handling;

import bot.model.query.Query;
import bot.timed.TimedAbsSender;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

/**
 * Created by Luca Mosetti on 2017
 */
public interface HandleCallbackQuery {

    /**
     * Respond to a CallbackQuery sent by a message
     *
     * @param absSender       used to send the respond(s)
     * @param callbackQueryId callbackQueryId to respond
     * @param query           callBackQuery
     * @param user            client who sent the CallbackQuery
     * @param message         message which sent the CallbackQuery
     */
    void respondCallbackQuery(TimedAbsSender absSender, String callbackQueryId, Query query, User user, Message message);

    /**
     * Respond to a CallbackQuery sent by an inline message
     * It will occur only if the bot supports inline query
     *
     * @param absSender       used to send the respond(s)
     * @param callbackQueryId callbackQueryId to respond
     * @param query           callBackQuery
     * @param user            client who sent the CallbackQuery
     * @param inlineMessageId inlineMessageId
     */
    void respondCallbackQuery(TimedAbsSender absSender, String callbackQueryId, Query query, User user, String inlineMessageId);
}
