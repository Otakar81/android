package com.bobo.iamhere;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_content);

        Intent intent = getIntent();

        String urlEsterno = intent.getStringExtra("url");
        String htmlEmbed = intent.getStringExtra("html");

        //Inizializzo il webContent e mostro il contenuto della notizia a video
        WebView contentNotiziaView = findViewById(R.id.webContentView);
        contentNotiziaView.getSettings().setJavaScriptEnabled(true);
        contentNotiziaView.setWebChromeClient(new WebChromeClient());

        if(urlEsterno != null && urlEsterno.trim().length() > 0) //Url esterno
        {
            contentNotiziaView.loadUrl(urlEsterno); //Set url
        }else{
            contentNotiziaView.loadData(htmlEmbed, "text/html", "UTF-8"); //HTML offline da DB
        }
    }
}
