package com.ldzs.recyclerlibrary.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.adapter.tree.TreeAdapter;
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;

import java.util.ArrayList;

/**
 * 包装RecyclerView的数据适配器,添加头和尾操作
 * 利用adapter的分类达到
 */
public class HeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HeaderAdapter";
    private final int TYPE_HEADER = -1;//从-1起始开始减
    private final int TYPE_NORMAL = 0;//默认从0开始
    private final int TYPE_NORMAL_ITEM_COUNT = 12;//随意取的值,确保装饰Adapter对象不会超过此界即可
    private final int TYPE_FOOTER = TYPE_NORMAL_ITEM_COUNT + 1;
    private RecyclerView.Adapter mAdapter;
    private final ArrayList<HeaderViewItem> mHeaderViews;
    private final ArrayList<HeaderViewItem> mFootViews;
    private int mHeaderCount, mFooterCount;//头/尾的总个数
    private OnItemClickListener mItemClickListener;

    public HeaderAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
        this.mHeaderViews = new ArrayList<>();
        this.mFootViews = new ArrayList<>();
    }

    /**
     * 设置当前数据适配器
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeader(position) || isFooter(position)) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    public boolean isHeader(int position) {
        return position >= 0 && position < mHeaderViews.size();
    }

    public boolean isFooter(int position) {
        int itemCount = getItemCount();
        return position < itemCount && position >= itemCount - mFootViews.size();
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFootViews.size();
    }


    public void addHeaderView(View view) {
        int viewType = TYPE_HEADER - mHeaderCount;
        int index = mHeaderViews.size();
        this.mHeaderViews.add(index, new HeaderViewItem(viewType, view));
        notifyItemInserted(index);
        mHeaderCount++;
        if (null != mAdapter) {
            //避免包装子条目混乱
            if (mAdapter instanceof TreeAdapter) {
                TreeAdapter treeAdapter = (TreeAdapter) this.mAdapter;
                treeAdapter.setHeaderCount(getHeadersCount());
            }
        }
    }

    /**
     * 此方法不开放,避免角标混乱
     *
     * @param view
     * @param index
     */
    protected void addFooterView(View view, int index) {
        //固定第一个
        int viewType = TYPE_FOOTER + mFooterCount;
        index = index > mFootViews.size() ? mFootViews.size() : index;
        this.mFootViews.add(index, new HeaderViewItem(viewType, view));//越界处理
        notifyItemInserted(getFooterStartIndex() + index);
        mFooterCount++;
    }

    public void addFooterView(View view) {
        addFooterView(view, mFootViews.size());
    }


    /**
     * 底部组起始位置
     *
     * @return
     */
    public int getFooterStartIndex() {
        int index = getHeadersCount();
        if (null != mAdapter) {
            index += mAdapter.getItemCount();
        }
        return index;
    }

    /**
     * 获得指定位置的headerView
     *
     * @param index
     * @return
     */
    public View getHeaderView(int index) {
        View view = null;
        if (0 <= index && index < mHeaderViews.size()) {
            view = mHeaderViews.get(index).view;
        }
        return view;
    }

    /**
     * 获得指定的位置的footerView
     *
     * @param index
     * @return
     */
    public View getFooterView(int index) {
        View view = null;
        if (0 <= index && index < mFootViews.size()) {
            view = mFootViews.get(index).view;
        }
        return view;
    }

    /**
     * 移除指定的HeaderView对象
     *
     * @param view
     */
    public void removeHeaderView(View view) {
        if (null == view) return;
        removeHeaderView(indexOfValue(mHeaderViews, view));
    }


    /**
     * 移除指定的HeaderView对象
     *
     * @param position
     */
    public void removeHeaderView(int position) {
        if (0 > position || mHeaderViews.size() <= position) return;
        mHeaderViews.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 移除指定的HeaderView对象
     *
     * @param view
     */
    public void removeFooterView(View view) {
        if (null == view) return;
        removeFooterView(indexOfValue(mFootViews, view));
    }

    /**
     * 移除指定的HeaderView对象
     *
     * @param position
     */
    public void removeFooterView(int position) {
        if (0 > position || mFootViews.size() <= position) return;
        mFootViews.remove(position);
        notifyItemRemoved(getItemCount() - getFootersCount() - position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (TYPE_NORMAL > viewType) {
            holder = new BaseViewHolder(getItemValue(mHeaderViews, viewType));
        } else if (TYPE_NORMAL_ITEM_COUNT < viewType) {
            holder = new BaseViewHolder(getItemValue(mFootViews, viewType));
        } else {
            holder = mAdapter.onCreateViewHolder(parent, viewType);
        }
        return holder;
    }

    private int indexOfValue(ArrayList<HeaderViewItem> items, View view) {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            HeaderViewItem viewItem = items.get(i);
            if (viewItem.view == view) {
                index = i;
                break;
            }
        }
        return index;
    }

    private View getItemValue(ArrayList<HeaderViewItem> items, int type) {
        View view = null;
        for (int i = 0; i < items.size(); i++) {
            HeaderViewItem viewItem = items.get(i);
            if (viewItem.viewType == type) {
                view = viewItem.view;
                break;
            }
        }
        return view;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (!isHeader(position)) {
            position -= getHeadersCount();
            if (null != mAdapter && position < mAdapter.getItemCount()) {
                mAdapter.onBindViewHolder(holder, position);
                if (mAdapter instanceof BaseViewAdapter) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int itemPosition = holder.getAdapterPosition() - getHeadersCount();
                            if (!onItemClick(v, itemPosition) && null != mItemClickListener) {
                                mItemClickListener.onItemClick(v, itemPosition);
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 子类点击使用
     *
     * @param v
     * @param position
     */
    protected boolean onItemClick(View v, int position) {
        return false;
    }

    @Override
    public int getItemCount() {
        int itemCount = getHeadersCount() + getFootersCount();
        if (null != mAdapter) {
            itemCount += mAdapter.getItemCount();
        }
        return itemCount;
    }


    @Override
    public int getItemViewType(int position) {
        int itemType = TYPE_NORMAL;
        if (isHeader(position)) {
            itemType = mHeaderViews.get(position).viewType;//头
        } else if (isFooter(position)) {
            itemType = mFootViews.get(getFootersCount() - (getItemCount() - position)).viewType; //尾
        } else {
            //子条目类型
            int itemPosition = position - getHeadersCount();
            if (mAdapter != null) {
                int adapterCount = mAdapter.getItemCount();
                if (itemPosition < adapterCount) {
                    itemType = mAdapter.getItemViewType(itemPosition);
                }
            }
        }
        return itemType;
    }

    @Override
    public long getItemId(int position) {
        if (mAdapter != null && position >= getHeadersCount()) {
            position = -getHeadersCount();
            int itemCount = mAdapter.getItemCount();
            if (position < itemCount) {
                return mAdapter.getItemId(position);
            }
        }
        return -1;
    }


    /**
     * 设置条目点击
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public static class HeaderViewItem {
        public final int viewType;
        public final View view;

        public HeaderViewItem(int viewType, View view) {
            this.viewType = viewType;
            this.view = view;
        }
    }
}