package com.ldzs.pulltorefreshrecyclerview.xml;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.ldzs.pulltorefreshrecyclerview.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public abstract class AssetReader<K, T> {

    private static final String TAG = "AssetReader";

    /**
     * 初始化资源
     */
    public HashMap<K, T> read(Context context) {
        InputStream inputStream = null;
        HashMap<K, T> configs = null;
        try {
            Log.e(TAG, "thread:" + Thread.currentThread().getName());
            Resources appResource = context.getResources();
            Config config = getClass().getAnnotation(Config.class);
            if (null != config && 1 == config.value().length) {
                configs = new HashMap<>();
                inputStream = appResource.getAssets().open(config.value()[0]);
                if (null != inputStream) {
                    XmlParser.OnParserListener listener = getParserListener(configs);
                    if (null != listener) {
                        XmlParser.startParser(inputStream, listener);
                    } else {
                        throw new NullPointerException("listener 不能为空!");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(inputStream);
        }
        return configs;
    }

    /**
     * 读取配置项
     *
     * @return
     */
    public abstract XmlParser.OnParserListener getParserListener(HashMap<K, T> configs);

}
