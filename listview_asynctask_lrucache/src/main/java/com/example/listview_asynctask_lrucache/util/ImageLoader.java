package com.example.listview_asynctask_lrucache.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.listview_asynctask_lrucache.adapter.MyAdapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {

    /**
     * ****************************************************************************
     */

    private LruCache<String, Bitmap> mCaches;

    public ImageLoader() {
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //自己定义LruCache的缓存大小
        int cacheSize = maxMemory / 4;
        //初始化LruCache
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //sizeOf()在每次加入缓存内存的时候都会进行调用
                return value.getByteCount();
            }
        };
    }

    //将内容（Bitmap）保存到LruCache
    public void addBitmapToCache(String url, Bitmap bitmap) {
        //先判断缓存当中是否已经存在了
        if (getBitmapFromCache(url) == null) {
            mCaches.put(url, bitmap);
        }
    }

    //根据url获取到缓存中的内容（Bitmap对象）
    public Bitmap getBitmapFromCache(String url) {
        //LruCache类似于Map（其实其底层是LinkedHashMap实现的）
        return mCaches.get(url);
    }

    /**
     * ****************************************************************************
     */

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

    public void showImageByAsyncTask(final ImageView imageView, final String url) {
        //下载图片前先判断缓存当中是否已经存在该图片了
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            //下载图片，并放入缓存
            new NewsAsyncTask(imageView, url).execute(url);
        } else {
            //缓存当中已经有该图片了，直接使用缓存中的，不需要再去下载
            imageView.setImageBitmap(bitmap);
        }
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;
        private String mUrl;

        public NewsAsyncTask(ImageView imageView, String url) {
            mImageView = imageView;
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
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

}
