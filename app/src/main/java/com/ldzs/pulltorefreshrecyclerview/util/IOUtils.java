package com.ldzs.pulltorefreshrecyclerview.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by cz on 16/1/27.
 */
public class IOUtils {
    /**
     * 关闭IO流对象
     *
     * @param streams
     */
    public static void closeStream(Closeable... streams) {
        if (null != streams) {
            try {
                for (int i = 0; i < streams.length; i++) {
                    if (null != streams[i]) {
                        streams[i].close();
                    }
                }
            } catch (IOException e) {
            }
        }
    }
}
