package org.dzw.cc;

import org.dzw.cc.annotation.Sub;

/**
 * @description:
 * @author: dzw
 * @date: 2019/03/21 19:44
 **/
public class Test {
    public static void main(String[] args) {
        Test test = new Test();
        test.test();
        test.test1();
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

    @Sub(value = 5, what = "This is a test")
    public static String msg = "Hello world!";

    public void test() {
        System.out.println("2=====================");
    }

    public void test1() {
        System.out.println("1=====================");
    }
}

class Consume implements Runnable {
    public void run() {

    }
}
