package com.ldzs.recyclerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.ldzs.recyclerlibrary.adapter.RefreshAdapter;
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;
import com.ldzs.recyclerlibrary.divide.SimpleItemDecoration;
import com.ldzs.recyclerlibrary.footer.RefreshFrameFooter;
import com.ldzs.recyclerlibrary.observe.HeaderAdapterDataObserve;

import cz.library.PullToRefreshLayout;
import cz.library.RefreshMode;


/**
 * Created by czz on 2016/8/13.
 */
public class PullToRefreshRecyclerView extends PullToRefreshLayout<RecyclerView> implements IRecyclerView{
    public static final int END_NORMAL=0x00;
    public static final int END_REFRESHING=0x01;

    public static final int CLICK=0x00;
    public static final int SINGLE_CHOICE=0x01;
    public static final int MULTI_CHOICE=0x02;
    public static final int RECTANGLE_CHOICE=0x03;


    @IntDef(value={CLICK,SINGLE_CHOICE,MULTI_CHOICE,RECTANGLE_CHOICE})
    public @interface ChoiceMode{
    }


    private final RefreshAdapter adapter;
    private final SimpleItemDecoration itemDecoration;
    private final RefreshFrameFooter refreshFooter;
    private OnPullFooterToRefreshListener listener;
    private Drawable choiceBackground;
    private int refreshState;
    private int choiceMode;

