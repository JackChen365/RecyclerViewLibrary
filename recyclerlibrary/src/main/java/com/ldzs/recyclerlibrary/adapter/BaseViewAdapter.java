package com.ldzs.recyclerlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract mAdapter which can be extended for Recyclerview
 */
public abstract class BaseViewAdapter<H extends BaseViewHolder, E> extends RecyclerView.Adapter<H> {

    private static final String TAG = "BaseViewAdapter";
    protected final ArrayList<E> mItems;
    protected final LayoutInflater mInflater;

    public BaseViewAdapter(Context context, List<E> items) {
        this.mInflater = LayoutInflater.from(context);
        this.mItems = new ArrayList<>();
        if (null != items && !items.isEmpty()) {
            this.mItems.addAll(items);
        }
    }

    /**
     * 创建view对象
     *
     * @param parent
     * @param layout
     * @return
     */
    protected View createView(ViewGroup parent, int layout) {
        return mInflater.inflate(layout, parent, false);
    }

    @Override
    public abstract H onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(H holder, int position);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Clear the list of the mAdapter
     *
     * @param list
     */
    public void clear(List<?> list) {
        int size = list.size();
        list.clear();
        notifyItemRangeRemoved(0, size);
    }


    public void addItem(E e, int index) {
        if (null != e) {
            this.mItems.add(index, e);
            notifyItemInserted(index);
        }
    }

    public void addItem(E e) {
        if (null != e) {
            this.mItems.add(e);
            int insertPosition = getItemCount() - 1;
            notifyItemInserted(insertPosition);
        }
    }

    public void addItems(List<E> items, int index) {
        if (null != items && !items.isEmpty()) {
            int size = items.size();
            this.mItems.addAll(index, items);
            notifyItemRangeInserted(index, size);
        }
    }

    public void addItems(List<E> items) {
        if (null != items && !items.isEmpty()) {
            int size = items.size();
            int itemCount = getItemCount();
            this.mItems.addAll(items);
            notifyItemRangeInserted(itemCount, size);
        }
    }

    public void remove(int index) {
        mItems.remove(index);
        notifyItemRemoved(index);
    }

    public void remove(E e) {
        if (null != e) {
            remove(mItems.indexOf(e));
        }
    }


    /**
     * 更新条目
     *
     * @param e
     */
    public void updateItem(E e) {
        if (null != e) {
            int index = mItems.indexOf(e);
            if (-1 != index) {
                mItems.set(index, e);
                notifyItemChanged(index);
            }
        }
    }

    public void swapItems(final ArrayList<E> items) {
        if (null != items && !items.isEmpty()) {
            mItems.clear();
            mItems.addAll(items);
            notifyItemRangeChanged(0, items.size());
        }
    }

    /**
     * 获得所有条目
     *
     * @return
     */
    public ArrayList<E> getItems() {
        return mItems;
    }


    public E getItem(int position) {
        E e = null;
        if (0 <= position && position < getItemsCount()) {
            e = this.mItems.get(position);
        }
        return e;
    }

    public E getLastItem() {
        return getItem(getItemsCount() - 1);
    }

    /**
     * 互换元素
     */
    public void swapItem(int oldPosition, int newPosition) {
        Collections.swap(mItems, oldPosition, newPosition);
    }

    /**
     * 互换元素,互通知刷新
     */
    public void swap(int oldPosition, int newPosition) {
        swapItem(oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
    }

    /**
     * 移除所有条目
     */
    public void clear() {
        this.mItems.clear();
        notifyItemRangeRemoved(0, getItemsCount());
    }

    public boolean isEmpty() {
        return 0 == getItemsCount();
    }


    public int getItemsCount() {
        return mItems.size();
    }

}
