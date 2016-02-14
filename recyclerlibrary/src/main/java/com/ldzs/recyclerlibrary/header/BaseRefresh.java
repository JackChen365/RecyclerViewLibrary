package com.ldzs.recyclerlibrary.header;

import android.support.annotation.IntDef;

/**
 * 刷新基类控制
 */
public interface BaseRefresh {
    int STATE_NORMAL = 0;
    int STATE_RELEASE_TO_REFRESH = 1;
    int STATE_REFRESHING = 2;
    int STATE_ERROR = 3;
    int STATE_COMPLETE = 4;
    int STATE_FINISH = 5;//加载结束
    int STATE_DONE = 6;//不启用

    /**
     * 刷新状态约束
     */
    @IntDef(value = {STATE_NORMAL, STATE_RELEASE_TO_REFRESH, STATE_REFRESHING, STATE_ERROR, STATE_COMPLETE, STATE_FINISH,STATE_DONE})
    @interface State {
    }

    /**
     * 刷新结束
     */
    void refreshComplete();

    /**
     * 是否处于刷新中
     *
     * @return
     */
    boolean isRefreshing();

    /**
     * 设置刷新状态
     *
     * @param status
     */
    void setState(@State int status);

    /**
     * 匹配当前状态
     *
     * @return
     */
    boolean isCurrentState(int state);
}
