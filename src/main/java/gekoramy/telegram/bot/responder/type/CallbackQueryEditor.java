package gekoramy.telegram.bot.responder.type;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;

/**
 * @author Luca Mosetti
 * @since 02/2018
 */
public interface CallbackQueryEditor extends MessageEditor {

    CallbackQueryEditor answer(AnswerCallbackQuery answerCallbackQuery);

}
