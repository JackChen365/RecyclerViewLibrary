package com.ldzs.recyclerlibrary.observe;

import android.support.v7.widget.RecyclerView;

import com.ldzs.recyclerlibrary.adapter.HeaderAdapter;

/**
 * RecyclerView数据变化观察者对象
 */
public class HeaderAdapterDataObserve extends RecyclerView.AdapterDataObserver {
    private HeaderAdapter mWrapAdapter;

    public HeaderAdapterDataObserve(HeaderAdapter mWrapAdapter) {
        this.mWrapAdapter = mWrapAdapter;
    }

    @Override
    public void onChanged() {
        mWrapAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeInserted(mWrapAdapter.getHeadersCount() + positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeChanged(mWrapAdapter.getHeadersCount() + positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        mWrapAdapter.notifyItemRangeChanged(mWrapAdapter.getHeadersCount() + positionStart, itemCount, payload);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeRemoved(mWrapAdapter.getHeadersCount() + positionStart, itemCount);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        mWrapAdapter.notifyItemMoved(mWrapAdapter.getHeadersCount() + fromPosition, toPosition);
    }
}