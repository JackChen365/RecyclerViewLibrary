package com.ldzs.recyclerlibrary.adapter.tree;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by cz on 16/1/23.
 * 一个RecyclerView的树形管理Adapter对象
 */
public abstract class TreeAdapter<E> extends RecyclerView.Adapter<BaseViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final ArrayList<TreeNode<E>> mNodeItems;//树的列表展示节点
    private final TreeNode<E> mRootNode;
    private int mHeaderCount;//头控件数


    public TreeAdapter(Context context, TreeNode<E> rootNode) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRootNode = rootNode;
        this.mNodeItems = new ArrayList<>();
        ArrayList<TreeNode<E>> nodes = getNodeItems(mRootNode);
        if (null != nodes) {
            this.mNodeItems.addAll(nodes);
        }

    }

    /**
     * 获取节点内所有可展开节点
     * 这里效率稍微了点,但可以接受
     */
    private synchronized ArrayList<TreeNode<E>> getNodeItems(TreeNode rootNode) {
        ArrayList<TreeNode<E>> nodeItems = new ArrayList<>();
        LinkedList<TreeNode<E>> nodes = new LinkedList<>();
        nodes.add(rootNode);
        while (!nodes.isEmpty()) {
            TreeNode<E> node = nodes.pollFirst();
            if (mRootNode == node || node.expand && !node.child.isEmpty()) {
                ArrayList<TreeNode<E>> child = node.child;
                int size = child.size();
                for (int i = size - 1; i >= 0; i--) {
                    TreeNode<E> childNode = child.get(i);
                    nodes.offerFirst(childNode);
                }
            }
            if (node != rootNode) {
                nodeItems.add(node);
            }
        }
        return nodeItems;
    }


    /**
     * 绑定节点信息
     *
     * @param holder
     * @param node
     * @param position
     */
    public abstract void onBindViewHolder(final BaseViewHolder holder, TreeNode<E> node, E e, int viewType, int position);

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        TreeNode<E> node = getNode(position);
        onBindViewHolder(holder, node, node.e, getItemViewType(position), position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = holder.getAdapterPosition();
                TreeNode<E> node = getNode(itemPosition);
                boolean expand = node.expand;
                node.expand = true;//置为true,取得当前展开后的节点
                final ArrayList<TreeNode<E>> addNodes = getNodeItems(node);
                node.expand = !expand;//更新展开状态
                if (!addNodes.isEmpty()) {
                    int size = addNodes.size();
                    onNodeExpand(node, holder, !expand);
                    if (expand) {
                        mNodeItems.removeAll(addNodes);
                        //关闭动作
                        notifyItemRangeRemoved(itemPosition + 1, size);
                    } else {
                        mNodeItems.addAll(itemPosition + 1, addNodes);
                        //展开动作
                        notifyItemRangeInserted(itemPosition + 1, size);
                    }
                }
            }
        });
    }

    /**
     *
     * 子类实现,节点展开或关闭
     *
     * @param node
     * @param holder
     * @param expand
     */
    protected void onNodeExpand(TreeNode<E> node, BaseViewHolder holder, boolean expand) {
    }

    /**
     * 获得列表对应位置节点
     *
     * @param position
     * @return
     */
    public TreeNode<E> getNode(int position) {
        return mNodeItems.get(position);
    }

    /**
     * 获得对应节点内容
     *
     * @param position
     * @return
     */
    public E getItem(int position) {
        return mNodeItems.get(position).e;
    }

    @Override
    public int getItemCount() {
        return mNodeItems.size();
    }

    /**
     * 设置某个一分级隐藏与显示
     *
     * @param level
     */
    public void setLevelExpand(int level, boolean expand) {

    }


    /**
     * 创建view对象
     *
     * @param parent
     * @param layout
     * @return
     */
    protected View createView(ViewGroup parent, int layout) {
        return mLayoutInflater.inflate(layout, parent, false);
    }

    /**
     * 树节点
     *
     * @param <E>
     */
    public static class TreeNode<E> {
        public boolean expand;//是否展开
        public int level;//当前节点级
        public E e;//节点
        public TreeNode<E> parent;//父节点
        public ArrayList<TreeNode<E>> child;//子节点

        public TreeNode(E e) {
            this(false, null, e);
        }

        public TreeNode(TreeNode<E> parent, E e) {
            this(false, parent, e);
        }

        public TreeNode(boolean expand, TreeNode<E> parent, E e) {
            this.expand = expand;
            this.level = null == parent ? 0 : parent.level + 1;
            this.e = e;
            this.parent = parent;
            this.child = new ArrayList<>();
        }

    }
}
