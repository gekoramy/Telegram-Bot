package bot.timed;

import org.telegram.telegrambots.api.methods.BotApiMethod;

import java.io.Serializable;

/**
 * Created by Luca Mosetti on 2017
 */
public interface TimedAbsSender {

    /**
     * Execute respecting the Telegram limits
     * In order to send in the correct order the updates to the same chat
     * you should set the chatId field even if the the method doesn't points one
     *
     * For example if you want this behaviour:
     * execute EditMessageText and only then execute AnswerCallbackQuery
     * you should call this method with same chatId, even if AnswerCallbackQuery doesn't have this field
     * otherwise AnswerCallbackQuery can be executed even much before EditMessageText
     *
     * chatId should be null only if there isn't a queue to respect / it's not possible to extract the chatId
     * This happens with InlineMessages
     *
     * @param chatId   the chatId of the receiver (even if the method doesn't have this field)
     * @param method   method to execute
     * @param <T>      extends Serializable
     * @param <Method> extends BotApiMethod
     */
    <T extends Serializable, Method extends BotApiMethod<T>> void requestExecute(Long chatId, Method method);

}
