package com.ldzs.recyclerlibrary.callback;

import android.view.View;

/**
 * Created by cz on 16/1/25.
 */
public class OnDelayItemClickListener implements OnItemClickListener {
    private static final int INTERVAL_TIME_MILLIS = 1 * 1000;
    private OnItemClickListener mListener;
    private static long mClickTimeMillis;//点击时间

    public OnDelayItemClickListener(OnItemClickListener mListener) {
        this(mListener, INTERVAL_TIME_MILLIS);
    }

    public OnDelayItemClickListener(OnItemClickListener mListener, long clickTimeMillis) {
        this.mListener = mListener;
        this.mClickTimeMillis = clickTimeMillis;
    }


    @Override
    public void onItemClick(View v, int position) {
        long startTimeMillis = System.currentTimeMillis();
        if (startTimeMillis - mClickTimeMillis > INTERVAL_TIME_MILLIS && null != mListener) {
            mClickTimeMillis = startTimeMillis;
            mListener.onItemClick(v, position);
        }
    }
}
