package com.ldzs.recyclerlibrary.observe;

import android.support.v7.widget.RecyclerView;

import com.ldzs.recyclerlibrary.adapter.drag.DragAdapter;

/**
 * RecyclerView数据变化观察者对象
 * 动态插入数据适配器对象观察者
 */
public class DynamicAdapterDataObserve extends RecyclerView.AdapterDataObserver {
    private DragAdapter mWrapAdapter;

    public DynamicAdapterDataObserve(DragAdapter mWrapAdapter) {
        this.mWrapAdapter = mWrapAdapter;
    }

    @Override
    public void onChanged() {
        mWrapAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeInserted(mWrapAdapter.getStartIndex(positionStart) + positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeChanged(mWrapAdapter.getStartIndex(positionStart) + positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        mWrapAdapter.notifyItemRangeChanged(mWrapAdapter.getStartIndex(positionStart) + positionStart, itemCount, payload);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeRemoved(mWrapAdapter.getStartIndex(positionStart) + positionStart, itemCount);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        mWrapAdapter.notifyItemMoved(mWrapAdapter.getStartIndex(fromPosition) + fromPosition, toPosition);
    }
}