package com.ldzs.pulltorefreshrecyclerview.ui.recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;

import cz.library.PullToRefreshLayout;
import cz.library.RefreshMode;

public class GridPullToRefreshActivity extends AppCompatActivity {
    private static final String TAG = "GridPullToRefreshActivity";
    private PullToRefreshRecyclerView mRecyclerView;
    private SimpleAdapter mAdapter;
    private int times = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_recycler_view);
        setTitle(getIntent().getStringExtra("title"));
        mRecyclerView = (PullToRefreshRecyclerView) this.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);

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
        mRecyclerView.setOnPullToRefreshListener(new PullToRefreshLayout.OnPullToRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.postDelayed(() -> {
                    mAdapter.addItemsNotify(Data.createItems(this, 10), 0);
                    mRecyclerView.onRefreshComplete();
                }, 1000);
            }
        });
        mRecyclerView.setOnPullFooterToRefreshListener(new PullToRefreshRecyclerView.OnPullFooterToRefreshListener() {
            @Override
            public void onRefresh() {
                if (times < 2) {
                    mRecyclerView.postDelayed(() -> {
                        mAdapter.addItemsNotify(Data.createItems(this, 10));
                        mRecyclerView.onRefreshFootComplete();
                    }, 1000);
                } else {
                    mRecyclerView.postDelayed(() -> mRecyclerView.setFooterRefreshDone(), 1000);
                }
                times++;
            }
        });

        mRecyclerView.setAdapter(mAdapter = new SimpleAdapter(this, R.layout.grid_text_item, Data.createItems(this, 10)));
        mRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Snackbar.make(v,getString(R.string.click_position,position),Snackbar.LENGTH_LONG).show();
            }
        });

        View headerView = getHeaderView();
        headerView.setBackgroundColor(Color.BLUE);
        mRecyclerView.addHeaderView(headerView);
        mRecyclerView.addFooterView(getHeaderView());
        mRecyclerView.addFooterView(getHeaderView());
        mRecyclerView.addFooterView(getHeaderView());

    }

    /**
     * 获得一个顶部控件
     */
    public View getHeaderView() {
        int textColor = Data.getRandomColor();
        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, (ViewGroup) findViewById(android.R.id.content), false);
        TextView headerView = (TextView) header;
        headerView.setTextColor(textColor);
        headerView.setText("HeaderView:" + (mRecyclerView.getHeaderViewCount()+mRecyclerView.getFooterViewCount()));
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.addHeaderView(getHeaderView());
            }
        });
        return headerView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
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
