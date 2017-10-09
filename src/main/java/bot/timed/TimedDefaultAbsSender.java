package bot.timed;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Daniil Nikanov aka JetCoder
 * <p>
 * Edited by Luca Mosetti on 2017
 */
public abstract class TimedDefaultAbsSender extends DefaultAbsSender implements TimedAbsSender {

    private static final long MANY_CHATS_SEND_INTERVAL = 33;
    private static final long ONE_CHAT_SEND_INTERVAL = 1000;
    private static final long CHAT_INACTIVE_INTERVAL = 1000 * 60 * 10;
    private static final long MAX_MESSAGES_PER_MINUTE = 10;

    private final ConcurrentHashMap<String, MessageQueue> mMessagesMap = new ConcurrentHashMap<>(32, 0.75f, 1);
    private final ArrayList<MessageQueue> mSendQueues = new ArrayList<>();
    private final AtomicBoolean mSendRequested = new AtomicBoolean(false);

    TimedDefaultAbsSender(DefaultBotOptions options) {
        super(options);
        Timer mSendTimer = new Timer(true);
        mSendTimer.schedule(new MessageSenderTask(), MANY_CHATS_SEND_INTERVAL, MANY_CHATS_SEND_INTERVAL);
    }

    /**
     * if (method instanceof SendBundleAnswerCallbackQuery) {
     *     super.execute((Method) ((SendBundleAnswerCallbackQuery) method).getMethod());
     *     super.execute(((SendBundleAnswerCallbackQuery) method).getAnswerCallbackQuery());
     * } else {
     *     super.execute((Method) method);
     * }
     * @param method to be sent
     * @param <T>
     * @param <Method>
     */
    protected abstract <T extends Serializable, Method extends BotApiMethod<T>> void syncExecute(Object method);

    private synchronized void syncExecute(long chatId, Object method) {
        try {
            syncExecute(method);
            Chats.update(chatId, System.currentTimeMillis());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Object method) {
        String chatId;
        // Not every messages are limited per minute (for example AnswerCallbackQuery)
        if (method instanceof SendMessage && (chatId = ((SendMessage) method).getChatId()) != null ||
                method instanceof EditMessageText && (chatId = ((EditMessageText) method).getChatId()) != null ||
                method instanceof SendLocation && (chatId = ((SendLocation) method).getChatId()) != null ||
                method instanceof EditMessageReplyMarkup && (chatId = ((EditMessageReplyMarkup) method).getChatId()) != null ||
                method instanceof SendBundleAnswerCallbackQuery && (chatId = ((SendBundleAnswerCallbackQuery) method).getChatId()) != null)
            sendTimed(chatId, method);
        else
            syncExecute(method);
    }

    private void sendTimed(String chatId, Object messageRequest) {
        MessageQueue queue = mMessagesMap.get(chatId);
        if (queue == null) {
            queue = new MessageQueue(Long.parseLong(chatId));
            queue.putMessage(messageRequest);
            mMessagesMap.put(chatId, queue);
        } else {
            queue.putMessage(messageRequest);
            // double check, because the queue can be removed from hashmap on state DELETE
            mMessagesMap.putIfAbsent(chatId, queue);
        }
        mSendRequested.set(true);
    }

    private final class MessageSenderTask extends TimerTask {
        @Override
        public void run() {
            // There're messages which has to be sent
            if (!mSendRequested.getAndSet(false))
                return;

            long currentTime = System.currentTimeMillis();
            mSendQueues.clear();
            boolean processNext = false;

            // 1st step
            // Find all chats in which already allowed to send message
            // (passed more than ONE_CHAT_SEND_INTERVAL ms from previous send)
            Iterator<Map.Entry<String, MessageQueue>> it = mMessagesMap.entrySet().iterator();
            while (it.hasNext()) {
                MessageQueue queue = it.next().getValue();

                // Check
                int state = queue.getCurrentState(currentTime);

                switch (state) {
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
            // Find oldest waiting queue and peek it's message
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
            syncExecute(sendQueue.getChatId(), sendQueue.getMessage(currentTime));
        }
    }

    private static class MessageQueue {
        private static final int EMPTY = 0;     // Queue is empty
        private static final int WAIT = 1;      // Queue has message(s) but not yet allowed to send
        private static final int DELETE = 2;    // None message of given queue was sent longer than CHAT_INACTIVE_INTERVAL, delete for optimisation
        private static final int SEND = 3;      // Queue has message(s) and ready to send
        private final long chatId;
        private final ConcurrentLinkedQueue<Object> mQueue = new ConcurrentLinkedQueue<>();
        private long mLastSendTime;             //Time of last peek from queue
        private volatile long mLastPutTime;     //Time of last put into queue

        private MessageQueue(long chatId) {
            this.chatId = chatId;
        }

        synchronized void putMessage(Object msg) {
            mQueue.add(msg);
            mLastPutTime = System.currentTimeMillis();
        }

        synchronized int getCurrentState(long currentTime) {
            // currentTime is passed as parameter for optimisation
            long interval = currentTime - mLastSendTime;
            boolean empty = mQueue.isEmpty();

            if (!empty && interval > ONE_CHAT_SEND_INTERVAL && Chats.getSent(chatId, currentTime).size() < MAX_MESSAGES_PER_MINUTE)
                return SEND;

            if (interval > CHAT_INACTIVE_INTERVAL)
                return DELETE;

            if (empty)
                return EMPTY;

            return WAIT;
        }

        public synchronized Object getMessage(long currentTime) {
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