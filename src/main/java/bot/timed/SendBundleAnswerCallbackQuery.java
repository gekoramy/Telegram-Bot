package bot.timed;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;

import java.io.Serializable;

/**
 * Created by gekoramy on 2017
 * <p>
 * To send a message, to edit one, or whatever in responds to a callbackquery
 * With an AnswerCallbackQuery attached
 *
 * @param <T>
 * @param <Method>
 */
public class SendBundleAnswerCallbackQuery<T extends Serializable, Method extends BotApiMethod<T>> {

    private final Method method;
    private final AnswerCallbackQuery answerCallbackQuery;
    private String chatId = null;

    public SendBundleAnswerCallbackQuery(Method method, AnswerCallbackQuery answerCallbackQuery) {
        if (!(method instanceof SendMessage) && !(method instanceof EditMessageText) && !(method instanceof EditMessageReplyMarkup))
            throw new IllegalArgumentException();

        if (method instanceof SendMessage)
            chatId = ((SendMessage) method).getChatId();

        if (method instanceof EditMessageText)
            chatId = ((EditMessageText) method).getChatId();

        if (method instanceof EditMessageReplyMarkup)
            chatId = ((EditMessageReplyMarkup) method).getChatId();

        this.method = method;
        this.answerCallbackQuery = answerCallbackQuery;
    }

    String getChatId() {
        return chatId;
    }

    public Method getMethod() {
        return method;
    }

    public AnswerCallbackQuery getAnswerCallbackQuery() {
        return answerCallbackQuery;
    }
}
