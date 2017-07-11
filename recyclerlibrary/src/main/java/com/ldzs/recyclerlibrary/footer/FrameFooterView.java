package com.ldzs.recyclerlibrary.footer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldzs.recyclerlibrary.BuildConfig;
import com.ldzs.recyclerlibrary.R;

/**
 * Created by Administrator on 2016/8/20.
 */
public class FrameFooterView extends RelativeLayout {
    private TextView clickView;
    private LinearLayout loadLayout;
    private TextView refreshHintView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private TextView errorRetry;
    private TextView completeText;
    public FrameFooterView(Context context) {
        this(context,null, R.attr.footerStyle);
    }

    public FrameFooterView(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.footerStyle);
    }

    public FrameFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context,R.layout.list_footer,this);
        clickView= (TextView) findViewById(R.id.refresh_click_view);
        loadLayout= (LinearLayout) findViewById(R.id.refresh_loading_layout);
        refreshHintView= (TextView) findViewById(R.id.refresh_hint_info);
        errorLayout= (LinearLayout) findViewById(R.id.refresh_error_layout);
        errorText= (TextView) findViewById(R.id.refresh_error_text);
        errorRetry= (TextView) findViewById(R.id.tv_error_try);
        completeText= (TextView) findViewById(R.id.refresh_complete_layout);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FrameFooterView, defStyleAttr, R.style.FrameFooterView);
        setFooterHeight(a.getDimension(R.styleable.FrameFooterView_footer_footerHeight,0f));
        setFooterClickTextHint(a.getString(R.styleable.FrameFooterView_footer_clickTextHint));
        setFooterTextSize(a.getDimensionPixelSize(R.styleable.FrameFooterView_footer_textSize,0));
        setFooterTextColor(a.getColor(R.styleable.FrameFooterView_footer_textColor, Color.TRANSPARENT));
        setFooterErrorHint(a.getString(R.styleable.FrameFooterView_footer_errorHint));
        setFooterComplete(a.getString(R.styleable.FrameFooterView_footer_complete));
        setFooterRetryItemSelector(a.getDrawable(R.styleable.FrameFooterView_footer_retryItemSelector));
        setFooterRetry(a.getString(R.styleable.FrameFooterView_footer_retry));
        setFooterLoad(a.getString(R.styleable.FrameFooterView_footer_load));
        a.recycle();
    }


    public void setFooterHeight(float height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if(null!=layoutParams){
            layoutParams.height= (int) height;
            requestLayout();
        }
    }

    public void setFooterClickTextHint(String hint) {
        clickView.setText(hint);
    }

    public void setFooterTextSize(int textSize) {
        clickView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        refreshHintView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        errorText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        errorRetry.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        completeText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
    }

    public void setFooterTextColor(int color) {
        clickView.setTextColor(color);
        refreshHintView.setTextColor(color);
        errorText.setTextColor(color);
        errorRetry.setTextColor(color);
        completeText.setTextColor(color);
    }

    public void setFooterLoad(String load) {
        refreshHintView.setText(load);
    }

    public void setFooterErrorHint(String hint) {
        errorText.setText(hint);
    }

    public void setFooterComplete(String complete) {
        completeText.setText(complete);
    }

    public void setFooterRetryItemSelector(Drawable drawable) {
        if(null!=drawable){
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN){
                errorRetry.setBackgroundDrawable(drawable);
            } else {
                errorRetry.setBackground(drawable);
            }
        }
    }

    public void setFooterRetry(String retry) {
        errorRetry.setText(retry);
    }

}
