package com.bobo.hackernewsreader.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DatabaseManager {

    public static void createTables(SQLiteDatabase database)
    {
        //Creo la tabella
        database.execSQL("CREATE TABLE IF NOT EXISTS news (id INTEGER PRIMARY KEY, title VARCHAR, url VARCHAR, html VARCHAR)");
    }

    public static void insertNews(SQLiteDatabase database, NewsDao news)
    {
        String sql = "INSERT INTO news (title, url, html) " +
                        "VALUES ('" + news.getTitle() + "', '" + news.getUrl() + "', '" + news.getHtml() + "')";

        database.execSQL(sql);
    }

    public static void deleteAllaNews(SQLiteDatabase database)
    {
        database.execSQL("DELETE FROM news");
    }

    public static ArrayList<NewsDao> getAllNews(SQLiteDatabase database)
    {
        ArrayList<NewsDao> result = new ArrayList<NewsDao>();

        Cursor c = database.rawQuery("SELECT * FROM news", null);

        int idIndex = c.getColumnIndex("id");
        int titleIndex = c.getColumnIndex("title");
        int urlIndex = c.getColumnIndex("url");
        int htmlIndex = c.getColumnIndex("html");

        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            String title = c.getString(titleIndex);
            String url = c.getString(urlIndex);
            String html = c.getString(htmlIndex);

            NewsDao news = new NewsDao( id, title, url, html);

            result.add(news);
        }

        c.close();

        return result;
    }

    public static NewsDao getNews(SQLiteDatabase database, long id)
    {
        NewsDao result = null;

        Cursor c = database.rawQuery("SELECT * FROM news where id = " + id, null);

        int idIndex = c.getColumnIndex("id");
        int titleIndex = c.getColumnIndex("title");
        int urlIndex = c.getColumnIndex("url");
        int htmlIndex = c.getColumnIndex("html");

        if (c.moveToNext())
        {
            String title = c.getString(titleIndex);
            String url = c.getString(urlIndex);
            String html = c.getString(htmlIndex);

            result = new NewsDao( id, title, url, html);
        }

        c.close();

        return result;
    }
}
