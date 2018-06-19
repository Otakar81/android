package com.bobo.hackernewsreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bobo.hackernewsreader.api.HackerNewsClient;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Recupero la listView e la popolo con l'elenco nelle news
        ListView listaNewsView = findViewById(R.id.listNews);

        try {

            ArrayList<String> elencoNotizie = HackerNewsClient.getListNews(HackerNewsClient.TYPE_TOP);

            //Creo e popolo l'adapter
            listaNewsView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elencoNotizie));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}