package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;
import com.ldzs.recyclerlibrary.adapter.CursorRecyclerAdapter;

/**
 * Created by cz on 16/3/15.
 */
public class CursorAdapter extends CursorRecyclerAdapter<BaseViewHolder> {
    private final LayoutInflater layoutInflater;

    public CursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, Cursor cursor, int position) {
        TextView textView = (TextView) holder.itemView;
        String text = cursor.getString(1);
        textView.setText(text);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(layoutInflater.inflate(R.layout.simple_text_item, parent, false));
    }
}
