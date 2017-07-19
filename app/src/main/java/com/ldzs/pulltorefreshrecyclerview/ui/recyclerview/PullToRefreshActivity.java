package com.ldzs.pulltorefreshrecyclerview.ui.recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleAdapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView;
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator;
import com.ldzs.recyclerlibrary.callback.OnExpandItemClickListener;
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;

import cz.library.RefreshMode;

/**
 * 1:示例添加头,添加信息,以及自定义的Adapter使用.
 * 2:示例底部加载情况,加载中/加载异常/加载完毕
 */
public class PullToRefreshActivity extends AppCompatActivity {
    private static final String TAG = "PullToRefreshActivity";
    private PullToRefreshRecyclerView recyclerView;
    private SimpleAdapter adapter;
    private int times = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_recycler_view);
        setTitle(getIntent().getStringExtra("title"));
        recyclerView = (PullToRefreshRecyclerView) this.findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.getItemAnimator().setAddDuration(300);
        recyclerView.getItemAnimator().setRemoveDuration(300);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setOnItemClickListener((v, position) -> Toast.makeText(getApplicationContext(), "Click:" + position, Toast.LENGTH_SHORT).show());
        recyclerView.addHeaderView(getHeaderView());
        recyclerView.addHeaderView(getHeaderView());
        recyclerView.addHeaderView(getHeaderView());

        recyclerView.addFooterView(getHeaderView());
        recyclerView.addFooterView(getHeaderView());
        recyclerView.addFooterView(getHeaderView());


        //下拉加载
        recyclerView.setOnPullToRefreshListener(() -> {
            Log.e(TAG,"onHeaderRefresh!");
            recyclerView.postDelayed(() -> {
                adapter.addItemsNotify(Data.createItems(this, 2), 0);
                recyclerView.onRefreshComplete();
            }, 1000);
        });
        //上拉刷新
        recyclerView.setOnPullFooterToRefreshListener(() -> {
            Log.e(TAG,"onFooterRefresh!");
//            if (times < 2) {
//                recyclerView.postDelayed(() -> {
//                    adapter.addItemsNotify(Data.createItems(this, 4));
//                    recyclerView.onRefreshFootComplete();
//                }, 1000);
//            } else if (times < 4) {
//                recyclerView.postDelayed(() -> {
//                    recyclerView.setOnFootRetryListener(v -> {
//                        adapter.addItemsNotify(Data.createItems(this, 4));
//                        recyclerView.onRefreshFootComplete();
//                    });
//                }, 1000);
//            } else {
//                recyclerView.postDelayed(() -> recyclerView.setFooterRefreshDone(), 1000);
//            }
//            times++;
        });
        //初始设置2个,考虑其不满一屏加载状态
        adapter = new SimpleAdapter(this, Data.createItems(this, 2));
        recyclerView.setAdapter(adapter);
        recyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Snackbar.make(v,getString(R.string.click_position,position),Snackbar.LENGTH_LONG).show();
            }
        });

        RadioGroup layout= (RadioGroup) findViewById(R.id.rg_refresh_mode);
        layout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_refresh_both:
                        recyclerView.setRefreshMode(RefreshMode.BOTH);
                        break;
                    case R.id.rb_refresh_start:
                        recyclerView.setRefreshMode(RefreshMode.PULL_FROM_START);
                        break;
                    case R.id.rb_refresh_end:
                        recyclerView.setRefreshMode(RefreshMode.PULL_FROM_END);
                        break;
                    case R.id.rb_refresh_none:
                        recyclerView.setRefreshMode(RefreshMode.DISABLED);
                        break;
                }
            }
        });
    }

    /**
     * 获得一个顶部控件
     */
    public View getHeaderView() {
        int textColor = Data.getRandomColor();
        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, (ViewGroup) findViewById(android.R.id.content), false);
        TextView headerView = (TextView) header;
        headerView.setTextColor(textColor);
        header.setBackgroundColor(Color.BLUE);
        headerView.setText("HeaderView:" + (recyclerView.getHeaderViewCount()+ recyclerView.getFooterViewCount()));
        headerView.setOnClickListener(v -> recyclerView.addHeaderView(getHeaderView()));
        return headerView;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            recyclerView.autoRefreshing(true);
            return true;
        } else if (id == R.id.action_re_refresh) {
            recyclerView.autoRefreshing(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
