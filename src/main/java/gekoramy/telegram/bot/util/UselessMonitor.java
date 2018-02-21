package gekoramy.telegram.bot.util;

import gekoramy.telegram.bot.model.Monitor;
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
 * @author Luca Mosetti
 * @since 02/2018
 */
public class UselessMonitor implements Monitor {
    @Override
    public void sent(User user, SendMessage sendMessage) {

    }

    @Override
    public void sent(User user, SendLocation sendLocation) {

    }

    @Override
    public void sent(User user, SendVenue sendVenue) {

    }

    @Override
    public void sent(User user, EditMessageText editMessageText) {

    }

    @Override
    public void sent(User user, EditMessageReplyMarkup editMessageReplyMarkup) {

    }

    @Override
    public void sent(User user, EditMessageLiveLocation editMessageLiveLocation) {

    }

    @Override
    public void sent(User user, AnswerInlineQuery answerInlineQuery) {

    }

    @Override
    public void sent(User user, SendChatAction sendChatAction) {

    }

    @Override
    public void sent(User user, SendContact sendContact) {

    }

    @Override
    public void sent(User user, SendGame sendGame) {

    }

    @Override
    public void received(long timeStamp, String cmd, boolean not_handled, Message msg) {

    }

    @Override
    public void received(long timeStamp, String cmd, boolean not_handled, CallbackQuery cbq) {

    }

    @Override
    public void received(long timeStamp, String cmd, boolean not_handled, InlineQuery iq) {

    }
}
