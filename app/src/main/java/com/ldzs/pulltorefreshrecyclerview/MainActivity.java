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
    private PullToRefreshRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (PullToRefreshRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.getItemAnimator().setAddDuration(300);
        recyclerView.getItemAnimator().setRemoveDuration(300);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SimpleAdapter adapter = SimpleAdapter.createFromResource(this, R.array.List);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnItemClickListener((v, position) -> {
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
