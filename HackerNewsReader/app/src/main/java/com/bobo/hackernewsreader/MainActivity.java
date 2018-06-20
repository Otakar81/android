package com.bobo.hackernewsreader;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bobo.hackernewsreader.api.HackerNewsClient;
import com.bobo.hackernewsreader.db.NewsDao;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creo il database
        database = this.openOrCreateDatabase("news_db", Context.MODE_PRIVATE, null);

        //Lista news
        final ArrayList<NewsDao> elencoNotizie;

        //Recupero la listView e la popolo con l'elenco nelle news
        ListView listaNewsView = findViewById(R.id.listNews);

        try {

            elencoNotizie = HackerNewsClient.getListNewsStub(HackerNewsClient.TYPE_TOP, false);

            //Creo e popolo l'adapter
            listaNewsView.setAdapter(new ArrayAdapter<NewsDao>(this, android.R.layout.simple_list_item_1, elencoNotizie));

            listaNewsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    NewsDao notizia = elencoNotizie.get(i);

                    String urlNotizia = notizia.getUrl();

                    Toast.makeText(MainActivity.this, urlNotizia, Toast.LENGTH_SHORT).show();

                    //Creo l'intent verso la activity di dettaglio
                    Intent intent = new Intent(MainActivity.this, DettaglioNewsActivity.class);
                    intent.putExtra("urlNotizia", urlNotizia);
                    intent.putExtra("idNotizia", notizia.getId());
                    startActivity(intent);
                }
            });


            if(elencoNotizie.size() == 0)
                Toast.makeText(MainActivity.this, "Non sono state trovate notizie online", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(MainActivity.this, "Si è verificato un errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    } //Fine onCreate
}