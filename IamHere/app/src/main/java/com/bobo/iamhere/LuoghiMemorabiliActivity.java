package com.bobo.iamhere;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.LocationDao;

import java.util.ArrayList;

public class LuoghiMemorabiliActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listaPostiView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luoghi_memorabili);

        //Cambio il titolo all'activity
        setTitle(getString(R.string.title_activity_luoghi_memorabili));

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


        //Inizializzo la ListView
        listaPostiView = findViewById(R.id.listaPostiView);
        final ArrayList<LocationDao> elencoPostiMemorabili = DatabaseManager.getAllLocation(MainActivity.database, false);

        //Popolo la lista delle località
        aggiornaLista(elencoPostiMemorabili);

        listaPostiView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LocationDao locationDao = elencoPostiMemorabili.get(position);

                //Creo un intent e vado sulla mappa
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("latitudine", locationDao.getLatitudine());
                intent.putExtra("longitudine", locationDao.getLongitudine());
                startActivity(intent);
            };
        });

        listaPostiView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(LuoghiMemorabiliActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Eliminazione luogo")
                        .setMessage("Sei sicuro di voler eliminare questo luogo?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Elimino il posto dall'elenco di quelli memorizzati
                                DatabaseManager.deleteLocation(MainActivity.database, elencoPostiMemorabili.get(position).getId());

                                //Avverto la lista che i dati sono cambiati
                                aggiornaLista(DatabaseManager.getAllLocation(MainActivity.database, false));

                                //((BaseAdapter) listaPostiView.getAdapter()).notifyDataSetChanged(); TODO Verificare perchè non funziona

                                Toast.makeText(getApplicationContext(), "Luogo eliminato", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Aggiorno la lista, necessario se sto tornando indietro dalla mappa ed ho aggiunto un luogo
        aggiornaLista(DatabaseManager.getAllLocation(MainActivity.database, false));
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

            //Sono già qui, non faccio nulla

        } else if (id == R.id.nav_luoghi_interesse) {
            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_share) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
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

        } else if (id == R.id.nav_note)
        {
            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***
     * Aggiorna la lista delle località
     * @param elencoPostiMemorabili
     */
    private void aggiornaLista(ArrayList<LocationDao> elencoPostiMemorabili)
    {
        ArrayAdapter<LocationDao> adapter = new ArrayAdapter<LocationDao>(this, android.R.layout.simple_list_item_1, elencoPostiMemorabili);
        listaPostiView.setAdapter(adapter);
    }
}
