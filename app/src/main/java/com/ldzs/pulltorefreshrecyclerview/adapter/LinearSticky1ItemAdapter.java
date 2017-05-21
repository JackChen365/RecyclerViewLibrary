package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;
import com.ldzs.recyclerlibrary.adapter.CacheViewHolder;
import com.ldzs.recyclerlibrary.callback.StickyCallback;
import com.ldzs.recyclerlibrary.strategy.GroupingStrategy;

import java.util.List;

/**
 * Created by Administrator on 2017/5/20.
 */

public class LinearSticky1ItemAdapter extends BaseViewAdapter<String> implements StickyCallback {
    private final GroupingStrategy groupingStrategy;

    public LinearSticky1ItemAdapter(Context context, List<String> items) {
        super(context, items);
        groupingStrategy=GroupingStrategy.of(this).reduce((String s1, String s2)->s1.charAt(0)!=s2.charAt(0));
    }


    @Override
    public CacheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CacheViewHolder(inflateView(parent, R.layout.sticky_text_item1));
    }

    @Override
    public void onBindViewHolder(CacheViewHolder holder, int position) {
        TextView stickyView=holder.textView(R.id.tv_sticky_view);
        TextView textView=holder.textView(R.id.tv_view);
        String item = getItem(position);
        boolean isStickyPosition=isStickyPosition(position);
        stickyView.setVisibility(isStickyPosition?View.VISIBLE:View.GONE);
        if(isStickyPosition){
            stickyView.setText(String.valueOf(item.charAt(0)));
        }
        textView.setText(item);
    }

    @Override
    public void initStickyView(View view, int position) {
        TextView textView= (TextView) view;
        String item = getItem(position);
        if(!TextUtils.isEmpty(item)){
            textView.setText(String.valueOf(item.charAt(0)));
        }
    }

    @Override
    public boolean isStickyPosition(int position) {
        return groupingStrategy.isGroupIndex(position);
    }


}
