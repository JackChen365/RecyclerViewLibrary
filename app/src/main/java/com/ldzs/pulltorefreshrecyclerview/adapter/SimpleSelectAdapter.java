package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;
import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;
import com.ldzs.recyclerlibrary.callback.Selectable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cz on 16/1/23.
 * @warning select adapter must be implement Selectable
 * @see com.ldzs.recyclerlibrary.callback.Selectable
 */
public class SimpleSelectAdapter<E> extends BaseViewAdapter<BaseViewHolder, E> implements Selectable{
    private int layout;

    public static SimpleSelectAdapter createFromResource(Context context, @ArrayRes int res) {
        return new SimpleSelectAdapter(context, context.getResources().getStringArray(res));
    }

    public SimpleSelectAdapter(Context context, E[] items) {
        this(context, R.layout.simple_text_item, Arrays.asList(items));
    }

    public SimpleSelectAdapter(Context context, @LayoutRes int layout, E[] items) {
        this(context, layout, Arrays.asList(items));
    }

    public SimpleSelectAdapter(Context context, List<E> items) {
        this(context, R.layout.simple_text_item, items);
    }

    public SimpleSelectAdapter(Context context, @LayoutRes int layout, List<E> items) {
        super(context, items);
        this.layout = layout;
    }

    @Override
    public void onSelectItem(RecyclerView.ViewHolder holder, int position, boolean select) {
        holder.itemView.setSelected(select);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(inflateView(parent, layout));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        E item = getItem(position);
        if (null != item) {
            textView.setText(item.toString());
        }
    }


}
