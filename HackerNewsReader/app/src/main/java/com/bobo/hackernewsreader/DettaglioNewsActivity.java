package com.bobo.hackernewsreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.bobo.hackernewsreader.db.DatabaseManager;
import com.bobo.hackernewsreader.db.NewsDao;

public class DettaglioNewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_news);

        Intent intent = getIntent();

        long idNotizia = intent.getLongExtra("idNotizia", -1);
        String urlNotizia = intent.getStringExtra("urlNotizia");


        WebView contentNotiziaView = findViewById(R.id.newsContentView);
        contentNotiziaView.getSettings().setJavaScriptEnabled(true);
        contentNotiziaView.setWebChromeClient(new WebChromeClient());

        if(idNotizia == -1) //Notizia online
        {
            contentNotiziaView.loadUrl(urlNotizia); //Set url
        }else{
            NewsDao news = DatabaseManager.getNews(MainActivity.database, idNotizia);

            contentNotiziaView.loadData(news.getHtml(), "text/html", "UTF-8"); //HTML offline da DB
        }
    }
}
