package com.ldzs.pulltorefreshrecyclerview.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/5/21.
 */

public class Sticky1Item {
    public final List<String> headerItems;
    public final String item;

    public Sticky1Item(String item) {
        this.item = item;
        this.headerItems=new ArrayList<>();
    }

    public Sticky1Item(String[] array, String item) {
        this.item = item;
        this.headerItems = new ArrayList<>();
        if(null!=array){
            this.headerItems.addAll(Arrays.asList(array));
        }
    }
}
