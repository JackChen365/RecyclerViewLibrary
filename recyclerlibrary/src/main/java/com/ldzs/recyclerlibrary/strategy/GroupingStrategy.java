package com.ldzs.recyclerlibrary.strategy;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ldzs.recyclerlibrary.IRecyclerAdapter;
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter2;
import com.ldzs.recyclerlibrary.callback.BinaryCondition;
import com.ldzs.recyclerlibrary.callback.Condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/5/20.
 * 分组策略
 */
public class GroupingStrategy {
    private static final String TAG="GroupingStrategy";
    private final IRecyclerAdapter adapter;
    private final List<Integer> indexItems;
    private Integer[] indexArray;
    private BinaryCondition binaryCondition;
    private Condition condition;

    public static<T> GroupingStrategy of(BaseViewAdapter<T> adapter){
        return new GroupingStrategy(adapter);
    }

    public static<T> GroupingStrategy of(BaseViewAdapter2 adapter){
        return new GroupingStrategy(adapter);
    }

    public GroupingStrategy(BaseViewAdapter2 adapter){
        this.adapter=adapter;
        this.indexItems=new ArrayList<>();
        registerAdapterDataObserver(adapter);
    }

    public GroupingStrategy(BaseViewAdapter adapter){
        this.adapter=adapter;
        this.indexItems=new ArrayList<>();
        registerAdapterDataObserver(adapter);
    }

    /**
     * 注册数据适配器数据监听,时时同步映射角标集
     * @param adapter
     */
    private void registerAdapterDataObserver(final RecyclerView.Adapter adapter) {
        //同步整个列表数据变化
        adapter.setHasStableIds(true);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                refreshIndexItems();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                refreshIndexItems();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                refreshIndexItems();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChanged() {
                super.onChanged();
                refreshIndexItems();
            }
        });
    }

    public<I> GroupingStrategy reduce(BinaryCondition<I> binaryCondition){
        this.binaryCondition =binaryCondition;
        refreshIndexItems();
        return this;
    }

    public<I> GroupingStrategy reduce(Condition<I> condition){
        this.condition =condition;
        refreshIndexItems();
        return this;
    }

    public boolean isGroupIndex(int position){
        return 0<=Arrays.binarySearch(getIndexArray(),position);
    }
    /**
     * 使用二分查找法,根据position找到数位中该段位的位置
     * @return
     */
    public int getGroupStartIndex(int position){
        int start = 0, end = indexItems.size();
        while (end - start > 1) {
            // 中间位置
            int middle = (start + end) >> 1;
            // 中值
            int middleValue = indexItems.get(middle);
            if (position > middleValue) {
                start = middle;
            } else if (position < middleValue) {
                end = middle;
            } else {
                start = middle;
                break;
            }
        }
        int index=0;
        if(-1<start&&start<indexItems.size()){
            index=indexItems.get(start);
        }
        return index;
    }

    /**
     * 刷新定位角标位置
     */
    void refreshIndexItems(){
        indexArray=null;
        if(null== binaryCondition&&null==condition){
            throw new NullPointerException("condition is null!");
        } else if(null!=binaryCondition){
            binaryConditionRefresh(adapter.getItems());
        } else if(null!=condition){
            conditionRefresh(adapter.getItems());
        }
    }

    Integer[] getIndexArray(){
        if(null==indexArray){
            indexArray=indexItems.toArray(new Integer[indexItems.size()]);
        }
        return indexArray;
    }

    void binaryConditionRefresh(List items){
        indexItems.clear();
        Object lastItem=null;
        for(int index=0;index<items.size();index++){
            Object item = items.get(index);
            if(null==lastItem|| binaryCondition.apply(lastItem,item)){
                indexItems.add(index);
            }
            lastItem=item;
        }
    }

    void conditionRefresh(List items){
        indexItems.clear();
        for(int index=0;index<items.size();index++){
            Object item = items.get(index);
            if(condition.apply(item)){
                indexItems.add(index);
            }
        }
    }
}
