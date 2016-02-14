package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;
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
    public void onBindViewHolder(BaseViewHolder holder, TreeNode<File> node, File file, int viewType, int position) {
        View itemView = holder.itemView;
        itemView.setPadding(PADDING * node.level, itemView.getPaddingTop(), itemView.getPaddingRight(), itemView.getPaddingBottom());
        switch (viewType) {
            case FILE_ITEM:
                FileHolder fileHolder = (FileHolder) holder;
                fileHolder.simpleNameView.setText(getSimpleName(file.getName()));
                fileHolder.nameView.setText(file.getName());
                break;
            case FOLDER_ITEM:
                FolderHolder folderHolder = (FolderHolder) holder;
                folderHolder.simpleNameView.setText(getSimpleName(file.getName()));
                folderHolder.folderView.setText(file.getName()+"("+node.child.size()+")");
                folderHolder.dragFlag.setSelected(node.expand);
                break;
        }
    }

    @Override
    protected void onNodeExpand(TreeNode<File> node, BaseViewHolder holder, boolean expand) {
        super.onNodeExpand(node, holder, expand);
        FolderHolder folderHolder = (FolderHolder) holder;
        folderHolder.dragFlag.setSelected(expand);
    }

    private String getSimpleName(String name) {
        return name.substring(0, Math.min(2, name.length()));
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder = null;
        switch (viewType) {
            case FILE_ITEM:
                holder = new FileHolder(createView(parent, R.layout.file_item));
                break;
            case FOLDER_ITEM:
                holder = new FolderHolder(createView(parent, R.layout.folder_item));
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

    public static class FolderHolder extends BaseViewHolder {
        private TextView simpleNameView;
        private TextView folderView;
        private ImageView dragFlag;

        public FolderHolder(View itemView) {
            super(itemView);
            simpleNameView = (TextView) itemView.findViewById(R.id.tv_simple_name);
            folderView = (TextView) itemView.findViewById(R.id.tv_folder_name);
            dragFlag = (ImageView) itemView.findViewById(R.id.iv_flag);
        }
    }

    public static class FileHolder extends BaseViewHolder {
        private TextView simpleNameView;
        private TextView nameView;

        public FileHolder(View itemView) {
            super(itemView);
            simpleNameView = (TextView) itemView.findViewById(R.id.tv_simple_name);
            nameView = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
