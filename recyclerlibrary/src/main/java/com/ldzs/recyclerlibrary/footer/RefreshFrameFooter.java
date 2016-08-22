package com.ldzs.recyclerlibrary.footer;

import android.content.Context;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldzs.recyclerlibrary.R;


/**
 * Created by czz on 2016/8/17.
 */
public class RefreshFrameFooter {
    private static final String TAG = "RefreshFrameFooter";
    public static final int FRAME_CLICK = 0;
    public static final int FRAME_LOAD = 1;
    public static final int FRAME_ERROR = 2;
    public static final int FRAME_DONE = 3;
    public static final int FRAME_HIDE = 4;

    @IntDef(value={FRAME_CLICK, FRAME_LOAD,FRAME_ERROR,FRAME_DONE,FRAME_HIDE})
    public @interface RefreshState {
    }

    private View[] frameGroup;
    private int refreshState = FRAME_CLICK;
    private View.OnClickListener mListener;
    private View container;
    private View lastFrame;

    /**
     * @param context
     * @param parent
     */
    public RefreshFrameFooter(Context context, ViewGroup parent) {
        this.container = LayoutInflater.from(context).inflate(R.layout.list_footer, parent, false);
        frameGroup = new View[4];
        frameGroup[FRAME_CLICK] = this.container.findViewById(R.id.refresh_click_view);
        frameGroup[FRAME_LOAD] = this.container.findViewById(R.id.refresh_loading_layout);
        frameGroup[FRAME_ERROR] = this.container.findViewById(R.id.refresh_error_layout);
        frameGroup[FRAME_DONE] = this.container.findViewById(R.id.refresh_complete_layout);
        frameGroup[FRAME_ERROR].findViewById(R.id.tv_error_try).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (null != mListener) {
                    setRefreshState(FRAME_LOAD);
                    //delayed three hundred millisecond show progress view
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onClick(v);
                        }
                    }, 300);
                }
            }
        });
        setRefreshState(FRAME_LOAD);
    }

    public void refreshComplete() {
        setRefreshState(FRAME_DONE);
    }

    public boolean isRefreshing() {
        return FRAME_LOAD == refreshState;
    }

    /**
     * set footer refresh state
     *
     * @param state
     */
    public void setRefreshState(@RefreshState int state) {
        if (refreshState == state) return;
        if(FRAME_HIDE==refreshState){
            this.container.setVisibility(View.GONE);
        } else if(View.VISIBLE!=this.container.getVisibility()){
            this.container.setVisibility(View.VISIBLE);
        }
        if(null!=lastFrame){
            lastFrame.setVisibility(View.GONE);
        }
        frameGroup[state].setVisibility(View.VISIBLE);
        lastFrame= frameGroup[state];
        refreshState = state;
    }


    private boolean isCurrentState(int state) {
        return refreshState == state;
    }


    public void setOnFootRetryListener(View.OnClickListener listener) {
        this.mListener = listener;
    }

    public View getFooterView(){
        return container;
    }
}
