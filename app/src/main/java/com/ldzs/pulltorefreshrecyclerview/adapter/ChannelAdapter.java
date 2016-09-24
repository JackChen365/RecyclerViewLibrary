package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.model.Channel;
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;
import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;
import com.ldzs.recyclerlibrary.adapter.CacheViewHolder;

import java.util.List;

/**
 * Created by cz on 16/1/27.
 */
public class ChannelAdapter extends BaseViewAdapter<Channel> {
    private boolean mDragStatus;

    public ChannelAdapter(Context context, List<Channel> items) {
        super(context, items);
    }


    @Override
    public CacheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CacheViewHolder(inflateView(parent,R.layout.channel_item));
    }

    @Override
    public void onBindViewHolder(CacheViewHolder holder, int position) {
        Channel item = getItem(position);
        holder.textView(R.id.tv_name).setText(item.name);
        holder.view(R.id.iv_flag).setVisibility(item.use ? View.GONE : View.VISIBLE);
        holder.imageView(R.id.iv_delete_icon).setVisibility(mDragStatus && item.use ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置当前拖动状态
     *
     * @param drag
     */
    public void setDragStatus(boolean drag) {
        this.mDragStatus = drag;
        notifyItemRangeChanged(0,getItemsCount());
    }
}
