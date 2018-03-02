package gekoramy.telegram.bot.responder;

import gekoramy.telegram.bot.model.Monitor;
import gekoramy.telegram.bot.timed.AbsResponder;
import gekoramy.telegram.bot.timed.TimedSender;
import gekoramy.telegram.bot.responder.type.MessageSender;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

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
    User user;

    public MessageResponder(TimedSender sender, Monitor monitor, String cmd, Message msg) {
        super(sender, monitor, cmd);
        this.msg = msg;
        this.user = msg.getFrom();
    }

    private void requestExecute(BotApiMethod<? extends Serializable> method) {
        sender.requestExecute(msg.getChatId(), method);
    }

    public MessageResponder send(SendMessage sendMessage) {
        requestExecute(sendMessage.setChatId(msg.getChatId()));
        monitor.sent(user, sendMessage);
        return this;
    }

    public MessageResponder send(SendVenue sendVenue) {
        requestExecute(sendVenue.setChatId(msg.getChatId()));
        monitor.sent(user, sendVenue);
        return this;
    }

    public MessageResponder send(SendLocation sendLocation) {
        requestExecute(sendLocation.setChatId(msg.getChatId()));
        monitor.sent(user, sendLocation);
        return this;
    }

    public MessageResponder send(SendChatAction sendChatAction) {
        requestExecute(sendChatAction.setChatId(msg.getChatId()));
        monitor.sent(user, sendChatAction);
        return this;
    }

    public MessageResponder send(SendContact sendContact) {
       requestExecute(sendContact.setChatId(msg.getChatId()));
        monitor.sent(user, sendContact);
        return this;
    }

    public MessageResponder send(SendGame sendGame) {
        requestExecute(sendGame.setChatId(msg.getChatId()));
        monitor.sent(user, sendGame);
        return this;
    }

    public void toComplete() {
        toComplete(msg.getChatId());
    }

    public void close() {
        monitor.received(timeStamp, cmd, !handled, msg);
    }
}
