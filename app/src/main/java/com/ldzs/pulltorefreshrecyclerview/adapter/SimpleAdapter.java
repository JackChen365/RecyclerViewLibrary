package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;
import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;
import com.ldzs.recyclerlibrary.adapter.CacheViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cz on 16/1/23.
 */
public class SimpleAdapter<E> extends BaseViewAdapter<E> {
    private static final String TAG="SimpleAdapter";
    private int layout;

    public static SimpleAdapter createFromResource(Context context, @ArrayRes int res) {
        return new SimpleAdapter(context, context.getResources().getStringArray(res));
    }


    public SimpleAdapter(Context context, E[] items) {
        this(context, R.layout.simple_text_item, Arrays.asList(items));
    }

    public SimpleAdapter(Context context, @LayoutRes int layout, E[] items) {
        this(context, layout, Arrays.asList(items));
    }

    public SimpleAdapter(Context context, List<E> items) {
        this(context, R.layout.simple_text_item, items);
    }

    public SimpleAdapter(Context context, @LayoutRes int layout, List<E> items) {
        super(context, items);
        this.layout = layout;
    }


    @Override
    public CacheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG,"onCreateViewHolder");
        return new CacheViewHolder(inflateView(parent, layout));
    }

    @Override
    public void onBindViewHolder(CacheViewHolder holder, int position) {
        Log.e(TAG,"onBindViewHolder:"+position);
        TextView textView = (TextView) holder.itemView;
        E item = getItem(position);
        if (null != item) {
            textView.setText(item.toString());
        }
    }
}
