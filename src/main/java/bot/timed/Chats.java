package bot.timed;

import bot.model.Command;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

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

    static synchronized void update(long chatId, long time) {
        chats.putIfAbsent(chatId, new Bundle(new Command("start", "")));
        chats.get(chatId).update(time);
    }

    static synchronized int getSent(long chatId, long now) {
        chats.putIfAbsent(chatId, new Bundle(new Command("start", "")));
        return chats.get(chatId).getSent(now);
    }

    private static final class Bundle {
        private Command command;
        private final ExpirationQueue updatesExecuted;

        private Bundle(Command command) {
            this.command = command;
            this.updatesExecuted = new ExpirationQueue(1, TimeUnit.MINUTES);
        }

        private Command getCommand() {
            return command;
        }

        private void setCommand(Command command) {
            this.command = command;
        }

        private synchronized void update(long time) {
            this.updatesExecuted.add(time);
        }

        private synchronized int getSent(long time) {
            return updatesExecuted.size(time);
        }
    }

    private static final class ExpirationQueue {
        private final Queue<Long> updates = new LinkedList<>();
        private final long duration;

        private ExpirationQueue(long duration, @NotNull TimeUnit timeUnit) {
            this.duration = timeUnit.toMillis(duration);
        }

        private synchronized void add(long currentTime) {
            updates.add(currentTime);
        }

        private synchronized int size(long time) {
            while (updates.size() > 0 && updates.peek() + duration < time) {
                updates.poll();
            }

            return updates.size();
        }
    }
}