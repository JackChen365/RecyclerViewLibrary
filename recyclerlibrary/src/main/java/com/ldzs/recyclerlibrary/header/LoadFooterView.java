package com.ldzs.recyclerlibrary.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ldzs.recyclerlibrary.R;


/**
 * Created by cz on 16/1/20
 * 底部加载更多头
 */
public class LoadFooterView extends RelativeLayout implements BaseRefreshFooter {
    private static final String TAG = "LoadFooterView";
    private final int FRAME_LOAD = 0;
    private final int FRAME_ERROR = 1;
    private final int FRAME_COMPLETE = 2;
    private final int FRAME_DONE = 3;
    private View[] mFrameGroups;
    private int mState = STATE_NORMAL;
    private OnClickListener mListener;

    public LoadFooterView(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public LoadFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.listview_vertical_footer, this);
        mFrameGroups = new View[3];
        mFrameGroups[FRAME_LOAD] = findViewById(R.id.footer_load_layout);
        mFrameGroups[FRAME_ERROR] = findViewById(R.id.footer_error_layout);
        mFrameGroups[FRAME_COMPLETE] = findViewById(R.id.tv_load_complete);
        mFrameGroups[FRAME_ERROR].findViewById(R.id.tv_error_try).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (null != mListener) {
                    setState(BaseRefresh.STATE_REFRESHING);
                    //延持200毫秒,让用户看到加载效果
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onClick(v);
                        }
                    }, 200);
                }
            }
        });
        setState(BaseRefresh.STATE_NORMAL);
    }

    @Override
    public void refreshComplete() {
        setState(STATE_NORMAL);
    }

    @Override
    public boolean isRefreshing() {
        return STATE_REFRESHING == mState;
    }

    /**
     * 设置底部刷新桢
     * 不要问我为什么一个一个问隐藏显示,单个设置他妹的不生效.可能和merge有关
     *
     * @param state
     */
    @Override
    public void setState(@State int state) {
        if (state == mState) return;
        switch (state) {
            case STATE_NORMAL:
            case STATE_REFRESHING:
                showFrame(FRAME_LOAD);
                break;
            case STATE_ERROR:
                showFrame(FRAME_ERROR);
                break;
            case STATE_FINISH:
                showFrame(FRAME_COMPLETE);
                break;
            case STATE_DONE:
                showFrame(FRAME_DONE);
                break;
        }
        mState = state;
    }

    /**
     * 显示指定桢
     *
     * @param frame
     */
    private void showFrame(int frame) {
        for (int i = 0; i < mFrameGroups.length; i++) {
            mFrameGroups[i].setVisibility(i == frame ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public boolean isCurrentState(int state) {
        return mState == state;
    }


    @Override
    public void setOnRetryListener(OnClickListener listener) {
        this.mListener = listener;
    }
}
