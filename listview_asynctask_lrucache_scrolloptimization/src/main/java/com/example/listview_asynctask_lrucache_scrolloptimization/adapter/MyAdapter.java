package com.example.listview_asynctask_lrucache_scrolloptimization.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.listview_asynctask_lrucache_scrolloptimization.R;
import com.example.listview_asynctask_lrucache_scrolloptimization.entity.NewsBean;
import com.example.listview_asynctask_lrucache_scrolloptimization.util.ImageLoader;

import java.util.List;

public class MyAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    //对ListView滚动优化需要的成员变量
    private int mStart, mEnd;
    public static String[] URLS;

    private boolean mFirstIn;

    /**
     * ***************************************************************************************
     */

    private Context mContext;
    private List<NewsBean> mList;
    private LayoutInflater mInflater;

    private ImageLoader mImageLoader;

    public MyAdapter(Context context, List<NewsBean> list, ListView listView) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = new ImageLoader(listView);

        //对URLS进行初始化
        URLS = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            URLS[i] = list.get(i).getNewsIconUrl();
        }
        listView.setOnScrollListener(this);

        //在初始化的时候进行初始值的设置——》表示这个ListView确实是第一次启动
        mFirstIn = true;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_title;
        public TextView tv_content;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lv_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NewsBean newsBean = mList.get(position);

        //设置默认的图片
        viewHolder.iv_icon.setImageResource(R.mipmap.ic_launcher);

        viewHolder.iv_icon.setTag(newsBean.getNewsIconUrl());
        mImageLoader.showImageByAsyncTask(viewHolder.iv_icon, newsBean.getNewsIconUrl());

        viewHolder.tv_title.setText(newsBean.getNewsTitle());
        viewHolder.tv_content.setText(newsBean.getNewsContent());

        return convertView;
    }

    /**
     * ***************************************************************************************
     */

    //实现ListView在滚动的时候不异步加载资源，停止滚动的时候再去异步加载对应的资源文件

    //所以需要去监听滚动事件
    //——》注意我们在实现接口的时候要去实现AbsListView这个类下面的OnScrollListener接口
    //——》表示ListView的滚动监听接口

    //把下载图片的功能交给了滚动，而不再是mImageLoader.showImageByAsyncTask()方法了
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //注意这个方法在第一次进入到ListView的时候是不会被调用的
        if (scrollState == SCROLL_STATE_IDLE) {
            //当滚动停止之后才开始异步加载
            mImageLoader.loadImages(mStart, mEnd);
        } else {
            //其他状态（处于正在滚动过程中）取消所有的异步加载操作
            mImageLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //这个方法在第一次进入到ListView的时候会被调用
        //System.out.println("firstVisibleItem = "+firstVisibleItem+" visibleItemCount = "+visibleItemCount+" totalItemCount = "+totalItemCount);
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        if (mFirstIn && visibleItemCount > 0) {//mFirstIn 控制第一次加载ListView的时候执行异步操作加载第一屏的图片信息
            mImageLoader.loadImages(mStart, mEnd);
            mFirstIn = false;
        }
    }
}
