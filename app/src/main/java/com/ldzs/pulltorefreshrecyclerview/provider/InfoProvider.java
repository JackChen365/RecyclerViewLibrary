package com.ldzs.pulltorefreshrecyclerview.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;

import com.ldzs.pulltorefreshrecyclerview.db.DbTable;
import com.ldzs.pulltorefreshrecyclerview.db.MyDb;
import com.ldzs.pulltorefreshrecyclerview.model.DbItem;

import java.util.HashMap;

/**
 * Created by momo on 2015/1/1.
 * 统计信息内容提供者
 */
public class InfoProvider extends ContentProvider {
    private UriMatcher matcher;
    private SparseArray<String> matchIds;
    private SparseArray<HashMap<String, String>> selectionMaps;
    public static SQLiteDatabase db;// 数据库操作对象

    public InfoProvider() {
        super();
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matchIds = new SparseArray<>();
        selectionMaps = new SparseArray<>();
        Class<? extends DbItem>[] classes = DbTable.CLASSES;
        for (int i = 0; i < classes.length; i++) {
            String table = DbTable.getTable(classes[i]);
            //添加匹配uri
            matcher.addURI(DbTable.AUTHORITY, table, i + 1);
            //添加匹配表名
            matchIds.append(i + 1, table);

            //添加selectionMap
            String[] selection = DbTable.getSelection(classes[i]);
            HashMap<String, String> selectionMap = new HashMap<>();
            for (int s = 0; s < selection.length; s++) {
                selectionMap.put(selection[s], selection[s]);
            }
            selectionMaps.append(i + 1, selectionMap);
        }
    }

    @Override
    public boolean onCreate() {
        db = new MyDb(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        int matchId = matcher.match(uri);
        String tableName = matchIds.get(matchId);
        HashMap<String, String> map = selectionMaps.get(matchId);
        if (TextUtils.isEmpty(tableName) || null == map) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        builder.setTables(tableName);
        builder.setProjectionMap(map);
        // 判断uid
        Cursor cursor = null;
        try {
            cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != cursor) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int matchId = matcher.match(uri);
        String tableName = matchIds.get(matchId);
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        ContentValues contentValues;
        if (null != values) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }
        long rowId = db.replace(tableName, null, contentValues);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        return null;
        // throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int matchId = matcher.match(uri);
        String tableName = matchIds.get(matchId);
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        int count = -1;
        if (!TextUtils.isEmpty(tableName) && null != db) {
            try {
                count = db.delete(tableName, selection, selectionArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (-1 != count) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int matchId = matcher.match(uri);
        String tableName = matchIds.get(matchId);
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        int count = -1;
        if (!TextUtils.isEmpty(tableName)) {
            try {
                count = db.update(tableName, values, selection, selectionArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (-1 != count) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int matchId = matcher.match(uri);
        String tableName = matchIds.get(matchId);
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        long lastId = -1;
        if (!TextUtils.isEmpty(tableName) && null != db) {
            db.beginTransaction();
            for (int i = 0; i < values.length; i++) {
                long rowId = db.replace(tableName, null, values[i]);
                if (i == values.length - 1) {
                    lastId = rowId;
                }
                if (0 >= rowId) {
                    //异常插入
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        if (lastId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, lastId);
            getContext().getContentResolver().notifyChange(noteUri, null);
        }
        return (int) lastId;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
