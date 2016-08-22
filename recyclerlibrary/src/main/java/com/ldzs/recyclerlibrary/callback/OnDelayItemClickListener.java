package com.ldzs.recyclerlibrary.callback;

import android.view.View;

/**
 * Created by cz on 16/1/25.
 */
public class OnDelayItemClickListener implements OnItemClickListener {
    private static final int INTERVAL_TIME_MILLIS = 1 * 1000;
    private OnItemClickListener listener;
    private static long clickTimeMillis;//点击时间

    public OnDelayItemClickListener(OnItemClickListener listener) {
        this(listener, INTERVAL_TIME_MILLIS);
    }

    public OnDelayItemClickListener(OnItemClickListener listener, long clickTimeMillis) {
        this.listener = listener;
        this.clickTimeMillis = clickTimeMillis;
    }


    @Override
    public void onItemClick(View v, int position) {
        long startTimeMillis = System.currentTimeMillis();
        if (startTimeMillis - clickTimeMillis > INTERVAL_TIME_MILLIS && null != listener) {
            clickTimeMillis = startTimeMillis;
            listener.onItemClick(v, position);
        }
    }
}
