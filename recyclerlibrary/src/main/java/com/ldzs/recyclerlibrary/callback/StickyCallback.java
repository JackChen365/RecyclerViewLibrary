package com.ldzs.recyclerlibrary.callback;

import android.view.View;

/**
 * Created by Administrator on 2017/5/20.
 */

public interface StickyCallback {
    void initStickyView(View view,int position);

    boolean isStickyPosition(int position);
}
