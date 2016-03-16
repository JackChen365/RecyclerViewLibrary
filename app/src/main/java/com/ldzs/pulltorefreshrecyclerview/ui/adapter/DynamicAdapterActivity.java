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

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by cz on 16/1/24.
 */
public class DynamicAdapterActivity extends AppCompatActivity {
    private static final String TAG = "DynamicAdapterActivity";
    private RecyclerView mRecyclerView;
    private DynamicAdapter mAdapter;
    private LinkedList<Integer> mAddItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_adapter);
        mAddItems = new LinkedList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(300);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(layoutManager);
        final SimpleAdapter adapter = new SimpleAdapter(this, Data.createItems(this, 40));
        mAdapter = new DynamicAdapter(this, adapter);
        mRecyclerView.setAdapter(mAdapter);
        Random random = new Random();
        findViewById(R.id.btn_item_add).setOnClickListener(v -> adapter.addItems(Data.createItems(this, 4),0));
        findViewById(R.id.btn_item_remove).setOnClickListener(v -> adapter.remove(2));
        View addView = findViewById(R.id.btn_add);
        addView.setOnClickListener(v -> addView(random.nextInt(mAdapter.getItemCount())));
        View removeView = findViewById(R.id.btn_remove);
        removeView.setOnClickListener(v -> {
            if (!mAddItems.isEmpty()) {
                mAdapter.removeFullItem(mAddItems.pollFirst());
            }
        });
    }


    private void addView(int position) {
        View view = getFullItemView();
        if (!mAddItems.contains(position)) {
            mAddItems.add(position);
            mAdapter.addFullItem(view, position);
        }
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
        return headerView;
    }
}
