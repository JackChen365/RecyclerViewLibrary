package com.ldzs.pulltorefreshrecyclerview.ui.recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.FriendAdapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.recyclerlibrary.PullToRefreshExpandRecyclerView;
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator;

import java.util.Random;

/**
 * Created by cz on 16/1/22.
 * 可展开的RecyclerView
 */
public class PullToRefreshExpandActivity extends AppCompatActivity {
    private PullToRefreshExpandRecyclerView mRecyclerView;
    private FriendAdapter mAdapter;
    private int times = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_recycler_view);
        setTitle(getIntent().getStringExtra("title"));
        mRecyclerView = (PullToRefreshExpandRecyclerView) this.findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(300);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setOnItemClickListener((v, position) -> Toast.makeText(getApplicationContext(), "Click:" + position, Toast.LENGTH_SHORT).show());

        mRecyclerView.addHeaderView(getHeaderView());


        mRecyclerView.setOnPullUpToRefreshListener(() -> {
            times = 0;
            mRecyclerView.postDelayed(() -> {
                addGroupItem(true);
                mRecyclerView.onRefreshComplete();
            }, 1000);
        });
        mRecyclerView.setOnPullDownToRefreshListener(() -> {
            if (times < 10) {
                mRecyclerView.postDelayed(() -> {
                    addGroupItem(false);
                    mRecyclerView.onRefreshComplete();
                }, 1000);
            } else {
                mRecyclerView.postDelayed(() -> mRecyclerView.setFooterComplete(), 1000);
            }
            times++;
        });

        mAdapter = new FriendAdapter(this, Data.createExpandItems(10, 4), true);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 获得一个顶部控件
     */
    public View getHeaderView() {
        int textColor = Data.getRandomColor();
        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, (ViewGroup) findViewById(android.R.id.content), false);
        header.setBackgroundColor(Color.BLUE);
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
            addGroupItem(true);
            return true;
        } else if (id == R.id.action_remove) {
            mAdapter.removeGroup(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addGroupItem(boolean top) {
        Random random = new Random();
        mAdapter.addGroupItems(getString(R.string.add_group) + mAdapter.getGroupCount(), Data.createItems(this, 2), top ? 0 : mAdapter.getGroupCount(), 0 == random.nextInt(2));
    }

}
