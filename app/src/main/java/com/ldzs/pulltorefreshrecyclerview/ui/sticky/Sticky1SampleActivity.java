package com.ldzs.pulltorefreshrecyclerview.ui.sticky;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.LinearSticky1ItemAdapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.recyclerlibrary.PullToRefreshStickyRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/5/20.
 * 此示例演示,自动分组,分组条目为不同条目区间运算
 * 分组逻辑为:(String s1, String s2)->s1.charAt(0)!=s2.charAt(0)
 */
public class Sticky1SampleActivity  extends AppCompatActivity {
    private boolean swap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky1);
        final PullToRefreshStickyRecyclerView refreshStickyRecyclerView= (PullToRefreshStickyRecyclerView) findViewById(R.id.recycler_view);
        refreshStickyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final LinearSticky1ItemAdapter adapter = new LinearSticky1ItemAdapter(this, Arrays.asList(Data.ITEMS));
        refreshStickyRecyclerView.setAdapter(adapter);

        findViewById(R.id.btn_wrap).setOnClickListener(v->{
            swap=!swap;
            if(swap){
                final Random random=new Random();
                final List<String> items=new ArrayList();
                for(char i='H';i<='Z';i++){
                    for(int k=1;k<=5+random.nextInt(5);k++){
                        items.add(String.valueOf(i)+" Item:"+k);
                    }
                }
                adapter.swapItemsNotify(items);
            } else {
                adapter.swapItemsNotify(Arrays.asList(Data.ITEMS));
            }
        });
    }
}
