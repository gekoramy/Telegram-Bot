package gekoramy.telegram.bot;

import gekoramy.telegram.bot.exception.NotHandledCommandException;
import gekoramy.telegram.bot.exception.TwoCommandException;
import gekoramy.telegram.bot.model.Command;
import gekoramy.telegram.bot.model.Monitor;
import gekoramy.telegram.bot.model.UseCaseCommand;
import gekoramy.telegram.bot.model.query.Query;
import gekoramy.telegram.bot.model.query.QueryParser;
import gekoramy.telegram.bot.responder.CallbackQueryResponder;
import gekoramy.telegram.bot.responder.InlineCallbackQueryResponder;
import gekoramy.telegram.bot.responder.InlineQueryResponder;
import gekoramy.telegram.bot.responder.MessageResponder;
import gekoramy.telegram.bot.timed.TimedSender;
import gekoramy.telegram.bot.util.Pair;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Associate Commands to UseCaseCommands
 *
 * @author Luca Mosetti
 * @since 2017
 */
public class CommandRegistry {
    private final String botUsername;
    private final Map<String, UseCaseCommand> commandRegistryMap;
    private final QueryParser queryParser;
    private final Monitor monitor;
    private final UseCaseCommand useless;

    private String defaultCmd = "defaultCmd";
    private String defaultInlineCmd = "defaultInlineCmd";

    CommandRegistry(String botUsername, Monitor monitor) {
        this.botUsername = botUsername;
        this.commandRegistryMap = new HashMap<>();
        this.queryParser = new QueryParser();
        this.monitor = monitor;

        useless = new UseCaseCommand(new Command(defaultCmd, ""));

        this.commandRegistryMap.put(defaultCmd, useless);
        this.commandRegistryMap.put(defaultInlineCmd, useless);
    }

    public Collection<UseCaseCommand> getRegisteredCommands() {
        Collection<UseCaseCommand> commands = this.commandRegistryMap.values();
        commands.remove(useless);
        commands.remove(useless);
        return commands;
    }

    /**
     * @param text 'command [argument] [argument]'
     * @return Pair.of(String Command, String Arguments)
     */
    private Pair<String, String> parse(String text) {
        String command;
        String parameters = null;

        if (text.contains(" ")) {
            command = text.substring(0, text.indexOf(" "));
            parameters = text.substring(text.indexOf(" ") + 1);
        } else {
            command = text;
        }

        command = command.replace("@" + this.botUsername, "").trim();

        return Pair.of(command, parameters);
    }

    /**
     * Add an association
     *
     * @param useCaseCommand UseCaseCommand
     * @throws TwoCommandException trying to register 2 UseCaseCommand with same Command
     */
    public final void register(UseCaseCommand useCaseCommand) throws TwoCommandException {
        if (this.commandRegistryMap.containsKey(useCaseCommand.getCommand().getCommandIdentifier()))
            throw new TwoCommandException(useCaseCommand.getCommand());

        useCaseCommand.init();
        this.commandRegistryMap.put(useCaseCommand.getCommand().getCommandIdentifier(), useCaseCommand);
    }

    /**
     * Optional UseCaseCommand used when a request doesn't use registered Commands
     *
     * @param cmd UseCaseCommand
     */
    public void setDefaultCmd(Command cmd) {
        if (!this.commandRegistryMap.containsKey(cmd.getCommandIdentifier()))
            throw new IllegalArgumentException();

        this.defaultCmd = cmd.getCommandIdentifier();
    }

    /**
     * Optional UseCaseCommand used when an inline request doesn't use registered Commands
     *
     * @param cmd UseCaseCommand
     */
    public void setDefaultInlineCmd(Command cmd) {
        if (!this.commandRegistryMap.containsKey(cmd.getCommandIdentifier()))
            throw new IllegalArgumentException();

        this.defaultInlineCmd = cmd.getCommandIdentifier();
    }

