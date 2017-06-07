package com.ldzs.recyclerlibrary.adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.adapter.drag.DynamicAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cz on 16/1/23.
 * 固定刷新尾的数据适配器
 * 永远固定与底部的刷新尾,不允许删除,配合PullToRefreshRecyclerView使用,而HeaderAdapter则可单独使用
 * 不会影响HeaderAdapter自身逻辑
 */
public class RefreshAdapter extends DynamicAdapter {
    private static final String TAG = "RefreshAdapter";
    private final int TYPE_FOOTER = -1;
    protected final List<FooterViewItem> footerViews;
    private View refreshFooterView;
    private int footerViewTotal;//尾的添加总个数

    public RefreshAdapter(RecyclerView.Adapter adapter) {
        super(adapter);
        this.footerViews=new ArrayList<>();
    }

    public void addRefreshFooterView(View view){
        int index = indexOfFooterView(view);
        if(0>index) addFooterView(view, getFooterViewCount());
        refreshFooterView=view;
    }

    public void removeRefreshFooterView(View view){
        int index = indexOfFooterView(view);
        if(-1<index) removeFooterView(index);
        refreshFooterView=null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if(TYPE_FOOTER>=viewType){
            viewHolder=new BaseViewHolder(getItemValue(footerViews,viewType));
        } else {
            viewHolder = super.onCreateViewHolder(parent, viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(!isFooterItem(position)){
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType;
        if(isFooterItem(position)){
            itemViewType = footerViews.get(getFooterViewCount() - (getItemCount() - position)).viewType; //尾
        } else {
            itemViewType = super.getItemViewType(position);
        }
        return itemViewType;
    }

    public int getFooterViewCount() {
        return footerViews.size();
    }

    /**
     * 此方法不开放,避免角标混乱
     *
     * @param view
     * @param index
     */
    protected void addFooterView(View view, int index) {
        int viewType = TYPE_FOOTER - footerViewTotal++;
        this.footerViews.add(index, new FooterViewItem(viewType, view));//越界处理
//        Log.e(TAG,"viewType:"+viewType+" itemCount:"+(super.getItemCount()+index)+" count:"+getItemCount()+" super:"+super.getItemCount());
        notifyItemInserted(super.getItemCount()+index);
    }

    public void addFooterView(View view) {
        int insertIndex;
        if(null==refreshFooterView){
            insertIndex=footerViews.size();
        } else {
            insertIndex=footerViews.size()-1;
        }
        addFooterView(view,insertIndex);
    }


    /**
     * 移除指定的HeaderView对象
     *
     * @param view
     */
    public void removeFooterView(View view) {
        if (null == view||view !=refreshFooterView) return;
        removeFooterView(indexOfValue(footerViews, view));
    }

    private int indexOfValue(List<FooterViewItem> items, View view) {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            FooterViewItem viewItem = items.get(i);
            if (viewItem.view == view) {
                index = i;
                break;
            }
        }
        return index;
    }

    private View getItemValue(List<FooterViewItem> items, int type) {
        View view = null;
        for (int i = 0; i < items.size(); i++) {
            FooterViewItem viewItem = items.get(i);
            if (viewItem.viewType == type) {
                view = viewItem.view;
                break;
            }
        }
        return view;
    }


    public int indexOfFooterView(View view){
        return indexOfValue(footerViews, view);
    }

    /**
     * 移除指定的FooterView对象
     *
     * @param position
     */
    public void removeFooterView(int position) {
        if (-1< position && position<footerViews.size() ){
            footerViews.remove(position);
            notifyItemRemoved(super.getItemCount()+position);
        }
    }


    /**
     * 移除指定位置的header view
     * @param index
     */
    public void removeHeaderView(int index){
        if(index< footerViewTotal){
            removeDynamicView(itemPositions[index]);
        }
    }


    private boolean isFooterItem(int position){
        boolean b = !footerViews.isEmpty() && position >= (getItemCount() - footerViews.size());
//        Log.e(TAG,"isFooterItem:"+b+" position:"+position);
        return b;
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        if(!footerViews.isEmpty()){
            itemCount+=footerViews.size();
        }
        return itemCount;
    }

    public View findRefreshView(@IdRes int id){
        View findView=null;
        for(FooterViewItem item:footerViews){
            if(null!=(findView=item.view.findViewById(id))){
                break;
            }
        }
        return findView;
    }

    @Override
    protected boolean isFullItem(int position) {
        return isFooterItem(position);
    }


    public static class FooterViewItem {
        public final int viewType;
        public final View view;

        public FooterViewItem(int viewType, View view) {
            this.viewType = viewType;
            this.view = view;
        }
    }
}
