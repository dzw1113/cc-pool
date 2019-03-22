/**
 * @description:
 * @author: dzw
 * @date: 2019/03/21 19:44
 **/
public class Test {
    public static void main(String[] args) {
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
        System.out.println("是");
    }
}

class Consume implements Runnable{
    public void run() {

    }
}
