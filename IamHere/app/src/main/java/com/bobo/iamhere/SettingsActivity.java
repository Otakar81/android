package com.bobo.iamhere;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.LocationDao;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Cambio il titolo all'activity
        setTitle(getString(R.string.title_activity_settings));

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


        /*
            Valorizzo gli elementi della pagina
         */

        //Min Time
        Spinner minTimeSpinner = findViewById(R.id.minTimeSpinner);

        //Popolo la lista
        int[] elencoValoriPossibili = getResources().getIntArray(R.array.min_time_value);
        int minTimeSelezionato = getMinTime();
        int posizioneSelezionata = getPosizioneSelezionata(elencoValoriPossibili, minTimeSelezionato);

        ArrayAdapter<CharSequence> minTimeAdapter = ArrayAdapter.createFromResource(this, R.array.min_time_label, android.R.layout.simple_spinner_dropdown_item);
        minTimeSpinner.setAdapter(minTimeAdapter);
        minTimeSpinner.setSelection(posizioneSelezionata);
        minTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
                int[] arrayValori = getResources().getIntArray(R.array.min_time_value);
                int selectedVal = arrayValori[pos];

                SharedPreferences.Editor editor = MainActivity.preferences.edit();
                editor.putInt("location_updates_minTime", selectedVal);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            { }
        });

        //Min Distance
        Spinner minDistanceSpinner = findViewById(R.id.minDistanceSpinner);

        //Popolo la lista
        elencoValoriPossibili = getResources().getIntArray(R.array.min_distance_value);
        int minDistanceSelezionata = getMinDistance();
        posizioneSelezionata = getPosizioneSelezionata(elencoValoriPossibili, minDistanceSelezionata);

        ArrayAdapter<CharSequence> minDistanceAdapter = ArrayAdapter.createFromResource(this, R.array.min_distance_label, android.R.layout.simple_spinner_dropdown_item);
        minDistanceSpinner.setAdapter(minDistanceAdapter);
        minDistanceSpinner.setSelection(posizioneSelezionata);
        minDistanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
                int[] arrayValori = getResources().getIntArray(R.array.min_distance_value);
                int selectedVal = arrayValori[pos];

                SharedPreferences.Editor editor = MainActivity.preferences.edit();
                editor.putInt("location_updates_minDistance", selectedVal);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            { }
        });

        //Google Places Radius
        Spinner googlePlacesRadiusSpinner = findViewById(R.id.settings_googlePlacesRadiusSpinner);

        //Popolo la lista
        elencoValoriPossibili = getResources().getIntArray(R.array.google_places_radius_value);
        int radiusSelezionato = getGooglePlacesRadius();
        posizioneSelezionata = getPosizioneSelezionata(elencoValoriPossibili, radiusSelezionato);

        ArrayAdapter<CharSequence> radiusAdapter = ArrayAdapter.createFromResource(this, R.array.google_places_radius_label, android.R.layout.simple_spinner_dropdown_item);
        googlePlacesRadiusSpinner.setAdapter(radiusAdapter);
        googlePlacesRadiusSpinner.setSelection(posizioneSelezionata);
        googlePlacesRadiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
                int[] arrayValori = getResources().getIntArray(R.array.google_places_radius_value);
                int selectedVal = arrayValori[pos];

                SharedPreferences.Editor editor = MainActivity.preferences.edit();
                editor.putInt("google_places_radius", selectedVal);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            { }
        });


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
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), MeteoActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_luoghi_memorabili) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), LuoghiMemorabiliActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_luoghi_interesse) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), GooglePlacesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_note)
        {
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

            if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                //Location lastKnowLocation = locationManager.getLastKnownLocation(getLocationProviderName());
                Location lastKnowLocation = getLastKnownLocation();

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
            /*Sono già qui, non faccio nulla
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            */

        } else if (id == R.id.nav_database)
        {
            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Ottiene l'ultima posizione conosciuta
     * @return
     */
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


    /**
     * Metodo di servizio<br>
     * Dato un elenco di valori possibili ed uno degli elementi dell'insieme, restituisce la posizione occupata dal secondo nel primo
     *
     * @param elencoValoriPossibili
     * @param valoreSelezionato
     * @return
     */
    private int getPosizioneSelezionata(int[] elencoValoriPossibili, int valoreSelezionato)
    {
        int posizioneSelezionata = 0;

        for (int valore:elencoValoriPossibili) {
            if(valore == valoreSelezionato)
                break;
            else
                posizioneSelezionata++;
        }

        return posizioneSelezionata;
    }




    /**
     * Restituisce il settings relativo al "minTime" usato per il calcolo dello spostameno della location
     * @return
     */
    public static int getMinTime()
    {
        SharedPreferences preferences = MainActivity.preferences;
        return preferences.getInt("location_updates_minTime", 30000); //Default: 30 secondi
    }

    /**
     * Restituisce il settings relativo al "minDistance" usato per il calcolo dello spostameno della location
     * @return
     */
    public static int getMinDistance()
    {
        SharedPreferences preferences = MainActivity.preferences;
        return preferences.getInt("location_updates_minDistance", 10); //Default: 10 metri
    }

    /**
     * Restituisce il raggio (in metri) settato per individuare i luoghi interessanti nelle vicinanze
     * @return
     */
    public static int getGooglePlacesRadius()
    {
        SharedPreferences preferences = MainActivity.preferences;
        return preferences.getInt("google_places_radius", 1500); //Default: 1500 metri
    }
}
