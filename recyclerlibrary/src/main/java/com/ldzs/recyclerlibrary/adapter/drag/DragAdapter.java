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
    private BaseViewAdapter adapter;

    /**
     * @param adapter 包装数据适配器
     */
    public DragAdapter(BaseViewAdapter adapter) {
        super(adapter);
        this.adapter = adapter;
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
            adapter.swapItem(oldIndex - getStartIndex(oldIndex), newIndex - getStartIndex(newIndex));
        }
    }

    private void itemsDy(int oldIndex, int newIndex) {
        int position = findPosition(newIndex);
        int newViewType = fullItemTypes.get(newIndex);
        int index = fullItemTypes.indexOfKey(newIndex);
        fullItemTypes.removeAt(index);
        fullItemTypes.put(oldIndex, newViewType);
        itemPositions[position] = oldIndex;//重置角标位置
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
        int newViewType = fullItemTypes.get(oldPosition);
        int index = fullItemTypes.indexOfKey(oldPosition);
        fullItemTypes.removeAt(index);
        fullItemTypes.put(newPosition, newViewType);
        itemPositions[position1] = newPosition;//重置角标位置
    }

    /**
     * 动态条目置换动态条目
     *
     * @param oldPosition
     * @param newPosition
     */
    private void dysDy(int oldPosition, int newPosition) {
        int oldViewType = fullItemTypes.get(oldPosition);
        int newViewType = fullItemTypes.get(newPosition);
        fullItemTypes.put(oldPosition, newViewType);
        fullItemTypes.put(newPosition, oldViewType);
        //替换view
        View oldView = fullViews.get(oldViewType);
        View newView = fullViews.get(newViewType);
        fullViews.put(oldViewType, newView);
        fullViews.put(newViewType, oldView);
    }

}