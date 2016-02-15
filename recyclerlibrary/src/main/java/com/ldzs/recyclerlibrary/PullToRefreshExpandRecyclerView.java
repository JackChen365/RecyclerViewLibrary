package com.ldzs.recyclerlibrary;

import android.content.Context;
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
    private ExpandAdapter mExpandAdapter;

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
    public void addHeaderView(View view) {
        super.addHeaderView(view);
        //设置headerView个数,用以解决局部刷新位置判断问题
        if (null != mExpandAdapter) {
            mExpandAdapter.setHeaderViewCount(getHeaderViewCount());
            invalidateItemDecorations();
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof ExpandAdapter) {
            mExpandAdapter = (ExpandAdapter) adapter;
            mExpandAdapter.setHeaderViewCount(getHeaderViewCount());
            mExpandAdapter.setOnExpandItemClickListener(new OnExpandItemClickListener() {
                @Override
                public void onItemClick(View v, int groupPosition, int childPosition) {
                    if (null != mExpandItemClickListener) {
                        mExpandItemClickListener.onItemClick(v, groupPosition, childPosition);
                    }
                }
            });
        } else {
            throw new IllegalArgumentException("Adapter must use ExpandAdapter!");
        }
    }


    public void setOnExpandItemClickListener(OnExpandItemClickListener listener) {
        this.mExpandItemClickListener = listener;
    }

}

