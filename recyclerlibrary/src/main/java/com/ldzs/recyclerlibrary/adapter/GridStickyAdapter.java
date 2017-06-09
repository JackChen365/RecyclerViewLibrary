package com.ldzs.recyclerlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ldzs.recyclerlibrary.callback.GridSpanCallback;
import com.ldzs.recyclerlibrary.callback.StickyCallback;
import com.ldzs.recyclerlibrary.strategy.GroupingStrategy;

import java.util.List;

/**
 * Created by Administrator on 2017/5/20.
 */
public abstract class GridStickyAdapter<T> extends BaseViewAdapter<T> implements StickyCallback,GridSpanCallback {

    public GridStickyAdapter(Context context, List<T> items) {
        super(context, items);
    }

    @Override
    public int getSpanSize(RecyclerView.LayoutManager layoutManager,int position) {
        int spanCount=1;
        GroupingStrategy groupingStrategy = getGroupingStrategy();
        if(layoutManager instanceof GridLayoutManager&&groupingStrategy.isGroupIndex(position)){
            GridLayoutManager gridLayoutManager= (GridLayoutManager) layoutManager;
            spanCount = gridLayoutManager.getSpanCount();
        }
        return spanCount;
    }
}
