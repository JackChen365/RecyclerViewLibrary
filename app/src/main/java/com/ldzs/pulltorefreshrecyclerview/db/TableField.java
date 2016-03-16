package com.ldzs.pulltorefreshrecyclerview.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cz on 16/3/5.
 * 标记字段名,防止字段修改,字段值不同
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {
    String value();

    boolean primaryKey() default false;//标记主键
}
