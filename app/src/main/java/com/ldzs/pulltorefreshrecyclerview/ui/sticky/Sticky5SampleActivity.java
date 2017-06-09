package com.ldzs.pulltorefreshrecyclerview.ui.sticky;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.GridStickyItem2Adapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.pulltorefreshrecyclerview.model.Sticky2Item;
import com.ldzs.recyclerlibrary.PullToRefreshStickyRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cz on 2017/6/9.
 */

public class Sticky5SampleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky3);
        final PullToRefreshStickyRecyclerView refreshStickyRecyclerView= (PullToRefreshStickyRecyclerView) findViewById(R.id.recycler_view);
        refreshStickyRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        List<Sticky2Item> items=new ArrayList<>();
        String lastItem=null;
        for(String item: Data.ITEMS){
            String firstItem=String.valueOf(item.charAt(0));
            if(null==lastItem||!lastItem.equals(firstItem)){
                items.add(new Sticky2Item(true,firstItem));
            }
            items.add(new Sticky2Item(false,item));
            lastItem=String.valueOf(item.charAt(0));
        }
        final GridStickyItem2Adapter adapter = new GridStickyItem2Adapter(this,items);
        refreshStickyRecyclerView.setAdapter(adapter);

    }
}
