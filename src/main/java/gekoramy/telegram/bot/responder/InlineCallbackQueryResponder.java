package gekoramy.telegram.bot.responder;

import gekoramy.telegram.bot.timed.AbsResponder;
import gekoramy.telegram.bot.responder.type.CallbackQueryEditor;
import gekoramy.telegram.bot.model.Monitor;
import gekoramy.telegram.bot.timed.TimedSender;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageLiveLocation;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;

import java.io.Serializable;

/**
 * It collects all the possible answers to a CallbackQuery received from an InlineMessage
 * Work as middle man between UsaCaseCommand - Monitor - TimedSender
 *
 * @author Luca Mosetti
 * @since 02/2018
 */
public class InlineCallbackQueryResponder extends AbsResponder implements CallbackQueryEditor {

    private final CallbackQuery cbq;

    public InlineCallbackQueryResponder(TimedSender sender, Monitor monitor, String cmd, CallbackQuery cbq) {
        super(sender, monitor, cmd);
        this.cbq = cbq;
    }

    private void requestExecute(BotApiMethod<? extends Serializable> method) {
        sender.requestExecute((long) cbq.getFrom().getId(), method);
    }

    public InlineCallbackQueryResponder answer(AnswerCallbackQuery answerCallbackQuery) {
        requestExecute(answerCallbackQuery.setCallbackQueryId(cbq.getId()));
        return this;
    }

    public InlineCallbackQueryResponder send(EditMessageText editMessageText) {
        requestExecute(editMessageText.setInlineMessageId(cbq.getInlineMessageId()));
        monitor.sent(cbq.getFrom(), editMessageText);
        return this;
    }

    public InlineCallbackQueryResponder send(EditMessageReplyMarkup editMessageReplyMarkup) {
        requestExecute(editMessageReplyMarkup.setInlineMessageId(cbq.getInlineMessageId()));
        monitor.sent(cbq.getFrom(), editMessageReplyMarkup);
        return this;
    }

    public InlineCallbackQueryResponder send(EditMessageLiveLocation editMessageLiveLocation) {
        requestExecute(editMessageLiveLocation.setInlineMessageId(cbq.getInlineMessageId()));
        monitor.sent(cbq.getFrom(), editMessageLiveLocation);
        return this;
    }

    @Override
    public void close() {
        monitor.received(timeStamp, cmd, handled, cbq);
    }
}
