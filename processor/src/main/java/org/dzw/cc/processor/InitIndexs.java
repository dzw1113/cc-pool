package org.dzw.cc.processor;

import org.greenrobot.eventbus.EventBus;

import org.dzw.cc.processor.SubMethodInfoIndexs1;

/**
 * @description: 初始化索引
 * @author: dzw
 * @date: 2019/03/25 22:37
 **/
public class InitIndexs {
    static EventBus build() {
        return EventBus.builder()
                .addIndex(new SubMethodInfoIndexs1())
                .build();
    }
}
