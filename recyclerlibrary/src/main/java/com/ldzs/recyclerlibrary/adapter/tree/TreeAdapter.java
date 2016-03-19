package com.ldzs.recyclerlibrary.adapter.tree;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;
import com.ldzs.recyclerlibrary.callback.OnNodeItemClickListener;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by cz on 16/1/23.
 * 一个RecyclerView的树形管理Adapter对象
 */
public abstract class TreeAdapter<E> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "TreeAdapter";
    protected final ArrayList<TreeNode<E>> mNodeItems;//树的列表展示节点
    protected final ArrayList<E> mItems;
    protected final TreeNode<E> mRootNode;
    private final LayoutInflater mLayoutInflater;
    private int mHeaderCount;//头控件数
    private OnNodeItemClickListener mListener;


    public TreeAdapter(Context context, TreeNode<E> rootNode) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRootNode = rootNode;
        this.mNodeItems = new ArrayList<>();
        this.mItems = new ArrayList<>();
        ArrayList<TreeNode<E>> nodes = getNodeItems(mRootNode);
        if (null != nodes) {
            this.mItems.addAll(getItems(nodes));
            this.mNodeItems.addAll(nodes);
        }

    }

    public void setHeaderCount(int headerCount) {
        this.mHeaderCount = headerCount;
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
                int itemPosition = holder.getAdapterPosition() - mHeaderCount;
                TreeNode<E> node = getNode(itemPosition);
                boolean expand = node.expand;
                node.expand = true;//置为true,取得当前展开后的节点
                final ArrayList<TreeNode<E>> addNodes = getNodeItems(node);
                node.expand = !expand;//更新展开状态
                if (!addNodes.isEmpty()) {
                    int size = addNodes.size();
                    onNodeExpand(node, holder, !expand);
                    if (expand) {
                        mItems.removeAll(getItems(addNodes));
                        mNodeItems.removeAll(addNodes);
                        //关闭动作
                        notifyItemRangeRemoved(itemPosition + 1, size);
                    } else {
                        mItems.addAll(itemPosition + 1, getItems(addNodes));
                        mNodeItems.addAll(itemPosition + 1, addNodes);
                        //展开动作
                        notifyItemRangeInserted(itemPosition + 1, size);
                    }
                } else if (null != mListener) {
                    mListener.onNodeItemClick(node, v, itemPosition);
                }
            }
        });
    }

    /**
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

    public void setOnNodeItemClickListener(OnNodeItemClickListener listener) {
        this.mListener = listener;
    }

    public ArrayList<E> getItems(ArrayList<TreeNode<E>> nodes) {
        ArrayList<E> items = new ArrayList<>();
        if (null != nodes && nodes.isEmpty()) {
            int size = nodes.size();
            for (int i = 0; i < size; i++) {
                items.add(nodes.get(i).e);
            }
        }
        return items;
    }

    /**
     * 移除指定节点
     *
     * @param node
     */
    public void removeNode(TreeNode<E> node) {
        if (null != node) {
            ArrayList<TreeNode<E>> childNodes = node.child;
            //移除节点内,所有子节点
            if (node.expand && !childNodes.isEmpty()) {
                int size = childNodes.size();
                //这里之所以反向减少.是因为正向减少的话.这边减,在递归里,child的条目在减少.正向会引起size,没减,但child减少的角标越界问题.反向则不会
                for (int i = size - 1; i >= 0; i--) {
                    TreeNode<E> treeNode = childNodes.get(i);
                    removeNode(treeNode);
                }
            }
            int index = mNodeItems.indexOf(node);
            if (0 <= index) {
                remove(index);
            }
        }
    }

    public void removeNode(int position) {
        removeNode(mNodeItems.get(position));
    }

    /**
     * 获取条目在节点位置
     *
     * @param e
     * @return
     */
    public int indexOfItem(E e) {
        return mItems.indexOf(e);
    }

    /**
     * 设置指定条目取值
     *
     * @param index
     * @param e
     */
    public void set(int index, E e) {
        mItems.set(index, e);
        TreeNode<E> node = mNodeItems.get(index);
        node.e = e;
        notifyItemChanged(index);
    }

    /**
     * 按位置移除
     *
     * @param position
     */
    private void remove(int position) {
        mItems.remove(position);
        TreeNode<E> node = mNodeItems.remove(position);
        notifyItemRemoved(position);
        //移除根节点内节点指向
        TreeNode<E> parent = node.parent;
        if (null != parent) {
            parent.child.remove(node);
            //通知父条目改动
            notifyItemChanged(mNodeItems.indexOf(parent));
        }
    }

    public void insertNode(E e) {
        insertNode(new TreeNode(mRootNode, e));
    }

    /**
     * 插入节点
     *
     * @param node
     */
    public void insertNode(TreeNode<E> node) {
        //这是认祖归宗罗
        node.parent = mRootNode;
        mRootNode.child.add(node);
        ArrayList<TreeNode<E>> nodeItems = new ArrayList<>();
        nodeItems.add(node);
        ArrayList<TreeNode<E>> items = getNodeItems(node);
        if (!items.isEmpty()) {
            nodeItems.addAll(items);
        }
        int itemCount = getItemCount();
        mNodeItems.addAll(nodeItems);
        mItems.addAll(getItems(nodeItems));
        notifyItemRangeInserted(itemCount, nodeItems.size());
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            boolean result = false;
            TreeNode r = (TreeNode) o;
            if (null != e && null != r.e) {
                result = e.equals(r.e);
            }
            return result;
        }

        @Override
        public String toString() {
            return e.toString();
        }
    }
}
