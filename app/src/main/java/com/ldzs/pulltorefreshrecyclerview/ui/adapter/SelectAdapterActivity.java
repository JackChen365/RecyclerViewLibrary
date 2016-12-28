package com.ldzs.pulltorefreshrecyclerview.ui.adapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleSelectAdapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.pulltorefreshrecyclerview.widget.RadioLayout;
import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView;
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator;
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;

import java.util.ArrayList;

/**
 * Created by cz on 16/1/23.
 * 此实现RecyclerView自带的点选事件
 * 1:单击/单选/多选/块选
 */
public class SelectAdapterActivity extends AppCompatActivity {
    private PullToRefreshRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        setTitle(getIntent().getStringExtra("title"));
        mRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(300);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        SimpleSelectAdapter adapter = new SimpleSelectAdapter(this, Data.createItems(this, 100));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addHeaderView(getHeaderView());
        mRecyclerView.addFooterView(getFooterView());
        RadioLayout layout = (RadioLayout) findViewById(R.id.rl_choice);
        layout.setOnCheckedListener(new RadioLayout.OnCheckedListener() {
            @Override
            public void onChecked(View v, int position, boolean isChecked) {
                mRecyclerView.setSelectMode(position);
            }
        });
        mRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(getApplicationContext(), "Click:" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setOnSingleSelectListener(new PullToRefreshRecyclerView.OnSingleSelectListener() {
            @Override
            public void onSingleSelect(View v, int newPosition, int oldPosition) {
                Toast.makeText(getApplicationContext(), "SingleSelect:" + newPosition, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setOnMultiSelectListener(new PullToRefreshRecyclerView.OnMultiSelectListener() {
            @Override
            public void onMultiSelect(View v, ArrayList<Integer> selectPositions) {
                Toast.makeText(getApplicationContext(), "MultiSelect:" + selectPositions, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setOnRectangleSelectListener(new PullToRefreshRecyclerView.OnRectangleSelectListener() {
            @Override
            public void onRectangleSelect(int startPosition, int endPosition) {
                Toast.makeText(getApplicationContext(), "Start:" + startPosition + " End:" + endPosition, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.findAdapterView(R.id.tv_last).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectAdapterActivity.this, "List Item", Toast.LENGTH_SHORT).show();
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
        headerView.setText("HeaderView:" + mRecyclerView.getHeaderViewCount());
        return headerView;
    }


    /**
     * 获得一个底部控件
     */
    public View getFooterView() {
        int color = Data.getRandomColor();
        int textColor = Data.getDarkColor(color);
        View footer = LayoutInflater.from(this).inflate(R.layout.recyclerview_footer, (ViewGroup) findViewById(android.R.id.content), false);
        TextView footerView = (TextView) footer;
        footerView.setText("FooterView:" + mRecyclerView.getFooterViewCount());
        footerView.setBackgroundColor(color);
        footerView.setTextColor(textColor);
        return footerView;
    }
}
