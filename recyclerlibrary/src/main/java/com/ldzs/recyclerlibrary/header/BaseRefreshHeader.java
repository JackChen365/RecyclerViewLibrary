package com.ldzs.recyclerlibrary.header;

/**
 * Created by cz on 16/1/20
 */
public interface BaseRefreshHeader extends BaseRefresh {

    /**
     * 获得刷新头原始高度,不变的
     *
     * @return
     */
    int getOriginalHeight();

    /**
     * 获得当前刷新高度
     *
     * @return
     */
    int getRefreshHeight();

    /**
     * 开始刷新状态
     */
    void pullToRefresh(float offsetValue);

    /**
     * 松开状态
     */
    void releaseToRefresh();

    /**
     * 设置正在刷新中
     */
    void setRefreshing();

}
