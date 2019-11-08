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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.DatabaseTools;
import com.bobo.iamhere.db.NotaDao;
import com.bobo.iamhere.dialogfragments.ElencoFilesDialog;
import com.bobo.iamhere.utils.PermissionUtils;

import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listaNoteView;

    static ArrayList<NotaDao> elencoNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //Cambio il titolo all'activity
        setTitle(getString(R.string.title_activity_note));

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


        //Inizializzo la lista di lavoro con l'elenco delle note
        elencoNote = DatabaseManager.getAllNote(MainActivity.database);


        //Inizializzo la list view
        listaNoteView = (ListView) findViewById(R.id.listaNote);

        //Sull'evento "onClick" della lista, vado in modifica della nota su NotaActivity
        listaNoteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NotaDao notaSelezionata = elencoNote.get(position);

                //Creo un intent e vado sulla seconda activity
                Intent intent = new Intent(getApplicationContext(), Note_DettaglioActivity.class);
                intent.putExtra("idNota", notaSelezionata.getId());
                startActivity(intent);

            }
        });

        //Sull'evento "onLongClick" della lista, apro popup di cancellazione della nota
        listaNoteView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                //Apro una dialog per confermare l'eliminazione
                new AlertDialog.Builder(NoteActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.note_delete)
                        .setMessage(R.string.note_delete_subtitle)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                NotaDao notaSelezionata = elencoNote.get(position);

                                DatabaseManager.deleteNota(MainActivity.database, notaSelezionata.getId());
                                elencoNote = DatabaseManager.getAllNote(MainActivity.database);
                                aggiornaLista();

                                Toast.makeText(NoteActivity.this, R.string.eliminazione_successo, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();

                return true;
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.action_aggiungi_nota:

                Intent intent = new Intent(getApplicationContext(), Note_DettaglioActivity.class);
                startActivity(intent);

                //Toast.makeText(getApplicationContext(), "Aggiungo nota", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return false;
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
            //Nulla, sono già qui

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

    @Override
    protected void onResume() {
        super.onResume();

        elencoNote = DatabaseManager.getAllNote(MainActivity.database);
        aggiornaLista();
    }



    private void aggiornaLista()
    {
        ArrayList<String> elencoNotePerLista = new ArrayList<String>();

        for (NotaDao nota:elencoNote)
        {
            String notaPerLista = nota.getNotaPerLista();
            elencoNotePerLista.add(notaPerLista);
        }

        //Popolo la lista
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elencoNotePerLista);
        listaNoteView.setAdapter(adapter);
    }
}
