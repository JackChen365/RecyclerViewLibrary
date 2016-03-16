package com.ldzs.pulltorefreshrecyclerview.ui.adapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleAdapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.recyclerlibrary.adapter.drag.DynamicAdapter;
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cz on 16/1/24.
 */
public class ExpandAdapterActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private DynamicAdapter mAdapter;
    private ArrayList<View> mViews;
    private int mAddIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_adapter);
        mViews = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(300);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new DynamicAdapter(this,new SimpleAdapter(this, Data.createItems(this, 150)));
        mRecyclerView.setAdapter(mAdapter);
        Random random = new Random();
        findViewById(R.id.btn_add).setOnClickListener(v -> mAdapter.addFullItem(getFullItemView(), random.nextInt(mAdapter.getItemCount())));
//        findViewById(R.id.btn_last_add).setOnClickListener(v -> mAdapter.addFullItem(getFullItemView(), mAddIndex + 1));
        findViewById(R.id.btn_remove).setOnClickListener(v -> mAdapter.removeFullItem(mViews.get(0)));
    }

    /**
     * 获得一个铺满的控件
     */
    public View getFullItemView() {
        int color = Data.getRandomColor();
        int darkColor = Data.getDarkColor(color);
        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, (ViewGroup) findViewById(android.R.id.content), false);
        TextView headerView = (TextView) header;
        header.setBackgroundColor(color);
        headerView.setTextColor(darkColor);
        headerView.setText("HeaderView:" + mAdapter.getFullItemCount());
        mViews.add(header);
        return headerView;
    }
}
