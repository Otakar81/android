package com.bobo.hackernewsreader.db;

public class NewsDao {

    private long id;
    private String title;
    private String url;
    private String html;
    private String timestamp;

    public NewsDao(long id, String title, String url, String html, String timestamp)
    {
        this.id = id;
        this.title = title;
        this.url = url;
        this.html = html;
        this.timestamp = timestamp;
    }

    public NewsDao(String title, String url, String html, String timestamp)
    {
        this.id = -1;
        this.title = title;
        this.url = url;
        this.html = html;
        this.timestamp = timestamp;
    }

    public NewsDao(String title, String url, String timestamp)
    {
        this.id = -1;
        this.title = title;
        this.url = url;
        this.html = null;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return title + "\n" + timestamp;
    }

}
