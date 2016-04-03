package com.ldzs.recyclerlibrary.callback;

import android.view.View;

import java.util.ArrayList;

/**
 * 选择监听器
 */
public interface OnCheckListener {
    void onSingleChoice(View v, int newPosition, int oldPosition);

    void onMultiChoice(View v, ArrayList<Integer> choicePositions);

    void onRectangleChoice(int startPosition, int endPosition);
}