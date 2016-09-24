package com.ldzs.pulltorefreshrecyclerview.ui.recyclerview;

import android.graphics.Color;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import cz.library.RefreshMode;

/**
 * 1:示例添加头,添加信息,以及自定义的Adapter使用.
 * 2:示例底部加载情况,加载中/加载异常/加载完毕
 */
public class PullToRefreshActivity extends AppCompatActivity {
    private static final String TAG = "PullToRefreshActivity";
    private PullToRefreshRecyclerView mRecyclerView;
    private SimpleAdapter mAdapter;
    private int times = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_recycler_view);
        setTitle(getIntent().getStringExtra("title"));
        mRecyclerView = (PullToRefreshRecyclerView) this.findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(300);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setOnItemClickListener((v, position) -> Toast.makeText(getApplicationContext(), "Click:" + position, Toast.LENGTH_SHORT).show());
        mRecyclerView.addHeaderView(getHeaderView());
        mRecyclerView.addHeaderView(getHeaderView());
        mRecyclerView.addHeaderView(getHeaderView());

        mRecyclerView.addFooterView(getHeaderView());
        mRecyclerView.addFooterView(getHeaderView());
        mRecyclerView.addFooterView(getHeaderView());


        //下拉加载
        mRecyclerView.setOnPullToRefreshListener(() -> {
            Log.e(TAG,"onHeaderRefresh!");
            mRecyclerView.postDelayed(() -> {
                mAdapter.addItems(Data.createItems(this, 2), 0);
                mRecyclerView.onRefreshComplete();
            }, 1000);
        });
        //上拉刷新
        mRecyclerView.setOnPullFooterToRefreshListener(() -> {
            Log.e(TAG,"onFooterRefresh!");
            if (times < 2) {
                mRecyclerView.postDelayed(() -> {
                    mAdapter.addItems(Data.createItems(this, 4));
                    mRecyclerView.onRefreshFootComplete();
                }, 1000);
            } else if (times < 4) {
                mRecyclerView.postDelayed(() -> {
                    mRecyclerView.setOnFootRetryListener(v -> {
                        mAdapter.addItems(Data.createItems(this, 4));
                        mRecyclerView.onRefreshFootComplete();
                    });
                }, 1000);
            } else {
                mRecyclerView.postDelayed(() -> mRecyclerView.setFooterRefreshDone(), 1000);
            }
            times++;
        });
        //初始设置2个,考虑其不满一屏加载状态
        mAdapter = new SimpleAdapter(this, Data.createItems(this, 2));
        mRecyclerView.setAdapter(mAdapter);

        RadioGroup layout= (RadioGroup) findViewById(R.id.rg_refresh_mode);
        layout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_refresh_both:
                        mRecyclerView.setRefreshMode(RefreshMode.BOTH);
                        break;
                    case R.id.rb_refresh_start:
                        mRecyclerView.setRefreshMode(RefreshMode.PULL_FROM_START);
                        break;
                    case R.id.rb_refresh_end:
                        mRecyclerView.setRefreshMode(RefreshMode.PULL_FROM_END);
                        break;
                    case R.id.rb_refresh_none:
                        mRecyclerView.setRefreshMode(RefreshMode.DISABLED);
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
        headerView.setText("HeaderView:" + (mRecyclerView.getHeaderViewCount()+mRecyclerView.getFooterViewCount()));
        headerView.setOnClickListener(v -> mRecyclerView.addHeaderView(getHeaderView()));
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
            mRecyclerView.autoRefreshing(true);
            return true;
        } else if (id == R.id.action_re_refresh) {
            mRecyclerView.autoRefreshing(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
