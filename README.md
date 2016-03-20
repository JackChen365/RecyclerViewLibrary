# RecyclerViewLibrary
A RecyclerView libirary ,has some support, like headerAdapter/TreeAdapter,and PulltoRefreen/Drag

#### gradle compile
```
dependencies {
    compile 'com.ldzs.recyclerlibrary:recyclerlibrary:1.0.1'
}
```

#### 一个RecyclerView扩展库,其中主要包含
1:针对RecyclerView数据适配器的封装</br>
2:RecyclerView上拉刷新,下拉加载</br>
3:RecyclerView拖动</br>


#### RecyclerView 自定义数据适配器
* HeaderAdapter:一个支持动态添加/移除头和尾的Adapter,不推荐单独使用.PullToRefreenceRecyclerView内己封装,可如ListView一般操作,且更灵活<br>
* ExpandAdapter:Recyclerview实现为如ExpandListView功能的数据适配器.<br>
![](https://github.com/momodae/RecyclerViewLibrary/blob/master/app/screenshot/S60214-155420.jpg)
* TreeAdapter:一个无限级的Adapter树<br>
![](https://github.com/momodae/RecyclerViewLibrary/blob/master/app/screenshot/S60214-155511.jpg)
* DynamicAdapter:完全动态化的Adapter,支持往任一位置插入自定义条目.<br>


================
#### RecyclerView 拖动(Drag)
* Linear/Grid拖动
* Dynamic条目拖动(跨度很大的任一条目支持)
* 自定义控制演示
![](https://github.com/momodae/RecyclerViewLibrary/blob/master/app/screenshot/S60214-155401.jpg)
================


================
#### RecyclerView 上拉刷新下拉加载
![](https://github.com/momodae/RecyclerViewLibrary/blob/master/app/screenshot/S60214-155428.jpg)
================

================
#### 部分实现介绍
RecyclerView#setAdapter
mAdapter==HeaderViewAdapter:RecyclerView内部维护Adapter,所以实现不影响使用者Adapter的情况下,类ListView般使用,实现like:ListView的HeaderListAdapter</br>
```java
    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter.setAdapter(adapter);
        super.setAdapter(mAdapter);
        adapter.registerAdapterDataObserver(new HeaderAdapterDataObserve(mAdapter));
    }
```
  #### HeaderAdapter:type定义
    type_header:为-1往下递减</br>
    type_footer:为12以上递加</br>
    实现动态无限添加与删除
```java
    private final int TYPE_HEADER = -1;//从-1起始开始减
    private final int TYPE_NORMAL = 0;//默认从0开始
    private final int TYPE_NORMAL_ITEM_COUNT = 12;//随意取的值,确保装饰Adapter对象不会超过此界即可
    private final int TYPE_FOOTER = TYPE_NORMAL_ITEM_COUNT + 1;
```
================
还有更难实现的DynamicAdapter等

2016/3/16 更新了cursorAdapter支持,直接拷贝了v4内的ListView的CursorAdapter的代码.稍做微改就完成了转换.因为CursorAdapter本身就是一个就是一个展示数据的adapter,并不局限于.为哪一个adapter服务.重新查看了CursorAdapter源码.发现,其数据更新推荐使用Loader,而不推荐使用CursorAdapter提供的autoQuery操作.也为我解释了.CursorAdapter的释放时机的问题.Loader自身维护Cursor的查询,数据更改查询,关闭等操作.而Cursor本身也界面也有藕合,设计更为合理.

#### 参考:RecyclerView-Animator/XRecyclerView.非常感谢.
