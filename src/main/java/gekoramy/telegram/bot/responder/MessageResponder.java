package gekoramy.telegram.bot.responder;

import gekoramy.telegram.bot.model.Monitor;
import gekoramy.telegram.bot.timed.AbsResponder;
import gekoramy.telegram.bot.timed.TimedSender;
import gekoramy.telegram.bot.responder.type.MessageSender;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.api.objects.Message;

import java.io.Serializable;

/**
 * It collects all the possible answers to a Message
 * Work as middle man between UsaCaseCommand - Monitor - TimedSender
 *
 * @author Luca Mosetti
 * @since 02/2018
 */
public class MessageResponder extends AbsResponder implements MessageSender {
    final Message msg;

    public MessageResponder(TimedSender sender, Monitor monitor, String cmd, Message msg) {
        super(sender, monitor, cmd);
        this.msg = msg;
    }

    private void requestExecute(BotApiMethod<? extends Serializable> method) {
        sender.requestExecute(msg.getChatId(), method);
    }

    public MessageResponder send(SendMessage sendMessage) {
        requestExecute(sendMessage.setChatId(msg.getChatId()));
        monitor.sent(msg.getFrom(), sendMessage);
        return this;
    }

    public MessageResponder send(SendVenue sendVenue) {
        requestExecute(sendVenue.setChatId(msg.getChatId()));
        monitor.sent(msg.getFrom(), sendVenue);
        return this;
    }

    public MessageResponder send(SendLocation sendLocation) {
        requestExecute(sendLocation.setChatId(msg.getChatId()));
        monitor.sent(msg.getFrom(), sendLocation);
        return this;
    }

    public MessageResponder send(SendChatAction sendChatAction) {
        requestExecute(sendChatAction.setChatId(msg.getChatId()));
        monitor.sent(msg.getFrom(), sendChatAction);
        return this;
    }

    public MessageResponder send(SendContact sendContact) {
       requestExecute(sendContact.setChatId(msg.getChatId()));
        monitor.sent(msg.getFrom(), sendContact);
        return this;
    }

    public MessageResponder send(SendGame sendGame) {
        requestExecute(sendGame.setChatId(msg.getChatId()));
        monitor.sent(msg.getFrom(), sendGame);
        return this;
    }

    public void toComplete() {
        toComplete(msg.getChatId());
    }

    public void close() {
        monitor.received(timeStamp, cmd, !handled, msg);
    }
}
