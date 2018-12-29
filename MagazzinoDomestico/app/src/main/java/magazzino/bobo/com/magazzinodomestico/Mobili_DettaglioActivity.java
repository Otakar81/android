package magazzino.bobo.com.magazzinodomestico;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.adapters.ContenitoreAdapter;
import magazzino.bobo.com.magazzinodomestico.adapters.OggettoAdapter;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.ContenitoreDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.LocationDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.OggettoDao;
import magazzino.bobo.com.magazzinodomestico.dialogfragments.ContenitoreDialog;
import magazzino.bobo.com.magazzinodomestico.dialogfragments.OggettoDialog;

public class Mobili_DettaglioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listaView;
    SearchView searchView;
    ArrayList<OggettoDao> elencoOggetti;
    ArrayList<ContenitoreDao> elencoContenitori;

    long ID_MOBILE_SELEZIONATO;
    String NOME_MOBILE_SELEZIONATO;

    LocationDao location;

    int tipoElementiDaMostrare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobili__dettaglio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Valorizzo gli ID del contenitore di cui sto visualizzando il contenuto, e creo il "LocationDao" da usare sul dialog
        Intent intent = getIntent();
        ID_MOBILE_SELEZIONATO = intent.getLongExtra("id_mobile", -1);
        NOME_MOBILE_SELEZIONATO = intent.getStringExtra("nome_mobile");
        long stanzaSelezionata = intent.getLongExtra("id_stanza", -1);

        location = new LocationDao(-1, stanzaSelezionata, ID_MOBILE_SELEZIONATO, -1);

        //Setto il titolo
        setTitle(NOME_MOBILE_SELEZIONATO);

        //Flag usato per filtrare la tipologia di oggetti da mostrare
        tipoElementiDaMostrare = LocationDao.CONTENITORE; //Di default, mostrerò i contenitori presenti nel mobile

        //Bottone fluttuante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Creo il dialog per il nuovo inserimento
                //A seconda di cosa sto mostrando, aprirò il dialog dei contenitori o degli oggetti
                if(tipoElementiDaMostrare == LocationDao.CONTENITORE)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Mobili_DettaglioActivity.this);
                    ContenitoreDialog dialog = ContenitoreDialog.newInstance(builder, false, location);
                    dialog.show(getSupportFragmentManager(),"contenitore_dialog");

                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(Mobili_DettaglioActivity.this);
                    OggettoDialog dialog = OggettoDialog.newInstance(builder, false, location);
                    dialog.show(getSupportFragmentManager(),"oggetto_dialog");
                }
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

        //Inizializzo la ListView e l'elenco con i valori di default
        listaView = findViewById(R.id.listaView);
        elencoContenitori = DatabaseManager.getAllContenitoriByLocation(MainActivity.database, location);
        aggiornaListaContenitori(elencoContenitori, true);

        listaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(tipoElementiDaMostrare == LocationDao.CONTENITORE) //Entro nel dettaglio del contenitore
                {
                    ContenitoreDao dao = (ContenitoreDao) parent.getItemAtPosition(position);

                    //Creo un intent e vado sul dettaglio
                    Intent intent = new Intent(getApplicationContext(), Contenitori_DettaglioActivity.class);
                    intent.putExtra("id_contenitore", dao.getId());
                    intent.putExtra("id_mobile", dao.getId_mobile());
                    intent.putExtra("id_stanza", dao.getId_stanza());
                    intent.putExtra("nome_contenitore", dao.getNome());
                    startActivity(intent);
                }
            };
        });

        listaView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                if(tipoElementiDaMostrare == LocationDao.CONTENITORE) //Apro il dialog di modifica del contenitore
                {
                    //Apre il dialog personalizzato, per modifica e cancellazione
                    ContenitoreDao dao = (ContenitoreDao) parent.getItemAtPosition(position); // elencoContenitori.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(Mobili_DettaglioActivity.this);
                    ContenitoreDialog dialog = ContenitoreDialog.newInstance(builder, true, location);
                    dialog.show(getSupportFragmentManager(),"contenitore_dialog");

                    //E lo valorizza con gli attributi dell'oggetto su cui abbiamo cliccato
                    dialog.valorizzaDialog(dao.getId(), dao.getNome(), dao.getId_stanza(), dao.getId_mobile(), dao.getId_categoria());

                }else{ //O dell'oggetto selezionato

                    //Apre il dialog personalizzato, per modifica e cancellazione
                    OggettoDao dao = (OggettoDao) parent.getItemAtPosition(position); // elencoContenitori.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(Mobili_DettaglioActivity.this);
                    OggettoDialog dialog = OggettoDialog.newInstance(builder, true, location);
                    dialog.show(getSupportFragmentManager(),"oggetto_dialog");

                    //E lo valorizza con gli attributi dell'oggetto su cui abbiamo cliccato
                    dialog.valorizzaDialog(dao.getId(), dao.getNome(), dao.getId_stanza(), dao.getId_mobile(), dao.getId_contenitore(), dao.getId_categoria());
                }

                return true;
            }
        });


        //Inzializzo la search view
        searchView = findViewById(R.id.searchField);
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
        getMenuInflater().inflate(R.menu.mobili__dettaglio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_mostra_contenitori) {

            if(tipoElementiDaMostrare != LocationDao.CONTENITORE) //Faccio qualcosa solo se non sto già mostrando i contenitori
            {
                //TODO Cambio icone
                //    item.setIcon(R.drawable.action_preferiti_no);

                //Modifico il valore della variabile
                tipoElementiDaMostrare = LocationDao.CONTENITORE;

                //Faccio refresh della lista, mostrando la tipologia di elementi richiesti dell'utente
                if(elencoContenitori == null)
                    elencoContenitori = DatabaseManager.getAllContenitoriByLocation(MainActivity.database, location);

                //Popolo la lista
                aggiornaListaContenitori(elencoContenitori, false);
            }

            return true;

        } else if (id == R.id.action_mostra_oggetti) {

            if(tipoElementiDaMostrare != LocationDao.OGGETTO) //Faccio qualcosa solo se non sto già mostrando i contenitori
            {
                //TODO Cambio icone
                //    item.setIcon(R.drawable.action_preferiti_no);

                //Modifico il valore della variabile
                tipoElementiDaMostrare = LocationDao.OGGETTO;

                //Faccio refresh della lista, mostrando la tipologia di elementi richiesti dell'utente
                if(elencoOggetti == null)
                    elencoOggetti = DatabaseManager.getAllOggettiByLocation(MainActivity.database, location);

                //Popolo la lista
                aggiornaListaOggetti(elencoOggetti, false);
            }

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
            startActivity(intent);        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /***
     * Aggiorna la lista
     * @param elencoNew
     */
    public void aggiornaListaOggetti(ArrayList<OggettoDao> elencoNew, boolean aggiornaDaDB)
    {
        //La variabile globale deve essere aggiornata, ma solo se sto aggiornando la lista dopo una modifica su DB
        if(aggiornaDaDB)
            elencoOggetti = elencoNew;

        ArrayAdapter<OggettoDao> valori = new OggettoAdapter(elencoNew, this);
        listaView.setAdapter(valori);
    }

    /***
     *
     * @param elencoNew
     * @param aggiornaDaDB
     */
    public void aggiornaListaContenitori(ArrayList<ContenitoreDao> elencoNew, boolean aggiornaDaDB)
    {
        //La variabile globale deve essere aggiornata, ma solo se sto aggiornando la lista dopo una modifica su DB
        if(aggiornaDaDB)
            elencoContenitori = elencoNew;

        ArrayAdapter<ContenitoreDao> valori = new ContenitoreAdapter(elencoNew, this);
        listaView.setAdapter(valori);
    }

    /***
     * Effettuo la ricerca nella lista
     *
     * @param searchText
     */
    private void search(String searchText)
    {
        //Se sto mostrando i contenitori, effettuo una ricerca sulla lista corrispondente
        if (tipoElementiDaMostrare == LocationDao.CONTENITORE) {

            ArrayList<ContenitoreDao> elencoRistretto = new ArrayList<ContenitoreDao>();

            for (ContenitoreDao dao: elencoContenitori) {
                if(dao.searchItem(searchText))
                    elencoRistretto.add(dao);
            }

            //Aggiorno la lista in visualizzazione
            aggiornaListaContenitori(elencoRistretto, false);

        } else { //Sto mostrando gli oggetti

            ArrayList<OggettoDao> elencoRistretto = new ArrayList<OggettoDao>();

            for (OggettoDao dao: elencoOggetti) {
                if(dao.searchItem(searchText))
                    elencoRistretto.add(dao);
            }

            //Aggiorno la lista in visualizzazione
            aggiornaListaOggetti(elencoRistretto, false);
        }
    }
}
