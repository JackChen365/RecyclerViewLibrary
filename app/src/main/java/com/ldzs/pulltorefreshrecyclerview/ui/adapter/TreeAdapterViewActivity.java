package com.ldzs.pulltorefreshrecyclerview.ui.adapter;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ldzs.pulltorefreshrecyclerview.R;
import com.ldzs.pulltorefreshrecyclerview.adapter.FileAdapter;
import com.ldzs.recyclerlibrary.adapter.tree.TreeAdapter;
import com.ldzs.recyclerlibrary.anim.FadeInDownAnimator;

import java.io.File;
import java.util.LinkedList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cz on 16/1/22.
 * 一个无限展开的RecyclerView数据适配器演示
 */
public class TreeAdapterViewActivity extends AppCompatActivity {
    private boolean isDestroy;
    private RecyclerView mRecyclerView;
    private FileAdapter mAdapter;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        setTitle(getIntent().getStringExtra("title"));
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setItemAnimator(new FadeInDownAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(100);
        mRecyclerView.getItemAnimator().setRemoveDuration(100);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.scan_files));
        progressDialog.show();
        Observable.create(sub -> sub.onNext(getAllFileNode())).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(result -> {
            if (isDestroy) return;
            progressDialog.dismiss();
            TreeAdapter.TreeNode<File> node = (TreeAdapter.TreeNode<File>) result;
            mAdapter = new FileAdapter(this, node);
            mRecyclerView.setAdapter(mAdapter);
        });
    }

    /**
     * 获取第一级文件树.懒加载,待实现
     *
     * @return
     */
    private TreeAdapter.TreeNode<File> getFileNode() {
        File rootFile = Environment.getExternalStorageDirectory();
        TreeAdapter.TreeNode<File> rootNode = new TreeAdapter.TreeNode<>(rootFile);
        File[] filesArray = rootFile.listFiles();
        if (null != filesArray) {
            int length = filesArray.length;
            for (int i = length - 1; i >= 0; i--) {
                TreeAdapter.TreeNode<File> childNode = new TreeAdapter.TreeNode<>(rootNode, filesArray[i]);
                rootNode.child.add(childNode);
            }
        }
        return rootNode;
    }

    /**
     * 获取整棵文件树,较耗时
     *
     * @return
     */
    private TreeAdapter.TreeNode<File> getAllFileNode() {
        File rootFile = Environment.getExternalStorageDirectory();
        LinkedList<File> files = new LinkedList<>();
        LinkedList<TreeAdapter.TreeNode<File>> fileNodes = new LinkedList<>();
        TreeAdapter.TreeNode<File> rootNode = new TreeAdapter.TreeNode<>(rootFile);
        fileNodes.offerFirst(rootNode);
        files.offerFirst(rootFile);
        while (!files.isEmpty()) {
            File file = files.pollFirst();
            TreeAdapter.TreeNode<File> fileTreeNode = fileNodes.removeFirst();
            if (file.isDirectory()) {
                File[] filesArray = file.listFiles();
                if (null != filesArray) {
                    int length = filesArray.length;
                    for (int i = length - 1; i >= 0; i--) {
                        files.addFirst(filesArray[i]);
                        TreeAdapter.TreeNode<File> childNode = new TreeAdapter.TreeNode<>(fileTreeNode, filesArray[i]);
                        fileNodes.addFirst(childNode);
                        fileTreeNode.child.add(childNode);
                    }
                }
            }
        }
        return rootNode;
    }

    @Override
    protected void onDestroy() {
        isDestroy = true;
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}
