package gekoramy.telegram.bot.timed;

import gekoramy.telegram.bot.model.Monitor;

/**
 * Generic Responder
 * Work as middle man between UsaCaseCommand - Monitor - TimedSender
 * It simplify all of them
 *
 * @author Luca Mosetti
 * @since 02/2018
 */
public abstract class AbsResponder implements AutoCloseable {

    protected final TimedSender sender;
    protected final Monitor monitor;
    protected final String cmd;
    protected final long timeStamp;
    protected boolean handled = true;

    protected AbsResponder(TimedSender sender, Monitor monitor, String cmd) {
        this.sender = sender;
        this.monitor = monitor;
        this.cmd = cmd;
        this.timeStamp = System.currentTimeMillis();
    }

    protected void toComplete(Long chatId) {
        Chats.setCommand(chatId, cmd);
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

}
