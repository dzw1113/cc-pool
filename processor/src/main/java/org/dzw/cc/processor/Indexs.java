package org.dzw.cc.processor;

import java.util.List;

/**
 * @description: 事件索引
 * @author: dzw
 * @date: 2019/03/24 00:50
 **/
public interface Indexs {

    List<SubMethodInfo> getSubMethodInfo(String key);
}
