package com.bobo.hackernewsreader.api;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.bobo.hackernewsreader.db.NewsDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/***
 * Client per recuperare i dati esposti dal sito di news
 */
public class HackerNewsClient {

    //Url radice per tutte le chiamate
    static final String API_URL = "https://hacker-news.firebaseio.com/v0/";
    static final int MAX_NEWS_ONLINE = 40;

    //Mappo i tipi di news che l'utente può chiedermi
    public final static int TYPE_TOP = 1;
    public final static int TYPE_NEW = 2;
    public final static int TYPE_BEST = 3;

    public static ArrayList<NewsDao> getListNewsStub(int type, boolean getAllNews) throws ExecutionException, InterruptedException, JSONException {
        ArrayList<NewsDao> result = new ArrayList<NewsDao>();


        result.add(new NewsDao("Titolo1", "http://www.news1.it", "2018-03-12 12:40"));
        result.add(new NewsDao("Titolo2", "http://www.news2.it", "2018-03-12 12:40"));
        result.add(new NewsDao("Titolo3", "http://www.news3.it", "2018-03-12 12:40"));

        return result;
    }

    public static ArrayList<NewsDao> getListNews(int type, boolean getAllNews, boolean getOfflineData) throws ExecutionException, InterruptedException, JSONException {
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

                try {
                    String title = notizia.getString("title");
                    String url = notizia.getString("url");
                    long timestamp = notizia.getLong("time");

                    if(getOfflineData) //Devo recuperare anche l'html della notizia collegata
                    {
                        if(url != null)
                        {
                            DownloadTask task = new DownloadTask();
                            String html = task.execute(url).get();

                            result.add(new NewsDao(title, url, html, convertTimestampToDate(timestamp)));
                        }

                    }else{
                        result.add(new NewsDao(title, url, convertTimestampToDate(timestamp)));
                    }

                }catch (JSONException e)
                {
                    Log.i("JSONException", e.getMessage());
                }
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
            case TYPE_NEW:
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

    /***
     * Converte il timestamp proveniente dalle api in un formato stampabile
     *
     * @param unixTimestam
     * @return
     */
    private static String convertTimestampToDate(long unixTimestam)
    {
        Date data = new Date(unixTimestam * 1000); //Vuole i millisecondi

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return  sdf.format(data);
    }

}