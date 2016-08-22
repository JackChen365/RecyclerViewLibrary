package com.ldzs.recyclerlibrary.footer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ldzs.recyclerlibrary.R;

/**
 * Created by Administrator on 2016/8/20.
 */
public class FrameFooterView extends RelativeLayout {

    public FrameFooterView(Context context) {
        this(context,null, R.attr.RefreshStyle);
    }

    public FrameFooterView(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.RefreshStyle);
    }

    public FrameFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
