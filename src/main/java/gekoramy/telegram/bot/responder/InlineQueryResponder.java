package gekoramy.telegram.bot.responder;

import gekoramy.telegram.bot.timed.AbsResponder;
import gekoramy.telegram.bot.model.Monitor;
import gekoramy.telegram.bot.timed.TimedSender;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;

/**
 * It collects all the possible answers to an InlineQuery
 * Work as middle man between UsaCaseCommand - Monitor - TimedSender
 *
 * @author Luca Mosetti
 * @since 02/2018
 */
public class InlineQueryResponder extends AbsResponder {

    private final InlineQuery iq;

    public InlineQueryResponder(TimedSender sender, Monitor monitor, String command, InlineQuery iq) {
        super(sender, monitor, command);
        this.iq = iq;
    }

    public void answer(AnswerInlineQuery answerInlineQuery) {
        sender.requestExecute(
                null,
                answerInlineQuery.setInlineQueryId(iq.getId())
        );
        monitor.sent(iq.getFrom(), answerInlineQuery);
    }

    @Override
    public void close() {
        monitor.received(timeStamp, cmd, !handled, iq);
    }
}
