package com.example.listview_asynctask.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoader {

    private ImageView mImageView;
    private String mUrl;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public Bitmap getBitmapFromUrl(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream());
            //Stream ——》Bitmap
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

    /**
     * @param imageView
     * @param url
     */
    public void showImageByThread(final ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getBitmapFromUrl(url);
                Message msg = Message.obtain();
                msg.obj = bitmap;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    public void showImageByAsyncTask(final ImageView imageView, final String url) {
        new NewsAsyncTask(imageView, url).execute(url);
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
            return getBitmapFromUrl(params[0]);
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