    public PullToRefreshRecyclerView(Context context) {
        this(context,null,0);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        refreshState = END_NORMAL;
        itemDecoration=new SimpleItemDecoration();
        refreshFooter = new RefreshFrameFooter(context, this);
        adapter = new RefreshAdapter(null);
        initFooterViewByMode(getRefreshMode());
        setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshRecyclerView);
        setListDivide(a.getDrawable(R.styleable.PullToRefreshRecyclerView_pv_listDivide));
        setListDivideHeight(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_listDivideHeight, 0f));
        setDivideHorizontalPadding(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_divideHorizontalPadding, 0));
        setDivideVerticalPadding(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_divideVerticalPadding, 0));
        setChoiceModeInner(a.getInt(R.styleable.PullToRefreshRecyclerView_pv_choiceMode, CLICK));
        setChoiceBackground(a.getDrawable(R.styleable.PullToRefreshRecyclerView_pv_choiceBackground));
        a.recycle();
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.targetView.addItemDecoration(itemDecoration);
    }

    public void setListDivide(Drawable drawable) {
        itemDecoration.setDrawable(drawable);
    }
    public void setListDivideHeight(float listDivideHeight) {
        itemDecoration.setStrokeWidth(Math.round(listDivideHeight));
    }

    public void setDivideHorizontalPadding(float padding) {
        this.itemDecoration.setDivideHorizontalPadding(Math.round(padding));
    }
    public void setDivideVerticalPadding(float padding) {
        this.itemDecoration.setDivideVerticalPadding(Math.round(padding));
    }

    public void setChoiceMode(@ChoiceMode int mode){
        setChoiceModeInner(mode);
    }
    private void setChoiceModeInner(int mode) {
        this.choiceMode=mode;
        invalidate();
    }

    public void setChoiceBackground(Drawable choiceBackground) {
        this.choiceBackground = choiceBackground;
    }

    @Override
    protected RecyclerView getTargetView() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollStateChanged(RecyclerView.SCROLL_STATE_IDLE);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                scrollStateChanged(newState);
            }
        });
        return recyclerView;
    }

    @Override
    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator){
        this.targetView.setItemAnimator(itemAnimator);
    }


    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager){
        this.targetView.setLayoutManager(layoutManager);
        if (layoutManager instanceof GridLayoutManager || layoutManager instanceof StaggeredGridLayoutManager) {
            itemDecoration.setDivideMode(SimpleItemDecoration.GRID);
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();
            itemDecoration.setDivideMode(OrientationHelper.HORIZONTAL == orientation ? SimpleItemDecoration.HORIZONTAL : SimpleItemDecoration.VERTICAL);
        }
    }

    @Override
    public int getHeaderViewCount() {
        return this.adapter.getHeadersCount();
    }

    @Override
    public void addHeaderView(View view) {
        checkNullObjectRef(view);
        adapter.addHeaderView(view);
        itemDecoration.setHeaderCount(getHeaderViewCount());
    }

    @Override
    public void removeHeaderView(View view) {
        checkNullObjectRef(view);
        adapter.removeHeaderView(view);
        itemDecoration.setHeaderCount(getHeaderViewCount());
    }

    @Override
    public void removeHeaderView(int index){
        checkIndexInBounds(index,getHeaderViewCount());
        adapter.removeHeaderView(index);
        itemDecoration.setHeaderCount(getHeaderViewCount());
    }


    @Override
    public int getFooterViewCount() {
        return adapter.getFootersCount();
    }

    @Override
    public void addFooterView(View view)  {
        checkNullObjectRef(view);
        adapter.addFooterView(view);
        itemDecoration.setFooterCount(getFooterViewCount());
    }

    @Override
    public void removeFooterView(View view)  {
        checkNullObjectRef(view);
        adapter.removeFooterView(view);
        itemDecoration.setFooterCount(getFooterViewCount());
    }

    @Override
    public void removeFooterView(int index)  {
        checkIndexInBounds(index,getFooterViewCount());
        adapter.removeFooterView(index);
        itemDecoration.setFooterCount(getFooterViewCount());
    }

    @Override
    public RecyclerView.ItemAnimator getItemAnimator() {
        return this.targetView.getItemAnimator();
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.adapter.setOnItemClickListener(listener);
    }

    @Override
    public void setOnFootRetryListener(OnClickListener listener) {
        this.refreshFooter.setRefreshState(RefreshFrameFooter.FRAME_ERROR);
        this.refreshFooter.setOnFootRetryListener(listener);
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        this.adapter.setAdapter(adapter);
        this.targetView.setAdapter(this.adapter);
        adapter.registerAdapterDataObserver(new HeaderAdapterDataObserve(this.adapter));
    }

    public void setRefreshFooterState(@RefreshFrameFooter.RefreshState int state){
        refreshFooter.setRefreshState(state);
    }

    @Override
    public void setRefreshMode(RefreshMode mode) {
        super.setRefreshMode(mode);
        initFooterViewByMode(mode);
    }


    private void initFooterViewByMode(RefreshMode mode) {
        if(null==adapter) return;
        boolean exist = this.adapter.hasRefreshFooterView();
        if(!exist&&mode.enableFooter()){
            addFooterView(refreshFooter.getFooterView());
            scrollStateChanged(RecyclerView.SCROLL_STATE_IDLE);
        } else if(exist){
            removeFooterView(refreshFooter.getFooterView());
        }
    }

    /**
     * check object is a null,when object is null reference throw NullPointerException
     * @param obj
     */
    private void checkNullObjectRef(Object obj){
        if(null==obj){
            throw new NullPointerException("The header view is null!");
        }
    }
    /**
     * check index is out of bounds,when object is out of bounds  throw IndexOutOfBoundsException
     * @param index
     * @param count
     */
    private void checkIndexInBounds(int index,int count) {
        if(0>index||index>=count){
            throw new IndexOutOfBoundsException("index out of bounds!");
        }
    }
    /**
     * on recyclerView scroll state changed
     * @param state
     */
    private void scrollStateChanged(int state) {
        RefreshMode refreshMode = getRefreshMode();
        if (state == RecyclerView.SCROLL_STATE_IDLE && null != listener && refreshMode.enableFooter()) {
            RecyclerView.LayoutManager layoutManager = targetView.getLayoutManager();
            int lastVisibleItemPosition = getLastVisiblePosition();
            if (lastVisibleItemPosition >= layoutManager.getItemCount() - 1 &&
                    layoutManager.getItemCount() >= layoutManager.getChildCount() && refreshState==END_NORMAL) {
                refreshState = END_REFRESHING;
                listener.onRefresh();
            }
        }
    }


    /**
     * get last visible position
     * @return last visible position
     */
    public int getLastVisiblePosition() {
        int lastVisibleItemPosition;
        RecyclerView.LayoutManager layoutManager = targetView.getLayoutManager();
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

    public void onRefreshFootComplete(){
        if(END_REFRESHING==refreshState){
            refreshState=END_NORMAL;
            this.targetView.requestLayout();
        }
    }

    public void setFooterRefreshDone(){
        refreshFooter.setRefreshState(RefreshFrameFooter.FRAME_DONE);
    }



    public void setOnPullFooterToRefreshListener(OnPullFooterToRefreshListener listener){
        this.listener=listener;
    }

    /**
     * set bottom refresh listener
     */
    public interface OnPullFooterToRefreshListener {
        void onRefresh();
    }



}
