# RecyclerViewLibrary
A RecyclerView libirary ,has some support, like headerAdapter/TreeAdapter,and PulltoRefreen/Drag

#### gradle compile
```
dependencies {
    compile 'com.ldzs.recyclerlibrary:recyclerlibrary:1.6.2'
}
```

#### 一个RecyclerView扩展库,其中主要包含
1:针对RecyclerView数据适配器的封装</br>
2:RecyclerView上拉刷新,下拉加载</br>
3:RecyclerView拖动</br>

### 2016/12/28
* 增加了pv_adapterView引用,此引用功能为PullToRefreshRecyclerView xml布局内增加的控件,标记为header/footer,则直接添加到adapter的header/footer内,减少代码添加header/footer做法.但因recyclerView的延持加载机制,导致findViewById找不到控件的方式,特别提供了findAdapterView方法.</br>

### 2017/5/21
* 增加了StickyHeader的效果.功能非常强大,在原有功能基础上扩展,简化分组数据逻辑,自动同步更改.最大化减少了Adapter的逻辑代码.并支持GridLayoutManager,一共四个示例.

### 2017/7/11
* 修正了GridLayoutManager分隔线问题
* 去掉了难以配置的RefreshFooter的引用,改为一个自定义的footerStyle决定

```xml
<style name="FrameFooterView" >
        <item name="footer_footerHeight">60dp</item>
        <item name="footer_clickTextHint">@string/click_load_more</item>
        <item name="footer_textSize">14sp</item>
        <item name="footer_textColor">@color/dark_gray</item>
        <item name="footer_errorHint">@string/load_error</item>
        <item name="footer_retryItemSelector" >@drawable/btn_retry_selector</item>
        <item name="footer_complete">@string/load_complete</item>
        <item name="footer_retry" >@string/re_try</item>
        <item name="footer_load">@string/loading</item>
    </style>
```

#### RecyclerView 自定义数据适配器
* DynamicAdapter:完全动态化的Adapter,支持往任一位置插入自定义条目,使用装饰设计模式,无使用限制
   like :
     1|2|3|
     --4--  //add
     5|6|7|
     8|9|10|<p>
* ExpandAdapter:Recyclerview实现为如ExpandListView功能的数据适配器.<br>
![](https://github.com/momodae/RecyclerViewLibrary/blob/master/app/screenshot/S60214-155420.png)
* TreeAdapter:一个无限级的Adapter树<br>
![](https://github.com/momodae/RecyclerViewLibrary/blob/master/app/screenshot/S60214-155511.png)
* SelectAdapter:一个封装了用户选择的数据适配器对象,选择模式有,点击/单选/多选/块选
* HeaderAdapter:一个支持动态添加/移除头和尾的Adapter,不推荐单独使用.PullToRefreenceRecyclerView内己封装,可如ListView一般操作,且更灵活己弃用(2016/9/24己弃用)<br>


================
#### RecyclerView 拖动(Drag)
* Linear/Grid拖动
* Dynamic条目拖动(跨度很大的任一条目支持)
* 自定义示例(新闻资讯类)演示<br>
![](https://github.com/momodae/RecyclerViewLibrary/blob/master/app/screenshot/S60214-155401.png)
================


================
#### RecyclerView 上拉刷新下拉加载(刷新框架来自另一个项目<a href="https://github.com/momodae/PullToRefreshLayout" target="_blank"> [ PullToRefreshLayout ]<br>
![](https://github.com/momodae/RecyclerViewLibrary/blob/master/app/screenshot/S60214-155428.png)
================

================
#### PullToRefreshRecyclerView介绍
	PullToRefreshLayout:另一个下拉刷新的加载库
	隔离封装的Adapter支持:
	其中控件内,提供的Adapter为:SelectAdapter,层级判断为:DynamicAdapter->RefreshAdapter->SelectAdapter
	其中DynamicAdapter为负责任一元素位置插入条目的扩展数据适配器.
	RefreshAdapter为固定底部的Footer的数据适配器.
	而最上层的SelectAdapter,则提供类似ListView的selectMode选择功能的数据适配器.适配器需实现Selectable接口
#### 实现的功能为:
* 1:recyclerView的下拉刷新,上拉加载,
* 2:顶部以及,底部的控件自由添加,删除,中间任一位置控件添加,此为确保RecyclerView数据一致性.比如新闻类应用.可能为了广告,为了某些提示条目,还需要去适合到逻辑Adapter内.导致条目很难看.</br>
* 3:Adapter的条目选择功能.
* 4:类ListView的Divide封装

================

###2016/3/16 
更新了cursorAdapter支持,直接拷贝了v4内的ListView的CursorAdapter的代码.稍做微改就完成了转换.因为CursorAdapter本身就是一个就是一个展示数据的adapter,并不局限于.为哪一个adapter服务.重新查看了CursorAdapter源码.发现,其数据更新推荐使用Loader,而不推荐使用CursorAdapter提供的autoQuery操作.也为我解释了.CursorAdapter的释放时机的问题.Loader自身维护Cursor的查询,数据更改查询,关闭等操作.而Cursor本身也界面也有藕合,设计更为合理.

###2016/9/24 
* 重构了部分代码.以适应开发.将另一个新写的PullToRefreLayout引入作为下拉刷新使用,独立了下拉刷新组件,上拉加载,通过单独的Adapter支持.
* 将包装的Adapter层级更为DynamicAdapter->RefreshAdapter->SelectAdapter,DynamicAdapter为Adapter支持动态插入任一布局功能(此功能极其强大.谁用谁知道哈),RefreshAdapter为Aadpter增加,底部刷新,以及其他布局支持.SelectAdapter则为布局增加了条目选择功能.每种布局又可以单独作为装饰器对象.直接使用.当然更推荐直接采用PullToRefreshRecyclerView

###2016/10/10
* 增加了部分listfood自定义属性.因为是布局,所以在自己的样式里.必须加入,不清楚好是不好...如果不看,引入项目,使用会报错,感觉好像不太好.
```xml
        <item name="refresh_footerHeight">40dp</item>
        <item name="refresh_clickTextHint">@string/click_load_more</item>
        <item name="refresh_textSize">14sp</item>
        <item name="refresh_textColor">@color/blue</item>
        <item name="refresh_errorHint">@string/load_error</item>
        <item name="refresh_retryItemSelector" >@drawable/btn_retry_selector</item>
        <item name="refresh_retry" >@string/re_try</item>
```
#### 参考:RecyclerView-Animator实现动画.非常感谢.
