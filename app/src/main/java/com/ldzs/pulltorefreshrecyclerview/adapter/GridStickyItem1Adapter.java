package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cz.library.util.Utils;
import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.model.Sticky1Item;
import com.ldzs.pulltorefreshrecyclerview.model.Sticky2Item;
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;
import com.ldzs.recyclerlibrary.adapter.CacheViewHolder;
import com.ldzs.recyclerlibrary.adapter.GridStickyAdapter;
import com.ldzs.recyclerlibrary.callback.StickyCallback;
import com.ldzs.recyclerlibrary.strategy.GroupingStrategy;

import java.util.List;

/**
 * Created by Administrator on 2017/5/20.
 */

public class GridStickyItem1Adapter extends GridStickyAdapter<Sticky2Item>{
    private static final String TAG = "GridStickyItem1Adapter";
    static final int ITEM_STICKY=0;
    static final int ITEM_NORMAL=1;
    private final GroupingStrategy groupingStrategy;

    public GridStickyItem1Adapter(Context context, List<Sticky2Item> items) {
        super(context, items);
        groupingStrategy=GroupingStrategy.of(this).reduce((Sticky2Item item)->item.title);
    }

    @Override
    public void onBindViewHolder(CacheViewHolder holder, int position) {
        Sticky2Item item = getItem(position);
        int itemViewType = getItemViewType(position);
        if(ITEM_STICKY==itemViewType){
            TextView textView= holder.textView(R.id.tv_sticky_view);
            textView.setText(item.item);
        } else if(ITEM_NORMAL==itemViewType){
            TextView textView= holder.textView(R.id.tv_view);
            textView.setText(item.item);
        }
    }


    @Override
    public CacheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CacheViewHolder holder;
        if(ITEM_STICKY==viewType){
            holder=new CacheViewHolder(inflateView(parent, R.layout.sticky_top_item));
        } else {
            holder=new CacheViewHolder(inflateView(parent, R.layout.sticky_text_item2));
        }
        return holder;
    }


    @Override
    public void initStickyView(View view, int position) {
        TextView stickyView= (TextView) view.findViewById(R.id.tv_sticky_view);
        int groupStartIndex = groupingStrategy.getGroupStartIndex(position);
        Sticky2Item item = getItem(groupStartIndex);
        stickyView.setText(item.item);
    }

    @Override
    public int getItemViewType(int position) {
        Sticky2Item item = getItem(position);
        return item.title?ITEM_STICKY:ITEM_NORMAL;
    }

    @Override
    public boolean isStickyPosition(int position) {
        return groupingStrategy.isGroupIndex(position);
    }


}
