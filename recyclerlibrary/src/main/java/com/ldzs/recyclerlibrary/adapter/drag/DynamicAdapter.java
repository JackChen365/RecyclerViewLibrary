package com.ldzs.recyclerlibrary.adapter.drag;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;
import com.ldzs.recyclerlibrary.callback.OnItemLongClickListener;

import java.util.Arrays;

/**
 * 一个可以在RecyclerView 己有的Adapter,添加任一的其他条目的Adapter对象
 * 使用装饰设计模式,无使用限制
 * like :
 * 1|2|3|
 * --4--  //add
 * 5|6|7|
 * 8|9|10|
 * <p>
 * 难点在于,如果将随机位置添加的自定义view的位置动态计算,不影响被包装Adapter
 *
 * @param
 */
public class DynamicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "DynamicAdapter";
    protected final int START_POSITION = 1024;//超出其他Header/Footer范围,避免混乱
    protected final SparseIntArray mFullItemTypes;
    protected final SparseArray<View> mFullViews;
    protected int[] mItemPositions;
    private Context mContext;
    private RecyclerView.Adapter mAdapter;
    private int mItemViewCount;
    private OnItemLongClickListener mLongItemListener;
    private OnItemClickListener mItemClickListener;


    /**
     * @param adapter 包装数据适配器
     */
    public DynamicAdapter(Context context, RecyclerView.Adapter adapter) {
        this.mContext = context;
        this.mAdapter = adapter;
        mItemPositions = new int[0];
        mFullItemTypes = new SparseIntArray();
        mFullViews = new SparseArray<>();
    }

    /**
     * 添加一个自定义view到末尾
     *
     * @param layout
     */
    public void addFullItem(@LayoutRes int layout) {
        View view = View.inflate(mContext, layout, null);
        addFullItem(view, getItemCount());
    }

    /**
     * 添加一个自定义view到末尾
     *
     * @param view
     */
    public void addFullItem(View view) {
        addFullItem(view, getItemCount());
    }

    /**
     * 添加一个自定义view到指定位置
     *
     * @param position
     */
    public void addFullItem(View view, int position) {
        if (RecyclerView.NO_POSITION != findPosition(position)) return;//己存在添加位置,则不添加
        int length = mItemPositions.length;
        int[] newPositions = new int[length + 1];
        newPositions[length] = position;
        System.arraycopy(mItemPositions, 0, newPositions, 0, mItemPositions.length);
        Arrays.sort(newPositions);
        mItemPositions = newPositions;
        int viewType = START_POSITION + mItemViewCount++;
        mFullItemTypes.put(position, viewType);
        mFullViews.put(viewType, view);
        //当只有一个时,通知插入
        if (1 == mItemPositions.length) {
            notifyItemInserted(position);
        } else {
            notifyItemChanged(position, null);
        }
    }

    /**
     * 移除指定view
     *
     * @param view
     */
    public void removeFullItem(View view) {
        removeFullItem(mFullViews.indexOfValue(view));
    }

    /**
     * 移除指定位置view
     *
     * @param position
     */
    public void removeFullItem(int position) {
        if (isFullItem(position)) {
            int itemType = getItemViewType(position);
            mFullViews.remove(itemType);
            int index = mFullItemTypes.indexOfKey(position);
            if (0 <= index) {
                mFullItemTypes.removeAt(index);
            }
            int length = mItemPositions.length;
            int[] newPositions = new int[length - 1];
            for (int i = 0, k = 0; i < length; i++) {
                if (position != mItemPositions[i]) {
                    newPositions[k++] = mItemPositions[i];
                }
            }
            mItemPositions = newPositions;
            notifyItemRemoved(position);
        }
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
                    return isFullItem(position) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams && (isFullItem(position))) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    /**
     * 判断当前显示是否为自定义铺满条目
     *
     * @param position
     * @return
     */
    private boolean isFullItem(int position) {
        return RecyclerView.NO_POSITION != findPosition(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = mFullViews.get(viewType);
        if (null != view) {
            holder = new BaseViewHolder(view);
        } else if (null != mAdapter) {
            holder = mAdapter.onCreateViewHolder(parent, viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (RecyclerView.NO_POSITION == findPosition(position) && null != mAdapter) {
            int startIndex = getStartIndex(position);
            mAdapter.onBindViewHolder(holder, position - startIndex);
            int findPosition = findPosition(position);
            if (RecyclerView.NO_POSITION == findPosition) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mItemClickListener) {
                            int itemPosition = holder.getAdapterPosition();
                            mItemClickListener.onItemClick(v, itemPosition);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        int index = findPosition(position);
        if (RecyclerView.NO_POSITION != index) {
            viewType = mFullItemTypes.get(position);
        } else if (null != mAdapter) {
            int startIndex = getStartIndex(position);
            viewType = mAdapter.getItemViewType(position - startIndex);
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        int itemCount = mFullViews.size();
        if (null != mAdapter) {
            itemCount += mAdapter.getItemCount();
        }
        return itemCount;
    }


    /**
     * 使用二分查找法,根据firstVisiblePosition找到SelectPositions中的位置
     *
     * @return
     */
    public int getStartIndex(int position) {
        int[] positions = mItemPositions;
        int start = 0, end = positions.length - 1, result = -1;
        while (start <= end) {
            int middle = (start + end) / 2;
            if (position == positions[middle]) {
                result = middle + 1;
                break;
            } else if (position < positions[middle]) {
                end = middle - 1;
            } else {
                start = middle + 1;
            }
        }
        return -1 == result ? start : result;
    }

    /**
     * 查找当前是否有返回值
     *
     * @param position
     * @return
     */
    public int findPosition(int position) {
        int[] positions = mItemPositions;
        int start = 0, end = positions.length - 1, result = -1;
        while (start <= end) {
            int middle = (start + end) / 2;
            if (position == positions[middle]) {
                result = middle;
                break;
            } else if (position < positions[middle]) {
                end = middle - 1;
            } else {
                start = middle + 1;
            }
        }
        return result;
    }


    /**
     * 获得添加view个数
     *
     * @return
     */
    public int getFullItemCount() {
        return mFullViews.size();
    }

    /**
     * 设置条目长按点击事件
     *
     * @param listener
     */
    public void setOnLongItemClickListener(OnItemLongClickListener listener) {
        this.mLongItemListener = listener;
    }

    /**
     * 设置条目点击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}