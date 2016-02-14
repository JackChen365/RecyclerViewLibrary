package com.ldzs.pulltorefreshrecyclerview.ui.drag;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.ChannelAdapter;
import com.ldzs.pulltorefreshrecyclerview.model.Channel;
import com.ldzs.pulltorefreshrecyclerview.util.IOUtils;
import com.ldzs.pulltorefreshrecyclerview.util.JsonUtils;
import com.ldzs.recyclerlibrary.DragRecyclerView;
import com.ldzs.recyclerlibrary.anim.FadeInDownAnimator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cz on 16/1/27.
 */
public class CustomDragActivity extends AppCompatActivity {
    private static final String TAG = "CustomDragActivity";
    private DragRecyclerView mRecyclerView;
    private ChannelAdapter mAdapter;
    private ArrayList<Channel> channels;
    private long st;
    private boolean mEdit;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costom_drag);
        setTitle(getIntent().getStringExtra("title"));
        channels = new ArrayList<>();
        mRecyclerView = (DragRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new FadeInDownAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(300);
        mRecyclerView.getItemAnimator().setMoveDuration(300);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setRecycleChildrenOnDetach(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        Observable.create(sub -> sub.onNext(getContentFromAssets(getResources(), "item.json"))).
                map(text -> JsonUtils.getLists(text.toString(), Channel.class)).
                observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(items -> {
            int size = items.size();
            for (int i = 0; i < size; i++) {
                Channel channel = items.get(i);
                if (channel.use) {
                    channels.add(channel);
                }
            }
            mAdapter = new ChannelAdapter(this, items);
            mRecyclerView.setAdapter(mAdapter);
            View view = View.inflate(this, R.layout.recyclerview_header3, null);
            TextView editView = (TextView) view.findViewById(R.id.tv_edit);
            editView.setOnClickListener(v -> {
                mAdapter.setDragStatus(mEdit = !mEdit);
                editView.setText(mEdit ? R.string.complete : R.string.channel_sort_delete);
            });
            mRecyclerView.setOnItemClickListener((v, position) -> {
                long l = System.currentTimeMillis();
                Log.e(TAG, "click" + position + " time:" + (l - st));
                st = l;
                int itemPosition = mRecyclerView.getItemPosition(position);//获得当前条目的位置
                int count = channels.size();
                Channel item = mAdapter.getItem(itemPosition);
                if (itemPosition < count) {
                    if (mEdit) {
                        item.use = false;
                        channels.remove(item);
                        mRecyclerView.setItemMove(position, count + 1);
                    } else {
                        Toast.makeText(getApplicationContext(), "Click:" + item.name, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    item.use = true;
                    channels.add(item);
                    mRecyclerView.setItemMove(position, count + 1);
                }
//                mRecyclerView.postDelayed(() -> mRecyclerView.invalidateItemDecorations(), mRecyclerView.getItemAnimator().getMoveDuration());
            });
            mRecyclerView.addDynamicView(view, 0);
            mRecyclerView.addDynamicView(R.layout.recyclerview_header4, channels.size() + 1);
            mRecyclerView.setOnDragItemEnableListener(position -> mEdit && mAdapter.getItem(position).use);

        });

    }

    /**
     * 从asset内读取文件内容
     *
     * @param resource
     * @param fileName
     * @return
     */
    public String getContentFromAssets(Resources resource, String fileName) {
        String result = new String();
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new InputStreamReader(resource.getAssets().open(fileName)));
            String line;
            while ((line = bufReader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            result = new String();
        } finally {
            IOUtils.closeStream(bufReader);
        }
        return result;
    }


}
