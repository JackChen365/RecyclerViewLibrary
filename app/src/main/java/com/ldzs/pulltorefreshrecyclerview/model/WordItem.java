package com.ldzs.pulltorefreshrecyclerview.model;

import com.ldzs.pulltorefreshrecyclerview.db.Table;

/**
 * Created by cz on 16/3/15.
 */
@Table("word")
public class WordItem implements DbItem{
    public String word;
}
