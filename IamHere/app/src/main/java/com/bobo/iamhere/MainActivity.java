package com.bobo.iamhere;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.LocationDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
        if (id == R.id.action_salva_corrente) {

            final EditText taskEditText = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Aggiunta luogo")
                    .setMessage("Specificare un alias per il luogo (opzionale)")
                    .setView(taskEditText)
                    .setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String alias = String.valueOf(taskEditText.getText());

                            if(alias == null || alias.trim().equals(""))
                                alias = "";

                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                            {
                                Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                salvaLocation(lastKnowLocation, alias, 0);

                                //Refresh dell'elenco dei luoghi
                                valorizzaDatiVideo(lastKnowLocation);
                            }

                        }
                    })
                    .setNegativeButton("Cancella", null)
                    .create();

            dialog.show();

            return true;

        } else if (id == R.id.action_salva_quick) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                salvaLocation(lastKnowLocation, DatabaseManager.NOME_LOCATION_VELOCE, 1);

                //Refresh dell'elenco dei luoghi
                valorizzaDatiVideo(lastKnowLocation);
            }

            return true;

        } else if (id == R.id.action_carica_quick) {

            LocationDao locationDao = DatabaseManager.getLocationVeloce(database);

            if(locationDao != null)
            {
                //Creo un intent e vado sulla mappa
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("latitudine", locationDao.getLatitudine());
                intent.putExtra("longitudine", locationDao.getLongitudine());
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(), "Non hai ancora salvato un 'luogo veloce'", Toast.LENGTH_SHORT).show();
            }

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
            //Sono già qui, non faccio nulla

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
            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_share) {

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

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

            //Recupero la lista del luoghi "preferiti" salvati sul database
            ArrayList<LocationDao> elencoLuoghi = DatabaseManager.getAllLocation(MainActivity.database, false);

            String infoDistanze = "Distanze dai luoghi memorizzati: \n";

            if(elencoLuoghi.size() == 0)
                infoDistanze += "\nNon ci sono luoghi memorizzati";

            //Valorizzo le distanze
            for (LocationDao luogo:elencoLuoghi)
            {
                Location indirizzoLuogo = new Location(luogo.toString());
                indirizzoLuogo.setLatitude(luogo.getLatitudine());
                indirizzoLuogo.setLongitude(luogo.getLongitudine());

                //Calcolo la distanza
                float distanza = location.distanceTo(indirizzoLuogo);

                //E la setto
                luogo.setDistanzaDaMe(distanza);
            }

            //Ordino la lista sulla base della distanza dal luogo in cui mi trovo
            Collections.sort(elencoLuoghi);

            //E stampo la lista ordinata a video
            for (LocationDao luogo:elencoLuoghi)
            {
                /*
                Location indirizzoLuogo = new Location(luogo.toString());
                indirizzoLuogo.setLatitude(luogo.getLatitudine());
                indirizzoLuogo.setLongitude(luogo.getLongitudine());
                */

                //Calcolo la distanza
                float distanza = luogo.getDistanzaDaMe();
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
                infoDistanze += "\n" + luogo.toStringShort() + " - " + distanza + unitaDiMisura;
            }



            TextView distanzeText = findViewById(R.id.infoDistanzeText);
            distanzeText.setText(infoDistanze);

        }catch (Exception e)
        {

        }
    }


    /***
     * Salva la location passata come argomento<br>
     *
     * @param location
     * @param alias
     */
    private void salvaLocation(Location location, String alias, int isLuogoPreferito)
    {
        if(location == null)
            return;


        //Se sto salvando la location "veloce", verifico che già non ci sia. In quel caso la elimino
        if(alias != null && !alias.trim().equals("") && alias.equalsIgnoreCase(DatabaseManager.NOME_LOCATION_VELOCE))
        {
            LocationDao locationVeloce = DatabaseManager.getLocationVeloce(database);

            if(locationVeloce != null)
                DatabaseManager.deleteLocation(database, locationVeloce.getId());
        }

        //Utilizzo il Geocoder per ottenere info sugli indirizzi in zona
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            List<Address> listaIndirizzi = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); //Voglio un solo risultato

            if(listaIndirizzi != null && listaIndirizzi.size() > 0)
            {
                Address indirizzo = listaIndirizzi.get(0);

                //Recupero le info della zona
                String country = indirizzo.getCountryName();
                String locality = indirizzo.getLocality();
                String adminArea = indirizzo.getAdminArea();
                String subAdminArea = indirizzo.getSubAdminArea();
                String postalCode = indirizzo.getPostalCode();
                String indirizzoPostale = indirizzo.getThoroughfare();

                //Salvo sul database il luogo selezionato
                LocationDao locationDao = new LocationDao(location.getLatitude(), location.getLongitude(), country, adminArea, subAdminArea, locality, postalCode, indirizzoPostale);
                locationDao.setLuogoPreferito(isLuogoPreferito);

                if(alias != null)
                    locationDao.setAlias(alias);

                DatabaseManager.insertLocation(MainActivity.database, locationDao);

                Toast.makeText(getApplicationContext(), "Luogo aggiunto", Toast.LENGTH_SHORT).show();
            }


        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
