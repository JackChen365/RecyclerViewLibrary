package com.ldzs.pulltorefreshrecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleAdapter;
import com.ldzs.pulltorefreshrecyclerview.model.ListItem;
import com.ldzs.pulltorefreshrecyclerview.xml.ListItemReader;
import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView;
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cz on 16/1/27.
 */
public class ListActivity extends AppCompatActivity {
    private PullToRefreshRecyclerView mRecyclerView;
    private SimpleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(300);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        setTitle(getIntent().getStringExtra("title"));
        final int index = getIntent().getIntExtra("position", 0);
        Observable.create(sub -> sub.onNext(new ListItemReader().read(getApplicationContext()))).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(items -> {
            HashMap<Integer, ArrayList<ListItem>> configs = (HashMap<Integer, ArrayList<ListItem>>) items;
            List<ListItem> listItems = configs.get(index);
            mRecyclerView.setAdapter(mAdapter = new SimpleAdapter(this, listItems));
        });
        mRecyclerView.setOnItemClickListener((v, position) -> {
            try {
                ListItem listItem = (ListItem) mAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("title", listItem.name);
                intent.setClass(this, Class.forName(getPackageName() + listItem.clazz));
                startActivity(intent);
            } catch (ClassNotFoundException e) {
            }
        });
    }
}
