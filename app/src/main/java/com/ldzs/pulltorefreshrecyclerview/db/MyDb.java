package com.ldzs.pulltorefreshrecyclerview.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.ldzs.pulltorefreshrecyclerview.model.DbItem;

import java.lang.reflect.Field;

/**
 * @author momo
 * @Date 2014/9/20
 */
public class MyDb extends SQLiteOpenHelper {
    private static final String DB_NAME = "my_db";
    private static final int CURRENT_VERSION = 1;

    public MyDb(Context context) {
        super(context, DB_NAME, null, CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        Class<? extends DbItem>[] classes = DbTable.CLASSES;
        for (int i = 0; i < classes.length; i++) {
            Class<? extends DbItem> clazz = classes[i];
            Field[] fields = clazz.getDeclaredFields();
            boolean primaryKey = true;
            Table table = clazz.getAnnotation(Table.class);
            if (null != table) {
                primaryKey = table.primaryKey();
            }
            String sql = "CREATE TABLE " + DbTable.getTable(clazz) + "(";
            for (int k = 0; k < fields.length; k++) {
                String fieldName;
                TableField tableField = fields[k].getAnnotation(TableField.class);
                if (null != tableField && !TextUtils.isEmpty(tableField.value())) {
                    fieldName = tableField.value();
                } else {
                    fieldName = fields[k].getName();
                }
                Class<?> type = fields[k].getType();
                String fieldType;
                if (int.class == type || short.class == type || Integer.class == type || Short.class == type) {
                    fieldType = " INTEGER";
                } else if (float.class == type || double.class == type || Float.class == type || Double.class == type) {
                    fieldType = " FLOAT";
                } else if (boolean.class == type || Boolean.class == type) {
                    fieldType = " BOOLEAN";
                } else if (long.class == type || Long.class == type) {
                    fieldType = " LONG";
                } else {
                    fieldType = " TEXT";
                }
                //过滤字段
                FieldFilter fieldFilter = fields[k].getAnnotation(FieldFilter.class);
                //主键
                if (0 == k && primaryKey) {
                    sql += ("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
                }
                if (null != fieldFilter && fieldFilter.enable()) {
                    if (fields.length - 1 == k) {
                        sql = sql.substring(0, sql.length() - 1) + ")";
                    }
                } else {
                    sql += (fieldName + " " + fieldType + (fields.length - 1 != k ? "," : ")"));
                }
            }
            db.execSQL(sql);
        }
        //统计界面
        onUpgrade(db, db.getVersion(), CURRENT_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
