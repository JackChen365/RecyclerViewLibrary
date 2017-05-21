package com.ldzs.recyclerlibrary.callback;

/**
 * Created by Administrator on 2017/5/20.
 */

public interface Function<R,T> {
    R call(T t);
}
