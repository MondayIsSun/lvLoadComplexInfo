package com.example.listview_asynctask_lrucache_scrolloptimization.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.listview_asynctask_lrucache_scrolloptimization.R;
import com.example.listview_asynctask_lrucache_scrolloptimization.adapter.MyAdapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ImageLoader {

    private ListView mListView;
    private Set<NewsAsyncTask> mTask;

    private LruCache<String, Bitmap> mCaches;

    public ImageLoader(ListView listView) {

        mListView = listView;
        mTask = new HashSet<>();

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mCaches.put(url, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String url) {
        return mCaches.get(url);
    }

    //执行Http操作下载图片
    public Bitmap getBitmapFromUrl(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            conn.disconnect();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //这个方法已经不再控制图片的下载了
    public void showImageByAsyncTask(final ImageView imageView, final String url) {
        //下载图片前先判断缓存当中是否已经存在该图片了
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            //设置ImageView的默认图片
            imageView.setImageResource(R.mipmap.ic_launcher);
            //至于什么时候显示真正的图片，等后台线程异步下载...
        } else {
            //缓存当中已经有该图片了，直接使用缓存中的，不需要再去下载
            imageView.setImageBitmap(bitmap);
        }
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;
        private String mUrl;

        public NewsAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            //下载图片
            Bitmap bitmap = getBitmapFromUrl(url);
            //保存在缓存中
            if (bitmap != null) {
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }

    //优化ListView滚动的加载图片的方法——》自己控制滚动的时候才触发
    public void loadImages(int start, int end) {
        for (int i = start; i < end; i++) {
            String url = MyAdapter.URLS[i];

            //先判断LruCache当中是否已缓存该图片了
            Bitmap bitmap = getBitmapFromCache(url);
            if (bitmap == null) {
                //下载图片，并放入缓存
                NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);
                mTask.add(task);
            } else {
                //缓存当中已经有该图片了，直接使用缓存中的，不需要再去下载
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void cancelAllTasks() {
        if (mTask != null) {
            for (NewsAsyncTask task : mTask) {
                task.cancel(false);
            }
        }
    }

}
