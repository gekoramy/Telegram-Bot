package gekoramy.telegram.bot.timed;

import org.telegram.telegrambots.api.methods.*;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Execute methods respecting the Telegram limits:
 * - MANY_CHATS_SEND_INTERVAL ms between messages
 * - ONE_CHAT_SEND_INTERVAL ms between messages to same chat
 * - maxMessagesPerMinute (default 10, Telegram says that shouldn't be more than 20)
 *
 * @author Luca Mosetti
 * @since 2017
 */
public abstract class TimedDefaultAbsSender extends DefaultAbsSender implements TimedSender {

    private static final long MANY_CHATS_SEND_INTERVAL = TimeUnit.MILLISECONDS.toMillis(33);
    private static final long ONE_CHAT_SEND_INTERVAL = TimeUnit.SECONDS.toMillis(1);
    private static final long CHAT_INACTIVE_INTERVAL = TimeUnit.MINUTES.toMillis(10);

    // Some methods are not limited
    private static final List<String> NO_WAIT_NO_TRACK = Arrays.asList(AnswerCallbackQuery.PATH, AnswerInlineQuery.PATH, AnswerPreCheckoutQuery.PATH, AnswerShippingQuery.PATH);

    private final long maxMessagesPerMinute;

    private final ConcurrentHashMap<Long, MessageQueue> mMessagesMap = new ConcurrentHashMap<>(32, 0.75f, 1);
    private final ArrayList<MessageQueue> mSendQueues = new ArrayList<>();
    private final AtomicBoolean mSendRequested = new AtomicBoolean(false);

    TimedDefaultAbsSender(DefaultBotOptions options, long maxMessagesPerMinute) {
        super(options);
        this.maxMessagesPerMinute = maxMessagesPerMinute < 10 ? 10 : maxMessagesPerMinute;

        // checks if there is to execute, and eventually executes, a method every MANY_CHATS_SEND_INTERVAL ms
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                new MessageSenderRunnable(),
                MANY_CHATS_SEND_INTERVAL,
                MANY_CHATS_SEND_INTERVAL,
                TimeUnit.MILLISECONDS
        );
    }

    protected abstract void onFailure(Exception e);

    private synchronized <T extends Serializable, M extends BotApiMethod<T>> void syncExecute(long chatId, M method) {
        try {
            super.execute(method);

            if (!NO_WAIT_NO_TRACK.contains(method.getMethod()))
                Chats.update(chatId, System.currentTimeMillis());
        } catch (TelegramApiException e) {
            onFailure(e);
        }
    }

    @Override
    public <T extends Serializable, M extends BotApiMethod<T>> void requestExecute(Long chatId, M method) {
        if (chatId == null)
            chatId = -1L;

        MessageQueue queue = mMessagesMap.get(chatId);
        if (queue == null) {
            queue = new MessageQueue(chatId);
            queue.putMessage(method);
            mMessagesMap.put(chatId, queue);
        } else {
            queue.putMessage(method);
            // double check, because the queue can be removed from hashmap on state DELETE
            mMessagesMap.putIfAbsent(chatId, queue);
        }
        mSendRequested.set(true);
    }

    private final class MessageSenderRunnable implements Runnable {
        @Override
        public void run() {
            try {
                // There're messages which has to be sent
                if (!mSendRequested.getAndSet(false))
                    return;

                long currentTime = System.currentTimeMillis();
                mSendQueues.clear();
                boolean processNext = false;

                // 1st step
                // Find all chats in which already allowed to send message
                // (passed more than ONE_CHAT_SEND_INTERVAL ms from previous send)
                Iterator<Map.Entry<Long, MessageQueue>> it = mMessagesMap.entrySet().iterator();
                while (it.hasNext()) {
                    MessageQueue queue = it.next().getValue();

                    // Check
                    switch (queue.getCurrentState(currentTime)) {
                        case MessageQueue.SEND:
                            mSendQueues.add(queue);
                            processNext = true;
                            break;

                        case MessageQueue.WAIT:
                            processNext = true;
                            break;

                        case MessageQueue.DELETE:
                            it.remove();
                            break;
                    }
                }

                // If any of chats are in state of WAIT or SEND
                // Request another iteration
                if (processNext) mSendRequested.set(true);

                // 2nd step
                // Find oldest waiting queue and poll its message
                MessageQueue sendQueue = null;
                long oldestPutTime = Long.MAX_VALUE;
                for (MessageQueue queue : mSendQueues) {
                    long putTime = queue.getPutTime();
                    if (putTime < oldestPutTime) {
                        oldestPutTime = putTime;
                        sendQueue = queue;
                    }
                }

                // Possible if on first step wasn't found any chats in state SEND
                if (sendQueue == null) return;

                // Invoke the send callback
                // ChatId is passed to check how many messages per minute has sent
                syncExecute(sendQueue.getChatId(), sendQueue.getMethod(currentTime));
            } catch (Exception e) {
                onFailure(e);
            }
        }
    }

    private class MessageQueue {
        private static final int EMPTY = 0;     // Queue is empty
        private static final int WAIT = 1;      // Queue has message(s) but not yet allowed to send
        private static final int DELETE = 2;    // None message of given queue was sent longer than CHAT_INACTIVE_INTERVAL, delete for optimisation
        private static final int SEND = 3;      // Queue has message(s) and ready to send
        private final long chatId;
        private final ConcurrentLinkedQueue<BotApiMethod<? extends Serializable>> mQueue = new ConcurrentLinkedQueue<>();
        private long mLastSendTime;             // Time of last poll from queue
        private volatile long mLastPutTime;     // Time of last put into queue

        private MessageQueue(long chatId) {
            this.chatId = chatId;
        }

        synchronized <T extends Serializable, M extends BotApiMethod<T>> void putMessage(M method) {
            mQueue.add(method);
            mLastPutTime = System.currentTimeMillis();
        }

        synchronized int getCurrentState(long currentTime) {
            // currentTime is passed as parameter for optimisation
            long interval = currentTime - mLastSendTime;
            boolean empty = mQueue.isEmpty();

            if (!empty && (NO_WAIT_NO_TRACK.contains(mQueue.peek().getMethod()) || (interval > ONE_CHAT_SEND_INTERVAL && Chats.getSent(chatId, currentTime) < maxMessagesPerMinute)))
                return SEND;

            if (interval > CHAT_INACTIVE_INTERVAL)
                return DELETE;

            if (empty)
                return EMPTY;

            return WAIT;
        }

        synchronized BotApiMethod<? extends Serializable> getMethod(long currentTime) {
            mLastSendTime = currentTime;
            return mQueue.poll();
        }

        long getPutTime() {
            return mLastPutTime;
        }

        long getChatId() {
            return chatId;
        }
    }
}