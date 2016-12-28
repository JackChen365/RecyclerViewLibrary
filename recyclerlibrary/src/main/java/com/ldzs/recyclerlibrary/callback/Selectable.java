package com.ldzs.recyclerlibrary.callback;

import android.support.v7.widget.RecyclerView;

/**
 * Created by czz on 2016/9/15.
 */
public interface Selectable<VH extends RecyclerView.ViewHolder> {
    void onSelectItem(VH holder, int position, boolean select);
}
