package com.bobo.hackernewsreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bobo.hackernewsreader.api.HackerNewsClient;
import com.bobo.hackernewsreader.db.DatabaseManager;
import com.bobo.hackernewsreader.db.NewsDao;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static SQLiteDatabase database;
    ListView listaNewsView;
    ArrayList<NewsDao> currentOnlineNews;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Aggiungo il menù nell'action bar
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.refreshOnlineDataButton:

                //Recupero tutte le top news dalle api
                Toast.makeText(MainActivity.this, "Sto caricando i dati, prego attendere", Toast.LENGTH_LONG).show();

                try {

                    currentOnlineNews = HackerNewsClient.getListNews(HackerNewsClient.TYPE_NEW, false, false);
                    refreshListaNewsView(currentOnlineNews);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Si è verificato un errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                return true;


            case R.id.showOnlineData:

                refreshListaNewsView(currentOnlineNews);
                Toast.makeText(MainActivity.this, "Mostro le ultime notizie", Toast.LENGTH_SHORT).show();

                return true;


            case R.id.showOfflineData:

                ArrayList<NewsDao> listaNews = new ArrayList<NewsDao>();

                try {

                    listaNews = DatabaseManager.getAllNews(database);
                    refreshListaNewsView(listaNews);

                    Toast.makeText(MainActivity.this, "Mostro le notizie offline", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Si è verificato un errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                return true;


            default:
                return false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creo il database
        database = this.openOrCreateDatabase("news_db", Context.MODE_PRIVATE, null);

        //Se non esistono, creo le tabelle
        DatabaseManager.createTables(database);

        //Recupero la listView e la popolo con l'elenco nelle news
        listaNewsView = findViewById(R.id.listNews);

        Toast.makeText(MainActivity.this, "Sto caricando le ultime notizie", Toast.LENGTH_SHORT).show();

        try {

            //Inizializzo la lista delle news onllne
            final ArrayList<NewsDao> elencoNews = HackerNewsClient.getListNews(HackerNewsClient.TYPE_NEW, false, false);
            currentOnlineNews = (ArrayList<NewsDao>) elencoNews.clone();

            //Creo e popolo l'adapter
            listaNewsView.setAdapter(new ArrayAdapter<NewsDao>(this, android.R.layout.simple_list_item_1, elencoNews));

            listaNewsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    NewsDao notizia = elencoNews.get(i); //TODO Funziona anche per quelle offline, o va gestito diversamente?

                    //Creo l'intent verso la activity di dettaglio
                    Intent intent = new Intent(MainActivity.this, DettaglioNewsActivity.class);
                    intent.putExtra("urlNotizia", notizia.getUrl());
                    intent.putExtra("idNotizia", notizia.getId());
                    intent.putExtra("titoloNotizia", notizia.getTitle());
                    intent.putExtra("timestampNotizia", notizia.getTimestamp());

                    startActivity(intent);
                }
            });

            listaNewsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                    final NewsDao notizia = elencoNews.get(position);

                    if(notizia.getId() != -1) //Solo per le notizie offline
                    {
                        //Apro una dialog per confermare l'eliminazione
                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Eliminare la news?")
                                .setMessage("Sei sicuro di voler eliminare la news?")
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                                DatabaseManager.deleteNews(database, notizia.getId());

                                                ArrayList<NewsDao> listaNews =  DatabaseManager.getAllNews(database);
                                                refreshListaNewsView(listaNews);

                                                Toast.makeText(MainActivity.this, "News eliminata", Toast.LENGTH_SHORT).show();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(MainActivity.this, "Si è verificato un errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();

                    }

                    return true;
                }
            });


            if(currentOnlineNews.size() == 0)
                Toast.makeText(MainActivity.this, "Non sono state trovate notizie online", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(MainActivity.this, "Si è verificato un errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    } //Fine onCreate


    private void refreshListaNewsView(ArrayList<NewsDao> elencoNews)
    {
        ArrayAdapter<NewsDao> adapter = (ArrayAdapter<NewsDao>)listaNewsView.getAdapter();
        adapter.clear();
        adapter.addAll(elencoNews);

        adapter.notifyDataSetChanged();
    }

}