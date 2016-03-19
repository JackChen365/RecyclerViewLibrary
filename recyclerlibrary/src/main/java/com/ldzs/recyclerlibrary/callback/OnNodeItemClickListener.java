package com.ldzs.recyclerlibrary.callback;

import android.view.View;

import com.ldzs.recyclerlibrary.adapter.tree.TreeAdapter;

/**
 * Created by cz on 16/3/17.
 */
public interface OnNodeItemClickListener<E> {
    void onNodeItemClick(TreeAdapter.TreeNode<E> node, View v,int position);
}
