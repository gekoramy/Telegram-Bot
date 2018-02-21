package gekoramy.telegram.bot.responder.type;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageLiveLocation;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;

/**
 * @author Luca Mosetti
 * @since 02/2018
 */
public interface MessageEditor extends Handling {

    MessageEditor send(EditMessageText editMessageText);

    MessageEditor send(EditMessageReplyMarkup editMessageReplyMarkup);

    MessageEditor send(EditMessageLiveLocation editMessageLiveLocation);

}
