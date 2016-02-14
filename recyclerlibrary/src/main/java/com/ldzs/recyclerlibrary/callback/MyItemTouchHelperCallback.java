package com.ldzs.recyclerlibrary.callback;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.ldzs.recyclerlibrary.adapter.drag.DragAdapter;

/**
 * Created by Alessandro on 12/01/2016.
 * 动态添加头尾Adapter的数据刷新通知对象
 */
public class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private static final String TAG = "MyItemTouchHelperCallback";
    private CallbackItemTouch mCallbackItemTouch;
    private OnDragItemEnableListener mDragListener;
    private boolean mLongPressDragEnable;//长按是否启用拖动
    private boolean mDynamicViewDragEnable;//动态添加view是否启用拖动
    private DragAdapter mAdapter;

    public MyItemTouchHelperCallback(CallbackItemTouch callbackItemTouch) {
        this.mCallbackItemTouch = callbackItemTouch;
    }

    /**
     * 设置长按是否拖动
     *
     * @param enable
     */
    public void setLongPressDrawEnable(boolean enable) {
        this.mLongPressDragEnable = enable;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return mLongPressDragEnable;
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        int flag = makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        if (null != mAdapter) {
            int index = mAdapter.findPosition(position);
            //动态添加的并启用的,可以拖动.或者自身条目本身启用可以拖动的.
            if (RecyclerView.NO_POSITION != index && mDynamicViewDragEnable ||
                    null != mDragListener && !mDragListener.itemEnable(position - mAdapter.getStartIndex(position))) {
                flag = makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.DOWN);
            }
        }
        return flag;
    }

    /**
     * 设置拖动条目启用监听
     *
     * @param listener
     */
    public void setDragItemEnableListener(OnDragItemEnableListener listener) {
        this.mDragListener = listener;
    }

    /**
     * 设置动态添加view是否启用拖动
     *
     * @param enable
     */
    public void setDynamicViewDragEnable(boolean enable) {
        this.mDynamicViewDragEnable = enable;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int position = viewHolder.getAdapterPosition();
        int targetPosition = target.getAdapterPosition();
        boolean itemEnable = false;
        if (null != mAdapter) {
            int index = mAdapter.findPosition(targetPosition);
            if (RecyclerView.NO_POSITION != index) {
                itemEnable = mDynamicViewDragEnable;
            } else if (null != mDragListener && mDragListener.itemEnable(targetPosition - mAdapter.getStartIndex(position))) {
                itemEnable = true;
            }
        } else {
            itemEnable = null != mDragListener && mDragListener.itemEnable(targetPosition);
        }
        if (itemEnable) {
            mCallbackItemTouch.onItemMove(position, targetPosition);
        }
        return itemEnable;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }


    public void setAdapter(DragAdapter adapter) {
        this.mAdapter = adapter;
    }
}
