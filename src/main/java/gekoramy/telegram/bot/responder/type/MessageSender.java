package gekoramy.telegram.bot.responder.type;

import gekoramy.telegram.bot.responder.MessageResponder;
import org.telegram.telegrambots.api.methods.send.*;

/**
 * @author Luca Mosetti
 * @since 02/2018
 */
public interface MessageSender extends Handling {

    MessageSender send(SendMessage sendMessage);

    MessageSender send(SendVenue sendVenue);

    MessageSender send(SendLocation sendLocation);

    MessageSender send(SendChatAction sendChatAction);

    MessageResponder send(SendContact sendContact);

    MessageResponder send(SendGame sendContact);

}
