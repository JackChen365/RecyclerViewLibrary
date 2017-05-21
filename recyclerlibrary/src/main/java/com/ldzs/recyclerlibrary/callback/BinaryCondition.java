package com.ldzs.recyclerlibrary.callback;

/**
 * Created by Administrator on 2017/5/20.
 */

public interface BinaryCondition<T> {
    boolean apply(T t1,T t2);
}
