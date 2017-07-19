package com.ldzs.recyclerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.adapter.SelectAdapter;
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;
import com.ldzs.recyclerlibrary.divide.SimpleItemDecoration;
import com.ldzs.recyclerlibrary.footer.RefreshFrameFooter;
import com.ldzs.recyclerlibrary.observe.DynamicAdapterDataObserve;

import java.util.ArrayList;
import java.util.List;

import cz.library.PullToRefreshLayout;
import cz.library.RefreshMode;


/**
 * Created by czz on 2016/8/13.
 * 这个控件,主要由几部分组成
 * 1:PullToRefreshLayout:另一个下拉刷新的加载库
 * 2:隔离封装的Adapter支持:
 *  其中控件内,提供的Adapter为:SelectAdapter,层级判断为:DynamicAdapter->RefreshAdapter->SelectAdapter
 *  其中DynamicAdapter为负责任一元素位置插入条目的扩展数据适配器.
 *  RefreshAdapter为固定底部的Footer的数据适配器.
 *  而最上层的SelectAdapter,则提供类似ListView的selectMode选择功能的数据适配器.适配器需实现Selectable接口
 * 实现的功能为:
 * 1:recyclerView的下拉刷新,上拉加载,
 * 2:顶部以及,底部的控件自由添加,删除,中间任一位置控件添加,此为确保RecyclerView数据一致性.比如新闻类应用.可能为了广告,为了某些提示条目,还需要去适合到逻辑Adapter内.导致条目很难看.
 * 3:Adapter的条目选择功能.
 * 4:类ListView的Divide封装
 *
 * 待优化/注意事件:
 *  1:当使用addDynamicView功能时,设置OnItemClickListener事件返回的position为插入的顺移的位置,
 *  比如1,9插入两个元素,当点击子元素为10位置元素时,将返回12,这时候如果想获取子条目位置,可以使用#getItemPosition方法.
 *  具体原因为,1 9 位置各插入一个条目,此时,点击第10个位置条目,真实子Adapter条目位置为8,取得8,但很难根据8还原为10.还原方式为while(0->8) !isDynamicItem()++ 效率很低.
 *  故此.只传回子类,也使用原始Position,使用时,调用DragRecyclerView的getItemPosition方法获取具体子条目位置
 *
 *  2:addDynamicView此方法,有一个问题,暂时未找到原因:如果谁清楚,请帮助解决一下,所以不用notifyItemInserted改用notifyDataSetChanged,性能差一点,但不会报错.
 // java.lang.IllegalArgumentException: Called removeDetachedView withBinary a view which is not flagged as tmp detached.ViewHolder{3c6be8ee position=17 id=-1, oldPos=-1, pLpos:-1}
 *
 *
 * 以上.2016/9/24
 */
public class PullToRefreshRecyclerView extends PullToRefreshLayout<RecyclerView> implements IRecyclerView{
    public static final int END_NONE=0x00;
    public static final int END_NORMAL=0x01;
    public static final int END_REFRESHING=0x02;

    public static final int CLICK=0x00;
    public static final int SINGLE_SELECT =0x01;
    public static final int MULTI_SELECT =0x02;
    public static final int RECTANGLE_SELECT =0x03;
    private static final String TAG = "PullToRefreshRecyclerView";


    @IntDef(value={CLICK, SINGLE_SELECT, MULTI_SELECT, RECTANGLE_SELECT})
    public @interface SelectMode {
    }

    private final SelectAdapter adapter;
    private final SimpleItemDecoration itemDecoration;
    private final RefreshFrameFooter refreshFooter;
    private OnPullFooterToRefreshListener listener;
    private DynamicAdapterDataObserve dataObserve=null;
    private int refreshState;

