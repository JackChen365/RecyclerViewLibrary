package com.ldzs.recyclerlibrary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.ldzs.recyclerlibrary.adapter.expand.ExpandAdapter;
import com.ldzs.recyclerlibrary.callback.OnExpandItemClickListener;


/**
 * Created by cz on 16/1/22.
 * 可展开的RecyclerView对象
 */
public class PullToRefreshExpandRecyclerView extends PullToRefreshRecyclerView {
    private OnExpandItemClickListener mExpandItemClickListener;
    private ExpandAdapter expandAdapter;

    public PullToRefreshExpandRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshExpandRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshExpandRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (!(adapter instanceof ExpandAdapter)) {
            throw new IllegalArgumentException("Adapter must extend ExpandAdapter!");
        } else {
            super.setAdapter(adapter);
            expandAdapter = (ExpandAdapter) adapter;
            expandAdapter.setHeaderViewCount(getHeaderViewCount());
            expandAdapter.setOnExpandItemClickListener(new OnExpandItemClickListener() {
                @Override
                public void onItemClick(View v, int groupPosition, int childPosition) {
                    if (null != mExpandItemClickListener) {
                        mExpandItemClickListener.onItemClick(v, groupPosition, childPosition);
                    }
                }
            });
        }
    }


    public void setOnExpandItemClickListener(OnExpandItemClickListener listener) {
        this.mExpandItemClickListener = listener;
    }

}

