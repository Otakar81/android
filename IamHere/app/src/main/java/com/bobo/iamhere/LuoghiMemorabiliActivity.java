package com.bobo.iamhere;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bobo.iamhere.adapters.LocationAdapter;
import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.DatabaseTools;
import com.bobo.iamhere.db.LocationDao;
import com.bobo.iamhere.dialogfragments.ElencoFilesDialog;
import com.bobo.iamhere.dialogfragments.LuoghiMemorabiliDialog;
import com.bobo.iamhere.utils.PermissionUtils;

import java.util.ArrayList;

public class LuoghiMemorabiliActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listaPostiView;

    //Variabili
    static boolean mostraSoloPreferiti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luoghi_memorabili);

        //Recupero l'oggetto che mi permetterà di caricare i layout
        final LayoutInflater inflater = this.getLayoutInflater();

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

        //Variabili
        mostraSoloPreferiti = false;


        //Inizializzo la ListView
        listaPostiView = findViewById(R.id.listaPostiView);
        final ArrayList<LocationDao> elencoPostiMemorabili = DatabaseManager.getAllLocation(MainActivity.database, false);

        //Popolo la lista delle località
        aggiornaLista(elencoPostiMemorabili);

        listaPostiView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LocationDao locationDao = (LocationDao) parent.getItemAtPosition(position); //elencoPostiMemorabili.get(position);

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

                //Apre il dialog personalizzato, per modifica e cancellazione del luogo
                LocationDao locationDao = (LocationDao) parent.getItemAtPosition(position); //elencoPostiMemorabili.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(LuoghiMemorabiliActivity.this);
                LuoghiMemorabiliDialog luoghiMemorabiliDialog = LuoghiMemorabiliDialog.newInstance(builder);
                luoghiMemorabiliDialog.show(getSupportFragmentManager(),"luoghi_dialog");

                //E lo valorizza con gli attributi dell'oggetto su cui abbiamo cliccato
                luoghiMemorabiliDialog.valorizzaDialog(locationDao.getId(), locationDao.getAlias(), locationDao.getLuogoPreferito() == 1);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.luoghi_memorabili_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_mostra_preferiti) {

            if (PermissionUtils.checkSelfPermission_LOCATION(this))
            {
                if(mostraSoloPreferiti) //Stavo mostrando solo i preferiti, l'utente mi ha chiesto di visualizzarli tutti
                    item.setIcon(R.drawable.action_preferiti_no);
                else
                    item.setIcon(R.drawable.action_preferiti_si);

                //Inverto il valore della variabile
                mostraSoloPreferiti = !mostraSoloPreferiti;

                //Faccio refresh della lista, mostrando solo i luoghi compatibili con la richiesta dell'utente
                ArrayList<LocationDao> elencoPostiMemorabili = DatabaseManager.getAllLocation(MainActivity.database, mostraSoloPreferiti);

                //Popolo la lista delle località
                aggiornaLista(elencoPostiMemorabili);
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
     * Aggiorna la lista delle località
     * @param elencoPostiMemorabili
     */
    private void aggiornaLista(ArrayList<LocationDao> elencoPostiMemorabili)
    {
        ArrayAdapter<LocationDao> adapter = new LocationAdapter(elencoPostiMemorabili, this);
        listaPostiView.setAdapter(adapter);
    }

    /***
     * Aggiorna la lista delle località, da dialog
     *
     */
    public void aggiornaListaFromDialog()
    {
        aggiornaLista(DatabaseManager.getAllLocation(MainActivity.database, mostraSoloPreferiti));
    }
}
