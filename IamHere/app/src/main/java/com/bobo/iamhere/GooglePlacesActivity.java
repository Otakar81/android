package com.bobo.iamhere;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.GooglePlacesTypeDao;
import com.bobo.iamhere.db.LocationDao;
import com.bobo.iamhere.ws.google.GooglePlacesService;
import com.bobo.iamhere.ws.google.PlaceDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GooglePlacesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Variabili globali
    ListView listaLuoghiInteressantiView;
    ArrayList<PlaceDao> elencoPostiInteressanti;

    String tipologiaSelezionata;

    String BASE_URL;
    String API_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_places);

        //Inizializzo i componenti
        listaLuoghiInteressantiView = findViewById(R.id.listLuoghiInteressanti);

        listaLuoghiInteressantiView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PlaceDao placeDao = elencoPostiInteressanti.get(position);

                //Creo un intent e vado sulla mappa
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("latitudine", placeDao.getLatitudine());
                intent.putExtra("longitudine", placeDao.getLongitudine());
                intent.putExtra("inserisci_marker", true);
                intent.putExtra("nome_luogo", placeDao.getName());
                startActivity(intent);
            };
        });

        try {

            //Recupero Api key e url
            ApplicationInfo app = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;

            API_KEY = bundle.getString("com.google.places.API_KEY");
            BASE_URL = bundle.getString("com.google.places.URI_SERVICE");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //Cambio il titolo all'activity
        setTitle(getString(R.string.title_activity_google_places));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Recupero le tipologie tra cui scegliere e popolo il menu
        ArrayList<GooglePlacesTypeDao> elencoTipi = DatabaseManager.getAllPlaceTypes(MainActivity.database);

        for (GooglePlacesTypeDao tipo:elencoTipi)
            menu.add(Menu.NONE, tipo.getId(), Menu.NONE, tipo.getCodice()).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);


