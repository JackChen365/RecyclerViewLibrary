package com.ldzs.pulltorefreshrecyclerview.ui.recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleAdapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView;

public class GridPullToRefreshActivity extends AppCompatActivity {
    private PullToRefreshRecyclerView mRecyclerView;
    private SimpleAdapter mAdapter;
    private int times = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        setTitle(getIntent().getStringExtra("title"));
        mRecyclerView = (PullToRefreshRecyclerView) this.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);

        View headerView = getHeaderView();
        headerView.setBackgroundColor(Color.BLUE);
        mRecyclerView.addHeaderView(headerView);

        mRecyclerView.setOnPullUpToRefreshListener(() -> {
            times = 0;
            mRecyclerView.postDelayed(() -> {
                mAdapter.addItems(Data.createItems(this, 10), 0);
                mRecyclerView.onRefreshComplete();
            }, 1000);
        });
        mRecyclerView.setOnPullDownToRefreshListener(() -> {
            if (times < 2) {
                mRecyclerView.postDelayed(() -> {
                    mAdapter.addItems(Data.createItems(this, 10));
                    mRecyclerView.onRefreshComplete();
                }, 1000);
            } else {
                mRecyclerView.postDelayed(() -> mRecyclerView.setFooterComplete(), 1000);
            }
            times++;
        });

        mRecyclerView.setAdapter(mAdapter = new SimpleAdapter(this, R.layout.grid_text_item, Data.createItems(this, 10)));
    }

    /**
     * 获得一个顶部控件
     */
    public View getHeaderView() {
        int textColor = Data.getRandomColor();
        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, (ViewGroup) findViewById(android.R.id.content), false);
        TextView headerView = (TextView) header;
        headerView.setTextColor(textColor);
        headerView.setText("HeaderView:" + mRecyclerView.getHeaderViewCount());
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
        if (id == R.id.action_add) {
            mAdapter.addItem(getString(R.string.header), 0);
            return true;
        } else if (id == R.id.action_remove) {
            mAdapter.remove(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
