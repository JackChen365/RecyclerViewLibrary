package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cz.library.util.Utils;
import com.cz.library.widget.FlowLayout;
import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.model.Sticky1Item;
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;
import com.ldzs.recyclerlibrary.adapter.CacheViewHolder;
import com.ldzs.recyclerlibrary.callback.StickyCallback;
import com.ldzs.recyclerlibrary.strategy.GroupingStrategy;

import java.util.List;

/**
 * Created by Administrator on 2017/5/20.
 */

public class LinearSticky2ItemAdapter extends BaseViewAdapter<Sticky1Item> implements StickyCallback {
    static final int ITEM_STICKY=0;
    static final int ITEM_NORMAL=1;
    private final GroupingStrategy groupingStrategy;

    public LinearSticky2ItemAdapter(Context context, List<Sticky1Item> items) {
        super(context, items);
        groupingStrategy=GroupingStrategy.of(this).reduce((Sticky1Item s1)->!s1.headerItems.isEmpty());
    }


    @Override
    public CacheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CacheViewHolder holder;
        if(ITEM_STICKY==viewType){
            holder=new CacheViewHolder(inflateView(parent, R.layout.sticky_flow_item));
        } else {
            holder=new CacheViewHolder(inflateView(parent, R.layout.sticky_text_item2));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(CacheViewHolder holder, int position) {
        Sticky1Item item = getItem(position);
        int itemViewType = getItemViewType(position);
        if(ITEM_STICKY==itemViewType){
            FlowLayout flowLayout= (FlowLayout) holder.view(R.id.fl_sticky_layout);
            if(!item.headerItems.isEmpty()){
                flowLayout.removeAllViews();
                final Context context = flowLayout.getContext();
                for(String text:item.headerItems){
                    flowLayout.addView(getTextLabel(context,text));
                }
            }
        } else if(ITEM_NORMAL==itemViewType){
            TextView textView=holder.textView(R.id.tv_view);
            textView.setText(item.item);
        }
    }

    @Override
    public void initStickyView(View view, int position) {
        FlowLayout flowLayout= (FlowLayout) view.findViewById(R.id.fl_sticky_layout);
        flowLayout.setLayoutTransition(null);
        int groupStartIndex = groupingStrategy.getGroupStartIndex(position);
        Sticky1Item item = getItem(groupStartIndex);
        if(!item.headerItems.isEmpty()){
            flowLayout.removeAllViews();
            final Context context = view.getContext();
            for(String text:item.headerItems){
                flowLayout.addView(getTextLabel(context,text));
            }
        }
    }

    @Override
    public GroupingStrategy getGroupingStrategy() {
        return groupingStrategy;
    }

    private TextView getTextLabel(Context context,String text){
        Button textView=new Button(context);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColorStateList(context,R.drawable.text2white_select_text));
        textView.setPadding(Utils.dip2px(12),Utils.dip2px(4),Utils.dip2px(12),Utils.dip2px(4));
        return textView;
    }

    @Override
    public int getItemViewType(int position) {
        Sticky1Item item = getItem(position);
        int viewType=ITEM_NORMAL;
        if(!item.headerItems.isEmpty()){
            viewType=ITEM_STICKY;
        }
        return viewType;
    }




}
