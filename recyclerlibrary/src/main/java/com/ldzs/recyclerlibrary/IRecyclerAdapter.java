package com.ldzs.recyclerlibrary;

import java.util.List;

/**
 * Created by Administrator on 2017/5/21.
 */

public interface IRecyclerAdapter<E> {
    List<E> getItems();

    E getItem(int position);
}
