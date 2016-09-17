package com.ldzs.recyclerlibrary.observe;

import android.support.v7.widget.RecyclerView;

import com.ldzs.recyclerlibrary.adapter.drag.DragAdapter;
import com.ldzs.recyclerlibrary.adapter.drag.DynamicAdapter;

/**
 * RecyclerView数据变化观察者对象
 * 动态插入数据适配器对象观察者
 */
public class DynamicAdapterDataObserve extends RecyclerView.AdapterDataObserver {
    private DynamicAdapter dynamicAdapter;

    public DynamicAdapterDataObserve(DynamicAdapter dynamicAdapter) {
        this.dynamicAdapter = dynamicAdapter;
    }

    @Override
    public void onChanged() {
        dynamicAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        int startIndex = dynamicAdapter.getStartIndex(positionStart);
        dynamicAdapter.itemRangeInsert(positionStart,itemCount);
        dynamicAdapter.notifyItemRangeInserted(startIndex + positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        dynamicAdapter.notifyItemRangeChanged(dynamicAdapter.getStartIndex(positionStart) + positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        dynamicAdapter.notifyItemRangeChanged(dynamicAdapter.getStartIndex(positionStart) + positionStart, itemCount, payload);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        dynamicAdapter.itemRangeRemoved(positionStart,itemCount);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        dynamicAdapter.notifyItemMoved(dynamicAdapter.getStartIndex(fromPosition) + fromPosition, toPosition);
    }
}