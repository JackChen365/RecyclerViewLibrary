package com.ldzs.recyclerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.callback.StickyCallback;
import com.ldzs.recyclerlibrary.strategy.GroupingStrategy;

/**
 * Created by Administrator on 2017/5/20.
 * 兼容PullToRefreshRecyclerView所有功能的StickyRecyclerView
 * 实现功能:
 * 1:增加任一布局的Sticky效果
 * 2:可动态配置数据的,以及更改StickyView大小
 * 3:支持布局/xml内直接写StickyView
 * 4:配合GroupingStrategy 最大化减少分组逻辑
 * {@link com.ldzs.recyclerlibrary.strategy.GroupingStrategy}
 *
 * 使用:
 * 数据适配器继承BaseViewAdapter 且实现StickyCallback接口
 * {@link StickyCallback}
 *
 * 示例:
 * 1:app/ui/sticky/Sticky1SampleActivity 演示:
 * 2:app/ui/sticky/Sticky2SampleActivity
 * 3:app/ui/sticky/Sticky3SampleActivity
 * 4:app/ui/sticky/Sticky4SampleActivity 演示GridLayoutManager的Sticky效果
 */
public class PullToRefreshStickyRecyclerView extends PullToRefreshRecyclerView  {
    private static final String TAG = "PullToRefreshStickyRecyclerView";
    private final LayoutInflater layoutInflater;
    private AdapterDataObserver observer;
    private StickyScrollListener listener;
    private View stickyView;

    public PullToRefreshStickyRecyclerView(Context context) {
        this(context,null);
    }

