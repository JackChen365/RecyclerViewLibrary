package com.ldzs.pulltorefreshrecyclerview.ui.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.CursorAdapter;
import com.ldzs.pulltorefreshrecyclerview.data.Data;
import com.ldzs.pulltorefreshrecyclerview.db.DbTable;
import com.ldzs.pulltorefreshrecyclerview.model.WordItem;
import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView;

/**
 * Created by cz on 16/3/15.
 * cursorLoader演示
 */
public class CursorAdapterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private PullToRefreshRecyclerView recyclerView;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursor_adapter);
        recyclerView = (PullToRefreshRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ContentResolver contentResolver = getContentResolver();
        Uri uri = DbTable.getUri(WordItem.class);

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, new String[]{"_id", "word"}, null, null, null);
            if (0 >= cursor.getCount()) {
                //插入数据
                final int count = 10;
                String[] items = Data.ITEMS;
                ContentValues[] values = new ContentValues[count];
                for (int i = 0; i < count; i++) {
                    values[i] = new ContentValues();
                    values[i].put("word", items[i]);
                }
                contentResolver.bulkInsert(uri, values);
                if (null != cursor) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        recyclerView.setAdapter(cursorAdapter = new CursorAdapter(this, null));
        findViewById(R.id.btn_add_header).setOnClickListener(v -> {
            ContentValues value = new ContentValues();
            value.put("word", "word:" + cursorAdapter.getItemCount());
            contentResolver.insert(uri, value);
        });
        findViewById(R.id.btn_remove_item).setOnClickListener(v -> {
            Cursor cursor1 = cursorAdapter.getCursor(0);
            String text = cursor1.getString(1);
            contentResolver.delete(uri, "word=?", new String[]{text});
        });
        findViewById(R.id.btn_update_item).setOnClickListener(v -> {
            Cursor cursor1 = cursorAdapter.getCursor(0);
            String text = cursor1.getString(1);
            ContentValues values = new ContentValues();
            values.put("word", "textChange");
            contentResolver.update(uri, values, "word=?", new String[]{text});
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DbTable.getUri(WordItem.class), DbTable.getSelection(WordItem.class), null, null, "_id ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
