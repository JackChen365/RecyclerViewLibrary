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
import java.util.List;

/**
 * Created by cz on 16/1/23.
 * 一个RecyclerView的树形管理Adapter对象
 */
public abstract class TreeAdapter<E> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "TreeAdapter";
    protected final ArrayList<TreeNode<E>> nodeItems;//树的列表展示节点
    protected final ArrayList<E> items;
    protected final TreeNode<E> rootNode;
    private final LayoutInflater layoutInflater;
    private int headerCount;//头控件数
    private OnNodeItemClickListener listener;


    public TreeAdapter(Context context, TreeNode<E> rootNode) {
        this.layoutInflater = LayoutInflater.from(context);
        this.rootNode = rootNode;
        this.nodeItems = new ArrayList<>();
        this.items = new ArrayList<>();
        refreshItems();
    }

    public void setHeaderCount(int headerCount) {
        this.headerCount = headerCount;
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
            if (this.rootNode == node || node.expand && !node.child.isEmpty()) {
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
                int itemPosition = holder.getAdapterPosition() - headerCount;
                TreeNode<E> node = getNode(itemPosition);
                boolean expand = node.expand;
                node.expand = true;//置为true,取得当前展开后的节点
                ArrayList<E> items = getItems(node);
                final ArrayList<TreeNode<E>> addNodes = getNodeItems(node);
                node.expand = !expand;//更新展开状态
                if (!addNodes.isEmpty()) {
                    int size = addNodes.size();
                    onNodeExpand(node, holder, !expand);
                    if (expand) {
                        TreeAdapter.this.items.removeAll(items);
                        nodeItems.removeAll(addNodes);
                        //关闭动作
                        notifyItemRangeRemoved(itemPosition + 1, size);
                    } else {
                        TreeAdapter.this.items.addAll(itemPosition + 1, items);
                        nodeItems.addAll(itemPosition + 1, addNodes);
                        //展开动作
                        notifyItemRangeInserted(itemPosition + 1, size);
                    }
                } else if (null != listener) {
                    listener.onNodeItemClick(node, v, itemPosition);
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
        return nodeItems.get(position);
    }

    /**
     * 获得对应节点内容
     *
     * @param position
     * @return
     */
    public E getItem(int position) {
        return nodeItems.get(position).e;
    }

    public List<E> getItems(){
        return items;
    }

    @Override
    public int getItemCount() {
        return nodeItems.size();
    }

    /**
     * 设置某个一分级隐藏与显示
     *
     * @param level
     */
    public void setLevelExpand(int level, boolean expand) {
    }

    public void setOnNodeItemClickListener(OnNodeItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 获取节点内所有可展开节点
     * 这里效率稍微了点,但可以接受
     */
    private ArrayList<E> getItems(TreeNode rootNode) {
        ArrayList<E> nodeItems = new ArrayList<>();
        LinkedList<TreeNode<E>> nodes = new LinkedList<>();
        nodes.add(rootNode);
        while (!nodes.isEmpty()) {
            TreeNode<E> node = nodes.pollFirst();
            if (this.rootNode == node || node.expand && !node.child.isEmpty()) {
                ArrayList<TreeNode<E>> child = node.child;
                int size = child.size();
                for (int i = size - 1; i >= 0; i--) {
                    TreeNode<E> childNode = child.get(i);
                    nodes.offerFirst(childNode);
                }
            }
            if (node != rootNode) {
                nodeItems.add(node.e);
            }
        }
        return nodeItems;
    }

    private ArrayList<E> getItems(ArrayList<TreeNode<E>> nodes) {
        ArrayList<E> items = new ArrayList<>();
        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            items.add(nodes.get(i).e);
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
            int index = nodeItems.indexOf(node);
            if (0 <= index) {
                remove(index);
            }
        }
    }

    public void removeNode(int position) {
        removeNode(nodeItems.get(position));
    }

    /**
     * 获取条目在节点位置
     *
     * @param e
     * @return
     */
    public int indexOfItem(E e) {
        return items.indexOf(e);
    }

    /**
     * 设置指定条目取值
     *
     * @param index
     * @param e
     */
    public void set(int index, E e) {
        items.set(index, e);
        TreeNode<E> node = nodeItems.get(index);
        node.e = e;
        notifyItemChanged(index);
    }

    /**
     * 按位置移除
     *
     * @param position
     */
    private void remove(int position) {
        items.remove(position);
        TreeNode<E> node = nodeItems.remove(position);
        notifyItemRemoved(position);
        //移除根节点内节点指向
        TreeNode<E> parent = node.parent;
        if (null != parent) {
            parent.child.remove(node);
            //通知父条目改动
            notifyItemChanged(nodeItems.indexOf(parent));
        }
    }

    public void insertNode(E e) {
        insertNode(new TreeNode(rootNode, e));
    }

    /**
     * 插入节点
     *
     * @param node
     */
    public void insertNode(TreeNode<E> node) {
        //这是认祖归宗罗
        node.parent = rootNode;
        rootNode.child.add(node);
        ArrayList<TreeNode<E>> nodeItems = new ArrayList<>();
        nodeItems.add(node);
        ArrayList<TreeNode<E>> items = getNodeItems(node);
        if (!items.isEmpty()) {
            nodeItems.addAll(items);
        }
        int itemCount = getItemCount();
        this.nodeItems.addAll(nodeItems);
        this.items.addAll(getItems(nodeItems));
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
        return layoutInflater.inflate(layout, parent, false);
    }

    public void refreshItems(){
        this.items.clear();
        this.nodeItems.clear();
        ArrayList<TreeNode<E>> nodes = getNodeItems(this.rootNode);
        if (null != nodes) {
            this.items.addAll(getItems(nodes));
            this.nodeItems.addAll(nodes);
        }
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
