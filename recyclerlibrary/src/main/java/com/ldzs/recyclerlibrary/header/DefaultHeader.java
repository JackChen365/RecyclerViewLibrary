package com.ldzs.recyclerlibrary.header;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldzs.recyclerlibrary.R;
import com.ldzs.recyclerlibrary.WheelView;
import com.nineoldandroids.animation.ValueAnimator;


/**
 * Created by cz on 16/1/20
 * 默认刷新头
 */
public class DefaultHeader extends LinearLayout implements BaseRefreshHeader {
    private static final String TAG = "DefaultHeader";
    private WheelView mWheelView;
    private TextView mStatusTextView;
    private int mState = STATE_NORMAL;

    public int mMeasuredHeight;

    public DefaultHeader(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public DefaultHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        //TODO 为了考虑横向的RecyclerView的打算
        setOrientation(VERTICAL);
        setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.listview_vertical_header, this);
        mWheelView = (WheelView) findViewById(R.id.pull_to_refresh_progress);
        mStatusTextView = (TextView) findViewById(R.id.refresh_status_textview);
        initViewHeight();
    }

    /**
     * 初始化view高度信息
     */
    private void initViewHeight() {
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        measure(w, h);
        mMeasuredHeight = getMeasuredHeight();
        layoutParams.height = 0;
        requestLayout();
    }


    @Override
    public boolean isCurrentState(int state) {
        return mState == state;
    }

    @Override
    public void refreshComplete() {
        postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 200);
    }

    @Override
    public boolean isRefreshing() {
        return mState == STATE_REFRESHING;
    }

    public void setRefreshHeight(int height) {
        if (height < 0) height = 0;
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) getLayoutParams();
        lp.height = height;
        setLayoutParams(lp);
    }


    @Override
    public int getOriginalHeight() {
        return mMeasuredHeight;
    }

    public int getRefreshHeight() {
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) getLayoutParams();
        return lp.height;
    }


    public void reset() {
        smoothScrollTo(0);
        setState(STATE_NORMAL);
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getRefreshHeight(), destHeight);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setRefreshHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }


    /**
     * 设置状态桢
     *
     * @param state
     */
    public void setState(@State int state) {
        if (state == mState) return;
        switch (state) {
            case STATE_NORMAL:
                mStatusTextView.setText(R.string.listview_header_hint_normal);
                break;
            case STATE_RELEASE_TO_REFRESH:
                if (mState != STATE_RELEASE_TO_REFRESH) {
                    mStatusTextView.setText(R.string.listview_header_hint_release);
                }
                break;
            case STATE_REFRESHING:
                mStatusTextView.setText(R.string.refreshing);
                break;
            case STATE_COMPLETE:
                mStatusTextView.setText(R.string.refresh_done);
                break;
            default:
        }
        mState = state;
    }

    @Override
    public void pullToRefresh(float offsetValue) {
        //添加现有高度值
        Log.e(TAG, "offset:" + offsetValue + " height:" + getRefreshHeight());
        if (0 < getRefreshHeight() || 0 < offsetValue) {
            setRefreshHeight((int) (offsetValue));
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (getRefreshHeight() >= mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_NORMAL);
                }
            }
            float angle = Math.max(0f, offsetValue / mMeasuredHeight * 360f);
            mWheelView.setProgress((int) angle);
        }
    }


    /**
     * 设置正在刷新中
     */
    @Override
    public void setRefreshing() {
        if (mState < STATE_REFRESHING && getRefreshHeight() >= mMeasuredHeight) {
            setState(STATE_REFRESHING);
        }
    }

    @Override
    public void releaseToRefresh() {
        int destHeight = 0;
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);
    }
}
