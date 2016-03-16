package com.ldzs.pulltorefreshrecyclerview.db;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.ldzs.pulltorefreshrecyclerview.model.DbItem;
import com.ldzs.pulltorefreshrecyclerview.model.WordItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by cz on 2015/1/1.
 */
public class DbTable {
    public static final String AUTHORITY = "com.ldzs.pulltorefreshrecyclerview";
    private static final HashMap<Class<? extends DbItem>, String[]> CLASS_SELECTIONS;
    public static final Class<? extends DbItem>[] CLASSES;
    private static final ArrayList<Class<?>> CLASS_LISTS;
    public static final String SEPARATE = "!SEPARATE";//设定的通用数据库分隔符,为防止重复
    //数据库操作
    public static final int INSERT = 0;//插入
    public static final int REMOVE = 1;//移除
    public static final int UPDATE = 2;//更新
    public static final int QUERY = 3;//查询

    @IntDef(value = {INSERT, REMOVE, UPDATE, QUERY})
    public @interface OP {
    }


    static {
        CLASS_SELECTIONS = new HashMap<>();
        CLASSES = new Class[]{WordItem.class};
        CLASS_LISTS = new ArrayList<>(CLASSES.length);
        CLASS_LISTS.addAll(Arrays.asList(CLASSES));
        for (int i = 0; i < CLASSES.length; i++) {
            Class<? extends DbItem> clazz = CLASSES[i];
            CLASS_SELECTIONS.put(clazz, getSelection(clazz));
        }
    }


    /**
     * 获得对象字段列
     *
     * @param clazz
     * @return
     */
    public static <T extends DbItem> String[] getSelection(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        ArrayList<String> selectionLists = new ArrayList<>();
        selectionLists.add("_id");
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String fieldName;
            TableField tableField = fields[i].getAnnotation(TableField.class);
            if (null != tableField && !TextUtils.isEmpty(tableField.value())) {
                fieldName = tableField.value();
            } else {
                fieldName = fields[i].getName();
            }
            FieldFilter fieldFilter = fields[i].getAnnotation(FieldFilter.class);
            if (null == fieldFilter || !fieldFilter.enable()) {
                selectionLists.add(fieldName);
            }
        }
        return selectionLists.toArray(new String[selectionLists.size()]);
    }

    public static String getTable(Class<? extends DbItem> clazz) {
        String tableName;
        Table table = clazz.getAnnotation(Table.class);
        if (null != table) {
            tableName = table.value();
        } else {
            tableName = clazz.getSimpleName();
        }
        return tableName;
    }

    /**
     * 根据对象获得指定数据库ContentValues对象
     *
     * @param item
     * @return
     * @throws IllegalAccessException
     */
    public static ContentValues getContentValue(DbItem item) throws IllegalAccessException {
        ContentValues values = null;
        Class<?> clazz = item.getClass();
        if (CLASS_LISTS.contains(clazz)) {
            values = new ContentValues();
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                String name;
                TableField tableField = field.getAnnotation(TableField.class);
                if (null != tableField && !TextUtils.isEmpty(tableField.value())) {
                    name = tableField.value();
                } else {
                    name = field.getName();
                }
                Class<?> type = field.getType();
                FieldFilter fieldFilter = fields[i].getAnnotation(FieldFilter.class);
                if (null == fieldFilter || !fieldFilter.enable()) {
                    if (int.class == type || Integer.class == type) {
                        values.put(name, field.getInt(item));
                    } else if (short.class == type || Short.class == type) {
                        values.put(name, field.getShort(item));
                    } else if (float.class == type || Float.class == type) {
                        values.put(name, field.getFloat(item));
                    } else if (double.class == type || Double.class == type) {
                        values.put(name, field.getDouble(item));
                    } else if (boolean.class == type || Boolean.class == type) {
                        values.put(name, field.getBoolean(item));
                    } else if (long.class == type || Long.class == type) {
                        values.put(name, field.getLong(item));
                    } else if (null != field.get(item)) {
                        values.put(name, field.get(item).toString());
                    }
                }
            }
        }
        return values;
    }

    /**
     * 根据class获得访问uri地址
     *
     * @param clazz
     * @return
     */
    public static Uri getUri(Class<? extends DbItem> clazz) {
        return Uri.parse("content://" + AUTHORITY + "/" + getTable(clazz));
    }
}
