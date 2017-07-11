package com.ldzs.recyclerlibrary.divide;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by cz on 16/1/22.
 */
public class SimpleItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "SimpleItemDecoration";
    //分隔线模式
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    public static final int GRID = 2;
    private int strokeWidth;
    private int horizontalPadding;
    private int verticalPadding;
    private int headerCount;
    private int footerCount;
    private boolean showHeader;
    private boolean showFooter;
    private Drawable drawable;
    private int divideMode;

    @IntDef(value = {HORIZONTAL, VERTICAL, GRID})
    public @interface Mode {
    }

    public SimpleItemDecoration() {
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setDivideHorizontalPadding(int padding) {
        this.horizontalPadding = padding;
    }

    public void setDivideVerticalPadding(int padding) {
        this.verticalPadding = padding;
    }

    public void showHeaderDecoration(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public void showFooterDecoration(boolean showFooter) {
        this.showFooter = showFooter;
    }

    public void setColorDrawable(int color) {
        this.drawable = new ColorDrawable(color);
    }


    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    /**
     * 设置分隔线模式
     *
     * @param mode
     */
    public void setDivideMode(@Mode int mode) {
        this.divideMode = mode;
    }

    public void setHeaderCount(int headerCount) {
        this.headerCount = headerCount;
    }

    public void setFooterCount(int footerCount) {
        this.footerCount = footerCount;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if(null==drawable) return ;
        switch (divideMode) {
            case VERTICAL:
                drawLinearVertical(c, parent, state);
                break;
            case HORIZONTAL:
                drawLinearHorizontal(c, parent, state);
                break;
            case GRID:
                drawGridVertical(c, parent,state);
                drawGridHorizontal(c, parent,state);
                break;
        }
    }


    public void drawLinearVertical(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        int itemCount = state.getItemCount();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int itemPosition = parent.getChildAdapterPosition(child);
            if (needDraw(itemCount, itemPosition)) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + strokeWidth;
                drawable.setBounds(left + verticalPadding, top, right - verticalPadding, bottom);
                drawable.draw(c);
            }
        }
    }

    public void drawLinearHorizontal(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        int itemCount = state.getItemCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int itemPosition = parent.getChildAdapterPosition(child);
            if (needDraw(itemCount, itemPosition)) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + strokeWidth;
                drawable.setBounds(left, top + horizontalPadding, right, bottom - horizontalPadding);
                drawable.draw(c);
            }
        }
    }


    public void drawGridVertical(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int strokeWidth = this.strokeWidth;
        int itemCount = state.getItemCount();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int itemPosition = layoutParams.getViewLayoutPosition();
            if(needDraw(itemCount,itemPosition)){
                //绘左侧
                int left = child.getLeft() - layoutParams.leftMargin - strokeWidth;
                int right = child.getLeft() - layoutParams.leftMargin;
                int top = child.getTop() + layoutParams.topMargin-strokeWidth;
                int bottom = child.getBottom() + layoutParams.bottomMargin+strokeWidth;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
                //绘右侧
                left = child.getRight() + layoutParams.rightMargin;
                right = child.getRight() + layoutParams.rightMargin+strokeWidth;
                top = child.getTop() + layoutParams.topMargin-strokeWidth;
                bottom = child.getBottom() + layoutParams.bottomMargin+strokeWidth;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
            }
        }
    }

    public void drawGridHorizontal(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int strokeWidth = this.strokeWidth;
        final int childCount = parent.getChildCount();
        int itemCount = state.getItemCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int itemPosition = layoutParams.getViewLayoutPosition();
            if(needDraw(itemCount,itemPosition)) {
                //绘上边
                int left = child.getLeft() ;
                int right = child.getRight();
                int top = child.getTop() - layoutParams.topMargin - strokeWidth;
                int bottom = child.getTop() - layoutParams.topMargin;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
                //绘下边
                left = child.getLeft();
                right = child.getRight();
                top = child.getBottom() + layoutParams.bottomMargin;
                bottom = child.getBottom() + layoutParams.bottomMargin + strokeWidth;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
            }
        }
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int strokeWidth = this.strokeWidth;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        int itemCount = state.getItemCount();
        int itemPosition = layoutParams.getViewLayoutPosition();
        if (!needDraw(itemCount, itemPosition)) {
            outRect.set(0, 0, 0, 0);
        } else {
            switch (divideMode) {
                case VERTICAL:
                    outRect.set(0, 0, 0, strokeWidth);
                    break;
                case HORIZONTAL:
                    outRect.set(0, 0, strokeWidth, 0);
                    break;
                case GRID:
                    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                        GridLayoutManager.SpanSizeLookup sizeLookup = gridLayoutManager.getSpanSizeLookup();
                        int spanSize = sizeLookup.getSpanSize(itemPosition);
                        int spanCount = gridLayoutManager.getSpanCount();
                        if (spanSize == spanCount) {
                            outRect.set(0, 0, 0, 0);
                            return;
                        }
                    }
                    outRect.set(strokeWidth, strokeWidth, strokeWidth, strokeWidth);
                    break;
            }
        }
    }

    /**
     * 是否需要绘制
     *
     * @param itemCount
     * @param itemPosition
     * @return
     */
    private boolean needDraw(int itemCount, int itemPosition) {
        boolean result = null!=drawable;
        if (headerCount > itemPosition) {
            result = showHeader;
        } else if (footerCount >= itemCount - itemPosition) {
            result = showFooter;
        }
        Log.i(TAG,"headerCount:"+headerCount+" footerCount:"+footerCount+" itemPosition:"+itemPosition+" result:"+result);
        return result;
    }

}
