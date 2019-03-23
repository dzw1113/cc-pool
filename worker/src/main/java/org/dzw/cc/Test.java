package org.dzw.cc;

import org.dzw.cc.annotation.Sub;

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
