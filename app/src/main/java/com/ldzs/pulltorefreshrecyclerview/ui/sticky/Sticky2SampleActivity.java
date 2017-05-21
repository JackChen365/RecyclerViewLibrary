package com.ldzs.pulltorefreshrecyclerview.ui.sticky;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.LinearSticky2ItemAdapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.pulltorefreshrecyclerview.model.Sticky1Item;
import com.ldzs.recyclerlibrary.PullToRefreshStickyRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/5/20.
 * 此示例演示Sticky在adapter内为一个单独的type,分组内型为组内单独条目,且header支持动态大小.
 * 分组逻辑为:(Sticky1Item s1)->!s1.headerItems.isEmpty()
 * 因动态大小加入,所以探测改为:遍历当前列表内是否有待出现下个阶段条目
 */
public class Sticky2SampleActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky2);
        final PullToRefreshStickyRecyclerView refreshStickyRecyclerView= (PullToRefreshStickyRecyclerView) findViewById(R.id.recycler_view);
        refreshStickyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Sticky1Item> items=new ArrayList<>();
        String lastItem=null;
        final Random random=new Random();
        for(String item:Data.ITEMS){
            String word = String.valueOf(item.charAt(0));
            if(null==lastItem||!lastItem.equals(word)){
                items.add(new Sticky1Item(getStickyItems(word,4+random.nextInt(4)),word));
            } else {
                items.add(new Sticky1Item(item));
            }
            lastItem=word;
        }
        refreshStickyRecyclerView.setAdapter(new LinearSticky2ItemAdapter(this, items));
    }

    private String[] getStickyItems(String item,int n){
        String[] items=new String[n];
        for(int i=0;i<n;i++){
            items[i]=item.toUpperCase()+(i+1);
        }
        return items;
    }
}
