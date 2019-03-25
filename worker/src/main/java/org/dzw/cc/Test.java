package org.dzw.cc;

import org.dzw.cc.annotation.Sub;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusBuilder;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

/**
 * @description:
 * @author: dzw
 * @date: 2019/03/21 19:44
 **/
public class Test {
    public static void main(String[] args) {
        Test test = new Test();
        test.test(1);
        test.test1(2);
//        AutoServiceProcessor asp = new AutoServiceProcessor();
//        ExecutorService es = Executors.newFixedThreadPool(5);
//        for (int i = 0; i < 5; i++) {
//            es.execute(new Runnable() {
//                public void run() {
//                    MessageCenter mc = MessageCenter.instance();
//                    mc.sendMessage();
//                    System.out.println(mc.queue);
////                    mc.receiveMessage();
//                }
//            });
//        }
        System.out.println(msg);
        System.out.println("是");

        String event = "Hello";

        EventBusBuilder builder = EventBus.builder();
        builder.ignoreGeneratedIndex(false);
        EventBus eventBus = builder.installDefaultEventBus();
//        System.out.println(EventBus.getDefault().equals(eventBus));

        EventBus.builder()
                .addIndex(new EventBusTestsIndex())
                .build();


        StringEventSubscriber stringEventSubscriber = new StringEventSubscriber();
//        eventBus.register(stringEventSubscriber);
        long start = System.currentTimeMillis();
        long time = System.currentTimeMillis() - start;
        eventBus.post(event);
    }


    public static class StringEventSubscriber {
        public String lastStringEvent;

        @Subscribe
        public void onEvent(String event) {
            lastStringEvent = event;
            System.out.println("我收到消息了");
        }
    }


    public static String msg = "Hello world!";

    @Sub(key = "11")
    public void test(Object obj) {
        System.out.println("2=====================");
    }

    @Sub(key = "22")
    public void test1(Object obj) {
        System.out.println("1=====================");
    }
}

class Consume implements Runnable {
    public void run() {

    }
}
