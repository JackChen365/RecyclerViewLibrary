package com.ldzs.pulltorefreshrecyclerview.ui.adapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleAdapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.recyclerlibrary.Mode;
import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView;
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator;

/**
 * Created by cz on 16/1/23.
 * 此实现RecyclerView可支持动态添加HeaderView/FooterView,且实现以下以点
 * 1:无任何添加限制.不像ListView headerView必须在setAdapter前添加
 * 2:可添加不限制数量的header/footer,可动态移除
 * 3:采用装饰者模式设计,不影响用户本身的Adapter的逻辑
 */
public class HeaderAdapterActivity extends AppCompatActivity {
    private PullToRefreshRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header);
        setTitle(getIntent().getStringExtra("title"));
        mRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(300);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setRefreshMode(Mode.DISABLED);//禁用刷新


        SimpleAdapter adapter = new SimpleAdapter(this, Data.createItems(this, 10));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addHeaderView(getHeaderView());
        mRecyclerView.addFooterView(getFooterView());
        findViewById(R.id.btn_add_header).setOnClickListener(v -> mRecyclerView.addHeaderView(getHeaderView()));
        findViewById(R.id.btn_remove_header).setOnClickListener(v -> mRecyclerView.removeHeaderView(0));
        findViewById(R.id.btn_add_footer).setOnClickListener(v -> mRecyclerView.addFooterView(getFooterView()));
        findViewById(R.id.btn_remove_footer).setOnClickListener(v -> mRecyclerView.removeFooterView(0));
        findViewById(R.id.btn_add_item).setOnClickListener(v -> adapter.addItems(Data.createItems(this, 2), 0));
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
        headerView.setOnClickListener(v -> {
            mRecyclerView.addHeaderView(getHeaderView());
        });
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
