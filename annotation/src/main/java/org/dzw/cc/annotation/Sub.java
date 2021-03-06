package org.dzw.cc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sub {
    String key();

    ThreadMode processMode() default ThreadMode.POSTING;

    boolean sticky() default false;

    int priority() default 0;
}
