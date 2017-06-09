package com.ldzs.recyclerlibrary.callback;

import android.view.View;

import com.ldzs.recyclerlibrary.strategy.GroupingStrategy;

/**
 * Created by Administrator on 2017/5/20.
 */

public interface StickyCallback {
    void initStickyView(View view,int position);

    GroupingStrategy getGroupingStrategy();
}
