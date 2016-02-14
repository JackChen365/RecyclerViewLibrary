package com.ldzs.recyclerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.text.NumberFormat;

/**
 * 自定义进度显示控件
 *
 * @author momo
 * @Date 2015/2/22 自定义三种旋转模式 1:设置进度旋转 2:androidl旋转方式 3:固定进度旋转 常规模式: 内环/外环
 */
public class WheelView extends View {
    private static final int MAX_PROGRESS = 360;
    // 环旋转模式
    public static final int SET_PROGRESS = 0;
    public static final int L_PROGRESS = 1;
    public static final int AUTO_PROGRESS = 2;
    // 自动联动模式
    private static final int OUT_CONTOUR = 0;
    private static final int INNER_CONTOUR = 1;
    private static final int ALL_CONTOUR = 2;
    private static final int RVS_CONTOUR = 3;

    // 内环绘制模式
    private static final int CONTOUR_PADDING = 0;
    private static final int CONTOUR_WIDTH = 1;

    // 绘制文字模式
    private static final int PROGRESS_TEXT = 0;
    private static final int CUSTOM_TEXT = 1;
    private static final int EMPTY_TEXT = 2;

    private int mWheelMode;
    private int mContourMode;
    private int mContourDrawMode;
    private int mTextMode;
    private int mStartDegress;// 当前起始绘制进度
    private int mCurrentDegress;// 绘制进度
    private int mOutcontourColor;
    private int mOutBundersColor;// 外圈轨迹颜色
    private int mInnercontourColor;
    private float mOutcontourWidth;
    private float mInnercontourWidth;// 为0时,填充整个内圆
    private int mLimite;
    private int mRotateAngle;
    private Paint mPaint;

    private String mText;
    private int mTextColor;
    private float mTextSize;

