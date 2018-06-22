package com.bobo.hackernewsreader.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

public class DatabaseManager {

    public static void createTables(SQLiteDatabase database)
    {
        //Creo la tabella
        database.execSQL("CREATE TABLE IF NOT EXISTS news (id INTEGER PRIMARY KEY, title VARCHAR, url VARCHAR, html VARCHAR, time VARCHAR)");
    }

    public static void insertNews(SQLiteDatabase database, NewsDao news)
    {
        SQLiteStatement stmt = database.compileStatement("INSERT INTO news (title, url, html, time) VALUES (?, ?, ?, ?)");
        stmt.bindString(1, news.getTitle());
        stmt.bindString(2, news.getUrl());
        stmt.bindString(3, news.getHtml());
        stmt.bindString(4, news.getTimestamp());
        stmt.execute();

        /*
        String sql = "INSERT INTO news (title, url, html, time) " +
                        "VALUES ('" + news.getTitle() + "', '" + news.getUrl() + "', '" + news.getHtml() + "', '" + news.getTimestamp() + "')";

        database.execSQL(sql);
        */
    }

    public static void deleteAllNews(SQLiteDatabase database)
    {
        database.execSQL("DELETE FROM news");
    }

    public static void deleteNews(SQLiteDatabase database, long idNews)
    {
        database.execSQL("DELETE FROM news where id = " + idNews);
    }

    public static ArrayList<NewsDao> getAllNews(SQLiteDatabase database)
    {
        ArrayList<NewsDao> result = new ArrayList<NewsDao>();

        Cursor c = database.rawQuery("SELECT * FROM news", null);

        int idIndex = c.getColumnIndex("id");
        int titleIndex = c.getColumnIndex("title");
        int urlIndex = c.getColumnIndex("url");
        int htmlIndex = c.getColumnIndex("html");
        int timeIndex = c.getColumnIndex("time");

        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            String title = c.getString(titleIndex);
            String url = c.getString(urlIndex);
            String html = c.getString(htmlIndex);
            String time = c.getString(timeIndex);

            NewsDao news = new NewsDao( id, title, url, html, time);

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
        int timeIndex = c.getColumnIndex("time");

        if (c.moveToNext())
        {
            String title = c.getString(titleIndex);
            String url = c.getString(urlIndex);
            String html = c.getString(htmlIndex);
            String time = c.getString(timeIndex);

            result = new NewsDao( id, title, url, html, time);
        }

        c.close();

        return result;
    }
}
