package com.bobo.iamhere;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.LocationDao;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Variabili utilizzate per la geolocalizzazione
    static LocationManager locationManager;
    static LocationListener locationListener;

    //Database
    public static SQLiteDatabase database;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode == 1) //Gestisco la richiesta fatta sopra. Nel caso ne avessi di più, in questo modo potrei distinguerle
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)//Ho il permesso per la richiesta n. requestCode
            {
            /*
                requestLocationUpdates mi chiede esplicitamente di verificare i permessi prima di chiamarlo. Quindi ho dovuto aggiungere l'if seguente
                I parametri numerici della chiamata riguardano il tempo e la variazione di distanza minimi ogni quanto richiamare l'update della posizione.
                Il valore 0 sta a significare update continui: maggiore precisione, ma maggior consumo della batteria.
             */
                startListening();

            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        //Inizializzo gli oggetti utilizzati per la geolocalizzazione
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); //Valorizzo il location manager dai servizi di sistema
        locationListener = createLocationListener();

        //Creo il database
        database = this.openOrCreateDatabase("location_db", Context.MODE_PRIVATE, null);

        //Se non esistono, creo le tabelle
        DatabaseManager.createTables(database);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Devo chiedere il permesso di accesso alla posizione
        if (Build.VERSION.SDK_INT < 23) //Sulle vecchie versioni di android, non devo chiedere permessi
        {
            startListening();

        } else {
            //Sulle versione di android superiori alla 23, devo esplicitamente chiedere il permesso all'utente
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) //Non ho il permesso
            {
                //Lo chiedo esplicitamente
                String[] permessiRichiesti = {Manifest.permission.ACCESS_FINE_LOCATION}; //Potrebbero essere molti, li chiedo tutti insieme nel caso

            /*
                Il requestCode viene usato per avere un id di riferimento su questa richiesta.
                Viene usato ad esempio nel onRequestPermissionsResult, che è il metodo chiamato quando si chiede il permesso
                Nota. 1 è un numero qualsiasi, avrei potuto usare altro
             */
                ActivityCompat.requestPermissions(this, permessiRichiesti, 1);

            } else { //Ho già il permesso, chiamo direttamente l'update

                startListening();

                Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                //Stampo a video le info, visto che già ho i permessi
                valorizzaDatiVideo(lastKnowLocation);
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_maps) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_gallery) {
            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_send) {
            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /***
     * Crea il LocationListener
     *
     * @return
     */
    private LocationListener createLocationListener()
    {
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                //Stampo a video le info
                valorizzaDatiVideo(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        return locationListener;
    }


    /***
     * Richiede gli update riguardo la posizione dell'utente
     */
    private void startListening()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, locationListener);
    }

    /***
     * Valorizza a video le informazioni relative al posto in cui mi trovo in questo momento
     *
     * @param location
     */
    private void valorizzaDatiVideo(Location location)
    {
        String elencoInfo = "";

        //Utilizzo il Geocoder per ottenere info sulla mia posizione
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {


            List<Address> indirizzi = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(indirizzi != null && indirizzi.size() > 0)
            {
                Address address = indirizzi.get(0);

                String latitudine = location.getLatitude() + "";
                String longitudine = location.getLongitude() + "";
                String accuracy = location.getAccuracy() + "";
                String altitudine = location.getAltitude() + "";
                String nazione = address.getCountryName();
                String regione = address.getAdminArea();
                String provincia = address.getSubAdminArea();
                String citta = address.getLocality();
                String indirizzoPostale = address.getThoroughfare();
                String cap = address.getPostalCode();

                //Valorizzo l'elenco delle info
                if(latitudine.trim().length() > 0)
                    elencoInfo += "\nLatitudine: " + latitudine;

                if(longitudine.trim().length() > 0)
                    elencoInfo += "\nLongitudine: " + longitudine;

                if(accuracy.trim().length() > 0)
                    elencoInfo += "\nAccuratezza: " + accuracy;

                if(altitudine.trim().length() > 0) {

                    double altitudineDouble = Double.parseDouble(altitudine);
                    altitudineDouble = (double) (Math.round( altitudineDouble * Math.pow( 10, 2 ) )/Math.pow( 10, 2 ));

                    elencoInfo += "\nAltitudine: " + altitudineDouble + "m";
                }

                if(nazione != null && nazione.trim().length() > 0)
                    elencoInfo += "\nNazione: " + nazione;

                if(regione != null && regione.trim().length() > 0)
                    elencoInfo += "\nRegione: " + regione;

                if(provincia != null && provincia.trim().length() > 0)
                    elencoInfo += "\nProvincia: " + provincia;

                if(citta != null && citta.trim().length() > 0)
                    elencoInfo += "\nComune: " + citta;

                if(indirizzoPostale != null && indirizzoPostale.trim().length() > 0)
                    elencoInfo += "\nIndirizzo: " + indirizzoPostale;

                if(cap != null && cap.trim().length() > 0)
                    elencoInfo += "\nCAP: " + cap;
            }

        } catch (IOException e) {
            e.printStackTrace();

            elencoInfo = "Nessuna info presente";
        } catch (Exception e)
        {
            e.printStackTrace();

            elencoInfo = "Nessuna info presente";
        }

        //Stampo le info generiche
        TextView listaInfoText = findViewById(R.id.elencoInfoText);
        listaInfoText.setText(elencoInfo);

        //E stampo le distanze dai luoghi memorizzati
        try{

            //Recupero la lista del luoghi salvati dal database
            ArrayList<LocationDao> elencoLuoghi = DatabaseManager.getAllLocation(MainActivity.database);

            String infoDistanze = "Distanze dai luoghi memorizzati: \n";

            if(elencoLuoghi.size() == 0)
                infoDistanze += "\nNon ci sono luoghi memorizzati";

            for (LocationDao luogo:elencoLuoghi)
            {
                Location indirizzoLuogo = new Location(luogo.toString());
                indirizzoLuogo.setLatitude(luogo.getLatitudine());
                indirizzoLuogo.setLongitude(luogo.getLongitudine());

                //Calcolo la distanza
                float distanza = location.distanceTo(indirizzoLuogo);
                String unitaDiMisura = "m";

                if(distanza > 1000) {
                    distanza = distanza / 1000;
                    unitaDiMisura = "km";

                    //Arrotondo ai due decimali
                    distanza = (float) (Math.round( distanza * Math.pow( 10, 2 ) )/Math.pow( 10, 2 ));
                }else{
                    distanza = (float) (Math.round( distanza * Math.pow( 10, 1 ) )/Math.pow( 10, 1 ));
                }

                //Stampo la riga di informazione
                infoDistanze += "\n" + luogo.toString() + " - " + distanza + unitaDiMisura;
            }


            TextView distanzeText = findViewById(R.id.infoDistanzeText);
            distanzeText.setText(infoDistanze);

        }catch (Exception e)
        {

        }
    }

}
