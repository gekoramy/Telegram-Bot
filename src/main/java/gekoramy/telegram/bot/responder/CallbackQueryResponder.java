package gekoramy.telegram.bot.responder;

import gekoramy.telegram.bot.responder.type.CallbackQueryEditor;
import gekoramy.telegram.bot.model.Monitor;
import gekoramy.telegram.bot.timed.TimedSender;
import gekoramy.telegram.bot.responder.type.MessageEditor;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageLiveLocation;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;

import java.io.Serializable;

/**
 * It collects all the possible answers to a CallbackQuery
 * Work as middle man between UsaCaseCommand - Monitor - TimedSender
 *
 * @author Luca Mosetti
 * @since 02/2018
 */
public class CallbackQueryResponder extends MessageResponder implements CallbackQueryEditor, MessageEditor {

    private final CallbackQuery cbq;

    public CallbackQueryResponder(TimedSender sender, Monitor monitor, String cmd, CallbackQuery cbq) {
        super(sender, monitor, cmd, cbq.getMessage());
        this.cbq = cbq;
    }

    private void requestExecute(BotApiMethod<? extends Serializable> method) {
        sender.requestExecute(msg.getChatId(), method);
    }

    public CallbackQueryResponder answer(AnswerCallbackQuery aCbq) {
        requestExecute(aCbq.setCallbackQueryId(cbq.getId()));
        return this;
    }

    public CallbackQueryResponder send(EditMessageText editMessageText) {
        if (!equalsFormattedTexts(editMessageText.getText(), msg.getText(), ParseMode.MARKDOWN)) {
            requestExecute(
                    editMessageText
                            .setMessageId(msg.getMessageId())
                            .setChatId(msg.getChatId())
            );
        }
        monitor.sent(cbq.getFrom(), editMessageText);
        return this;
    }

    public CallbackQueryResponder send(EditMessageReplyMarkup editMessageReplyMarkup) {
        requestExecute(
                editMessageReplyMarkup
                        .setMessageId(msg.getMessageId())
                        .setChatId(msg.getChatId())
        );
        monitor.sent(cbq.getFrom(), editMessageReplyMarkup);
        return this;
    }

    public CallbackQueryResponder send(EditMessageLiveLocation editMessageLiveLocation) {
        requestExecute(
                editMessageLiveLocation
                        .setMessageId(msg.getMessageId())
                        .setChatId(msg.getChatId())
        );
        monitor.sent(cbq.getFrom(), editMessageLiveLocation);
        return this;
    }

    @Override
    public void close() {
        monitor.received(timeStamp, cmd, handled, cbq);
    }

    /**
     * Compare formatted and unformatted text
     *
     * @param formattedText   EditMessage text
     * @param unformattedText Message text to be edited
     * @param parseMode       ParseMode
     * @return formattedText.replaceAll(...).equals(unformattedText)
     * false if parseMode is not supported
     */
    public boolean equalsFormattedTexts(String formattedText, String unformattedText, String parseMode) {
        switch (parseMode) {
            /*
             * *bold text*
             * _italic text_
             * [text](http://www.example.com/)
             * `inline fixed-width code`
             * ```text
             * pre-formatted fixed-width code block
             * ```
             */
            case ParseMode.MARKDOWN:
                return formattedText
                        .trim()
                        .replaceAll("\\*", "")
                        .replaceAll("_", "")
                        .replaceAll("`", "")
                        .replaceAll("```", "").equals(unformattedText);

            /*
             * <b>bold</b>, <strong>bold</strong>
             * <i>italic</i>, <em>italic</em>
             * <a href="http://www.example.com/">inline URL</a>
             * <code>inline fixed-width code</code>
             * <pre>pre-formatted fixed-width code block</pre>
             */
            case ParseMode.HTML:
                return formattedText
                        .trim()
                        .replaceAll("<b>", "").replaceAll("</b>", "")
                        .replaceAll("<i>", "").replaceAll("</i>", "")
                        .replaceAll("<code>", "").replaceAll("</code>", "")
                        .replaceAll("<pre>", "").replaceAll("</pre>", "")
                        .equals(unformattedText);

            default:
                return false;
        }
    }
}
