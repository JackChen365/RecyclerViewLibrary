package com.ldzs.recyclerlibrary.callback;

import android.view.View;


public interface OnExpandItemClickListener {
    void onItemClick(View v, int groupPosition, int childPosition);
}