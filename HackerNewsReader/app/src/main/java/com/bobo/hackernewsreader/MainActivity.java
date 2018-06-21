package com.bobo.hackernewsreader;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static SQLiteDatabase database;
    ListView listaNewsView;

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

        ArrayList<NewsDao> listaNews = new ArrayList<NewsDao>();

        switch (item.getItemId())
        {
            case R.id.refreshOfflineDataButton:

                //Recupero tutte le top news dalle api
                Toast.makeText(MainActivity.this, "Sto caricando i dati, prego attendere qualche minuto", Toast.LENGTH_LONG).show();


                try {

                    listaNews = HackerNewsClient.getListNews(HackerNewsClient.TYPE_TOP, false, true);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Si è verificato un errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                if(listaNews.size() > 0)
                {
                    Toast.makeText(getApplicationContext(),
                            "Trovate " + listaNews.size() + " news. Procedo col salvataggio offline", Toast.LENGTH_LONG).show();

                    //Svuoto il database
                    DatabaseManager.deleteAllNews(database);

                    //Procedo col salvataggio offline
                    for (NewsDao notizia:listaNews)
                    {
                        DatabaseManager.insertNews(database, notizia);
                    }

                    Toast.makeText(getApplicationContext(),"Salvataggio offline completato", Toast.LENGTH_LONG).show();

                    refreshListaNewsView(listaNews);
                }

                return true;


            case R.id.showOnlineData:

                listaNews = new ArrayList<NewsDao>();

                try {

                    listaNews = HackerNewsClient.getListNews(HackerNewsClient.TYPE_NEW, false, false);
                    refreshListaNewsView(listaNews);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Si è verificato un errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                return true;


            case R.id.showOfflineData:

                listaNews = new ArrayList<NewsDao>();

                try {

                    listaNews = DatabaseManager.getAllNews(database);
                    refreshListaNewsView(listaNews);

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

        //Lista news
        final ArrayList<NewsDao> elencoNotizie;

        //Recupero la listView e la popolo con l'elenco nelle news
        listaNewsView = findViewById(R.id.listNews);

        try {

            elencoNotizie = HackerNewsClient.getListNews(HackerNewsClient.TYPE_NEW, false, false);

            //Creo e popolo l'adapter
            listaNewsView.setAdapter(new ArrayAdapter<NewsDao>(this, android.R.layout.simple_list_item_1, elencoNotizie));

            listaNewsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    NewsDao notizia = elencoNotizie.get(i);

                    //Creo l'intent verso la activity di dettaglio
                    Intent intent = new Intent(MainActivity.this, DettaglioNewsActivity.class);
                    intent.putExtra("urlNotizia", notizia.getUrl());
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


    private void refreshListaNewsView(ArrayList<NewsDao> elencoNews)
    {
        ArrayAdapter<NewsDao> adapter = (ArrayAdapter<NewsDao>)listaNewsView.getAdapter();
        adapter.clear();
        adapter.addAll(elencoNews);

        adapter.notifyDataSetChanged();
    }

}