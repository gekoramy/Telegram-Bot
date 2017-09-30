package bot.timed;

import bot.model.Command;
import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Luca Mosetti on 2017
 * <p>
 * When a new conversation with this bot is started
 * there're saved some information about the ChatId (Not the User!)
 * <p>
 * The command which has to be completed
 * The time of the updates received in the last minute
 */
public class Chats {

    private static final Map<Long, Bundle> chats = new HashMap<>();

    public static Command getCommand(long chatId) {
        chats.putIfAbsent(chatId, new Bundle(new Command("start", "")));
        return chats.get(chatId).getCommand();
    }

    public static void setCommand(long chatId, Command command) {
        chats.putIfAbsent(chatId, new Bundle(command));
        chats.get(chatId).setCommand(command);
    }

    static synchronized void update(long chatId, long time) throws ExecutionException {
        chats.putIfAbsent(chatId, new Bundle(new Command("start", "")));
        chats.get(chatId).update(time);
    }

    static synchronized List<Long> getSent(long chatId, long now) {
        chats.putIfAbsent(chatId, new Bundle(new Command("start", "")));
        return chats.get(chatId).getSent(now);
    }

    private static final class Bundle {
        private Command command;
        private final List<Long> updatesExecuted;

        public Bundle(Command command) {
            this.command = command;
            this.updatesExecuted = new ArrayList<>();
        }

        @Contract(pure = true)
        public Command getCommand() {
            return command;
        }

        public void setCommand(Command command) {
            this.command = command;
        }

        public synchronized void update(long time) {
            this.updatesExecuted.add(time);
        }

        public synchronized List<Long> getSent(long time) {
            for (int i = 0; i < updatesExecuted.size(); i++) {
                if ((updatesExecuted.get(i) + (1000 * 60)) < time)
                    updatesExecuted.remove(i);
            }
            return updatesExecuted;
        }
    }
}