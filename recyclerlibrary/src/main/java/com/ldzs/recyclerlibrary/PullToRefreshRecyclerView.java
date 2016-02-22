package com.ldzs.recyclerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ldzs.recyclerlibrary.adapter.RefreshHeaderAdapter;
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;
import com.ldzs.recyclerlibrary.divide.SimpleItemDecoration;
import com.ldzs.recyclerlibrary.header.BaseRefresh;
import com.ldzs.recyclerlibrary.header.BaseRefreshFooter;
import com.ldzs.recyclerlibrary.header.BaseRefreshHeader;
import com.ldzs.recyclerlibrary.header.DefaultHeader;
import com.ldzs.recyclerlibrary.header.LoadFooterView;
import com.ldzs.recyclerlibrary.observe.HeaderAdapterDataObserve;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * RecyclerView
 * <p>
 * 以下修改
 * 1:修改刷新方式控件
 * 1.1:两边
 * 1.2:头
 * 1.3:尾
 * 1.4:不启用
 * 2:源码简化
 */
public class PullToRefreshRecyclerView extends RecyclerView {
    private static final String TAG = "PullToRefreshRecyclerView";
    //刷新模式
    public static final int REFRESH_BOTH = 0x00;
    public static final int REFRESH_HEADER = 0x01;
    public static final int REFRESH_BOTTOM = 0x02;
    public static final int REFRESH_NONE = 0x03;
    private static final float RESISTANCE = 3;//阻力倍数
    protected BaseRefreshHeader mRefreshHeader;
    protected BaseRefreshFooter mRefreshFooter;
    protected SimpleItemDecoration mSimpleItemDecoration;
    protected OnPullUpToRefreshListener mUpListener;
    protected OnPullDownToRefreshListener mDownListener;
    protected RefreshHeaderAdapter mAdapter;
    private Mode mode;//刷新模式
    private float mScrollOffset;//滑动距离
    private float mLastY;


