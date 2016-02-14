package com.ldzs.recyclerlibrary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by cz on 16/1/23.
 * 固定刷新头和尾的数据适配器
 * 永远固定第一个刷新头,与底部的刷新尾,不允许删除,配合PullToRefreshRecyclerView使用,而HeaderAdapter则可单独使用
 * 不会影响HeaderAdapter自身逻辑
 */
public class RefreshHeaderAdapter extends HeaderAdapter {

    public RefreshHeaderAdapter(RecyclerView.Adapter adapter) {
        super(adapter);
    }


    @Override
    public void removeHeaderView(int position) {
        int headersCount = getHeadersCount();
        if (1 < headersCount) {//第一个不允许删除
            position = (0 == position) ? position + 1 : position;
            super.removeHeaderView(position);
        }
    }


    @Override
    public void removeFooterView(int position) {
        //必须剩下最后一个,且移除最后一个时减1
        int footersCount = getFootersCount();
        if (1 < footersCount) {
            //移除最后一个时-1,因为这个并不是用户添加的.也不在用户操作范围内
            position = (position == footersCount) ? position - 1 : position;
            super.removeFooterView(position);
        }
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
