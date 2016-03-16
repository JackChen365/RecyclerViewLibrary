package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.model.Channel;
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;
import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;

import java.util.List;

/**
 * Created by cz on 16/1/27.
 */
public class ChannelAdapter extends BaseViewAdapter<ChannelAdapter.ViewHolder, Channel> {
    private static final String TAG = "ChannelAdapter";
    private boolean mDragStatus;

    public ChannelAdapter(Context context, List<Channel> items) {
        super(context, items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateView(parent, R.layout.channel_item));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Channel item = getItem(position);
        holder.name.setText(item.name);
        holder.flagView.setVisibility(item.use ? View.GONE : View.VISIBLE);
        holder.deleteView.setVisibility(mDragStatus && item.use ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置当前拖动状态
     *
     * @param drag
     */
    public void setDragStatus(boolean drag) {
        this.mDragStatus = drag;
        notifyItemRangeChanged(0,getItemsCount(),null);
    }

    public static class ViewHolder extends BaseViewHolder {
        private View deleteView;
        private View flagView;
        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            deleteView = itemView.findViewById(R.id.iv_delete_icon);
            flagView = itemView.findViewById(R.id.iv_flag);
            name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