    public PullToRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mode = Mode.BOTH;
        mAdapter = new RefreshHeaderAdapter(null);
        mSimpleItemDecoration = new SimpleItemDecoration(this);//初始化分隔线
        addItemDecoration(mSimpleItemDecoration);
        //初始化header/footer
        DefaultHeader refreshHeader = new DefaultHeader(context);
        addHeaderView(refreshHeader);
        mRefreshHeader = refreshHeader;
        LoadFooterView footView = new LoadFooterView(context);
        addFooterView(footView);
        mRefreshFooter = footView;
        //初始化自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshRecyclerView);
        setDivideDrawable(a.getResourceId(R.styleable.PullToRefreshRecyclerView_pv_listDivide, R.color.transparent));
        setListDivideHeight(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_listDivideHeight, 0));
        setDivideHorizontalPadding(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_divideHorizontalPadding, 0));
        setDivideVerticalPadding(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_divideVerticalPadding, 0));
        setRefreshMode(Mode.values()[a.getInt(R.styleable.PullToRefreshRecyclerView_pv_refreshMode, REFRESH_BOTH)]);
        a.recycle();

    }


    /**
     * 添加顶部控件
     *
     * @param view
     */
    public void addHeaderView(View view) {
        mAdapter.addHeaderView(view);
        mSimpleItemDecoration.setHeaderCount(mAdapter.getHeadersCount());//更新分隔线顶部控件个数
        invalidateItemDecorations();
    }


    /**
     * 添加底部控件
     *
     * @param view
     */
    public void addFooterView(final View view) {
        mAdapter.addFooterView(view);
        mSimpleItemDecoration.setFooterCount(mAdapter.getFootersCount());//更新分隔线底部控件个数
        invalidateItemDecorations();
    }

    /**
     * 移除指定顶部控件
     *
     * @return
     */
    public void removeHeaderView(View view) {
        mAdapter.removeHeaderView(view);
    }

    /**
     * 移除指定位置顶部view
     *
     * @param position
     */
    public void removeHeaderView(int position) {
        mAdapter.removeHeaderView(position);
    }

    /**
     * 移除指定底部控件
     *
     * @return
     */
    public void removeFooterView(View view) {
        mAdapter.removeFooterView(view);
    }

    /**
     * 移除指定位置底部view
     *
     * @param position
     */
    public void removeFooterView(int position) {
        mAdapter.removeFooterView(position);
    }

    /**
     * 获得顶部控件个数
     *
     * @return
     */
    public int getHeaderViewCount() {
        return mAdapter.getHeadersCount();
    }

    /**
     * 获得底部控件个数
     *
     * @return
     */
    public int getFooterViewCount() {
        return mAdapter.getFootersCount();
    }

    public void setDivideDrawable(int res) {
        mSimpleItemDecoration.setDrawable(res);
        invalidateItemDecorations();
    }

    public void setListDivideHeight(float height) {
        mSimpleItemDecoration.setStrokeWidth(Math.round(height));
        invalidateItemDecorations();
    }

    public void setDivideHorizontalPadding(float padding) {
        mSimpleItemDecoration.setDivideHorizontalPadding(Math.round(padding));
        invalidateItemDecorations();
    }

    public void setDivideVerticalPadding(float padding) {
        mSimpleItemDecoration.setDivideVerticalPadding(Math.round(padding));
        invalidateItemDecorations();
    }

    /**
     * 顶部刷新完毕
     */
    public void onRefreshComplete() {
        if (mRefreshHeader.isRefreshing()) {
            mRefreshHeader.refreshComplete();
        } else if (mRefreshFooter.isRefreshing()) {
            mRefreshFooter.refreshComplete();
        }
    }

    /**
     * 手动通知刷新
     */
    public void setRefreshing() {
        //正在刷新中,则不执行
        if (mRefreshHeader.isRefreshing() || mRefreshHeader.isCurrentState(BaseRefresh.STATE_RELEASE_TO_REFRESH))
            return;
        scrollToPosition(0);
        // TODO 以动画滑动到顶部,会出现延持不统一情况,待解决
//        smoothScrollToPosition(0);
//        ItemAnimator itemAnimator = getItemAnimator();
//        long moveDuration = itemAnimator.getMoveDuration();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f);
        valueAnimator.setDuration(200);
//        valueAnimator.setStartDelay(moveDuration);
        final int originalHeight = mRefreshHeader.getOriginalHeight();//获得原始高度
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                mRefreshHeader.pullToRefresh(fraction * originalHeight);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRefreshHeader.setState(BaseRefresh.STATE_RELEASE_TO_REFRESH);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束后,改变刷新头状态,并执行回调
                mRefreshHeader.setRefreshing();
                mUpListener.onRefresh();
            }
        });
        valueAnimator.start();
    }

    /**
     * 设置底部加载完毕
     */
    public void setFooterComplete() {
        mRefreshFooter.setState(BaseRefresh.STATE_FINISH);
    }

    /**
     * 设置底部重试
     *
     * @param listener
     */
    public void setFooterRetryListener(OnClickListener listener) {
        mRefreshFooter.setState(BaseRefresh.STATE_ERROR);
        mRefreshFooter.setOnRetryListener(listener);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof GridLayoutManager || layout instanceof StaggeredGridLayoutManager) {
            mSimpleItemDecoration.setDivideMode(SimpleItemDecoration.GRID);
        } else if (layout instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) layout;
            int orientation = layoutManager.getOrientation();//与系统方向一致
            mSimpleItemDecoration.setDivideMode(OrientationHelper.HORIZONTAL == orientation ? SimpleItemDecoration.HORIZONTAL : SimpleItemDecoration.VERTICAL);
        }
    }


    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter.setAdapter(adapter);
        super.setAdapter(mAdapter);
        adapter.registerAdapterDataObserver(new HeaderAdapterDataObserve(mAdapter));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        float y = e.getRawY();
        if (MotionEvent.ACTION_DOWN == e.getActionMasked()) {
            mLastY = y;
            mScrollOffset = mRefreshHeader.getRefreshHeight() * RESISTANCE;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float y = ev.getRawY();
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                final float deltaY = y - mLastY;
                if (listOnTop()) {
                    if (mode.disableHeader() && 0 < deltaY) {
                        mRefreshHeader.pullToRefresh((deltaY + mScrollOffset) / RESISTANCE);
                        if (mRefreshHeader.isCurrentState(DefaultHeader.STATE_RELEASE_TO_REFRESH) || mRefreshHeader.isCurrentState(BaseRefresh.STATE_NORMAL)) {
                            return false;
                        }
                    }
                } else {
                    mLastY = y;//不在最顶端时,记录当前位置
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (listOnTop() && mode.disableHeader()) {
                    if (mRefreshHeader.isCurrentState(BaseRefresh.STATE_RELEASE_TO_REFRESH) && null != mUpListener) {
                        mRefreshHeader.setRefreshing();
                        mUpListener.onRefresh();
                    }
                    mRefreshHeader.releaseToRefresh();//释放控制
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        refreshDown(state);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        refreshDown(RecyclerView.SCROLL_STATE_IDLE);
    }

    /**
     * 设置动画控制对象
     * 复写进行劫持操作.用以局部制制动画展示
     *
     * @param animator
     */
    @Override
    public void setItemAnimator(ItemAnimator animator) {
        super.setItemAnimator(animator);
    }

    /**
     * 底部刷新
     *
     * @param state
     */
    private void refreshDown(int state) {
        if (state == RecyclerView.SCROLL_STATE_IDLE && null != mDownListener && mode.disableFooter()) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition = getLastVisiblePosition();
            if (lastVisibleItemPosition >= layoutManager.getItemCount() - 1 &&
                    layoutManager.getItemCount() >= layoutManager.getChildCount() && mRefreshFooter.isCurrentState(BaseRefresh.STATE_NORMAL)) {
                if (mRefreshHeader.isRefreshing())
                    mRefreshHeader.setState(BaseRefresh.STATE_COMPLETE);//取消上一个请求
                mRefreshFooter.setState(BaseRefresh.STATE_REFRESHING);
                mDownListener.onRefresh();
            }
        }
    }


    /**
     * 获得底部可见位置
     *
     * @return
     */
    private int getLastVisiblePosition() {
        int lastVisibleItemPosition;
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] spanCount = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(spanCount);
            lastVisibleItemPosition = spanCount[0];
            for (int value : spanCount) {
                if (value > lastVisibleItemPosition) {
                    lastVisibleItemPosition = value;
                }
            }
        } else {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
        return lastVisibleItemPosition;
    }


    private boolean listOnTop() {
        boolean onTop = false;
        View view = mAdapter.getHeaderView(0);
        if (null != view) {
            onTop = null != view.getParent();
        }
        return onTop;
    }


    /**
     * 设置刷新模式
     *
     * @param mode
     */
    public void setRefreshMode(Mode mode) {
        this.mode = mode;
        if (mode.disableHeader()) {
            mRefreshHeader.setState(BaseRefresh.STATE_NORMAL);
        } else {
            mRefreshHeader.setState(BaseRefresh.STATE_DONE);
        }
        if (mode.disableFooter()) {
            mRefreshFooter.setState(BaseRefresh.STATE_NORMAL);//刷用底部刷新
        } else {
            mRefreshFooter.setState(BaseRefresh.STATE_DONE);//刷用底部刷新
        }
    }

    /**
     * 设置顶部刷新
     *
     * @param listener
     */
    public void setOnPullUpToRefreshListener(OnPullUpToRefreshListener listener) {
        this.mUpListener = listener;
    }

    /**
     * 设置底部刷新
     *
     * @param listener
     */
    public void setOnPullDownToRefreshListener(OnPullDownToRefreshListener listener) {
        this.mDownListener = listener;
    }

    /**
     * 设置条目点击
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
    }

    /**
     * 顶部刷新监听
     */
    public interface OnPullUpToRefreshListener {
        void onRefresh();
    }

    /**
     * 底部刷新监听
     */
    public interface OnPullDownToRefreshListener {
        void onRefresh();
    }

}