//        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Recupero il tipo selezionato
            GooglePlacesTypeDao tipo = DatabaseManager.getPlaceType(MainActivity.database, id);
            tipologiaSelezionata = tipo.getCodice();

            //Valorizzo il titolo della lista, specificando quale è la tipologia dei luoghi che sto per mostrare
            TextView googlePlacesListTitle = findViewById(R.id.googlePlacesListTitle);
            googlePlacesListTitle.setText("Tipologia: " + tipologiaSelezionata);

            //Valorizzo la lista a video
            Location lastKnowLocation = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            callRestApi(lastKnowLocation);
        }

        return super.onOptionsItemSelected(item);
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
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), MeteoActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_luoghi_memorabili) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), LuoghiMemorabiliActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_luoghi_interesse) {

            /* Sono qui
            Intent intent = new Intent(getApplicationContext(), GooglePlacesActivity.class);
            startActivity(intent);
            */

        } else if (id == R.id.nav_note)
        {
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

            if (ContextCompat.checkSelfPermission(GooglePlacesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                Location lastKnowLocation = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Double latitude = lastKnowLocation.getLatitude();
                Double longitude = lastKnowLocation.getLongitude();

                String uri = "https://www.google.com/maps/search/?api=1&query=" +latitude+","+longitude; //Apre la mappa e la centra sulle coordinate con un marker
                //String uri = "https://maps.google.com/maps?daddr=" +latitude+","+longitude; //Apre direttamente il "calcola percorso" fino alle coordinate passate

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String ShareSub = "Here is my location";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }

        } else if (id == R.id.nav_settings)
        {
            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /***
     * Chiamata alle Api Rest
     */
    private void callRestApi(final Location lastKnowLocation)
    {
        double latitudine = lastKnowLocation.getLatitude();
        double longitudine = lastKnowLocation.getLongitude();

        //Creo il retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final GooglePlacesService googlePlacesService = retrofit.create(GooglePlacesService.class);

        String location = latitudine + "," + longitudine;

        Call<JsonObject> call = googlePlacesService.nearbyPlaces(location, 1500, tipologiaSelezionata, API_KEY);

        call.enqueue(new Callback<JsonObject>() {

            @Override

            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                ArrayList<PlaceDao> luoghi = new ArrayList<PlaceDao>();

                JsonObject respJson = response.body();

                String status = respJson.get("status").getAsString();

                if(status.equalsIgnoreCase("OK"))
                {
                    JsonArray listaRisultati = respJson.getAsJsonArray("results");

                    for(int i = 0; i < listaRisultati.size(); i++)
                    {
                        JsonObject object = listaRisultati.get(i).getAsJsonObject();

                        //Campi raw
                        String name = object.get("name").getAsString();
                        String id = object.get("place_id").getAsString();
                        String formattedAddress = object.get("vicinity").getAsString();
                        String icon = object.get("icon").getAsString();
                        float rating;

                        //Coordinate geografiche
                        JsonObject geometry = object.get("geometry").getAsJsonObject();
                        JsonObject location = geometry.get("location").getAsJsonObject();
                        double latitudine = location.get("lat").getAsDouble();
                        double longitudine = location.get("lng").getAsDouble();

                        //Url delle foto sulla mappa
                        String urlPhoto = ""; //TODO? Ne vale la pena?
                        //private int priceLevel; //TODO
                        int isOpenNow;

                        try{
                            rating = object.get("rating").getAsFloat();
                        }catch (NullPointerException e)
                        {
                            rating = -1;
                        }

                        try{

                            JsonObject opening_hours = object.get("opening_hours").getAsJsonObject();

                            boolean isOpenNowBool = opening_hours.get("open_now").getAsBoolean();

                            if(isOpenNowBool)
                                isOpenNow = 1;
                            else
                                isOpenNow = 0;

                        }catch (NullPointerException e)
                        {
                            isOpenNow = -1;
                        }

                        //Calcolo la distanza da me
                        Location indirizzoLuogo = new Location(name);
                        indirizzoLuogo.setLatitude(latitudine);
                        indirizzoLuogo.setLongitude(longitudine);

                        //Creo il Dao e lo aggiungo
                        PlaceDao place = new PlaceDao(id, latitudine, longitudine, icon, name, formattedAddress, urlPhoto, rating, isOpenNow);
                        place.setDistanzaDaMe(lastKnowLocation.distanceTo(indirizzoLuogo));

                        luoghi.add(place);
                    }


                    elencoPostiInteressanti = luoghi;
                    aggiornaLista(luoghi);

                }else{
                    //TODO
                    /*
                    Status Codes

                    The "status" field within the search response object contains the status of the request, and may contain debugging information to help you track down why the request failed. The "status" field may contain the following values:

                        OK indicates that no errors occurred; the place was successfully detected and at least one result was returned.
                        ZERO_RESULTS indicates that the search was successful but returned no results. This may occur if the search was passed a latlng in a remote location.
                        OVER_QUERY_LIMIT indicates that you are over your quota.
                        REQUEST_DENIED indicates that your request was denied, generally because of lack of an invalid key parameter.
                        INVALID_REQUEST generally indicates that a required query parameter (location or radius) is missing.
                        UNKNOWN_ERROR indicates a server-side error; trying again may be successful.

                     */
                }

                Log.d("GET RESULTS", "Numero di luoghi recuperati: " + luoghi.size());
            }

            @Override

            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Log.e("Errore", throwable.toString());
            }

        });
    }

    /***
     * Aggiorna la ListView con l'elenco dei posti interessanti
     * @param elencoPostiInteressanti
     */
    private void aggiornaLista(ArrayList<PlaceDao> elencoPostiInteressanti)
    {
        //Ordino la lista
        Collections.sort(elencoPostiInteressanti);

        ArrayAdapter<PlaceDao> adapter = new ArrayAdapter<PlaceDao>(this, android.R.layout.simple_list_item_1, elencoPostiInteressanti);
        listaLuoghiInteressantiView.setAdapter(adapter);
    }
}
