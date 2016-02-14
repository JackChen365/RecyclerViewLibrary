package com.ldzs.recyclerlibrary.adapter.drag;

import android.content.Context;
import android.view.View;

import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;

/**
 * 可替换元素位置的动态添加数据适配器对象
 *
 * @param
 */
public class DragAdapter extends DynamicAdapter {
    private BaseViewAdapter mAdapter;

    /**
     * @param adapter 包装数据适配器
     */
    public DragAdapter(Context context, BaseViewAdapter adapter) {
        super(context, adapter);
        mAdapter = adapter;
    }


    /**
     * 互换元素
     *
     * @param oldPosition
     * @param newPosition
     */
    public void swap(int oldPosition, int newPosition) {
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; swapItem(i, i + 1), i++) ;
        } else {
            for (int i = oldPosition; i > newPosition; swapItem(i, i - 1), i--) ;
        }
    }

    /**
     * 转换条目
     *
     * @param oldIndex
     * @param newIndex
     */
    private void swapItem(int oldIndex, int newIndex) {
        int position1 = findPosition(oldIndex);
        int position2 = findPosition(newIndex);
        boolean startDynamic = -1 != position1;
        boolean endDynamic = -1 != position2;
        //四种置换方式
        if (startDynamic && endDynamic) {
            dysDy(oldIndex, newIndex);
        } else if (startDynamic) {
            dysItem(oldIndex, newIndex, position1);
        } else if (endDynamic) {
            itemsDy(oldIndex, newIndex);
        } else {
            mAdapter.swapItem(oldIndex - getStartIndex(oldIndex), newIndex - getStartIndex(newIndex));
        }
    }

    private void itemsDy(int oldIndex, int newIndex) {
        int position = findPosition(newIndex);
        int newViewType = mFullItemTypes.get(newIndex);
        int index = mFullItemTypes.indexOfKey(newIndex);
        mFullItemTypes.removeAt(index);
        mFullItemTypes.put(oldIndex, newViewType);
        mItemPositions[position] = oldIndex;//重置角标位置
    }

    /**
     * 动态条目置换普通条目
     *
     * @param oldPosition
     * @param newPosition
     * @param position1
     */
    private void dysItem(int oldPosition, int newPosition, int position1) {
        //直接更换插入对象到指定位置,装饰对象不用改动
        int newViewType = mFullItemTypes.get(oldPosition);
        int index = mFullItemTypes.indexOfKey(oldPosition);
        mFullItemTypes.removeAt(index);
        mFullItemTypes.put(newPosition, newViewType);
        mItemPositions[position1] = newPosition;//重置角标位置
    }

    /**
     * 动态条目置换动态条目
     *
     * @param oldPosition
     * @param newPosition
     */
    private void dysDy(int oldPosition, int newPosition) {
        int oldViewType = mFullItemTypes.get(oldPosition);
        int newViewType = mFullItemTypes.get(newPosition);
        mFullItemTypes.put(oldPosition, newViewType);
        mFullItemTypes.put(newPosition, oldViewType);
        //替换view
        View oldView = mFullViews.get(oldViewType);
        View newView = mFullViews.get(newViewType);
        mFullViews.put(oldViewType, newView);
        mFullViews.put(newViewType, oldView);
    }

}