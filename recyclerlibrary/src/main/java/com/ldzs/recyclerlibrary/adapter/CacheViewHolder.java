package com.ldzs.recyclerlibrary.adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 数据Holder对象
 */
public class CacheViewHolder extends BaseViewHolder {
    private final SparseArray<View> cacheViews;

    public CacheViewHolder(View itemView) {
        super(itemView);
        this.cacheViews=new SparseArray<>();
        cacheView(itemView);
    }

    private void cacheView(View itemView) {
        if(itemView instanceof ViewGroup){
            ViewGroup layout = (ViewGroup) itemView;
            this.cacheViews.append(layout.getId(),layout);
            for(int i=0;i<layout.getChildCount();i++){
                View childView = layout.getChildAt(i);
                if(childView instanceof ViewGroup){
                    cacheView(childView);
                } else {
                    this.cacheViews.append(childView.getId(),childView);
                }
            }
        } else {
            this.cacheViews.append(itemView.getId(),itemView);
        }
    }

    public View view(@IdRes int id){
        return this.cacheViews.get(id);
    }

    public TextView textView(@IdRes int id){
        TextView textView=null;
        View findView=this.cacheViews.get(id);
        if(null!=findView&&findView instanceof TextView){
            textView=(TextView)findView;
        }
        return textView;
    }

    public ImageView imageView(@IdRes int id){
        ImageView imageView=null;
        View findView=this.cacheViews.get(id);
        if(null!=findView&&findView instanceof ImageView){
            imageView=(ImageView)findView;
        }
        return imageView;
    }
}