package com.bobo.iamhere;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.DatabaseTools;
import com.bobo.iamhere.db.LocationDao;
import com.bobo.iamhere.dialogfragments.ElencoFilesDialog;
import com.bobo.iamhere.utils.PermissionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Variabili utilizzate per la geolocalizzazione
    static LocationManager locationManager;
    static LocationListener locationListener;

    //Database
    public static SQLiteDatabase database;

    //Utilizzato per memorizzare i settaggi utente
    public static SharedPreferences preferences;

    //Variabili
    static boolean mostraSoloPreferiti;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode == PermissionUtils.REQUEST_FINE_LOCATION) //Gestisco la richiesta fatta sopra. Nel caso ne avessi di più, in questo modo potrei distinguerle
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
        //database = this.openOrCreateDatabase("location_db", Context.MODE_PRIVATE, null);
        database = DatabaseManager.openOrCreateDatabase(getApplicationContext());


        //Se non esistono, creo le tabelle
        DatabaseManager.createTables(database);

        //Inizializzo le shared preferences     TODO
        preferences = getSharedPreferences("com.bobo.iamhere", Context.MODE_PRIVATE);

        //Variabili
        mostraSoloPreferiti = false;

        //Setto randomicamente lo sfondo
        ConstraintLayout layoutPagina = findViewById(R.id.layoutHome);
        layoutPagina.setBackground(getDrawableSfondo());



        /* Abilito l'app ad aprire connessioni anche nel main thread (lo uso per recuperare le icone dei luoghi suggeriti da GooglePlaces nella mappa
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        */



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
            if (!PermissionUtils.checkSelfPermission_LOCATION(this)) //Non ho il permesso
            {
                //Lo chiedo esplicitamente
                //String[] permessiRichiesti = {Manifest.permission.ACCESS_FINE_LOCATION}; //Potrebbero essere molti, li chiedo tutti insieme nel caso

            /*
                Il requestCode viene usato per avere un id di riferimento su questa richiesta.
                Viene usato ad esempio nel onRequestPermissionsResult, che è il metodo chiamato quando si chiede il permesso
                Nota. 1 è un numero qualsiasi, avrei potuto usare altro
             */
                ActivityCompat.requestPermissions(this, PermissionUtils.PERMISSIONS_LOCATION, PermissionUtils.REQUEST_FINE_LOCATION);

            } else { //Ho già il permesso, chiamo direttamente l'update

                startListening();

                //Location lastKnowLocation = locationManager.getLastKnownLocation(getLocationProviderName());
                Location lastKnowLocation = MainActivity.getLastKnownLocation(this);

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

            //Recupero l'oggetto che mi permetterà di caricare i layout
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.dialog_luogo_preferito, null);

                    //final EditText taskEditText = new EditText(this);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.nuovo_luogo_title)
                    .setMessage(R.string.nuovo_luogo_message)
                    .setView(dialoglayout)
                    .setPositiveButton(R.string.nuovo_luogo_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            EditText aliasView = dialoglayout.findViewById(R.id.aliasLuogo);
                            CheckBox isPreferitoView = dialoglayout.findViewById(R.id.isLuogoPreferito);

                            String alias = aliasView.getText().toString();
                            int isChecked = 0;

                            if(isPreferitoView.isChecked())
                                isChecked = 1;

                            if(alias == null || alias.trim().equals(""))
                                alias = "";

                            if (PermissionUtils.checkSelfPermission_LOCATION(MainActivity.this))
                            {
                                //Location lastKnowLocation = locationManager.getLastKnownLocation(getLocationProviderName());
                                Location lastKnowLocation = MainActivity.getLastKnownLocation(MainActivity.this);
                                salvaLocation(lastKnowLocation, alias, isChecked);

                                //Refresh dell'elenco dei luoghi
                                valorizzaDatiVideo(lastKnowLocation);
                            }

                        }
                    })
                    .setNegativeButton(R.string.nuovo_luogo_no, null)
                    .create();

            dialog.show();

            return true;

        } else if (id == R.id.action_salva_quick) {

            if (PermissionUtils.checkSelfPermission_LOCATION(this))
            {
                //Location lastKnowLocation = locationManager.getLastKnownLocation(getLocationProviderName());
                Location lastKnowLocation = MainActivity.getLastKnownLocation(this);
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
                Toast.makeText(getApplicationContext(), R.string.no_quick_place, Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.action_mostra_preferiti) {

            if (PermissionUtils.checkSelfPermission_LOCATION(this))
            {
                if(mostraSoloPreferiti) //Stavo mostrando solo i preferiti, l'utente mi ha chiesto di visualizzarli tutti
                    item.setIcon(R.drawable.action_preferiti_no);
                else
                    item.setIcon(R.drawable.action_preferiti_si);

                //Inverto il valore della variabile
                mostraSoloPreferiti = !mostraSoloPreferiti;

                //Location lastKnowLocation = locationManager.getLastKnownLocation(getLocationProviderName());
                Location lastKnowLocation = MainActivity.getLastKnownLocation(this);

                //Refresh dell'elenco dei luoghi
                valorizzaDatiVideo(lastKnowLocation);
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
            /*Sono già qui, non faccio nulla
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            */

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
                //Location lastKnowLocation = locationManager.getLastKnownLocation(getLocationProviderName());
                Location lastKnowLocation = MainActivity.getLastKnownLocation(this);

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
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
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

    /**
     * Restituisce il LocationProvider più preciso tra quelli abilitati dall'utente
     * @return
     */
    public static String getLocationProviderName()
    {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return LocationManager.GPS_PROVIDER;
        else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            return LocationManager.NETWORK_PROVIDER;
        else
            return LocationManager.PASSIVE_PROVIDER;
    }

    /***
     * Richiede gli update riguardo la posizione dell'utente
     */
    private void startListening()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(getLocationProviderName(), SettingsActivity.getMinTime(), SettingsActivity.getMinDistance(), locationListener);
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
            lastKnowLocation = locationManager.getLastKnownLocation(getLocationProviderName());

            if(lastKnowLocation == null)
                lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return lastKnowLocation;
    }
    */

    public static Location getLastKnownLocation(Activity contex)
    {
        Location lastKnowLocation = null;

        if (ContextCompat.checkSelfPermission(contex, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            lastKnowLocation = locationManager.getLastKnownLocation(getLocationProviderName());

            if(lastKnowLocation == null)
                lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return lastKnowLocation;
    }

    /***
     * Valorizza a video le informazioni relative al posto in cui mi trovo in questo momento
     *
     * @param location
     */
    private void valorizzaDatiVideo(Location location)
    {
        String elencoInfo = "";

        if(location == null) //Non sono riuscito a risalire alla posizione attuale
        {
            //Stampo le info generiche
            TextView listaInfoText = findViewById(R.id.elencoInfoText);
            listaInfoText.setText(getResources().getString(R.string.no_info));

            //Nascondo la TextView dei luoghi preferiti
            TextView distanzeText = findViewById(R.id.infoDistanzeText);
            distanzeText.setVisibility(View.INVISIBLE);

            Toast.makeText(this, R.string.location_null, Toast.LENGTH_LONG).show();

        } else {

            //Variabili per le info aggiuntive
            String nazione = null;
            String regione = null;
            String provincia = null;
            String citta = null;
            String indirizzoPostale = null;
            String cap = null;


            //Verifico se ho connessione internet: ne ho bisogno per ottenere le info sulla mia posizione attuale
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if(isConnected) //Provo a recuperare le info aggiuntive solo se sono loggato su internet
            {
                //Utilizzo il Geocoder per ottenere info sulla mia posizione
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {

                    List<Address> indirizzi = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if(indirizzi != null && indirizzi.size() > 0)
                    {
                        //Recupero le info sul luogo attuale
                        Address address = indirizzi.get(0);

                        nazione = address.getCountryName();
                        regione = address.getAdminArea();
                        provincia = address.getSubAdminArea();
                        citta = address.getLocality();
                        indirizzoPostale = address.getThoroughfare();
                        cap = address.getPostalCode();
                    }

                } catch (IOException e) {

                    e.printStackTrace();

                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

            //Stampo le info
            //Recupero la traduzione delle parole chiave usate per comporre la infoBox
            String str_info_seiIn = getResources().getString(R.string.elenco_info01);
            String str_info_nelComuneDi = getResources().getString(R.string.elenco_info02);
            String str_info_coordinateGPS = getResources().getString(R.string.elenco_info03);
            String str_info_altitudine = getResources().getString(R.string.elenco_info04);

            String latitudine = location.getLatitude() + "";
            String longitudine = location.getLongitude() + "";
            String accuracy = location.getAccuracy() + "";
            String altitudine = location.getAltitude() + "";

            boolean isPrimaRiga = true;

            //Nazione e regione
            if(regione != null && regione.trim().length() > 0) {

                if(!isPrimaRiga) {
                    elencoInfo += "\n";
                }else{
                    isPrimaRiga = false;
                }

                elencoInfo += str_info_seiIn + " " + regione;

                if(nazione != null && nazione.trim().length() > 0)
                    elencoInfo += " (" + nazione + ")";

            }

            //Comune e provincia
            if(citta != null && citta.trim().length() > 0) {

                if(!isPrimaRiga) {
                    elencoInfo += "\n";
                }else{
                    isPrimaRiga = false;
                }

                elencoInfo += str_info_nelComuneDi + " " + citta;

                if(provincia != null && provincia.trim().length() > 0)
                    elencoInfo += " (" + provincia + ") ";
            }

            //Via e CAP
            if(indirizzoPostale != null && indirizzoPostale.trim().length() > 0) {

                if(!isPrimaRiga) {
                    elencoInfo += "\n";
                }else{
                    isPrimaRiga = false;
                }

                elencoInfo += indirizzoPostale;

                if(cap != null && cap.trim().length() > 0)
                    elencoInfo += " (" + cap + ")";
            }

            //Coordinate
            if(!isPrimaRiga) {
                elencoInfo += "\n";
            }else{
                isPrimaRiga = false;
            }

            elencoInfo += str_info_coordinateGPS + " (" + latitudine + ", " + longitudine + ")";

            //Altitudine
            if(altitudine.trim().length() > 0) {

                double altitudineDouble = Double.parseDouble(altitudine);
                altitudineDouble = (double) (Math.round( altitudineDouble * Math.pow( 10, 2 ) )/Math.pow( 10, 2 ));

                elencoInfo += "\n" + str_info_altitudine + " " + altitudineDouble + "m";
            }



            //Stampo le info generiche
            TextView listaInfoText = findViewById(R.id.elencoInfoText);
            listaInfoText.setText(elencoInfo);


            //E stampo le distanze dai luoghi memorizzati
            try{

                //Recupero la lista del luoghi "preferiti" salvati sul database
                ArrayList<LocationDao> elencoLuoghi = DatabaseManager.getAllLocation(MainActivity.database, mostraSoloPreferiti);

                //Inizializzo la stringa che mostrerà le info sulle distanze dai luoghi preferiti, se presenti
                String infoDistanze = "";

                if(elencoLuoghi.size() == 0)
                {
                    infoDistanze += "\n" + getResources().getString(R.string.info_distanze_no_preferiti);

                }else{

                    if(mostraSoloPreferiti)
                        infoDistanze = getResources().getString(R.string.info_distanze_header_preferiti) + " \n";
                    else
                        infoDistanze = getResources().getString(R.string.info_distanze_header) + " \n";
                }


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
                distanzeText.setVisibility(View.VISIBLE);
                distanzeText.setText(infoDistanze);

            }catch (Exception e)
            {

            }
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

                //Alcune di queste info potrebbero essere NULL. Le gestisco
                if(country == null)
                    country = "";

                if(locality == null)
                    locality = "";

                if(adminArea == null)
                    adminArea = "";

                if(subAdminArea == null)
                    subAdminArea = "";

                if(postalCode == null)
                    postalCode = "";

                if(indirizzoPostale == null)
                    indirizzoPostale = "";

                //Salvo sul database il luogo selezionato
                LocationDao locationDao = new LocationDao(location.getLatitude(), location.getLongitude(), country, adminArea, subAdminArea, locality, postalCode, indirizzoPostale);
                locationDao.setLuogoPreferito(isLuogoPreferito);

                if(alias != null)
                    locationDao.setAlias(alias);

                DatabaseManager.insertLocation(MainActivity.database, locationDao);

                Toast.makeText(getApplicationContext(), R.string.luogo_aggiunto, Toast.LENGTH_SHORT).show();
            }


        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Seleziona una immagine da mostrare come sfondo
     * @return
     */
    private Drawable getDrawableSfondo()
    {
        int indiceMax = 10;
        int indiceMin = 1;

        Drawable sfondo;

        Random rn = new Random();
        int randomNum = rn.nextInt((indiceMax - indiceMin) + 1) + indiceMin;


        switch (randomNum)
        {
            case 1:
                sfondo = getResources().getDrawable(R.drawable.sfondo01, null);
                break;
            case 2:
                sfondo = getResources().getDrawable(R.drawable.sfondo02, null);
                break;
            case 3:
                sfondo = getResources().getDrawable(R.drawable.sfondo03, null);
                break;
            case 4:
                sfondo = getResources().getDrawable(R.drawable.sfondo04, null);
                break;
            case 5:
                sfondo = getResources().getDrawable(R.drawable.sfondo05, null);
                break;
            case 6:
                sfondo = getResources().getDrawable(R.drawable.sfondo06, null);
                break;
            case 7:
                sfondo = getResources().getDrawable(R.drawable.sfondo07, null);
                break;
            case 8:
                sfondo = getResources().getDrawable(R.drawable.sfondo08, null);
                break;
            case 9:
                sfondo = getResources().getDrawable(R.drawable.sfondo09, null);
                break;
            case 10:
                sfondo = getResources().getDrawable(R.drawable.sfondo10, null);
                break;

            default:
                sfondo = getResources().getDrawable(R.drawable.sfondo01, null);
                break;
        }

        return sfondo;
    }

}
