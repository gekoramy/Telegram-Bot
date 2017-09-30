package bot.model.handling;

import bot.timed.TimedAbsSender;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.User;

/**
 * Created by Luca Mosetti on 2017
 */
public interface HandleLocation {

    /**
     * Responds to a location send by the client
     *
     * @param absSender used to send the respond(s)
     * @param user      client
     * @param chat      client's chat
     * @param location  client's location
     */
    void respondLocation(TimedAbsSender absSender, User user, Chat chat, Location location);

}
