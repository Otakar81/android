package com.bobo.iamhere;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bobo.iamhere.adapters.MeteoAdapter;
import com.bobo.iamhere.db.DatabaseTools;
import com.bobo.iamhere.dialogfragments.ElencoFilesDialog;
import com.bobo.iamhere.utils.PermissionUtils;
import com.bobo.iamhere.ws.openweathermap.GiornataMeteoDao;
import com.bobo.iamhere.ws.openweathermap.MeteoDao;
import com.bobo.iamhere.ws.openweathermap.OpenWeatherMapService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeteoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    ArrayList<GiornataMeteoDao> elencoPrevisioni;

    String LANGUAGE_CODE = "en"; //Default, poi viene preso dal sistema
    Locale LOCALE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo);

        //Cambio il titolo all'activity
        setTitle(getString(R.string.title_activity_meteo));

        //Setto la lingua
        LOCALE = getResources().getConfiguration().locale;
        LANGUAGE_CODE = LOCALE.getLanguage();

        //Carico la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Inizializzo il drawler
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Inizializzo la navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        try {

            //Recupero Api key e url
            ApplicationInfo app = this.getPackageManager().getApplicationInfo(this.getPackageName(),PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;

            String API_KEY = bundle.getString("openweathermap.API_KEY");
            String URI_SERVICE = bundle.getString("openweathermap.URI_SERVICE");


            //Recupero le coordinate correnti
            if (PermissionUtils.checkSelfPermission_LOCATION(this)) {
                Location lastKnowLocation = MainActivity.getLastKnownLocation(this); //MainActivity.locationManager.getLastKnownLocation(MainActivity.getLocationProviderName());

                if(lastKnowLocation != null)
                    callRestApi(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude(), URI_SERVICE, API_KEY);
                else
                    Toast.makeText(this, R.string.location_null, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();

            String genericError = getResources().getString(R.string.error_generic);

            Toast.makeText(this, genericError + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if( id == R.id.nav_home)
        {
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_maps) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_meteo) {

            //Sono già qui, non faccio nulla

        } else if (id == R.id.nav_luoghi_memorabili) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), LuoghiMemorabiliActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_luoghi_interesse) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), GooglePlacesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

            if (PermissionUtils.checkSelfPermission_LOCATION(this))
            {
                Location lastKnowLocation = MainActivity.getLastKnownLocation(this);

                if(lastKnowLocation != null)
                {
                    String share_message = getResources().getString(R.string.share_message);
                    String share_subject = getResources().getString(R.string.share_subject);

                    Double latitude = lastKnowLocation.getLatitude();
                    Double longitude = lastKnowLocation.getLongitude();

                    String uri = "https://www.google.com/maps/search/?api=1&query=" +latitude+","+longitude; //Apre la mappa e la centra sulle coordinate con un marker
                    //String uri = "https://maps.google.com/maps?daddr=" +latitude+","+longitude; //Apre direttamente il "calcola percorso" fino alle coordinate passate

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");

                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, share_subject);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share_message + ":  " + uri);
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_share)));

                }else{
                    Toast.makeText(this, R.string.location_null, Toast.LENGTH_LONG).show();
                }
            }

        } else if (id == R.id.nav_settings)
        {
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_note)
        {
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_database_export) {

            if (!PermissionUtils.checkSelfPermission_STORAGE(this)) { //Se non mi è stato dato, lo chiedo nuovamente

                if(Build.VERSION.SDK_INT >= 23) //Non ho bisogno di chiedere il permesso per versioni precedenti
                    requestPermissions(PermissionUtils.PERMISSIONS_STORAGE, PermissionUtils.REQUEST_EXTERNAL_STORAGE);

            } else { //Procedo

                DatabaseTools.backupDatabase(this, MainActivity.database, getResources().getString(R.string.app_name));
            }


        } else if (id == R.id.nav_database_import) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ElencoFilesDialog dialog = ElencoFilesDialog.newInstance(builder, MainActivity.database, getResources().getString(R.string.app_name));
            dialog.show(getSupportFragmentManager(),"files_dialog");

        } else if (id == R.id.nav_database_delete) {

            final Activity appoggio = this;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.database_delete_conferma)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseTools.deleteListBackupFiles(appoggio, getResources().getString(R.string.app_name));
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .show();

        } else if(id == R.id.nav_play_store)
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=G.Claudio+De+Caro"));
            startActivity(browserIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Ottiene l'ultima posizione conosciuta
     * @return

    private Location getLastKnownLocation()
    {
        Location lastKnowLocation = null;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            lastKnowLocation = MainActivity.locationManager.getLastKnownLocation(MainActivity.getLocationProviderName());

            if(lastKnowLocation == null)
                lastKnowLocation = MainActivity.locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return lastKnowLocation;
    }
    */

    /***
     * Chiamata alle Api Rest
     */
    private void callRestApi(double latitudine, double longitudine, String uriService, String apiKey)
    {
        //Creo il retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(uriService)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final OpenWeatherMapService openWeatherMapService = retrofit.create(OpenWeatherMapService.class);

        Call<JsonObject> call = openWeatherMapService.meteoForecast(latitudine, longitudine, LANGUAGE_CODE, apiKey);

        call.enqueue(new Callback<JsonObject>() {

            @Override

            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject jsonContent = response.body();

                JsonArray listaPrevisioni = jsonContent.get("list").getAsJsonArray();

                //Memorizzo i giorni interessati
                ArrayList<String> giorniInteressati = new ArrayList<String>();
                ArrayList<MeteoDao> meteoPrevisto = new ArrayList<MeteoDao>();

                for (int i = 0; i < listaPrevisioni.size(); i++)
                {
                    JsonObject meteoGiornoOra = listaPrevisioni.get(i).getAsJsonObject(); //Contiene le previsioni ogni tre ore

                    //Recupero la data
                    String dataOra = meteoGiornoOra.get("dt_txt").getAsString(); //Nel formato: 2018-06-08 03:00:00
                    String dataGiorno = dataOra.substring(0, 10);
                    String oraPrevisione = dataOra.substring(11, 16);

                    //Recupero le info meteo
                    JsonArray previsioneDelGiorno = meteoGiornoOra.get("weather").getAsJsonArray();
                    JsonObject weather = previsioneDelGiorno.get(0).getAsJsonObject();
                    String previsione = weather.get("main").getAsString();
                    String previsioneDescription = weather.get("description").getAsString();
                    String previsioneIcona = weather.get("icon").getAsString();

                    //La temperatura
                    JsonObject temperaturaJson = meteoGiornoOra.get("main").getAsJsonObject();
                    int temperaturaCelsius = (int) (temperaturaJson.get("temp").getAsFloat() - 273.15f);

                    //Ed il vento
                    JsonObject ventoJson = meteoGiornoOra.get("wind").getAsJsonObject();
                    String vento = ventoJson.get("speed").getAsString();

                    //Le nuvole
                    JsonObject nuvoleJson = meteoGiornoOra.get("clouds").getAsJsonObject();
                    String nuvole = nuvoleJson.get("all").getAsString();

                    //Umidità
                    JsonObject umiditaJson = meteoGiornoOra.get("main").getAsJsonObject();
                    String umidita = umiditaJson.get("humidity").getAsString();


                    //Valorizzo le liste
                    if(!giorniInteressati.contains(dataGiorno))
                        giorniInteressati.add(dataGiorno);

                    MeteoDao previsioneMeteo = new MeteoDao(dataGiorno, oraPrevisione, previsione, previsioneDescription, temperaturaCelsius + "", vento, previsioneIcona, nuvole, umidita);
                    meteoPrevisto.add(previsioneMeteo);
                }

                //Adesso prepariamo la lista.
                ArrayList<GiornataMeteoDao> previsioni = new ArrayList<GiornataMeteoDao>();

                //Preparo gli oggetti per la stampa della data
                SimpleDateFormat sdfOrigine = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfOutput = new SimpleDateFormat("EEEE, d MMMM", LOCALE);

                for(int i = 0; i < giorniInteressati.size(); i++)
                {
                    String giorno = giorniInteressati.get(i);

                    //Elaboro la data in formato leggibile
                    String dataLeggibile = giorno;

                    try {

                        Date data = sdfOrigine.parse(giorno);
                        dataLeggibile = sdfOutput.format(data);

                    } catch (ParseException e) {
                        //e.printStackTrace();
                    }

                    GiornataMeteoDao giornataMeteo = new GiornataMeteoDao(dataLeggibile);

                    for(int j = 0; j < meteoPrevisto.size(); j++)
                    {
                        MeteoDao meteo = meteoPrevisto.get(j);

                        if(meteo.getData().equalsIgnoreCase(giorno))
                            giornataMeteo.addPrevisione(meteo);
                    }

                    previsioni.add(giornataMeteo);
                }

                //elencoPrevisioni = previsioni;


                if(previsioni != null && previsioni.size() > 0)
                {
                    //Creo l'adapter
                    ArrayAdapter<GiornataMeteoDao> adapter = new MeteoAdapter(previsioni, MeteoActivity.this);

                    //Popolo la lista con i risultati meteo
                    ListView listView = findViewById(R.id.listMeteoResults);
                    listView.setAdapter(adapter);

                    //Mostro la lista
                    findViewById(R.id.listMeteoResults).setVisibility(View.VISIBLE);


                }


                Log.d("GET RESULTS", "Numero di giorni recuperati: " + previsioni.size());
            }

            @Override

            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Log.e("Errore", throwable.toString());
            }

        });
    }
}
