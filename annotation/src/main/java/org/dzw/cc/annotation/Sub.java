package org.dzw.cc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 消费者
 * @author: dzw
 * @date: 2019/03/21 20:41
 **/
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface Sub {
}