    public PullToRefreshStickyRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PullToRefreshStickyRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.layoutInflater=LayoutInflater.from(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshStickyRecyclerView);
        setStickyView(a.getResourceId(R.styleable.PullToRefreshStickyRecyclerView_pv_stickyView,NO_ID));
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        removeLayoutStickyView();
        super.onFinishInflate();
    }

    /**
     * 获取布局配置sticky view对象
     */
    private void removeLayoutStickyView() {
        final int childCount=getChildCount();
        if(0<childCount&&null==stickyView) {
            for(int i=0;i<childCount;i++){
                View childView = getChildAt(i);
                ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
                if(layoutParams instanceof PullToRefreshStickyRecyclerView.LayoutParams){
                    PullToRefreshStickyRecyclerView.LayoutParams stickyLayoutParams= (LayoutParams) layoutParams;
                    if(stickyLayoutParams.layoutStickyView){
                        stickyView=childView;
                        removeView(childView);
                        break;
                    }
                }
            }
        }
    }

    public void setStickyView(int resourceId) {
        if(NO_ID!=resourceId){
            setStickyView(layoutInflater.inflate(resourceId,this,false));
        }
    }


    public void setStickyView(View view){
        if(null!=this.stickyView){
            removeView(this.stickyView);
        }
        //不添加,等待setAdapter时添加,避免出现无数据显示一个空的头情况
        this.stickyView=view;
    }

    public int getItemCount(){
        int itemCount=0;
        RecyclerView.Adapter adapter = getAdapter();
        if(null!=adapter){
            itemCount=adapter.getItemCount();
        }
        return itemCount;
    }

    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        super.onLayout(b, left, top, right, bottom);
        if(null!=stickyView){
            stickyView.layout(left,0,right,stickyView.getMeasuredHeight());
        }
    }

    @Override
    public void setAdapter(final RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if(!(adapter instanceof StickyCallback)){
            throw new IllegalArgumentException("RecyclerView.Adapter must be implements StickyCallback!");
        } else if(null!=stickyView){
            removeView(stickyView);
            addView(stickyView);
            RecyclerView refreshView = getRefreshView();
            refreshView.removeOnScrollListener(listener);
            listener=new StickyScrollListener((StickyCallback) adapter);
            refreshView.addOnScrollListener(listener);
            if(null==observer){
                observer=new AdapterDataObserver((StickyCallback) adapter);
            } else {
                adapter.unregisterAdapterDataObserver(observer);
            }
            adapter.registerAdapterDataObserver(observer);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PullToRefreshStickyRecyclerView.LayoutParams(getContext(),attrs);
    }

    public static class LayoutParams extends PullToRefreshRecyclerView.LayoutParams{
        public boolean layoutStickyView;
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PullToRefreshStickyRecyclerView);
            layoutStickyView = a.getBoolean(R.styleable.PullToRefreshStickyRecyclerView_pv_layoutStickyView, false);
            a.recycle();
        }
    }

    class AdapterDataObserver extends RecyclerView.AdapterDataObserver{
        private final StickyCallback callback;

        public AdapterDataObserver(StickyCallback callback) {
            this.callback = callback;
        }


        @Override
        public void onChanged() {
            super.onChanged();
            //此处,当数据完全更新后,滑动到顶部,并更新显示头,否则会出现头与数据列不一致情况
            int firstVisiblePosition = getFirstVisiblePosition()-getHeaderViewCount();
            GroupingStrategy groupingStrategy = callback.getGroupingStrategy();
            int itemCount = getItemCount();
            int startIndex = groupingStrategy.getGroupStartIndex(firstVisiblePosition);
            if(startIndex<itemCount){
                callback.initStickyView(stickyView, startIndex);
            }
        }
    }

    class StickyScrollListener extends RecyclerView.OnScrollListener{
        private final StickyCallback callback;
        private final GroupingStrategy groupingStrategy;
        private int lastVisibleItemPosition;

        public StickyScrollListener(StickyCallback callback) {
            this.callback = callback;
            this.groupingStrategy=callback.getGroupingStrategy();
            //初始化第一个节点信息,若数据罗多,延持到滑动时,会导致初始化第一个失败
            int itemCount = getItemCount();
            if(0<itemCount){
                this.callback.initStickyView(stickyView,0);
            }
            this.lastVisibleItemPosition=RecyclerView.NO_POSITION;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = getLayoutManager();
            int spanCount=1;
            if(layoutManager instanceof GridLayoutManager){
                spanCount=((GridLayoutManager)layoutManager).getSpanCount();
            }
            int headerViewCount = getHeaderViewCount();
            int firstVisibleItemPosition = getFirstVisiblePosition();
            if(firstVisibleItemPosition<headerViewCount){
                stickyView.setVisibility(View.GONE);
            } else {
                stickyView.setVisibility(View.VISIBLE);
                int realVisibleItemPosition=firstVisibleItemPosition-headerViewCount;
                //初始化当前位置Sticky信息
                int lastRealPosition=realVisibleItemPosition+spanCount;
                for(int position=realVisibleItemPosition;position<=lastRealPosition;position++) {
                    if (lastVisibleItemPosition != firstVisibleItemPosition && groupingStrategy.isGroupIndex(position)) {
                        lastVisibleItemPosition = firstVisibleItemPosition;
                        int startIndex = groupingStrategy.getGroupStartIndex(realVisibleItemPosition);
                        if(startIndex<layoutManager.getItemCount()){
                            callback.initStickyView(stickyView, startIndex);
                        }
                        break;
                    }
                }
                stickyView.setTranslationY(0);
                //在这个范围内,找到本页内可能出现的下一个阶段的条目位置.
                int stickyPosition = findStickyPosition(realVisibleItemPosition+1, getLastVisiblePosition());
                if(RecyclerView.NO_POSITION!=stickyPosition){
                    View nextAdapterView = layoutManager.findViewByPosition(stickyPosition+headerViewCount);
                    if (null!=nextAdapterView&&nextAdapterView.getTop() < stickyView.getHeight()) {
                        stickyView.setTranslationY(nextAdapterView.getTop()-stickyView.getHeight());
                    }
                }
            }
        }

        int findStickyPosition(int position,int lastVisibleItemPosition){
            int stickyPosition=RecyclerView.NO_POSITION;
            for(int index=position;index<=lastVisibleItemPosition;index++){
                if(groupingStrategy.isGroupIndex(index)){
                    stickyPosition=index;
                    break;
                }
            }
            return stickyPosition;
        }
    }
}
