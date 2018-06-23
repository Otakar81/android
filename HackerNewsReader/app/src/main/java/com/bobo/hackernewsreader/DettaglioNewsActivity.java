package com.bobo.hackernewsreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.bobo.hackernewsreader.api.DownloadTask;
import com.bobo.hackernewsreader.api.HackerNewsClient;
import com.bobo.hackernewsreader.db.DatabaseManager;
import com.bobo.hackernewsreader.db.NewsDao;

import java.util.ArrayList;

public class DettaglioNewsActivity extends AppCompatActivity {

    NewsDao notizia;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Aggiungo il menù nell'action bar
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.menu_dettaglio, menu);

        //Se la notizia da mostrare è stata già salvata per offiline, nascondo il relativo pulsante
        if(notizia.getId() != -1)
            menu.findItem(R.id.salvaOffiline).setVisible(false);
        else
            menu.findItem(R.id.salvaOffiline).setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        ArrayList<NewsDao> listaNews = new ArrayList<NewsDao>();

        switch (item.getItemId())
        {
            case R.id.salvaOffiline:

                try {

                    //Recupero l'html della notizia
                    String urlNews = notizia.getUrl();

                    if(urlNews != null)
                    {
                        //Nascondo il pulsante di salvataggio offline
                        item.setVisible(false);

                        /*
                            De Caro (2018-06-23)
                            Il Download dell'html è lento.
                            Per ora quindi mi limito a salvare il riferimento alla news senza il contenuto
                            In questo modo accedendo alla lista delle news salvate mi rimane il riferimento a quelle che ho ritenuto
                            interessanti per una vista futura, anche se poi il contenuto verrà cmq reperito online al momento della visualizzazione

                        DownloadTask task = new DownloadTask();
                        String html = task.execute(urlNews).get();

                        Log.i("Debug", "Caricato html");

                        notizia.setHtml(html);
                        */

                        DatabaseManager.insertNews(MainActivity.database, notizia);

                        Log.i("Debug", "Salvato su DB");

                        //E stampo un messaggio felicioso
                        Toast.makeText(getApplicationContext(), "Notizia salvata tra le preferite", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Si è verificato un errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                return true;

            default:
                return false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_news);

        Intent intent = getIntent();

        long idNotizia = intent.getLongExtra("idNotizia", -1);
        String urlNotizia = intent.getStringExtra("urlNotizia");
        String titoloNotizia = intent.getStringExtra("titoloNotizia");
        String timestampNotizia = intent.getStringExtra("timestampNotizia");

        //Valorizzo l'oggetto NewsDao per la notizia correntemente mostrata
        notizia = new NewsDao(idNotizia, titoloNotizia, urlNotizia, "", timestampNotizia);


        //Inizializzo il webContent e mostro il contenuto della notizia a video
        WebView contentNotiziaView = findViewById(R.id.newsContentView);
        contentNotiziaView.getSettings().setJavaScriptEnabled(true);
        contentNotiziaView.setWebChromeClient(new WebChromeClient());

        if(idNotizia == -1) //Notizia online
        {
            contentNotiziaView.loadUrl(urlNotizia); //Set url
        }else{

            NewsDao news = DatabaseManager.getNews(MainActivity.database, idNotizia);

            if(news.getHtml() != null && !news.getHtml().trim().equals(""))
                contentNotiziaView.loadData(news.getHtml(), "text/html", "UTF-8"); //HTML offline da DB
            else
                contentNotiziaView.loadUrl(urlNotizia); //Se ho salvato solo il riferimento, set url come per le online
        }
    }
}
