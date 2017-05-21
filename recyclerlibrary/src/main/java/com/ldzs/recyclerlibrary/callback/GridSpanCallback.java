package com.ldzs.recyclerlibrary.callback;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2017/5/21.
 */

public interface GridSpanCallback {
    int getSpanSize(RecyclerView.LayoutManager layoutManager,int position);
}
