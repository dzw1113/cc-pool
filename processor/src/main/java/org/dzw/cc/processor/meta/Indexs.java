package org.dzw.cc.processor.meta;

import java.util.List;

/**
 * @description: 事件索引
 * @author: dzw
 * @date: 2019/03/24 00:50
 **/
public interface Indexs {

    List<SubscriberMethodInfo> getSubMethodInfo(String key);

    SubscriberInfo getSubMethodInfo(Class<?> subscriberClass);
}
