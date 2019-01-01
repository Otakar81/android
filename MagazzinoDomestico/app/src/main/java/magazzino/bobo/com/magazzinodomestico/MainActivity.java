package magazzino.bobo.com.magazzinodomestico;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import magazzino.bobo.com.magazzinodomestico.db.dao.LocationDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.OggettoDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.StanzaDao;
import magazzino.bobo.com.magazzinodomestico.dialogfragments.OggettoDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Database
    public static SQLiteDatabase database;

    //Elementi della pagina
    ListView listaOggettiView;
    SearchView searchView;
    ArrayList<OggettoDao> elencoOggetti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Bottone fluttuante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creo il dialog per il nuovo inserimento
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                OggettoDialog dialog = OggettoDialog.newInstance(builder, false, null);
                dialog.show(getSupportFragmentManager(),"oggetto_dialog");
            }
        });

        //Menu laterale
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Creo il database
        database = this.openOrCreateDatabase("magazzino_db", Context.MODE_PRIVATE, null);

        //Se non esistono, creo le tabelle
        DatabaseManager.createTables(database);

        //Se non ci sono ancora stanze su database, faccio redirect sull'activity relativa
        ArrayList<StanzaDao> elencoStanze = DatabaseManager.getAllStanze(database);

        if(elencoStanze.size() == 0)
        {
            Intent intent = new Intent(getApplicationContext(), StanzeActivity.class);
            startActivity(intent);
        }

        //Inizializzo la ListView
        listaOggettiView = findViewById(R.id.listaOggettiView);
        elencoOggetti = DatabaseManager.getAllOggetti(MainActivity.database);

        //Popolo la lista
        aggiornaLista(elencoOggetti, true);


        listaOggettiView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                //Apre il dialog personalizzato, per modifica e cancellazione
                OggettoDao dao = (OggettoDao) parent.getItemAtPosition(position); // elencoContenitori.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                OggettoDialog dialog = OggettoDialog.newInstance(builder, true, null);
                dialog.show(getSupportFragmentManager(),"oggetto_dialog");

                //E lo valorizza con gli attributi dell'oggetto su cui abbiamo cliccato
                dialog.valorizzaDialog(dao.getId(), dao.getNome(), dao.getId_stanza(), dao.getId_mobile(), dao.getId_contenitore(), dao.getId_categoria());

                return true;
            }
        });


        //Inzializzo la search view
        searchView = findViewById(R.id.searchOggetti);
        searchView.setActivated(true);
        searchView.setQueryHint("Type your keyword here");
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
        //getMenuInflater().inflate(R.menu.main, menu);
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
            // Nulla, sono già qui
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
            elencoOggetti = elencoNew;

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
