package com.ldzs.pulltorefreshrecyclerview.ui.sticky;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.LinearSticky1ItemAdapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.recyclerlibrary.PullToRefreshStickyRecyclerView;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/5/20.
 * 此示例演示,自动分组,分组条目为不同条目区间运算
 * 分组逻辑为:(String s1, String s2)->s1.charAt(0)!=s2.charAt(0)
 */
public class Sticky1SampleActivity  extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky1);
        final PullToRefreshStickyRecyclerView refreshStickyRecyclerView= (PullToRefreshStickyRecyclerView) findViewById(R.id.recycler_view);
        refreshStickyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final LinearSticky1ItemAdapter adapter = new LinearSticky1ItemAdapter(this, Arrays.asList(Data.ITEMS));
        refreshStickyRecyclerView.setAdapter(adapter);
    }
}
