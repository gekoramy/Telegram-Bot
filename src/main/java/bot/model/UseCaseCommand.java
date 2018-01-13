package bot.model;

import bot.timed.TimedAbsSender;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

/**
 * Created by Luca Mosetti on 2017
 * <p>
 * All the registered UseCaseCommand have to extend this class
 */
public abstract class UseCaseCommand {

    /**
     * Every UseCaseCommand has a unique Command
     */
    private final Command command;

    public UseCaseCommand(Command command) {
        if (command == null)
            throw new IllegalArgumentException("command cannot be null");

        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void init() {
    }

    /**
     * Responds to a command
     * /command [arguments]
     *
     * @param absSender used to send the respond(s)
     * @param user      client
     * @param chat      client chat
     * @param arguments possible [arguments]
     */
    public abstract void respondCommand(TimedAbsSender absSender, User user, Chat chat, String arguments);

    /**
     * Responds to a complete command / arguments
     * /command [arguments]
     *
     * @param absSender used to send the respond(s)
     * @param message   message to respond
     */
    public abstract void respondMessage(TimedAbsSender absSender, Message message);

    /**
     * Compare formatted and unformatted text
     *
     * @param formattedText   EditMessage text
     * @param unformattedText Message text to be edited
     * @param parseMode       ParseMode
     * @return formattedText.replaceAll(...).equals(unformattedText)
     * false if parseMode is not supported
     */
    protected boolean equalsFormattedTexts(String formattedText, String unformattedText, String parseMode) {
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

    @Override
    public String toString() {
        return command.toString();
    }
}

