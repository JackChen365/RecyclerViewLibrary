package com.ldzs.pulltorefreshrecyclerview.model;

/**
 * Created by cz on 16/1/27.
 */
public class ListItem {
    public String name;
    public String clazz;

    public ListItem() {
    }

    public ListItem(String name, String clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return name;
    }
}
