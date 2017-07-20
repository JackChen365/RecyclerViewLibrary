package com.ldzs.pulltorefreshrecyclerview.ui.sticky;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.LinearSticky1ItemAdapter;
import com.ldzs.recyclerlibrary.PullToRefreshStickyRecyclerView;
import com.ldzs.recyclerlibrary.callback.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/5/20.
 * 此示例演示,自动分组,分组后不同运态增删数据的分组自动同步功能
 * 分组逻辑为:(String s1, String s2)->s1.charAt(0)!=s2.charAt(0)
 */
public class Sticky3SampleActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky3);
        final PullToRefreshStickyRecyclerView refreshStickyRecyclerView= (PullToRefreshStickyRecyclerView) findViewById(R.id.recycler_view);
        refreshStickyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> items=new ArrayList<>();
        for(char i='A';i<'N';i++){
            for(int k=0;k<5;k++){
                items.add(String.valueOf(i)+" index:"+(k+1));
            }
        }
        final LinearSticky1ItemAdapter adapter = new LinearSticky1ItemAdapter(this, items);
        refreshStickyRecyclerView.setAdapter(adapter);
        refreshStickyRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(Sticky3SampleActivity.this,"position:"+position,Toast.LENGTH_SHORT).show();
            }
        });

        final Random random=new Random();
        findViewById(R.id.btn_remove).setOnClickListener(v ->
            adapter.removeNotify(random.nextInt(adapter.getItemCount())));
    }
}
