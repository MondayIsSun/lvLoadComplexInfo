package com.example.listview_asynctask_lrucache.entity;

public class NewsBean {

    private String newsIconUrl;
    private String newsTitle;
    private String newsContent;

    public NewsBean(String newsIconUrl, String newsTitle, String newsContent) {
        this.newsIconUrl = newsIconUrl;
        this.newsTitle = newsTitle;
        this.newsContent = newsContent;
    }

    public String getNewsIconUrl() {
        return newsIconUrl;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsContent() {
        return newsContent;
    }
}
