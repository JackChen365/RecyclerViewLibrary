package com.ldzs.recyclerlibrary.divide;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by cz on 16/1/22.
 */
public class SimpleItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "SimpleItemDecoration";
    //分隔线模式
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int GRID = 2;
    private final RecyclerView mRecyclerView;
    private int mStrokeWidth;
    private int mHorizontalPadding;
    private int mVerticalPadding;
    private int mHeaderCount;
    private int mFooterCount;
    private boolean mShowHeader;
    private boolean mShowFooter;
    private Drawable mDrawable;
    private int mDivideMode;

    @IntDef(value = {HORIZONTAL, VERTICAL, GRID})
    public @interface Mode {
    }

    public SimpleItemDecoration(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.mStrokeWidth = strokeWidth;
        mRecyclerView.invalidateItemDecorations();
    }

    public void setDivideHorizontalPadding(int padding) {
        this.mHorizontalPadding = padding;
        mRecyclerView.invalidateItemDecorations();
    }

    public void setDivideVerticalPadding(int padding) {
        this.mVerticalPadding = padding;
        mRecyclerView.invalidateItemDecorations();
    }

    public void setShowHeader(boolean showHeader) {
        this.mShowHeader = showHeader;
        mRecyclerView.invalidateItemDecorations();
    }

    public void setShowFooter(boolean showFooter) {
        this.mShowFooter = showFooter;
        mRecyclerView.invalidateItemDecorations();
    }

    public void setColorDrawable(int color) {
        this.mDrawable = new ColorDrawable(color);
        mRecyclerView.invalidateItemDecorations();
    }

    public void setDrawable(@DrawableRes int res) {
        Resources resources = mRecyclerView.getResources();
        this.mDrawable = resources.getDrawable(res);
        mRecyclerView.invalidateItemDecorations();
    }

    public void setDrawable(Drawable drawable) {
        this.mDrawable = drawable;
        mRecyclerView.invalidateItemDecorations();
    }

    /**
     * 设置分隔线模式
     *
     * @param mode
     */
    public void setDivideMode(@Mode int mode) {
        this.mDivideMode = mode;
        mRecyclerView.invalidateItemDecorations();
    }

    public void setHeaderCount(int headerCount) {
        this.mHeaderCount = headerCount;
        mRecyclerView.invalidateItemDecorations();
    }

    public void setFooterCount(int footerCount) {
        this.mFooterCount = footerCount;
        mRecyclerView.invalidateItemDecorations();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        switch (mDivideMode) {
            case VERTICAL:
                drawLinearVertical(c, parent, state);
                break;
            case HORIZONTAL:
                drawLinearHorizontal(c, parent, state);
                break;
            case GRID:
                drawVertical(c, parent);
                drawHorizontal(c, parent);
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
                final int bottom = top + mStrokeWidth;
                mDrawable.setBounds(left + mVerticalPadding, top, right - mVerticalPadding, bottom);
                mDrawable.draw(c);
            }
        }
    }

    public void drawLinearHorizontal(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mStrokeWidth;
            mDrawable.setBounds(left, top + mHorizontalPadding, right, bottom - mHorizontalPadding);
            mDrawable.draw(c);
        }
    }


    public void drawVertical(Canvas c, RecyclerView parent) {
        int strokeWidth = this.mStrokeWidth;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin - strokeWidth;
            final int right = child.getRight() + params.rightMargin + strokeWidth;
            final int top = child.getBottom() + params.bottomMargin + strokeWidth;
            final int bottom = top + mDrawable.getIntrinsicHeight();
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int strokeWidth = this.mStrokeWidth;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin + strokeWidth;
            final int right = left + mDrawable.getIntrinsicWidth();
            final int top = child.getTop() - params.topMargin - strokeWidth;
            final int bottom = child.getBottom() + params.bottomMargin + strokeWidth;
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int strokeWidth = mStrokeWidth;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        int itemCount = state.getItemCount();
        int itemPosition = layoutParams.getViewLayoutPosition();
        if (needDraw(itemCount, itemPosition)) {
            switch (mDivideMode) {
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
                        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
                        int spanSize = sizeLookup.getSpanSize(position);
                        int spanCount = gridLayoutManager.getSpanCount();
                        if (spanSize == spanCount) return;   //横向占满行的不进行分隔
                    }
                    outRect.set(strokeWidth, strokeWidth, strokeWidth, strokeWidth);
                    break;
            }
        } else {
            outRect.set(0, 0, 0, 0);
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
        boolean result = true;
        if (mHeaderCount > itemPosition) {
            result = mShowHeader;
        } else if (mFooterCount >= itemCount - itemPosition) {
            result = mShowFooter;
        }
        return result;
    }

}
