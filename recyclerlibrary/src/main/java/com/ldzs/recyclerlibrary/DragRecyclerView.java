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
    private MyItemTouchHelperCallback helperCallback;
    private DragAdapter adapter;

    public DragRecyclerView(Context context) {
        this(context, null, 0);
    }

    public DragRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        helperCallback = new MyItemTouchHelperCallback(this);
        helperCallback.setLongPressDrawEnable(true);
    }

    /**
     * 获得真实的位置
     *
     * @param position
     * @return
     */
    public int getItemPosition(int position) {
        return position - adapter.getStartIndex(position);
    }

    /**
     * 只有继承BaseViewAdapter才会
     *
     * @param adapter
     */
    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof BaseViewAdapter) {
            super.setAdapter(this.adapter = new DragAdapter(getContext(), (BaseViewAdapter) adapter));
            helperCallback.setAdapter(this.adapter);
            adapter.registerAdapterDataObserver(new DynamicAdapterDataObserve(this.adapter));
            new ItemTouchHelper(helperCallback).attachToRecyclerView(this);
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
        helperCallback.setDragItemEnableListener(listener);
    }

    /**
     * 设置长按是否拖动
     *
     * @param enable
     */
    public void setLongPressDrawEnable(boolean enable) {
        helperCallback.setLongPressDrawEnable(enable);
    }

    /**
     * 设置条目移动.
     *
     * @param oldPosition
     * @param newPosition
     */
    public void setItemMove(int oldPosition, final int newPosition) {
        if (null != adapter) {
            adapter.swap(oldPosition, newPosition);
            adapter.notifyItemMoved(oldPosition, newPosition);
            //动态结束后,刷新条目
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemChanged(newPosition);
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
        if (null != adapter) {
            adapter.addFullItem(LayoutInflater.from(getContext()).inflate(layout, this, false), position);
        }
    }

    /**
     * 动态添加view
     *
     * @param view
     */
    public void addDynamicView(View view, int position) {
        if (null != adapter) {
            adapter.addFullItem(view, position);
        }
    }

    /**
     * 设置条目点击
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        if (null != adapter) {
            adapter.setOnItemClickListener(listener);
//            adapter.setOnItemClickListener(new OnDelayItemClickListener(listener, getItemAnimator().getMoveDuration()));
        }
    }


}