    private ValueAnimator mValueAnimator;


    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context) {
        this(context, null, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        setWheelMode(a.getInt(R.styleable.WheelView_wev_wheelMode, SET_PROGRESS));
        setContourMode(a.getInt(R.styleable.WheelView_wev_contourMode, OUT_CONTOUR));
        setTextMode(a.getInt(R.styleable.WheelView_wev_textMode, EMPTY_TEXT));
        setOutContourColor(a.getColor(R.styleable.WheelView_wev_outContourColor, Color.GREEN));
        setOuterBoundsColor(a.getColor(R.styleable.WheelView_wev_outerBoundsColor, Color.GREEN));
        setInnercontourColor(a.getColor(R.styleable.WheelView_wev_innerContourColor, Color.GREEN));
        setOutcontourWidth(a.getDimension(R.styleable.WheelView_wev_outContourWidth, dp2px(2)));
        setInnerContourSize(a.getDimension(R.styleable.WheelView_wev_innerContourSize, 0));
        setContourMode(a.getInt(R.styleable.WheelView_wev_contourMode, CONTOUR_PADDING));
        setText(a.getString(R.styleable.WheelView_wev_text));
        setTextSize(a.getDimension(R.styleable.WheelView_wev_textSize, sp2px(12)));
        setTextColor(a.getColor(R.styleable.WheelView_wev_textColor, Color.YELLOW));
        a.recycle();
    }

    public float dp2px(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    public float sp2px(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpValue, getResources().getDisplayMetrics());
    }

    /**
     * 设置旋转模式
     *
     * @param mode
     */
    public void setWheelMode(int mode) {
        this.mWheelMode = mode;
        if (AUTO_PROGRESS == mode) {
            setAutoProgress(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setProgress(Integer.valueOf(animation.getAnimatedValue().toString()));
                }
            });
        } else if (L_PROGRESS == mode) {
            setAutoProgress(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (mStartDegress == mLimite)
                        mCurrentDegress += 6;
                    if (mCurrentDegress >= 290 || mStartDegress > mLimite) {
                        mStartDegress += 6;
                        mCurrentDegress -= 6;
                    }
                    if (mStartDegress > mLimite + 290) {
                        mLimite = mStartDegress;
                        mStartDegress = mLimite;
                        mCurrentDegress = 1;
                    }
                    mRotateAngle += 4;
                    invalidate();
                }
            });
        } else if (null != mValueAnimator) {
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator.removeAllListeners();
            mValueAnimator.cancel();
        }
    }

    /**
     * 设置环旋转模式
     *
     * @param contourMode
     */
    public void setContourMode(int contourMode) {
        this.mContourMode = contourMode;
        invalidate();
    }

    /**
     * 设置外圈环颜色
     *
     * @param color
     */
    public void setOutContourColor(int color) {
        this.mOutcontourColor = color;
        invalidate();
    }

    public void setOuterBoundsColor(int color) {
        this.mOutBundersColor = color;
        invalidate();
    }

    /**
     * 设置内圈环颜色
     *
     * @param color
     */
    public void setInnercontourColor(int color) {
        this.mInnercontourColor = color;
        invalidate();
    }

    /**
     * 设置外环宽
     *
     * @param width
     */
    public void setOutcontourWidth(float width) {
        this.mOutcontourWidth = width;
        invalidate();
    }

    /**
     * 设置内环宽
     *
     * @param width
     */
    public void setInnerContourSize(float width) {
        this.mInnercontourWidth = width;
        invalidate();
    }

    /**
     * 设置文字模式
     *
     * @param textMode
     */
    public void setTextMode(int textMode) {
        this.mTextMode = textMode;
        invalidate();
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        this.mCurrentDegress = (progress > 360) ? 360 : progress;
        invalidate();
    }

    /**
     * 获得进度比例
     *
     * @return
     */
    public float getFraction() {
        return this.mCurrentDegress * 1.0f / MAX_PROGRESS;
    }

    /**
     * 设置绘制文字
     *
     * @param text
     */
    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    /**
     * 设置绘制文字颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        this.mTextColor = color;
        invalidate();
    }

    /**
     * 设置绘制字体大小
     *
     * @param size
     */
    public void setTextSize(float size) {
        this.mTextSize = size;
        invalidate();
    }

    /**
     * 设置内环旋转模式
     *
     * @param mode
     */
    public void setInnerContourDrawMode(int mode) {
        this.mContourDrawMode = mode;
        invalidate();
    }

    private void setAutoProgress(ValueAnimator.AnimatorUpdateListener listener) {
        if (null != mValueAnimator) {
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator.removeAllListeners();
            mValueAnimator.cancel();
        }
        mValueAnimator = ObjectAnimator.ofInt(360);
        mValueAnimator.setDuration(3 * 1000);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setRepeatCount(-1);
        // 设置插入器
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.addUpdateListener(listener);
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRotateAngle = 0;
                mCurrentDegress = 0;
            }
        });
        mValueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        // 绘制文字
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        String text = null;
        switch (mTextMode) {
            case PROGRESS_TEXT:
                text = NumberFormat.getPercentInstance().format(mCurrentDegress * 1f / MAX_PROGRESS);
                break;
            case CUSTOM_TEXT:
                text = mText;
                break;
            default:
                break;
        }
        if (!TextUtils.isEmpty(text)) {
            int width = getWidth();
            int height = getHeight();
            Rect bounds = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), bounds);
            // 计算居中x,y   bounds获得字体最小绘制矩阵很奇怪,在最体100%时,宽度有问题.但平时是最合适的
            canvas.drawText(text, width / 2 - bounds.centerX(), height / 2 - bounds.centerY(), mPaint);
        }
    }

    private void drawProgress(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int degress = mCurrentDegress;
        switch (mContourMode) {
            case OUT_CONTOUR:
                // 画外圆,空心
                drawArc(canvas, Style.STROKE, 360, false, new RectF(mOutcontourWidth, mOutcontourWidth, width - mOutcontourWidth, height - mOutcontourWidth), mOutBundersColor, mOutcontourWidth);
                drawArc(canvas, Style.STROKE, degress, false, new RectF(mOutcontourWidth, mOutcontourWidth, width - mOutcontourWidth, height - mOutcontourWidth), mOutcontourColor,
                        mOutcontourWidth);
                break;
            case INNER_CONTOUR:
                // 画内圆,实心
                drawArc(canvas, Style.FILL, degress, true, getInnerRect(width, height), mInnercontourColor, mInnercontourWidth);
                break;
            case RVS_CONTOUR:
                // 内外反向
                degress = 360 - mCurrentDegress;
            case ALL_CONTOUR:
                // 画内外圆
                drawArc(canvas, Style.STROKE, 360, false, new RectF(mOutcontourWidth, mOutcontourWidth, width - mOutcontourWidth, height - mOutcontourWidth), mOutBundersColor, mOutcontourWidth);
                drawArc(canvas, Style.STROKE, mCurrentDegress, false, new RectF(mOutcontourWidth, mOutcontourWidth, width - mOutcontourWidth, height - mOutcontourWidth), mOutcontourColor,
                        mOutcontourWidth);
                drawArc(canvas, Style.FILL, degress, true, getInnerRect(width, height), mInnercontourColor, mInnercontourWidth);
                break;
            default:
                break;
        }
    }

    private RectF getInnerRect(int width, int height) {
        RectF rect = new RectF(0, 0, width, height);// 初始化为整体矩阵
        switch (mContourDrawMode) {
            case CONTOUR_WIDTH:
                // 中间+线宽
                rect = new RectF(rect.centerX() - mInnercontourWidth, rect.centerY() - mInnercontourWidth, rect.centerX() + mInnercontourWidth, rect.centerY() + mInnercontourWidth);
                break;
            case CONTOUR_PADDING:
            default:
                // 以内边距+内线宽
                rect = new RectF(rect.left + mOutcontourWidth + mInnercontourWidth, rect.top + mOutcontourWidth + mInnercontourWidth, rect.right - mOutcontourWidth - mInnercontourWidth, rect.bottom
                        - mOutcontourWidth - mInnercontourWidth);
                break;
        }
        return rect;
    }

    private void drawArc(Canvas canvas, Style style, int degress, boolean useCenter, RectF rect, int color, float width) {
        mPaint.reset();
        mPaint.setStyle(style);
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
        canvas.save();
        canvas.rotate(mRotateAngle, getWidth() / 2, getHeight() / 2);
        //l 自动旋转模式下,以变动startDegress起始位置
        int startDegress = (L_PROGRESS != mWheelMode) ? 270 : mStartDegress;
        // 绘制矩阵自动减去线宽
        canvas.drawArc(rect, startDegress, degress, useCenter, mPaint);
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        clearAnimation();
        super.onDetachedFromWindow();
    }
}
