package com.ldzs.recyclerlibrary.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.StateSet;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView;
import com.ldzs.recyclerlibrary.callback.OnCheckListener;

import java.util.ArrayList;

/**
 * Created by cz on 4/3/16.
 * 一个可设置选择模式的数据乱配器
 * 如果本身是StateListDrawable,则需要自己设置select颜色
 */
public class SelectAdapter extends HeaderAdapter {
    // 三种选择状态
    public static final int CLICK = PullToRefreshRecyclerView.CLICK;//单击
    public static final int SINGLE_CHOICE = PullToRefreshRecyclerView.SINGLE_CHOICE;//单选
    public static final int MULTI_CHOICE = PullToRefreshRecyclerView.MULTI_CHOICE;//多选
    public static final int RECTANGLE_CHOICE = PullToRefreshRecyclerView.RECTANGLE_CHOICE;//块选择
    private static final String TAG = "SelectAdapter";
    private final ArrayList<Integer> multiSelectItems;//选中集
    private final ArrayList<Integer> realyMultiSelectItems;//真实的选中位置
    private int selectPosition;// 选中位置
    private int start, end;//截选范围
    private int mode;//选择模式
    private OnCheckListener listener;
    private Drawable drawable;

    public SelectAdapter(RecyclerView.Adapter adapter) {
        super(adapter);
        multiSelectItems = new ArrayList<>();
        realyMultiSelectItems = new ArrayList<>();
    }

    /**
     * 设置选择模式
     *
     * @param mode
     */
    public void setSelectMode(int mode) {
        clearSelect();
        this.mode = mode;
    }

    /**
     * 设置选中背景
     *
     * @param drawable
     */
    public void setChoiceBackground(Drawable drawable) {
        this.drawable = drawable;
    }

    /**
     * 清除选择状态
     */
    private void clearSelect() {
        int headersCount = getHeadersCount();
        switch (mode) {
            case SINGLE_CHOICE:
                //清除单选择状态
                int i = selectPosition + headersCount;
                selectPosition = -1;
                notifyItemChanged(i);
                break;
            case MULTI_CHOICE:
                ArrayList<Integer> positions = new ArrayList<>(realyMultiSelectItems);
                multiSelectItems.clear();
                realyMultiSelectItems.clear();
                for (Integer position : positions) {
                    notifyItemChanged(position);
                }
                break;
            case RECTANGLE_CHOICE:
                int s = Math.min(start, end) + headersCount, e = Math.max(start, end) + headersCount;
                start = end = 0;
                notifyItemRangeChanged(s, e - s);
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        //在这里动态更改view背景
        setItemBackground(viewHolder.itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        View itemView = holder.itemView;
        int headersCount = getHeadersCount();
        switch (mode) {
            case SINGLE_CHOICE:
                itemView.setSelected(selectPosition + headersCount == position);
                break;
            case MULTI_CHOICE:
                itemView.setSelected(realyMultiSelectItems.contains(position));
                break;
            case RECTANGLE_CHOICE:
                int s = Math.min(start + headersCount, end + headersCount);
                int e = Math.max(start + headersCount, end + headersCount);
                itemView.setSelected(s <= position && e >= position);
                break;
        }
    }

    /**
     * 重设条目背景,如果本身是StateListDrawable,则需要自己设置select颜色
     *
     * @param itemView
     */
    private void setItemBackground(View itemView) {
        Drawable background = itemView.getBackground();
        if (null != background && background instanceof StateListDrawable) return;
        StateListDrawable stateListDrawable = new StateListDrawable();
        if (null == background) {
            background = new ColorDrawable(Color.TRANSPARENT);
        }
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, drawable);
        stateListDrawable.addState(StateSet.WILD_CARD, background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            itemView.setBackground(stateListDrawable);
        } else {
            itemView.setBackgroundDrawable(stateListDrawable);
        }
    }

    @Override
    protected boolean onItemClick(View v, int position) {
        super.onItemClick(v, position);
        int headersCount = getHeadersCount();
        switch (mode) {
            case MULTI_CHOICE:
                selectPosition = start = end = -1;
                if (multiSelectItems.contains(position)) {
                    multiSelectItems.remove(Integer.valueOf(position));
                    realyMultiSelectItems.remove(Integer.valueOf(position + headersCount));
                } else {
                    multiSelectItems.add(Integer.valueOf(position));
                    realyMultiSelectItems.add(Integer.valueOf(position + headersCount));
                }
                if (null != listener) {
                    listener.onMultiChoice(v, multiSelectItems);
                }
                notifyItemChanged(position + headersCount);
                break;
            case RECTANGLE_CHOICE:
                if (-1 != start && -1 != end) {
                    int s = start, e = end;
                    start = end = -1;//重置
                    notifyItemRangeChanged(Math.min(s + headersCount, e + headersCount), Math.max(s + headersCount, e + headersCount));
                } else if (-1 == start) {
                    start = position;
                    notifyItemChanged(start + headersCount);
                } else if (-1 == end) {
                    end = position;
                    if (null != listener) {
                        listener.onRectangleChoice(start, end);
                    }
                    notifyItemRangeChanged(Math.min(start + headersCount, end + headersCount), Math.max(start + headersCount, end + headersCount));
                }
                break;
            case SINGLE_CHOICE:
                start = end = -1;
                multiSelectItems.clear();
                realyMultiSelectItems.clear();
                if (null != listener) {
                    listener.onSingleChoice(v, position, selectPosition);
                }
                notifyItemChanged(selectPosition + headersCount);//通知上一个取消
                selectPosition = position;
                notifyItemChanged(position + headersCount);//本次选中
                break;
        }
        return CLICK != mode;
    }

    /**
     * 设置选择监听
     *
     * @param listener
     */
    public void setOnCheckListener(OnCheckListener listener) {
        this.listener = listener;
    }

}
