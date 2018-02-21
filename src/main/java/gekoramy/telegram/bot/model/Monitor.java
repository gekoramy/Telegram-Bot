package gekoramy.telegram.bot.model;

import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageLiveLocation;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;

/**
 * Useful to analyze / to track:
 * - updates received
 * - object sent
 *
 * @author Luca Mosetti
 * @since 02/2018
 */
public interface Monitor {

    void sent(User user, SendMessage sendMessage);

    void sent(User user, SendLocation sendLocation);

    void sent(User user, SendVenue sendVenue);

    void sent(User user, EditMessageText editMessageText);

    void sent(User user, EditMessageReplyMarkup editMessageReplyMarkup);

    void sent(User user, EditMessageLiveLocation editMessageLiveLocation);

    void sent(User user, AnswerInlineQuery answerInlineQuery);

    void sent(User user, SendChatAction sendChatAction);

    void sent(User user, SendContact sendContact);

    void sent(User user, SendGame sendGame);

    void received(long timeStamp, String cmd, boolean not_handled, Message msg);

    void received(long timeStamp, String cmd, boolean not_handled, CallbackQuery cbq);

    void received(long timeStamp, String cmd, boolean not_handled, InlineQuery iq);
}
