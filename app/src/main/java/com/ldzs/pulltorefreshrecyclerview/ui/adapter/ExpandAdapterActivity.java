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
    private RecyclerView recyclerView;
    private DynamicAdapter adapter;
    private ArrayList<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_adapter);
        views = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.getItemAnimator().setAddDuration(300);
        recyclerView.getItemAnimator().setRemoveDuration(300);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DynamicAdapter(new SimpleAdapter(this, Data.createItems(this, 150)));
        recyclerView.setAdapter(adapter);
        Random random = new Random();
        findViewById(R.id.btn_add).setOnClickListener(v -> adapter.addDynamicView(getFullItemView(), random.nextInt(adapter.getItemCount())));
//        findViewById(R.id.btn_last_add).setOnClickListener(v -> adapter.addDynamicView(getFullItemView(), mAddIndex + 1));
        findViewById(R.id.btn_remove).setOnClickListener(v -> {
            adapter.removeDynamicView(views.get(0));
        });
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
        headerView.setText("HeaderView:" + adapter.getDynamicItemCount());
        views.add(header);
        return headerView;
    }
}
