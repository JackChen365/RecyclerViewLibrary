package com.ldzs.pulltorefreshrecyclerview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;
import com.ldzs.recyclerlibrary.adapter.expand.ExpandAdapter;

import java.util.ArrayList;

/**
 * Created by cz on 16/1/22.
 */
public class FriendAdapter extends ExpandAdapter<String, String> {
    private static final String TAG = "FriendAdapter";

    public FriendAdapter(Context context, ArrayList<Entry<String, ArrayList<String>>> items) {
        super(context, items);
    }

    public FriendAdapter(Context context, ArrayList<Entry<String, ArrayList<String>>> items, boolean expand) {
        super(context, items, expand);
    }


    @Override
    public BaseViewHolder createGroupHolder(ViewGroup parent) {
        return new GroupHolder(inflateView(parent, R.layout.group_item));
    }

    @Override
    public BaseViewHolder createChildHolder(ViewGroup parent) {
        return new ItemHolder(inflateView(parent, R.layout.text_item));
    }

    @Override
    public void onBindGroupHolder(BaseViewHolder holder, int groupPosition) {
        GroupHolder groupHolder = (GroupHolder) holder;
        groupHolder.imageFlag.setSelected(getGroupExpand(groupPosition));//当前分组展开状态
        groupHolder.textView.setText(getGroup(groupPosition));
        groupHolder.count.setText("(" + getChildrenCount(groupPosition) + ")");//子孩子个数
    }

    @Override
    public BaseViewHolder onBindChildHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        ItemHolder itemHolder = (ItemHolder) holder;
        String item = getChild(groupPosition, childPosition);
        itemHolder.textView.setText(item);
        return null;
    }

    @Override
    protected void onGroupExpand(BaseViewHolder holder, boolean expand, int groupPosition) {
        super.onGroupExpand(holder, expand, groupPosition);
        GroupHolder groupHolder = (GroupHolder) holder;
        groupHolder.imageFlag.setSelected(expand);
    }

    public static class GroupHolder extends BaseViewHolder {
        public ImageView imageFlag;
        public TextView textView;
        public TextView count;

        public GroupHolder(View itemView) {
            super(itemView);
            imageFlag = (ImageView) itemView.findViewById(R.id.iv_group_flag);
            textView = (TextView) itemView.findViewById(R.id.tv_group_name);
            count = (TextView) itemView.findViewById(R.id.tv_group_count);
        }
    }

    public static class ItemHolder extends BaseViewHolder {
        public TextView textView;

        public ItemHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
    }

}
