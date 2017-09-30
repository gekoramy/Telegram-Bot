package bot.model.handling;

import bot.model.query.Query;
import bot.timed.TimedAbsSender;
import org.telegram.telegrambots.api.objects.CallbackQuery;

/**
 * Created by Luca Mosetti on 2017
 */
public interface HandleCallbackQuery {

    /**
     * Respond to a CallbackQuery send by a client
     *
     * @param absSender used to send the respond(s)
     * @param cbq       callBackQuery
     * @param query     query
     */
    void respondCallbackQuery(TimedAbsSender absSender, CallbackQuery cbq, Query query);
}