    public PullToRefreshRecyclerView(Context context) {
        this(context,null,0);
        addTargetView();
        setRefreshHeader(refreshHeader);
        targetView.addItemDecoration(itemDecoration);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        refreshState = END_NORMAL;
        adapter = new SelectAdapter(null);
        adapter.setHasStableIds(true);
        itemDecoration=new SimpleItemDecoration();
        refreshFooter = new RefreshFrameFooter(context, this);
        initFooterViewByMode(getRefreshMode());
        setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshRecyclerView);
        setListDivide(a.getDrawable(R.styleable.PullToRefreshRecyclerView_pv_listDivide));
        setListDivideHeight(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_listDivideHeight, 0f));
        setDivideHorizontalPadding(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_divideHorizontalPadding, 0));
        setDivideVerticalPadding(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_divideVerticalPadding, 0));
        setSelectModeInner(a.getInt(R.styleable.PullToRefreshRecyclerView_pv_choiceMode, CLICK));
        setSelectMaxCount(a.getInteger(R.styleable.PullToRefreshRecyclerView_pv_choiceMaxCount,SelectAdapter.MAX_COUNT));
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        if(0==getChildCount()){
            super.onFinishInflate();
        } else {
            for(int i=0;i<getChildCount();){
                View childView = getChildAt(i);
                removeViewAt(0);
                PullToRefreshRecyclerView.LayoutParams layoutParams= (LayoutParams) childView.getLayoutParams();
                if(LayoutParams.ITEM_HEADER==layoutParams.itemType){
                    adapter.addHeaderView(childView);
                } else if(LayoutParams.ITEM_FOOTER==layoutParams.itemType){
                    adapter.addFooterView(childView);
                }
            }
            addTargetView();
            setRefreshHeader(refreshHeader);
        }
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

    public void showHeaderViewDivide(boolean show){
        this.itemDecoration.showHeaderDecoration(show);
        this.targetView.invalidateItemDecorations();
    }

    public void showFooterViewDivide(boolean show){
        this.itemDecoration.showFooterDecoration(show);
        this.targetView.invalidateItemDecorations();
    }

    public void setSelectMode(@SelectMode int mode){
        setSelectModeInner(mode);
    }

    private void setSelectModeInner(int mode) {
        adapter.setSelectMode(mode);
        invalidate();
    }

    /**
     * 设置可选择条目最大数,仅针对MULTI_SELECT 有效
     * @param count
     */
    public void setSelectMaxCount(int count) {
        this.adapter.setSelectMaxCount(count);
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
    public RecyclerView.LayoutManager getLayoutManager() {
        return this.targetView.getLayoutManager();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        if(MeasureSpec.UNSPECIFIED==widthMode){
            measureWidth=targetView.getMeasuredWidth();
        }
        if(MeasureSpec.UNSPECIFIED==heightMode){
            measureHeight=targetView.getMeasuredHeight();
        }
        setMeasuredDimension(measureWidth,measureHeight);
    }

    @Override
    public int getHeaderViewCount() {
        return this.adapter.getHeaderViewCount();
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
        adapter.removeDynamicView(view);
        itemDecoration.setHeaderCount(getHeaderViewCount());
    }

    @Override
    public void removeHeaderView(int index){
        checkIndexInBounds(index, getHeaderViewCount());
        adapter.removeHeaderView(index);
        itemDecoration.setHeaderCount(getHeaderViewCount());
    }


    @Override
    public int getFooterViewCount() {
        return adapter.getFooterViewCount();
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
        checkIndexInBounds(index, getFooterViewCount());
        adapter.removeFooterView(index);
        itemDecoration.setFooterCount(getFooterViewCount());
    }


    public void addDynamicView(View view,int position){
        if(null!=view){
            adapter.addDynamicView(view,position);
        }
    }

    public void removeDynamicView(View view){
        if(null!=view){
            adapter.removeDynamicView(view);
        }
    }

    public void itemRangeGlobalRemoved(int positionStart,int itemCount){
        adapter.itemRangeGlobalRemoved(positionStart, itemCount);
    }

    @Override
    public RecyclerView.ItemAnimator getItemAnimator() {
        return this.targetView.getItemAnimator();
    }

    @Override
    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.targetView.addOnScrollListener(listener);
    }

    @Override
    public void removeOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.targetView.removeOnScrollListener(listener);
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
        RecyclerView.Adapter originalAdapter = this.adapter.getAdapter();
        if(null!=originalAdapter){
            if(null!=dataObserve){
                originalAdapter.unregisterAdapterDataObserver(dataObserve);
            }
        } else {
            this.targetView.setAdapter(this.adapter);
        }
        this.adapter.setAdapter(adapter);
        if(null!=adapter){
            adapter.registerAdapterDataObserver(dataObserve=new DynamicAdapterDataObserve(this.adapter));
        }
    }

    public RecyclerView.Adapter getOriginalAdapter(){
        return this.adapter;
    }

    public RecyclerView.Adapter getAdapter(){
        return this.adapter.getAdapter();
    }

    public void setRefreshFooterState(@RefreshFrameFooter.RefreshState int state){
        refreshFooter.setRefreshState(state);
    }

    @Override
    public void setRefreshMode(RefreshMode mode) {
        super.setRefreshMode(mode);
        initFooterViewByMode(mode);
    }

    /**
     * 滚动到指定位置
     * @param position
     */
    public void scrollToPosition(int position){
        RecyclerView.LayoutManager layoutManager = targetView.getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager){
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastItem = linearLayoutManager.findLastVisibleItemPosition();
            if (position <= firstItem ){
                targetView.scrollToPosition(position);
            }else if ( position <= lastItem ){
                int top = targetView.getChildAt(position - firstItem).getTop();
                targetView.scrollBy(0, top);
            }else{
                targetView.scrollToPosition(position);
            }
        }
    }


    private void initFooterViewByMode(RefreshMode mode) {
        if(null==adapter) return;
        View footerView = refreshFooter.getFooterView();
        if(mode.enableFooter()){
            refreshState = END_NORMAL;
            adapter.addRefreshFooterView(footerView);
            refreshFooter.setRefreshState(RefreshFrameFooter.FRAME_LOAD);
            scrollStateChanged(RecyclerView.SCROLL_STATE_IDLE);
        } else {
            refreshState = END_NONE;
            adapter.removeRefreshFooterView(footerView);
        }
        itemDecoration.setFooterCount(getFooterViewCount());
    }

    /**
     * 查找 header/footer view
     * @param id
     * @return
     */
    public View findAdapterView(@IdRes int id){
        View findView = findViewById(id);
        if(null==findView){
            findView=adapter.findRefreshView(id);
        }
        if(null==findView){
            findView=adapter.findDynamicView(id);
        }
        return findView;
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
     * 获得子条目的位置
     *
     * @param position
     * @return
     */
    public int getItemPosition(int position) {
        return position - adapter.getStartIndex(position);
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

    /**
     * get first visible position
     * @return last visible position
     */
    public int getFirstVisiblePosition() {
        int lastVisibleItemPosition;
        RecyclerView.LayoutManager layoutManager = targetView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] spanCount = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(spanCount);
            lastVisibleItemPosition = spanCount[0];
            for (int value : spanCount) {
                if (value > lastVisibleItemPosition) {
                    lastVisibleItemPosition = value;
                }
            }
        } else {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
        return lastVisibleItemPosition;
    }

    @Override
    public void autoRefreshing(final boolean anim) {
        scrollToPosition(0);//执行自动滚动时,自动将条目滑到0,这里自动刷新之所以post事件,是因为同时执行.scroll事件会被屏蔽.导致失效
        post(new Runnable() {
            @Override
            public void run() {
                PullToRefreshRecyclerView.super.autoRefreshing(anim);
            }
        });
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

    public void setSingleSelectPosition(int position){
        this.adapter.setSingleSelectPosition(position);
    }


    public void setMultiSelectItems(List<Integer> items){
        this.adapter.setMultiSelectItems(items);
    }

    public void setRectangleSelectPosition(int start,int end){
        this.adapter.setRectangleSelectPosition(start,end);
    }

    /*
     * 设置单选选择监听
     *
     * @param singleSelectListener
     */
    public void setOnSingleSelectListener(OnSingleSelectListener singleSelectListener) {
        adapter.setOnSingleSelectListener(singleSelectListener);
    }

    public int getSingleSelectPosition(){
        return this.adapter.getSingleSelectPosition();
    }


    public List<Integer> getMultiSelectItems(){
        return this.adapter.getMultiSelectItems();
    }


    public Range<Integer> getRectangleSelectPosition(){
        return this.adapter.getRectangleSelectPosition();
    }

    /*
    * 设置多选选择监听
    *
    * @param singleSelectListener
    */
    public void setOnMultiSelectListener(OnMultiSelectListener multiSelectListener) {
        adapter.setOnMultiSelectListener(multiSelectListener);
    }

    /*
    * 设置截取选择监听
    *
    * @param singleSelectListener
    */
    public void setOnRectangleSelectListener(OnRectangleSelectListener rectangleSelectListener) {
        adapter.setOnRectangleSelectListener(rectangleSelectListener);
    }

    /**
     * set bottom refresh listener
     */
    public interface OnPullFooterToRefreshListener {
        void onRefresh();
    }

    /**
     * 选择监听器
     */
    public interface OnSingleSelectListener {
        void onSingleSelect(View v, int newPosition, int oldPosition);
    }

    public interface OnMultiSelectListener{
        void onMultiSelect(View v, ArrayList<Integer> selectPositions,int lastSelectCount,int maxCount);
    }

    public interface OnRectangleSelectListener{
        void onRectangleSelect(int startPosition, int endPosition);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new PullToRefreshRecyclerView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PullToRefreshRecyclerView.LayoutParams(getContext(),attrs);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams{
        public static final int ITEM_HEADER=0x00;
        public static final int ITEM_FOOTER=0x01;
        public static final int ITEM_NONE=0x02;
        public int itemType;
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PullToRefreshRecyclerView);
            itemType = a.getInt(R.styleable.PullToRefreshRecyclerView_pv_adapterView, ITEM_HEADER);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }



}
