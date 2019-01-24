package magazzino.bobo.com.magazzinodomestico;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.adapters.StanzaAdapter;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.StanzaDao;
import magazzino.bobo.com.magazzinodomestico.dialogfragments.StanzaDialog;

public class StanzeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listaStanzeView;
    ArrayList<StanzaDao> elencoStanze;
    ConstraintLayout layout;
    SearchView searchView;


    @Override
    protected void onResume() {
        super.onResume();

        //Aggiorno la lista
        aggiornaLista(DatabaseManager.getAllStanze(MainActivity.database), true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stanze);

        //Recupero l'action bar e la status bar e cambio i loro colori
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorStanze)));

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorStanzeDark));

        setSupportActionBar(toolbar);

        //Bottone fluttuante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //Creo il dialog per l'inserimento
                AlertDialog.Builder builder = new AlertDialog.Builder(StanzeActivity.this);
                StanzaDialog dialog = StanzaDialog.newInstance(builder, false);
                dialog.show(getSupportFragmentManager(),"stanza_dialog");
            }
        });

        //Drawler laterale
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Inizializzo la ListView ed il layout
        layout = findViewById(R.id.layout);
        listaStanzeView = findViewById(R.id.listaStanzeView);
        elencoStanze = DatabaseManager.getAllStanze(MainActivity.database);

        //Popolo la lista
        aggiornaLista(elencoStanze, true);

        listaStanzeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                StanzaDao dao = (StanzaDao) parent.getItemAtPosition(position);

                //Creo un intent e vado sul dettaglio
                Intent intent = new Intent(getApplicationContext(), Stanze_DettaglioActivity.class);
                intent.putExtra("id_stanza", dao.getId());
                intent.putExtra("nome_stanza", dao.getNome());
                intent.putExtra("numero_mobili", dao.getNumeroMobili());
                intent.putExtra("numero_contenitori", dao.getNumeroContenitori());
                intent.putExtra("numero_oggetti", dao.getNumeroOggetti());
                startActivity(intent);
            };
        });

        listaStanzeView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                //Apre il dialog personalizzato, per modifica e cancellazione
                StanzaDao dao = (StanzaDao) parent.getItemAtPosition(position); //elencoStanze.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(StanzeActivity.this);
                StanzaDialog dialog = StanzaDialog.newInstance(builder, true);
                dialog.show(getSupportFragmentManager(),"stanza_dialog");

                //E lo valorizza con gli attributi dell'oggetto su cui abbiamo cliccato
                dialog.valorizzaDialog(dao.getId(), dao.getNome());

                return true;
            }
        });


        //Se non ci sono ancora stanze, avverto l'utente di crearne una ed apro il dialog di creazione
        if(elencoStanze.size() == 0) {

            Snackbar.make(layout, R.string.stanza_devi_creare_prima, Snackbar.LENGTH_LONG).show();

            //Creo il dialog per l'inserimento
            AlertDialog.Builder builder = new AlertDialog.Builder(StanzeActivity.this);
            StanzaDialog dialog = StanzaDialog.newInstance(builder, false);
            dialog.show(getSupportFragmentManager(),"stanza_dialog");
        }

        //Inzializzo la search view
        searchView = findViewById(R.id.searchStanze);
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
        //getMenuInflater().inflate(R.menu.stanze, menu);
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
            // Nulla, sono già qui
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
            startActivity(intent);        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***
     * Aggiorna la lista
     * @param elencoNew
     */
    public void aggiornaLista(ArrayList<StanzaDao> elencoNew, boolean aggiornaDaDB)
    {
        //La variabile globale deve essere aggiornata, ma solo se sto aggiornando la lista dopo una modifica su DB
        if(aggiornaDaDB) {
            elencoStanze = elencoNew;

            //Aggiorno la variabile usata per accorgersi se ci sono stanze nel database. In caso negativo, forza la creazione della prima
            if(elencoStanze.size() == 0)
                MainActivity.haveRooms = false;
            else
                MainActivity.haveRooms = true;
        }

        ArrayAdapter<StanzaDao> valori = new StanzaAdapter(elencoNew, this);
        listaStanzeView.setAdapter(valori);
    }

    /***
     * Effettuo la ricerca nella lista
     *
     * @param searchText
     */
    private void search(String searchText)
    {
        ArrayList<StanzaDao> elencoRistretto = new ArrayList<StanzaDao>();

        for (StanzaDao dao: elencoStanze) {
            if(dao.searchItem(searchText))
                elencoRistretto.add(dao);
        }

        //Aggiorno la lista in visualizzazione
        aggiornaLista(elencoRistretto, false);
    }
}
