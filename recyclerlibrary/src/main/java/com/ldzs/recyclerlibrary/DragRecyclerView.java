package com.ldzs.recyclerlibrary;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;
import com.ldzs.recyclerlibrary.adapter.drag.DragAdapter;
import com.ldzs.recyclerlibrary.callback.CallbackItemTouch;
import com.ldzs.recyclerlibrary.callback.MyItemTouchHelperCallback;
import com.ldzs.recyclerlibrary.callback.OnDragItemEnableListener;
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;
import com.ldzs.recyclerlibrary.observe.DynamicAdapterDataObserve;

/**
 * 可拖动排序的gridView
 *
 * @date 2015/8/23
 * <p>
 */
public class DragRecyclerView extends RecyclerView implements CallbackItemTouch {
    private MyItemTouchHelperCallback mHelperCallback;
    private DragAdapter mAdapter;

    public DragRecyclerView(Context context) {
        this(context, null, 0);
    }

    public DragRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHelperCallback = new MyItemTouchHelperCallback(this);
        mHelperCallback.setLongPressDrawEnable(true);
    }

    /**
     * 获得真实的位置
     *
     * @param position
     * @return
     */
    public int getItemPosition(int position) {
        return position - mAdapter.getStartIndex(position);
    }

    /**
     * 只有继承BaseViewAdapter才会
     *
     * @param adapter
     */
    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof BaseViewAdapter) {
            super.setAdapter(mAdapter = new DragAdapter(getContext(), (BaseViewAdapter) adapter));
            mHelperCallback.setAdapter(mAdapter);
            adapter.registerAdapterDataObserver(new DynamicAdapterDataObserve(mAdapter));
            new ItemTouchHelper(mHelperCallback).attachToRecyclerView(this);
        } else {
            throw new IllegalArgumentException("adapter must be extends BaseViewAdapter!");
        }
    }

    /**
     * 设置拖动条目启用监听
     *
     * @param listener
     */
    public void setOnDragItemEnableListener(OnDragItemEnableListener listener) {
        mHelperCallback.setDragItemEnableListener(listener);
    }

    /**
     * 设置长按是否拖动
     *
     * @param enable
     */
    public void setLongPressDrawEnable(boolean enable) {
        mHelperCallback.setLongPressDrawEnable(enable);
    }

    /**
     * 设置条目移动.
     *
     * @param oldPosition
     * @param newPosition
     */
    public void setItemMove(int oldPosition, final int newPosition) {
        if (null != mAdapter) {
            mAdapter.swap(oldPosition, newPosition);
            mAdapter.notifyItemMoved(oldPosition, newPosition);
            //动态结束后,刷新条目
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemChanged(newPosition);
                }
            }, getItemAnimator().getMoveDuration());
        }
    }

    @Override
    public void onItemMove(int oldPosition, int newPosition) {
        setItemMove(oldPosition, newPosition);
    }

    /**
     * 动态添加view
     *
     * @param layout
     */
    public void addDynamicView(@LayoutRes int layout, int position) {
        if (null != mAdapter) {
            mAdapter.addFullItem(LayoutInflater.from(getContext()).inflate(layout, this, false), position);
        }
    }

    /**
     * 动态添加view
     *
     * @param view
     */
    public void addDynamicView(View view, int position) {
        if (null != mAdapter) {
            mAdapter.addFullItem(view, position);
        }
    }

    /**
     * 设置条目点击
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        if (null != mAdapter) {
            mAdapter.setOnItemClickListener(listener);
//            mAdapter.setOnItemClickListener(new OnDelayItemClickListener(listener, getItemAnimator().getMoveDuration()));
        }
    }


}