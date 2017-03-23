package com.ldzs.recyclerlibrary.adapter.expand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;
import com.ldzs.recyclerlibrary.callback.OnExpandItemClickListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cz on 16/1/22.
 * 一个可展开的RecyclerView数据适配器
 */
public abstract class ExpandAdapter<K, E> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "ExpandAdapter";
    private static final int HEADER_ITEM = 0;//标题分类
    private static final int CHILD_ITEM = 1;//条目分类
    private final LayoutInflater layoutInflater;
    private final ArrayList<Entry<K, List<E>>> items;//数据集
    private final ArrayList<Integer> itemSteps;//每个分类段个数
    private final ArrayList<Boolean> groupStatus;//每个分组当前展开状态
    private OnExpandItemClickListener listener;
    private int headerCount;//顶部view总数

    public ExpandAdapter(Context context, LinkedHashMap<K, List<E>> items) {
        this(context, items, false);
    }

    public ExpandAdapter(Context context, LinkedHashMap<K, List<E>> items, boolean expand) {
        this(context, getNewItems(items), expand);
    }


    public ExpandAdapter(Context context, List<Entry<K, List<E>>> items) {
        this(context, items, false);
    }

    /**
     * ----
     * ----
     * ----
     * ----
     *
     * ---- 0
     *  ----
     *  ----
     * ----
     *  ----
     * @param context
     * @param items
     * @param expand
     */
    public ExpandAdapter(Context context, List<Entry<K, List<E>>> items, boolean expand) {
        layoutInflater = LayoutInflater.from(context);
        this.items = new ArrayList<>();
        groupStatus = new ArrayList<>();
        itemSteps = new ArrayList<>();
        if (null != items) {
            //包装对象
            this.items.addAll(items);
            int size = items.size();
            for (int i = 0; i < size; i++) {
                groupStatus.add(expand);//记录初始展开状态
            }
            //初始化状态
            updateGroupItemInfo();
        }
    }

    /**
     * 转换hashMap
     *
     * @param items
     * @param <K>
     * @param <E>
     * @return
     */
    private static <K, E> List<ExpandAdapter.Entry<K, List<E>>> getNewItems(LinkedHashMap<K, List<E>> items) {
        ArrayList<ExpandAdapter.Entry<K, List<E>>> newItems = new ArrayList<>();
        if (!items.isEmpty()) {
            for (Map.Entry<K, List<E>> entry : items.entrySet()) {
                newItems.add(new ExpandAdapter.Entry<>(entry.getKey(), entry.getValue()));
            }
        }
        return newItems;
    }

    /**
     * 更新组信息
     */
    private void updateGroupItemInfo() {
        itemSteps.clear();
        int total = 0;
        int size = items.size();
        for (int i = 0; i < size; i++) {
            Boolean expand = groupStatus.get(i);
            List<E> childItems = items.get(i).items;
            //记录初始个数
            itemSteps.add(total);//记录每个阶段总个数
            int itemSize = null == childItems || !expand ? 0 : childItems.size();
            total += (itemSize + 1);
        }
        Log.e(TAG,"itemSteps:"+itemSteps);
    }

    public void setHeaderViewCount(int count) {
        this.headerCount = count;
    }

    /**
     * 获得分类个数
     *
     * @return
     */
    public int getGroupCount() {
        return items.size();
    }

    /**
     * 获得子分类个数
     *
     * @param groupPosition
     * @return
     */
    public int getChildrenCount(int groupPosition) {
        return items.get(groupPosition).items.size();
    }

    /**
     * 获得分组对象
     *
     * @param groupPosition
     * @return
     */
    public K getGroup(int groupPosition) {
        return items.get(groupPosition).k;
    }

    public List<E> getGroupItems(int groupPosition) {
        return items.get(groupPosition).items;
    }

    public E getChild(int groupPosition, int childPosition) {
        return getGroupItems(groupPosition).get(childPosition);
    }


    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        Log.e(TAG,"position:"+position);
        int viewType = getItemViewType(position);
        final int groupPosition = getGroupPosition(position);
        switch (viewType) {
            case HEADER_ITEM:
                onBindGroupHolder(holder, groupPosition);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //展开.或关闭条目列
                        int newPosition = holder.getAdapterPosition()-headerCount;
                        Log.e(TAG,"newPosition:"+newPosition);
                        int newGroupPosition = getGroupPosition(newPosition);
                        Boolean expand = groupStatus.get(newGroupPosition);
                        groupStatus.set(newGroupPosition, !expand);//状态置反
                        onGroupExpand(holder, !expand, newGroupPosition);
                        expandGroup(newPosition, newGroupPosition, expand);
                    }
                });
                break;
            case CHILD_ITEM:
                final int childPosition = getChildPosition(position);
                onBindChildHolder(holder, groupPosition, childPosition);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != listener) {
                            listener.onItemClick(v, groupPosition, childPosition);
                        }
                    }
                });
                break;
        }
    }

    /**
     * 当组展开或关闭时回用
     */
    protected void onGroupExpand(BaseViewHolder holder, boolean expand, int groupPosition) {
        // 由子类填写,用于局部更新标题状态等
    }

    /**
     * 展开组
     *
     * @param expand
     */
    private void expandGroup(int position, int groupPosition, boolean expand) {
        List<E> childItems = getGroupItems(groupPosition);//关闭
        int expandCount = (null == childItems) ? 0 : childItems.size();
        //更新各节点起始位置,更新各节点个数
        updateGroupItemInfo();
        if (expand) {
            notifyItemRangeRemoved(position+1, expandCount);
        } else {
            notifyItemRangeInserted(position+1, expandCount);
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder = null;
        switch (viewType) {
            case HEADER_ITEM:
                holder = createGroupHolder(parent);
                break;
            case CHILD_ITEM:
                holder = createChildHolder(parent);
                break;
        }
        return holder;
    }

    /**
     * 创建组视图对象
     *
     * @param parent
     * @return
     */
    public abstract BaseViewHolder createGroupHolder(ViewGroup parent);

    /**
     * 创建子视图对象
     *
     * @param parent
     * @return
     */
    public abstract BaseViewHolder createChildHolder(ViewGroup parent);

    /**
     * 绑字group视图数据
     *
     * @param holder
     * @param groupPosition
     * @return
     */
    public abstract void onBindGroupHolder(BaseViewHolder holder, int groupPosition);

    /**
     * 绑定子视图数据
     *
     * @param holder
     * @param position
     * @return
     */
    public abstract BaseViewHolder onBindChildHolder(BaseViewHolder holder, int groupPosition, int position);

    @Override
    public int getItemCount() {
        int totalCount = groupStatus.size();
        for (int i = 0; i < items.size(); i++) {
            List<E> items = this.items.get(i).items;
            if (null != items && groupStatus.get(i)) {
                totalCount += items.size();
            }
        }
        return totalCount;
    }

    @Override
    public int getItemViewType(int position) {
        //根据快速滑动角位置,设置左边指示位置,使用二分查找
        Integer[] positions = new Integer[itemSteps.size()];
        itemSteps.toArray(positions);
        int findPosition = getSelectPosition(positions, position);
        Integer stepPosition = itemSteps.get(findPosition);
        int viewType = HEADER_ITEM;
        if (0 < position - stepPosition) {
            viewType = CHILD_ITEM;
        }
        return viewType;
    }

    /**
     * 获得当前位置下分组位置
     *
     * @param position
     * @return
     */
    private int getGroupPosition(int position) {
        Integer[] positions = new Integer[itemSteps.size()];
        itemSteps.toArray(positions);
        return getSelectPosition(positions, position);
    }

    /**
     * 获得子孩子当前分组位置
     *
     * @param position
     * @return
     */
    private int getChildPosition(int position) {
        Integer[] positions = new Integer[itemSteps.size()];
        itemSteps.toArray(positions);
        int findPosition = getSelectPosition(positions, position);
        Integer stepPosition = itemSteps.get(findPosition);
        return position - stepPosition - 1;
    }

    /**
     * 使用二分查找法,根据firstVisiblePosition找到SelectPositions中的位置
     *
     * @return
     */
    public static int getSelectPosition(Integer[] positions, int firstVisiblePosition) {
        int start = 0, end = positions.length;
        while (end - start > 1) {
            // 中间位置
            int middle = (start + end) >> 1;
            // 中值
            int middleValue = positions[middle];
            if (firstVisiblePosition > middleValue) {
                start = middle;
            } else if (firstVisiblePosition < middleValue) {
                end = middle;
            } else {
                start = middle;
                break;
            }
        }
        return start;
    }

    /**
     * 添加一个组对象
     *
     * @param item
     * @param items
     */
    public void addGroupItems(K item, List<E> items) {
        addGroupItems(item, items, getGroupCount(), false);
    }

    /**
     * 指定默认展开形式添加组
     *
     * @param item
     * @param items
     * @param index
     * @param expand
     */
    public void addGroupItems(K item, List<E> items, int index, boolean expand) {
        int groupCount = getGroupCount();//原来组个数
        Entry entry = new Entry(item, items);
        this.items.add(index, entry);
        groupStatus.add(index, expand);//添加展开状态
        updateGroupItemInfo();//更新组信息
        int itemSize = null == items || !expand ? 0 : items.size();//添加个数
        int startIndex; //计算起始位置
        if (0 == index) {
            startIndex = 0;//第一个
        } else if (index == groupCount) {
            startIndex = getItemCount(); //最后一个
        } else {
            startIndex = itemSteps.get(index);//中间
        }
        notifyItemRangeInserted(startIndex, itemSize + 1);
    }

    /**
     * 移除一个大分组
     *
     * @param groupPosition
     */
    public void removeGroup(int groupPosition) {
        if (items.isEmpty()) return;
        Integer startIndex = itemSteps.remove(groupPosition);//起始位置
        Boolean expand = groupStatus.remove(groupPosition);
        int itemSize =0;//子孩子个数
        if(expand){
            Entry<K, List<E>> listEntry = items.get(groupPosition);
            if(null!=listEntry.items){
                itemSize=listEntry.items.size();
            }
        }
        items.remove(groupPosition);
        updateGroupItemInfo();
        notifyItemRangeRemoved(startIndex, expand ? itemSize + 1 : 1);
    }

    /**
     * 移除大分组内子条目
     *
     * @param groupPosition
     * @param childPosition
     */
    public void removeGroup(int groupPosition, int childPosition) {
        if (items.isEmpty()) return;
        Boolean expand = groupStatus.get(groupPosition);//当前分组展开状态
        Entry<K,List<E>> entry = items.get(groupPosition);
        List items = entry.items;
        if (items.isEmpty()) return;
        items.remove(childPosition);
        if (expand) {
            updateGroupItemInfo();//更新所有检测信息
            Integer startIndex = itemSteps.get(groupPosition) + 1;//位置从大分组+1开始算
            int removePosition = startIndex + childPosition;//移除的位置
            notifyItemRemoved(removePosition);
        }
    }

    public void swapItems(LinkedHashMap<K, List<E>> items) {
        swapItems(getNewItems(items));
    }

    public void swapItems(List<Entry<K, List<E>>> items) {
        swapItems(items, false);
    }

    /**
     * 置换数据
     *
     * @param items
     */
    public void swapItems(List<Entry<K, List<E>>> items, boolean expand) {
        if (!items.isEmpty()) {
            groupStatus.clear();
            itemSteps.clear();
            this.items.clear();
            this.items.addAll(items);
            int size = items.size();
            for (int i = 0; i < size; i++) {
                groupStatus.add(expand);//记录初始展开状态
            }
            //初始化状态
            updateGroupItemInfo();
            notifyDataSetChanged();
        }
    }


    /**
     * 创建view对象
     *
     * @param parent
     * @param layout
     * @return
     */
    protected View inflateView(ViewGroup parent, int layout) {
        return layoutInflater.inflate(layout, parent, false);
    }

    public void setOnExpandItemClickListener(OnExpandItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置展开状态
     *
     * @param expand
     */
    public void setExpand(boolean expand) {
        //展开所有
        groupStatus.clear();
        int size = items.size();
        for (int i = 0; i < size; i++) {
            groupStatus.add(expand);//记录初始展开状态
        }
        updateGroupItemInfo();//更新状态
        notifyDataSetChanged();
    }

    /**
     * 获得当前分组展开状态
     *
     * @param groupPosition
     * @return
     */
    public boolean getGroupExpand(int groupPosition) {
        return groupStatus.get(groupPosition);
    }

    /**
     * group组对象
     *
     * @param <K>
     * @param <E>
     */
    public static class Entry<K, E> {
        private final K k;
        private final E items;

        public Entry(K k, E items) {
            this.k = k;
            this.items = items;
        }
    }

}
