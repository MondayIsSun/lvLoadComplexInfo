package com.example.listview_asynctask_lrucache;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.listview_asynctask_lrucache.adapter.MyAdapter;
import com.example.listview_asynctask_lrucache.entity.NewsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    /**
     * Lru ——》 Least Recently Used 近期最少使用算法
     */

    private ListView mListView;
    private static String URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.lv_main);

        new NewsAsyncTask().execute(URL);
    }

    //获取网络JSON信息数据
    private List<NewsBean> getJSONData(String url) {
        List<NewsBean> newsBeanList = new ArrayList<>();
        try {
            //1、new URL(url).openStream()打开一个InputStream——》读取流当中信息（用相应的处理流的方式来解析流信息）
            String jsonString = readStream(new URL(url).openStream());
            //Log.d("xys", jsonString);

            //2、开始解析读取到的JSON字符串
            JSONObject jsonObject;
            JSONArray jsonArray;
            NewsBean newsBean;
            try {
                jsonObject = new JSONObject(jsonString);
                jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String newsIconUrl = jsonObject.getString("picSmall");
                    String newsTitle = jsonObject.getString("name");
                    String newsContent = jsonObject.getString("description");
                    newsBean = new NewsBean(newsIconUrl, newsTitle, newsContent);
                    newsBeanList.add(newsBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return newsBeanList;
    }

    //使用流来读取网络传输的信息——》一切都是二进制，一切都是流
    private String readStream(InputStream is) {
        InputStreamReader isr = null;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //通过异步任务处理来获取JSON信息
    class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {
        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJSONData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> list) {
            mListView.setAdapter(new MyAdapter(MainActivity.this, list));
        }
    }
}
