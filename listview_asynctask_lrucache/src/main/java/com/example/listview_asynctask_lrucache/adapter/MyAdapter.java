package com.example.listview_asynctask_lrucache.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.listview_asynctask_lrucache.R;
import com.example.listview_asynctask_lrucache.entity.NewsBean;
import com.example.listview_asynctask_lrucache.util.ImageLoader;

import java.util.List;

public class MyAdapter extends BaseAdapter {


    private Context mContext;
    private List<NewsBean> mList;
    private LayoutInflater mInflater;

    private ImageLoader mImageLoader;

    public MyAdapter(Context context, List<NewsBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = new ImageLoader();
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
}
