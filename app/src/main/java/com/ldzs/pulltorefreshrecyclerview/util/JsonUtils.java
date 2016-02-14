package com.ldzs.pulltorefreshrecyclerview.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldzs.pulltorefreshrecyclerview.model.Channel;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 */
public class JsonUtils {

    private static final Gson gson = new Gson();

    /**
     * 获得list<T>解析集
     *
     * @param result
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> getLists(final String result, final Class<T> clazz) {
        Type type = null;
        if (Channel.class == clazz) {
            type = new TypeToken<ArrayList<Channel>>() {
            }.getType();
        }
        return gson.fromJson(result, type);
    }


}
