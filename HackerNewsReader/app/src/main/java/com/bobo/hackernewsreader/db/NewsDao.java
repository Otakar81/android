package com.bobo.hackernewsreader.db;

public class NewsDao {

    private long id;
    private String title;
    private String url;
    private String html;

    public NewsDao(long id, String title, String url, String html)
    {
        this.id = id;
        this.title = title;
        this.url = url;
        this.html = html;
    }

    public NewsDao(String title, String url)
    {
        this.id = -1;
        this.title = title;
        this.url = url;
        this.html = null;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getHtml() {
        return html;
    }

    @Override
    public String toString() {
        return title;
    }
}
