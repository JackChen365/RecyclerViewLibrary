package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.recyclerlibrary.adapter.CacheViewHolder;
import com.ldzs.recyclerlibrary.adapter.tree.TreeAdapter;

import java.io.File;

/**
 * Created by cz on 16/1/23.
 */
public class FileAdapter extends TreeAdapter<File> {
    private static final int FOLDER_ITEM = 0;
    private static final int FILE_ITEM = 1;
    private final int PADDING;

    public FileAdapter(Context context, TreeNode<File> rootNode) {
        super(context, rootNode);
        PADDING = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onBindViewHolder(CacheViewHolder holder, TreeNode<File> node, File file, int viewType, int position) {
        View itemView = holder.itemView;
        itemView.setPadding(PADDING * node.level, itemView.getPaddingTop(), itemView.getPaddingRight(), itemView.getPaddingBottom());
        switch (viewType) {
            case FILE_ITEM:
                holder.textView(R.id.tv_simple_name).setText(getSimpleName(file.getName()));
                holder.textView(R.id.tv_name).setText(file.getName());
                break;
            case FOLDER_ITEM:
                holder.textView(R.id.tv_simple_name).setText(getSimpleName(file.getName()));
                holder.textView(R.id.tv_name).setText(file.getName() + "(" + node.child.size() + ")");
                holder.view(R.id.iv_flag).setSelected(node.expand);
                break;
        }
    }

    @Override
    protected void onNodeExpand(TreeNode<File> node, CacheViewHolder holder, boolean expand) {
        super.onNodeExpand(node, holder, expand);
        holder.view(R.id.iv_flag).setSelected(expand);
    }

    private String getSimpleName(String name) {
        return name.substring(0, Math.min(2, name.length()));
    }


    @Override
    public CacheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CacheViewHolder holder = null;
        switch (viewType) {
            case FILE_ITEM:
                holder = new CacheViewHolder(createView(parent, R.layout.file_item));
                break;
            case FOLDER_ITEM:
                holder = new CacheViewHolder(createView(parent, R.layout.folder_item));
                break;
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        File file = getItem(position);
        int viewType = FILE_ITEM;
        if (file.isDirectory()) {
            viewType = FOLDER_ITEM;
        }
        return viewType;
    }

}