    /**
     * Execute the 'respondCommand' method of the corrected UseCaseCommand
     * interpreted by extracting the command from 'message'
     *
     * @param absSender used to answer the respond(s)
     * @param message   message to respond
     */
    final void respondCommand(TimedSender absSender, Message message) {
        if (!message.hasText() || !message.isCommand())
            throw new IllegalArgumentException();

        Pair<String, String> parameters = parse(message.getText().substring(1));
        String command = this.commandRegistryMap.containsKey(parameters.a()) ? parameters.a() : defaultCmd;
        String arguments = parameters.b();

        try (MessageResponder responder = new MessageResponder(absSender, monitor, command, message)) {
            this.commandRegistryMap.get(command).respondCommand(
                    responder,
                    message.getChat(),
                    message.getFrom(),
                    arguments
            );
        }
    }

    /**
     * Execute the 'respondMessage' method of the corrected UseCaseCommand
     * required by the argument 'command'
     *
     * @param absSender used to postGMRequest the respond(s)
     * @param message   message to respond
     * @param command   unique command
     * @throws NotHandledCommandException there is no Command-UseCaseCommand association for this command
     */
    final void respondMessage(TimedSender absSender, Message message, String command) throws NotHandledCommandException {
        if (!this.commandRegistryMap.containsKey(command))
            throw new NotHandledCommandException(command);

        try (MessageResponder responder = new MessageResponder(absSender, monitor, command, message)) {
            this.commandRegistryMap.get(command).respondMessage(
                    responder,
                    message
            );
        }
    }

    /**
     * Execute the 'respondCallbackQuery' method of the corrected UseCaseCommand
     * interpreted by extracting the command from 'cbq'
     * this extraction is made by the QueryParser
     *
     * @param absSender used to postGMRequest the respond(s)
     * @param cbq       CallbackQuery
     */
    final void respondCallbackQuery(TimedSender absSender, CallbackQuery cbq) {
        Query query = queryParser.parse(cbq.getData());

        if (this.commandRegistryMap.containsKey(query.getCommandIdentifier())) {
            if (cbq.getMessage() != null) {
                try (CallbackQueryResponder responder = new CallbackQueryResponder(absSender, monitor, query.getCommandIdentifier(), cbq)) {
                    this.commandRegistryMap.get(query.getCommandIdentifier()).respondCallbackQuery(
                            responder,
                            query,
                            cbq.getFrom(),
                            cbq.getMessage()
                    );
                }
            } else {
                try (InlineCallbackQueryResponder responder = new InlineCallbackQueryResponder(absSender, monitor, query.getCommandIdentifier(), cbq)) {
                    this.commandRegistryMap.get(query.getCommandIdentifier()).respondCallbackQuery(
                            responder,
                            query,
                            cbq.getFrom()
                    );
                }
            }
        }
    }

    /**
     * Execute the 'respondInlineQuery' method of the corrected UseCaseCommand
     * interpreted by extracting the command from parsed 'inlineQuery'
     *
     * @param absSender   used to answer the respond(s)
     * @param inlineQuery InlineQuery
     */
    final void respondInlineQuery(TimedSender absSender, InlineQuery inlineQuery) {
        if (!inlineQuery.hasQuery() && inlineQuery.getQuery().isEmpty()) {
            try (InlineQueryResponder responder = new InlineQueryResponder(absSender, monitor, this.defaultInlineCmd, inlineQuery)) {
                this.commandRegistryMap.get(this.defaultInlineCmd).respondInlineQuery(
                        responder,
                        inlineQuery.getFrom(),
                        ""
                );
            }
            return;
        }

        Pair<String, String> parameters = parse(inlineQuery.getQuery());
        String command = this.commandRegistryMap.containsKey(parameters.a()) ? parameters.a() : defaultInlineCmd;
        String arguments = parameters.b();

        try (InlineQueryResponder responder = new InlineQueryResponder(absSender, monitor, command, inlineQuery)) {
            this.commandRegistryMap.get(command).respondInlineQuery(
                    responder,
                    inlineQuery.getFrom(),
                    arguments
            );
        }
    }
}
