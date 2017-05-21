package com.ldzs.pulltorefreshrecyclerview.ui.sticky;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.GridStickyItem1Adapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.pulltorefreshrecyclerview.model.Sticky2Item;
import com.ldzs.recyclerlibrary.PullToRefreshStickyRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/5/20.
 * 此示例演示 GridLayoutManager下的Sticky效果
 * 分组逻辑为:(String s1, String s2)->s1.charAt(0)!=s2.charAt(0)
 */
public class Sticky4SampleActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky3);
        final PullToRefreshStickyRecyclerView refreshStickyRecyclerView= (PullToRefreshStickyRecyclerView) findViewById(R.id.recycler_view);
        refreshStickyRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        List<Sticky2Item> items=new ArrayList<>();
        String lastItem=null;
        for(String item:Data.ITEMS){
            String firstItem=String.valueOf(item.charAt(0));
            if(null==lastItem||!lastItem.equals(firstItem)){
                items.add(new Sticky2Item(true,firstItem));
            }
            items.add(new Sticky2Item(false,item));
            lastItem=String.valueOf(item.charAt(0));
        }
        final GridStickyItem1Adapter adapter = new GridStickyItem1Adapter(this,items);
        refreshStickyRecyclerView.setAdapter(adapter);

        findViewById(R.id.btn_remove).setVisibility(View.GONE);
        //TODO 待测试,下个版本再测.写得有些累了
//        final Random random=new Random();
//        findViewById(R.id.btn_remove).setOnClickListener(v ->
//            adapter.removeNotify(random.nextInt(adapter.getItemCount())));
    }
}
