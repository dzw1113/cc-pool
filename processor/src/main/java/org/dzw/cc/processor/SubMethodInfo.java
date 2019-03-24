package org.dzw.cc.processor;

import org.dzw.cc.annotation.ProcessMode;

/**
 * @description: 消费者方法信息
 * @author: dzw
 * @date: 2019/03/24 00:25
 **/
public class SubMethodInfo {

    final String methodName;
    final ProcessMode processMode;
    final int priority;
    final boolean sticky;

    public SubMethodInfo(String methodName, ProcessMode processMode,
                         int priority, boolean sticky) {
        this.methodName = methodName;
        this.processMode = processMode;
        this.priority = priority;
        this.sticky = sticky;
    }

    public SubMethodInfo(String methodName) {
        this(methodName, ProcessMode.POSTING, 0, false);
    }

    public SubMethodInfo(String methodName, Class<?> eventType, ProcessMode processMode) {
        this(methodName, processMode, 0, false);
    }
}
