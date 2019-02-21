package magazzino.bobo.com.magazzinodomestico;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.adapters.OggettoAdapter;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseTools;
import magazzino.bobo.com.magazzinodomestico.db.dao.ContenitoreDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.LocationDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.OggettoDao;
import magazzino.bobo.com.magazzinodomestico.dialogfragments.ContenitoreDialog;
import magazzino.bobo.com.magazzinodomestico.dialogfragments.ElencoFilesDialog;
import magazzino.bobo.com.magazzinodomestico.dialogfragments.OggettoDialog;

public class Contenitori_DettaglioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listaOggettiView;
    SearchView searchView;
    ArrayList<OggettoDao> elencoOggetti;

    long ID_CONTENITORE_SELEZIONATO;
    String NOME_CONTENITORE_SELEZIONATO;

    @Override
    protected void onResume() {
        super.onResume();

        //Aggiorno la lista
        //aggiornaLista(DatabaseManager.getAllOggettiByLocation(MainActivity.database, -1, -1, -1, ID_CONTENITORE_SELEZIONATO), true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenitori__dettaglio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Valorizzo gli ID del contenitore di cui sto visualizzando il contenuto, e creo il "LocationDao" da usare sul dialog
        Intent intent = getIntent();
        ID_CONTENITORE_SELEZIONATO = intent.getLongExtra("id_contenitore", -1);
        NOME_CONTENITORE_SELEZIONATO = intent.getStringExtra("nome_contenitore");
        final long idCategoriaContenitoreSelezionato = intent.getLongExtra("id_categoria", -1);
        long mobileSelezionato = intent.getLongExtra("id_mobile", -1);
        long stanzaSelezionata = intent.getLongExtra("id_stanza", -1);

        final LocationDao location = new LocationDao(-1, stanzaSelezionata, mobileSelezionato, ID_CONTENITORE_SELEZIONATO);

        //Setto il titolo
        //setTitle(NOME_CONTENITORE_SELEZIONATO);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(NOME_CONTENITORE_SELEZIONATO);
        actionBar.setSubtitle(getResources().getString(R.string.oggetti));

        //Bottone fluttuante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Creo il dialog per il nuovo inserimento
                AlertDialog.Builder builder = new AlertDialog.Builder(Contenitori_DettaglioActivity.this);
                OggettoDialog dialog = OggettoDialog.newInstance(builder, false, location, idCategoriaContenitoreSelezionato);
                dialog.show(getSupportFragmentManager(),"oggetto_dialog");
            }
        });

        //Menu laterale
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Navigation View
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Inizializzo la ListView
        listaOggettiView = findViewById(R.id.listaOggettiView);
        elencoOggetti = DatabaseManager.getAllOggettiByLocation(MainActivity.database, location);

        //Popolo la lista
        aggiornaLista(elencoOggetti, true);


        listaOggettiView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                //Apre il dialog personalizzato, per modifica e cancellazione
                OggettoDao dao = (OggettoDao) parent.getItemAtPosition(position); // elencoContenitori.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(Contenitori_DettaglioActivity.this);
                OggettoDialog dialog = OggettoDialog.newInstance(builder, true, location, idCategoriaContenitoreSelezionato);
                dialog.show(getSupportFragmentManager(),"oggetto_dialog");

                //E lo valorizza con gli attributi dell'oggetto su cui abbiamo cliccato
                dialog.valorizzaDialog(dao.getId(), dao.getNome(), dao.getDescrizione(), dao.getNumero_oggetti(), dao.getId_stanza(), dao.getId_mobile(), dao.getId_contenitore(), dao.getId_categoria());

                return true;
            }
        });


        //Inzializzo la search view
        searchView = findViewById(R.id.searchOggetti);
        searchView.setActivated(true);
        searchView.setQueryHint(getResources().getString(R.string.search_query_hint));
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Effettuo la ricerca
                search(newText);

                return false;
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
        //getMenuInflater().inflate(R.menu.contenitori__dettaglio, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_stanze) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), StanzeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_mobili) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), MobiliActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_contenitori) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), ContenitoriActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_categorie) {

            //Creo un intent e vado sulla activity corrispondente
            Intent intent = new Intent(getApplicationContext(), CategorieActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_database_export) {

            DatabaseTools.backupDatabase(this, MainActivity.database, getResources().getString(R.string.app_name));

        }else if (id == R.id.nav_database_import) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ElencoFilesDialog dialog = ElencoFilesDialog.newInstance(builder, MainActivity.database, getResources().getString(R.string.app_name));
            dialog.show(getSupportFragmentManager(),"files_dialog");

        }else if (id == R.id.nav_database_delete) {

            final Activity appoggio = this;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.database_delete_conferma)
                    .setPositiveButton(R.string.conferma, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseTools.deleteListBackupFiles(appoggio, getResources().getString(R.string.app_name));
                        }
                    })
                    .setNegativeButton(R.string.annulla, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        }else if (id == R.id.nav_share) {

            String uri = "https://play.google.com/store/apps/details?id=magazzino.bobo.com.magazzinodomestico";

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String ShareSub = getResources().getString(R.string.try_it);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /***
     * Aggiorna la lista
     * @param elencoNew
     */
    public void aggiornaLista(ArrayList<OggettoDao> elencoNew, boolean aggiornaDaDB)
    {
        //La variabile globale deve essere aggiornata, ma solo se sto aggiornando la lista dopo una modifica su DB
        if(aggiornaDaDB)
        {
            elencoOggetti = elencoNew;

            if(searchView != null) //Dopo una operazione che ha cambiato la lista, azzero la stringa di ricerca
                searchView.setQuery("", false);
        }

        //ArrayAdapter<OggettoDao> valori = new ArrayAdapter<OggettoDao>(this, android.R.layout.simple_list_item_1, elencoNew);
        ArrayAdapter<OggettoDao> valori = new OggettoAdapter(elencoNew, this);
        listaOggettiView.setAdapter(valori);
    }

    /***
     * Effettuo la ricerca nella lista
     *
     * @param searchText
     */
    private void search(String searchText)
    {
        ArrayList<OggettoDao> elencoRistretto = new ArrayList<OggettoDao>();

        for (OggettoDao dao: elencoOggetti) {
            if(dao.searchItem(searchText))
                elencoRistretto.add(dao);
        }

        //Aggiorno la lista in visualizzazione
        aggiornaLista(elencoRistretto, false);
    }
}
