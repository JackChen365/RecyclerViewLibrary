package com.ldzs.recyclerlibrary.callback;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by czz on 2016/9/15.
 */
public interface Selectable {
    void onSelectItem(RecyclerView.ViewHolder holder, int position, boolean select);
}
