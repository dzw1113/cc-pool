package org.dzw.cc.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @description: 消息中心
 * @author: dzw
 * @date: 2019/03/21 20:42
 **/
public class MessageWorker {


    public static final LinkedBlockingQueue queue = new LinkedBlockingQueue();

    private static final ExecutorService es = Executors.newCachedThreadPool();

    private final ThreadLocal<PubThreadState> currentSendThreadState = new ThreadLocal<PubThreadState>() {
        @Override
        protected PubThreadState initialValue() {
            return new PubThreadState();
        }
    };

    private static volatile MessageWorker instance;

    private MessageWorker() {
        getInstance();
    }

    public static MessageWorker getInstance() {
        MessageWorker messageWorker = instance;
        if (messageWorker == null) {
            synchronized (MessageWorker.class) {
                messageWorker = MessageWorker.instance;
                if (messageWorker == null) {
                    messageWorker = MessageWorker.instance = new MessageWorker();
                }
            }
        }
        return messageWorker;
    }


    public void send(Object message, Class<?> T) {
        try {
            PubThreadState postingState = currentSendThreadState.get();
            List<Object> queue = postingState.queue;
            queue.add(message);

            if (!postingState.isSending) {
//                postingState.isMainThread = isMainThread();
                postingState.isSending = true;
                if (postingState.canceled) {
                    throw new RuntimeException("Internal error. Abort state was not reset");
                }
                try {
                    while (!queue.isEmpty()) {
                        //todo
//                        postSingleEvent(queue.remove(0), postingState);
                    }
                } finally {
                    postingState.isSending = false;
                    postingState.isMainThread = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Object message) {
        send(message, message.getClass());
    }

    public void send(String key, Object message) {
    }

    public static void main(String[] args) {
        //add方法在添加元素的时候，若超出了度列的长度会直接抛出异常
        //put方法，若向队尾添加元素的时候发现队列已经满了会发生阻塞一直等待空间，以加入元素。
        // offer方法在添加元素时，如果发现队列已满无法添加的话，会直接返回false。
        //===============================================================================
        // poll: 若队列为空，返回null。
        //remove:若队列为空，抛出NoSuchElementException异常。
        //take:若队列为空，发生阻塞，等待有元素。

        String message = "消息";
        try {

            MessageWorker.getInstance().send("11");
//            System.out.println(linkedBlockingQueue.poll());
//            linkedBlockingQueue.put(1);
//            System.out.println(linkedBlockingQueue.poll());
//            linkedBlockingQueue.put(new User("张三",19));
//            System.out.println(linkedBlockingQueue.poll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final static class PubThreadState {
        final List<Object> queue = new ArrayList<Object>();
        boolean isSending;
        boolean isMainThread;
        Object message;
        boolean canceled;
    }

    public void cancel(Object message) {
        PubThreadState postingState = currentSendThreadState.get();
        if (!postingState.isSending) {
            throw new RuntimeException(
                    "This method may only be called from inside event handling methods on the posting thread");
        } else if (message == null) {
            throw new RuntimeException("Event may not be null");
        } else if (postingState.message != message) {
            throw new RuntimeException("Only the currently handled event may be aborted");
        }
        //todo
//        else if (postingState.subscription.subscriberMethod.threadMode != ThreadMode.POSTING) {
//            throw new RuntimeException(" event handlers may only abort the incoming event");
//        }

        postingState.canceled = true;
    }
}
