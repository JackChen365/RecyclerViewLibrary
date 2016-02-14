package com.ldzs.recyclerlibrary.header;

import android.view.View;

/**
 * Created by cz on 16/1/20.
 * 底部刷新控件
 */
public interface BaseRefreshFooter extends BaseRefresh {
    /**
     * @param listener
     */
    void setOnRetryListener(View.OnClickListener listener);
}
