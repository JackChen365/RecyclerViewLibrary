package com.ldzs.recyclerlibrary.adapter.drag;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter;
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter2;
import com.ldzs.recyclerlibrary.adapter.BaseViewHolder;
import com.ldzs.recyclerlibrary.callback.GridSpanCallback;
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;
import com.ldzs.recyclerlibrary.callback.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一个可以在RecyclerView 己有的Adapter,添加任一的其他条目的Adapter对象
 * 使用装饰设计模式,无使用限制
 * like :
 * 1|2|3|
 * --4--  //0 start 0 -1
 * 5|6|7|
 * --8--  //1  start 1
 * 9|10|11|
 *
 * item1
 * item2
 * --欢迎来到信用钱包--
 * item3
 *
 * Model item==2
 * <p>
 * 难点在于,如果将随机位置添加的自定义view的位置动态计算,不影响被包装Adapter
 *
 * @param
 */
public class DynamicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "DynamicAdapter";
    protected final int START_POSITION = 1024;//超出其他Header/Footer范围,避免混乱
    protected final SparseIntArray fullItemTypes;
    protected final SparseArray<View> fullViews;
    private int headerViewCount;
    protected int[] itemPositions;
    protected RecyclerView.Adapter adapter;
    private int itemViewCount;
    private OnItemLongClickListener longItemListener;
    private OnItemClickListener itemClickListener;


    /**
     * @param adapter 包装数据适配器
     */
    public DynamicAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        itemPositions = new int[0];
        fullItemTypes = new SparseIntArray();
        fullViews = new SparseArray<>();
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        this.adapter=adapter;
        notifyDataSetChanged();
    }

    /**
     * 条目范围插入
     *
     * @param positionStart
     * @param itemCount
     */
    public void itemRangeInsert(int positionStart, int itemCount) {
        //重置所有移除范围内的动态条信息
        ArrayList<Integer> itemPositionLists = new ArrayList<>();
        SparseIntArray newFullItems=new SparseIntArray();
        int length = itemPositions.length;
        for(int i=0;i<length;i++){
            int position = itemPositions[i];
            int newPosition = position;
            //范围外条目,整体后退
            if(positionStart<=position){
                newPosition=position+itemCount;
            }
            newFullItems.put(newPosition,fullItemTypes.get(position));
            itemPositionLists.add(newPosition);
        }
        fullItemTypes.clear();
        for(int i=0;i<newFullItems.size();i++){
            fullItemTypes.append(newFullItems.keyAt(i),newFullItems.valueAt(i));
        }
        int size = itemPositionLists.size();
        Log.e(TAG,"position:"+Arrays.toString(itemPositions)+" itemPositions:"+itemPositionLists+" positionStart:"+positionStart);
        itemPositions = new int[size];
        for (int i = 0; i < size; i++) {
            itemPositions[i] = itemPositionLists.get(i);
        }
        notifyItemRangeInserted(positionStart, itemCount);
    }

    /**
     * 全局范围内条目删除
     * 范围内删除所有条目,包括自定义添加条目
     * @param positionStart
     * @param removeCount
     */
    public void itemRangeGlobalRemoved(int positionStart, int removeCount) {
        //重置所有移除范围内的动态条信息
        int startIndex = getStartIndex(positionStart);
        positionStart+=startIndex;
        //计算出最后移除范围
        int index=0;
        int positionEnd=positionStart;
        while(index<removeCount){
            if(!isDynamicItem(positionEnd++))index++;
        }
        removeCount=positionEnd-positionStart;

        List<Integer> positionList=new ArrayList<>();
        for(int i=0;i<itemPositions.length;positionList.add(itemPositions[i++]));

        for(int position=positionStart;position<positionEnd;position++){
            if(isDynamicItem(position)) {
                int value = fullItemTypes.valueAt(startIndex);
                fullViews.remove(value);
                fullItemTypes.removeAt(startIndex);
                positionList.remove(startIndex);
            }
        }
        Log.e(TAG,"array:"+positionList);
        int size = fullItemTypes.size();
        for(int i=startIndex;i<size;i++){
            Integer position = positionList.get(i);
            int newPosition=position-removeCount;
            int value = fullItemTypes.get(position);
            fullItemTypes.delete(position);
            fullItemTypes.put(newPosition,value);
            positionList.set(i,newPosition);
        }
        itemPositions=new int[size];
        for(int i=0;i<size;itemPositions[i]=positionList.get(i),i++);
        if(0<removeCount){
            notifyItemRangeRemoved(positionStart,removeCount);
        }
        Log.e(TAG,"position:"+Arrays.toString(itemPositions)+" positionStart:"+positionStart+" positionEnd:"+positionEnd+" startIndex:"+startIndex+" realCount:"+getRealItemCount()+" itemCount:"+removeCount);
    }

    /**
     * 条目范围内删除,用户条目,不包含自定义插入条目
     * like remove 0 from 8
     *  --0--
     *  1 2 3
     *  --4--
     *  5 6
     *  --7--
     *  8 9 10
     *  11 12 13
     * result:
     *  --0--
     *  (1 2 3)
     *  --4--
     *  (5 6)
     *  --7--
     *  (8 9 10)
     *  11 12 13
     *
     *  难度最大的地方在于.动态移除.以及动态插件条目信息更新
     *  1:先计算出,当前移除位置,到指定需要移除位置条目数的最终位置.上面示例是从从0开始,移除8个,那么最终位置为11
     *  2:范围移除.但是中间有自定义条目插入.所以其中移除还是分段移除.并且更新信息.这里需要算一步,更新信息,再删一步.
     *      如(1,2,3)这一段.起始位置为1(--0--),需要删除3个,会记录删除偏移量3,然后检测到(--4--),动态更新(--4--)条目信息.
     *      将其往前移3,删除1-3元素后,(--4--)的起始变为1,后续逻辑相同.
     *  3:任何范围外的超出的,都会直接减去最终的startOffset值,形成插入信息一致更新.
     *
     *  为实现此效果.中间修改代码很多次.主要是没想通具体逻辑.就是第二步的逻辑.只有达到此效果.才是真正的动态化.
     * @param positionStart
     * @param itemCount
     */
    public void itemRangeRemoved(int positionStart, int itemCount) {
        //重置所有移除范围内的动态条信息
        int startIndex = getStartIndex(positionStart);
        //计算出最后移除范围
        int index=0;
        int positionEnd=positionStart;
        while(index<itemCount){
            if(!isDynamicItem(positionEnd++))index++;
        }
        Log.e(TAG,"itemCount:"+itemCount+" adapterCount:"+adapter.getItemCount()+" start:"+positionStart+" end:"+positionEnd);

        int length = itemPositions.length;
        final int[] finalArray=new int[length];
        System.arraycopy(itemPositions,0,finalArray,0,length);

        int start=0,startOffset=0,totalOffset=0;
        for(int position=positionStart;position<positionEnd;position++){
            boolean isDefaultItem = RecyclerView.NO_POSITION == findPosition(finalArray, position);
            if(isDefaultItem) {
                if(0==startOffset) start=position-totalOffset;
                startOffset++;
                totalOffset++;
            }
            //判断为插入条目,或者最后一个时,执行偏移运算
            if(!isDefaultItem||positionEnd-1==position){
                for(int i=startIndex;i<length;i++){
                    int itemPosition = itemPositions[i];
                    itemPositions[i]-=startOffset;
                    int value = fullItemTypes.get(itemPosition);
                    fullItemTypes.delete(itemPosition);
                    fullItemTypes.put(itemPositions[i],value);
                }
                Log.e(TAG,"start:"+start+" offset:"+startOffset+" index:"+startIndex);
                notifyItemRangeRemoved(start,startOffset);
                startIndex++;
                startOffset=0;
            }
        }
        Log.e(TAG,"position:"+Arrays.toString(itemPositions)+" positionStart:"+positionStart+" startIndex:"+startIndex);
        //之前实现方式,存在不足,但思考了很久.不舍得删了.
        //这里运算比较复杂,需要时时更新所有ItemPosition以及fullItemType信息.整个动态化逻辑里,这里最复杂.
//        for(int i=0;i<length;i++){
//            final int position = itemPositions[i];
//            if(position>positionStart&&position<=positionEnd){
//                //置换ItemPosition位置
//                int deleteStart=-1,deleteCount=0;
//                for(index=start;index<position;index++){
//                    //移除中间条目.这里不能采用rangeRemove,因为条目插入中间.采用这个方法.会直接导致中间条目无法保留
//                    if(RecyclerView.NO_POSITION==findPosition(finalArray,index)){
//                        if(-1==deleteStart) deleteStart=index-startOffset;
//                        deleteCount++;
//                        startOffset++;
//                    }
//                }
//                start=position;
//                //范围内条目,整体前进
//                int newPosition=position-startOffset;
//                int value = fullItemTypes.get(position);
//                fullItemTypes.delete(position);
//                fullItemTypes.put(newPosition,value);
//                itemPositions[i]=newPosition;
//                notifyItemRangeRemoved(deleteStart,deleteCount);
//            } else if(position>positionEnd){
//                //大于移除范围条目
//                int newPosition=position-startOffset;
//                int value = fullItemTypes.get(position);
//                fullItemTypes.delete(position);
//                fullItemTypes.put(newPosition,value);
//                itemPositions[i]=newPosition;
//            }
//        }
    }


    /**
     * 添加一个自定义view到末尾
     *
     * @param layout
     */
    public void addDynamicView(Context context,@LayoutRes int layout) {
        View view = View.inflate(context, layout, null);
        addDynamicView(view, getRealItemCount());
    }

    public void addHeaderView(View view){
        addDynamicView(view,getHeaderViewCount());
    }

    public int getHeaderViewCount(){
        return headerViewCount;
    }

    /**
     * 添加一个自定义view到指定位置
     *
     * @param position
     */
    public void addDynamicView(View view, int position) {
        if (RecyclerView.NO_POSITION != findPosition(position)) return;//己存在添加位置,则不添加
        int length = itemPositions.length;
        int[] newPositions = new int[length + 1];
        newPositions[length] = position;
        System.arraycopy(itemPositions, 0, newPositions, 0, itemPositions.length);
        Arrays.sort(newPositions);
        itemPositions = newPositions;
        int viewType = START_POSITION + itemViewCount++;
        fullItemTypes.put(position, viewType);
        fullViews.put(viewType, view);

        updateHeaderViewCount();


        //当只有一个时,通知插入,这里有一个问题,暂时未找到原因:如果谁清楚,请帮助解决一下,所以不用notifyItemInserted改用notifyDataSetChanged,性能差一点,但不会报错.
        // java.lang.IllegalArgumentException: Called removeDetachedView withBinary a view which is not flagged as tmp detached.ViewHolder{3c6be8ee position=17 id=-1, oldPos=-1, pLpos:-1}
//        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    /**
     * update header view count
     */
    private void updateHeaderViewCount() {
        int length;
        headerViewCount=0;
        length=itemPositions.length;
        int index=0;
        while(index<length&&index==itemPositions[index]){
            index++;
            headerViewCount++;
        }
    }

    /**
     * 由子类复写.返回装饰底部控件个数
     * @return
     */
    public int getFooterViewCount(){
        return 0;
    }

    /**
     * 移除指定view
     *
     * @param view
     */
    public void removeDynamicView(View view) {
        int index = fullViews.indexOfValue(view);
        if(-1<index){
            int viewType = fullViews.keyAt(index);
            index=fullItemTypes.indexOfValue(viewType);
            if(-1<index){
                int position = fullItemTypes.keyAt(index);
                removeDynamicView(position);
            }
        }
    }

    public int indexOfDynamicView(View view){
        return fullViews.indexOfValue(view);
    }


    /**
     * 移除指定位置view
     *
     * @param removePosition
     */
    public void removeDynamicView(int removePosition) {
        if (isDynamicItem(removePosition)) {
            int itemType = getItemViewType(removePosition);
            fullViews.delete(itemType);
            int length = itemPositions.length;
            int[] newPositions = new int[length - 1];
            SparseIntArray newFullItems=new SparseIntArray();
            for (int i = 0, k = 0; i < length; i++) {
                int position = itemPositions[i];
                if (removePosition != position) {
                    int newPosition=position;
                    if(removePosition<position){
                        newPosition=position-1;
                    }
                    newPositions[k++] = newPosition;
                    newFullItems.put(newPosition,fullItemTypes.get(position));
                }
            }
            fullItemTypes.clear();
            for(int i=0;i<newFullItems.size();i++){
                fullItemTypes.append(newFullItems.keyAt(i),newFullItems.valueAt(i));
            }
            itemPositions = newPositions;
            updateHeaderViewCount();
            notifyItemRemoved(removePosition);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = ((GridLayoutManager) manager);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int spanCount=1;
                    if(null!=adapter&&adapter instanceof GridSpanCallback){
                        spanCount=((GridSpanCallback)adapter).getSpanSize(gridLayoutManager,position-getHeaderViewCount());
                    }
                    return isDynamicItem(position)||isFullItem(position) ? gridLayoutManager.getSpanCount() : spanCount;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (null!=layoutParams && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams && ((isDynamicItem(position))||isFullItem(position))) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            p.setFullSpan(true);
        }
    }

    /**
     * 由子类实现,条目是否铺满
     * @see #onViewAttachedToWindow #onAttachedToRecyclerView
     * @param position
     * @return
     */
    protected boolean isFullItem(int position){
        return false;
    }

    /**
     * 判断当前显示是否为自定义铺满条目
     *
     * @param position
     * @return
     */
    private boolean isDynamicItem(int position) {
        return RecyclerView.NO_POSITION != findPosition(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = fullViews.get(viewType);
        if (null != view) {
            holder = new BaseViewHolder(view);
        } else if (null != adapter) {
            holder = adapter.onCreateViewHolder(parent, viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (!isDynamicItem(position) && null != adapter) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //这里看起来很矛盾,其实是必然的设计,因为position可以往下减为真实的子Adapter的位置,但是往上,无法逆反,为实现drag条目转换功能,所以只能传递真实位置回具体条目
                    int itemPosition = holder.getAdapterPosition();
                    int realPosition = itemPosition - getStartIndex(itemPosition);
                    if (onItemClick(v, realPosition) && null != itemClickListener) {
                        itemClickListener.onItemClick(v, itemPosition);
                    }
                }
            });
            int startIndex = getStartIndex(position);
            adapter.onBindViewHolder(holder, position - startIndex);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        int index = findPosition(position);
        if (RecyclerView.NO_POSITION != index) {
            viewType = fullItemTypes.get(position);
        } else if (null != adapter) {
            int startIndex = getStartIndex(position);
            viewType = adapter.getItemViewType(position - startIndex);
        }
        return viewType;
    }

    public View findDynamicView(@IdRes int id){
        View findView=null;
        for(int i=0;i<fullViews.size();i++){
            View view = fullViews.valueAt(i);
            if(null!=(findView=view.findViewById(id))){
                break;
            }
        }
        return findView;
    }

    private int getRealItemCount(){
        return getItemCount()- getFooterViewCount();
    }

    @Override
    public int getItemCount() {
        int itemCount = fullViews.size();
        if (null != adapter) {
            itemCount += adapter.getItemCount();
        }
        Log.e(TAG,"Dynamic itemCount:"+itemCount+" fullView:"+fullViews.size());
        return itemCount;
    }

    /**
     * 子类点击使用
     *
     * @param v
     * @param position
     */
    protected boolean onItemClick(View v, int position) {
        return true;
    }


    /**
     * 使用二分查找法,根据firstVisiblePosition找到SelectPositions中的位置
     *
     * @return
     */
    public int getStartIndex(int position) {
        int[] positions = itemPositions;
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
        if(-1 == result){
            result=start;
        } else {
            start=result-1;
            end = positions.length - 1;
            //当position为0时,插入条目为0,1 这时候应该取得2
            while(start<end&&positions[start]+1==positions[start+1]){
                start++;
                result++;
            }
        }
        return result;
    }

    public int findPosition(int position) {
        return findPosition(itemPositions,position);
    }

    /**
     * 查找当前是否有返回值
     * @param array
     * @param position
     * @return
     */
    public int findPosition(int[] array,int position) {
        int[] positions = array;
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
    public int getDynamicItemCount() {
        return fullViews.size();
    }

    /**
     * 设置条目长按点击事件
     *
     * @param listener
     */
    public void setOnLongItemClickListener(OnItemLongClickListener listener) {
        this.longItemListener = listener;
    }

    /**
     * 设置条目点击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
}