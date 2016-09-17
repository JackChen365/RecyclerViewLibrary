package com.ldzs.recyclerlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract adapter which can be extended for RecyclerView
 */
public abstract class BaseViewAdapter<H extends BaseViewHolder, E> extends RecyclerView.Adapter<H> {

    private static final String TAG = "BaseViewAdapter";
    protected final ArrayList<E> items;
    protected final LayoutInflater inflater;

    public BaseViewAdapter(Context context, List<E> items) {
        this.inflater = LayoutInflater.from(context);
        this.items = new ArrayList<>();
        if (null != items && !items.isEmpty()) {
            this.items.addAll(items);
        }
    }

    /**
     * 创建view对象
     *
     * @param parent
     * @param layout
     * @return
     */
    protected View inflateView(ViewGroup parent, int layout) {
        return inflater.inflate(layout, parent, false);
    }

    @Override
    public abstract H onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(H holder, int position);

    @Override
    public int getItemCount() {
        return items.size();
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
            this.items.add(index, e);
            notifyItemInserted(index);
        }
    }

    public int indexOfItem(E e) {
        int index = -1;
        if (null != e) {
            index = this.items.indexOf(e);
        }
        return index;
    }

    public boolean contains(E e){
        return -1!=indexOfItem(e);
    }

    public void set(int index, E e) {
        if (index < getItemCount()) {
            items.set(index, e);
            notifyItemChanged(index);
        }
    }

    public void addItem(E e) {
        if (null != e) {
            this.items.add(e);
            int insertPosition = getItemCount() - 1;
            notifyItemInserted(insertPosition);
        }
    }

    public void addItems(List<E> items, int index) {
        if (null != items && !items.isEmpty()) {
            int size = items.size();
            this.items.addAll(index, items);
            notifyItemRangeInserted(index, size);
        }
    }

    public void addItems(List<E> items) {
        if (null != items && !items.isEmpty()) {
            int size = items.size();
            int itemCount = getItemCount();
            this.items.addAll(items);
            notifyItemRangeInserted(itemCount, size);
        }
    }

    public void remove(int index) {
        if(!items.isEmpty()&&index<items.size()){
            items.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void remove(int start,int count){
        int index=0;
        int minCount=Math.min(items.size(),count);
        while(index++<minCount){
            items.remove(start);
        }
    }

    public void removeNotifyItem(int start,int count){
        int index=0;
        int minCount=Math.min(items.size(),count);
        while(index++<minCount){
            items.remove(start);
        }
        notifyItemRangeRemoved(start,minCount);
    }

    public void remove(E e) {
        if (null != e) {
            remove(items.indexOf(e));
        }
    }


    /**
     * 更新条目
     *
     * @param e
     */
    public void updateItem(E e) {
        if (null != e) {
            int index = items.indexOf(e);
            if (-1 != index) {
                items.set(index, e);
                notifyItemChanged(index);
            }
        }
    }

    public void swapItems(final ArrayList<E> items) {
        if (null != items && !items.isEmpty()) {
            this.items.clear();
            this.items.addAll(items);
            notifyItemRangeChanged(0, items.size());
        }
    }

    /**
     * 获得所有条目
     *
     * @return
     */
    public ArrayList<E> getItems() {
        return items;
    }


    public E getItem(int position) {
        E e = null;
        if (0 <= position && position < getItemsCount()) {
            e = this.items.get(position);
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
        Collections.swap(items, oldPosition, newPosition);
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
        this.items.clear();
        notifyItemRangeRemoved(0, getItemsCount());
    }

    public boolean isEmpty() {
        return 0 == getItemsCount();
    }


    public int getItemsCount() {
        return items.size();
    }

}
