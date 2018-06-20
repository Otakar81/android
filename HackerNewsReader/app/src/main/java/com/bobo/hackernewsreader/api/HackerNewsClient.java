package com.bobo.hackernewsreader.api;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.bobo.hackernewsreader.db.NewsDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/***
 * Client per recuperare i dati esposti dal sito di news
 */
public class HackerNewsClient {

    //Url radice per tutte le chiamate
    static final String API_URL = "https://hacker-news.firebaseio.com/v0/";
    static final int MAX_NEWS_ONLINE = 50;

    //Mappo i tipi di news che l'utente può chiedermi
    public final static int TYPE_TOP = 1;
    public final static int TYPE_NEWS = 2;
    public final static int TYPE_BEST = 3;

    public static ArrayList<NewsDao> getListNewsStub(int type, boolean getAllNews) throws ExecutionException, InterruptedException, JSONException {
        ArrayList<NewsDao> result = new ArrayList<NewsDao>();


        result.add(new NewsDao("Titolo1", "http://www.news1.it"));
        result.add(new NewsDao("Titolo2", "http://www.news2.it"));
        result.add(new NewsDao("Titolo3", "http://www.news3.it"));

        return result;
    }

    public static ArrayList<NewsDao> getListNews(int type, boolean getAllNews) throws ExecutionException, InterruptedException, JSONException {
        ArrayList<NewsDao> result = new ArrayList<NewsDao>();

        //Recupero gli id delle notizie da recuperare
        ArrayList<String> elencoIds = getListIdNews(type);

        int counter = 0;

        for (String idNotizia: elencoIds)
        {
            //Per le chiamate in tempo reale, prendo solo i primi MAX risultati
            if(!getAllNews && counter == MAX_NEWS_ONLINE)
                break;

            //Recupero il dettaglio della notizia
            JSONObject notizia = getDettaglioNotizia(idNotizia);

            if(notizia != null)
            {
                String title = notizia.getString("title");
                String url = notizia.getString("url");

                result.add(new NewsDao(title, url));
            }

            counter++;
        }

        return result;
    }


    private static ArrayList<String> getListIdNews(int type) throws ExecutionException, InterruptedException {

        //String testJson = "[ 17348327, 17350043, 17349368, 17347078, 17349758, 17346307 ]";
        ArrayList<String> elencoIds = new ArrayList<String>();

        String apiCall = "";

        switch (type)
        {
            case TYPE_TOP:
                apiCall = "topstories.json";
            case TYPE_NEWS:
                apiCall = "newstories.json";
            case TYPE_BEST:
                apiCall = "beststories.json";
            default:
                apiCall = "topstories.json";
        }

        apiCall = API_URL + apiCall;

        DownloadTask task = new DownloadTask();
        String jsonData = task.execute(apiCall).get();
        //Log.i("JsonData", jsonData);

        if(jsonData.length() > 0)
        {
            try {

                JSONArray jsonArray = new JSONArray(jsonData);

                for (int i = 0; i < jsonArray.length(); i++)
                    elencoIds.add(jsonArray.getString(i));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return elencoIds;
    }

    private static JSONObject getDettaglioNotizia(String idNotizia) throws ExecutionException, InterruptedException {
        String apiCall = API_URL + "item/" + idNotizia + ".json";

        JSONObject jsonObject = null;



        DownloadTask task = new DownloadTask();
        String jsonData = task.execute(apiCall).get();

        if(jsonData.length() > 0)
        {
            try {

                jsonObject = new JSONObject(jsonData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }

}