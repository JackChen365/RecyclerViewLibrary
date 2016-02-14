package com.ldzs.pulltorefreshrecyclerview.data;


import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.recyclerlibrary.adapter.expand.ExpandAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by cz on 16/1/21.
 */
public class Date {
    private static int COUNT;
    private static Integer[] COLORS;
    private static final HashMap<String, Integer> mTagCount;
    private static final int[] LAYOUTS;
    private static final Random mRandom;

    static {
        mTagCount = new HashMap<>();
        LAYOUTS = new int[]{R.layout.recyclerview_header1, R.layout.recyclerview_header2, R.layout.recyclerview_header3, R.layout.recyclerview_header4};
        mRandom = new Random();
        //取系统颜色集
        Class<Color> clazz = Color.class;
        try {
            Field filed = clazz.getDeclaredField("sColorNameMap");
            filed.setAccessible(true);
            Object obj = filed.get(null);
            if (obj instanceof HashMap) {
                HashMap<String, Integer> colorNameMap = (HashMap<String, Integer>) obj;
                COLORS = new Integer[colorNameMap.size()];
                colorNameMap.values().toArray(COLORS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> createItems(Object object, int count) {
        COUNT = 0;
        String tag = object.toString();
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add("Item:" + COUNT++);
        }
        return items;
    }

    public static ArrayList<ExpandAdapter.Entry<String, ArrayList<String>>> createExpandItems(Object object, int count) {
        return createExpandItems(object, count, 10);
    }

    public static ArrayList<ExpandAdapter.Entry<String, ArrayList<String>>> createExpandItems(Object object, int count, int childCount) {
        String tag = object.toString();
        ArrayList<ExpandAdapter.Entry<String, ArrayList<String>>> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ArrayList<String> childItems = new ArrayList<>();
            for (int k = 0; k < childCount; k++) {
                childItems.add("Group:" + i + " Child:" + k);
            }
            items.add(new ExpandAdapter.Entry("Group:" + i, childItems));
        }
        return items;
    }

    /**
     * 获得一个Header的控件
     */
    public static View getHeaderItemView(Activity activity) {
        int color = Date.getRandomColor();
        int darkColor = Date.getDarkColor(color);
        View header = LayoutInflater.from(activity).inflate(LAYOUTS[mRandom.nextInt(2)],
                (ViewGroup) activity.findViewById(android.R.id.content), false);
        TextView headerView = (TextView) header;
        header.setBackgroundColor(color);
        headerView.setTextColor(darkColor);
        return headerView;
    }

    /**
     * 获得一个Header的控件
     */
    public static View getRandomItemView(Activity activity) {
        int color = Date.getRandomColor();
        View header = LayoutInflater.from(activity).inflate(LAYOUTS[mRandom.nextInt(LAYOUTS.length)],
                (ViewGroup) activity.findViewById(android.R.id.content), false);
        header.setBackgroundColor(color);
        return header;
    }


    /**
     * 获得一个随机的颜色
     *
     * @return
     */
    public static int getRandomColor() {
        Random random = new Random();
        return COLORS[random.nextInt(COLORS.length)];
    }

    /**
     * 获得一个处理过的颜色
     *
     * @param color
     * @return
     */
    public static int getDarkColor(int color) {
        int max = 0xFF;
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.rgb(r + 30 > max ? r - 30 : r + 30, g + 30 > max ? g - 30 : g + 30, b + 30 > max ? b - 30 : b + 30);
    }
}
