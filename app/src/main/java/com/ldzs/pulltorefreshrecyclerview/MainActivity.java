package com.ldzs.pulltorefreshrecyclerview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleAdapter;
import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView;
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator;

public class MainActivity extends AppCompatActivity {
    private PullToRefreshRecyclerView mRecyclerView;

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
        SimpleAdapter adapter = SimpleAdapter.createFromResource(this, R.array.List);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setOnItemClickListener((v, position) -> {
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("title", adapter.getItem(position).toString());
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Uri uri = Uri.parse("https://github.com/momodae");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
