package com.ldzs.recyclerlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by cz on 16/1/23.
 * 固定刷新尾的数据适配器
 * 永远固定与底部的刷新尾,不允许删除,配合PullToRefreshRecyclerView使用,而HeaderAdapter则可单独使用
 * 不会影响HeaderAdapter自身逻辑
 *
 *
 */
public class RefreshAdapter extends HeaderAdapter {
    public RefreshAdapter(RecyclerView.Adapter adapter) {
        super(adapter);
    }

    public void addRefreshFooterView(View view, int index){
        super.addFooterView(view,index);
    }

    public void removeRefreshFooterView(View view){
        super.removeFooterView(view);
    }


    @Override
    public void addFooterView(View view, int index) {
        //如果添加的是末尾,则-1,否则直接添加
        int footersCount = getFootersCount();
        if (index == footersCount) {
            super.addFooterView(view, 0 == index ? 0 : index - 1);
        } else {
            super.addFooterView(view);
        }
    }

}
