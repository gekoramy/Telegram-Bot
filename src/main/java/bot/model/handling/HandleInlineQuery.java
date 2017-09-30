package bot.model.handling;

import bot.timed.TimedAbsSender;
import org.telegram.telegrambots.api.objects.User;

/**
 * Created by Luca Mosetti on 2017
 */
public interface HandleInlineQuery {

    /**
     * Respond to an InlineQuery send by a client
     * command [arguments]
     *
     * @param absSender used to send the respond(s)
     * @param from      client
     * @param id        client's id
     * @param arguments arguments of a complete command
     */
    void respondInlineQuery(TimedAbsSender absSender, User from, String id, String arguments);

}
