package com.ldzs.recyclerlibrary;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ldzs.recyclerlibrary.callback.OnItemClickListener;

/**
 * Created by czz on 2016/8/20.
 */
public interface IRecyclerView {

    /**
     * set a RecyclerView.Adapter,when adapter is null throw a NullPointerException
     * @param adapter
     */
    void setAdapter(RecyclerView.Adapter adapter) throws NullPointerException;

    /**
     * set a RecyclerView.ItemAnimator
     * @param itemAnimator
     */
    void setItemAnimator(RecyclerView.ItemAnimator itemAnimator);

    /**
     * set a LayoutManager
     * @param layoutManager
     */
    void setLayoutManager(RecyclerView.LayoutManager layoutManager);

    RecyclerView.LayoutManager getLayoutManager();

    /**
     * get header view count
     * @return header view count
     */
    int getHeaderViewCount();

    /**
     * add a new header view,when view is null throws a NullPointerException
     * @param view
     */
    void addHeaderView(View view);

    /**
     * remove a exist view,when view is null throws a NullPointerException
     * @param view
     */
    void removeHeaderView(View view);

    /**
     * remove header view by index,when index < 0 or < getHeaderViewCount() throw an IndexOutOfBoundsException
     * @param index
     */
    void removeHeaderView(int index);

    /**
     * get footer view count
     * @return view count
     */
    int getFooterViewCount();
    /**
     * add a new footer view,when view is null throws a NullPointerException
     * @param view
     */
    void addFooterView(View view);
    /**
     * remove a exist header view,when index < 0 or < getHeaderViewCount() throw an IndexOutOfBoundsException
     * @param view
     */
    void removeFooterView(View view);
    /**
     * remove header view by index,when index < 0 or < getHeaderViewCount() throw an IndexOutOfBoundsException
     * @param index
     */
    void removeFooterView(int index);


    /**
     * get recycler view item animator
     * @return
     */
    RecyclerView.ItemAnimator getItemAnimator();

    void addOnScrollListener(RecyclerView.OnScrollListener listener);

    void removeOnScrollListener(RecyclerView.OnScrollListener listener);

    /**
     * set recycler view item click listener
     * @param listener
     */
    void setOnItemClickListener(OnItemClickListener listener);

    void setOnPullFooterToRefreshListener(PullToRefreshRecyclerView.OnPullFooterToRefreshListener listener);

    /**
     * set footer retray listener;
     * @param listener
     */
    void setOnFootRetryListener(View.OnClickListener listener);


}
