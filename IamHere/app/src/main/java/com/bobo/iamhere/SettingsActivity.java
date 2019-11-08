package com.bobo.iamhere;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bobo.iamhere.db.DatabaseTools;
import com.bobo.iamhere.dialogfragments.ElencoFilesDialog;
import com.bobo.iamhere.utils.PermissionUtils;

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
            /*Sono già qui, non faccio nulla
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            */

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
     *
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
