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
import com.ldzs.recyclerlibrary.observe.DynamicAdapterDataObserve;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by cz on 16/1/24.
 */
public class DynamicAdapterActivity extends AppCompatActivity {
    private static final String TAG = "DynamicAdapterActivity";
    private RecyclerView recyclerView;
    private DynamicAdapter adapter;
    private LinkedList<View> addViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_adapter);
        addViews = new LinkedList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.getItemAnimator().setAddDuration(300);
        recyclerView.getItemAnimator().setRemoveDuration(300);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        final SimpleAdapter adapter = new SimpleAdapter(this, Data.createItems(this, 100));
        this.adapter = new DynamicAdapter(this, adapter);
        adapter.registerAdapterDataObserver(new DynamicAdapterDataObserve(this.adapter));
        recyclerView.setAdapter(this.adapter);
        Random random = new Random();
        int[] array=new int[]{1,3,4,7,10,12,15,18,20};
        for(int i=0;i<array.length;addView(array[i]),i++);
        findViewById(R.id.btn_item_add).setOnClickListener(v -> adapter.addItem("new:"+adapter.getItemCount(),random.nextInt(adapter.getItemCount())));
        findViewById(R.id.btn_item_remove).setOnClickListener(v -> {
            if(0!=this.adapter.getItemCount()){
                adapter.removeNotifyItem(0,8);
            }
        });
        findViewById(R.id.btn_global_item_remove).setOnClickListener(v->{
            adapter.remove(0,8);
            this.adapter.itemRangeGlobalRemoved(0,8);
        });
        findViewById(R.id.btn_add).setOnClickListener(v -> addView(random.nextInt(this.adapter.getItemCount())));
        findViewById(R.id.btn_remove).setOnClickListener(v -> {
            if (!addViews.isEmpty()) {
                this.adapter.removeFullItem(addViews.pollFirst());
            }
        });
    }

    private void addView(int position) {
        View view = getFullItemView();
        if (!addViews.contains(position)) {
            addViews.add(view);
            adapter.addFullItem(view, position);
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
        headerView.setText("HeaderView:" + adapter.getFullItemCount());
        return headerView;
    }
}
