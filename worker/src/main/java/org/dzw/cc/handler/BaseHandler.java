package org.dzw.cc.handler;

/**
 * @description: 基础消息处理
 * @author: dzw
 * @date: 2019/03/22 01:13
 **/
public class BaseHandler {

    //抽象工厂
    public void onEvent(String event) {
        System.out.println(event);
        System.out.println("我拿到消息了!");
    }
}
