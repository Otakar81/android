package magazzino.bobo.com.magazzinodomestico;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.adapters.CategoriaAdapter;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseTools;
import magazzino.bobo.com.magazzinodomestico.db.dao.CategoriaDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.ContenitoreDao;
import magazzino.bobo.com.magazzinodomestico.dialogfragments.CategoriaDialog;
import magazzino.bobo.com.magazzinodomestico.dialogfragments.ElencoFilesDialog;

public class CategorieActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listaCategorieView;
    ArrayList<CategoriaDao> elencoCategorie;
    SearchView searchView;



    @Override
    protected void onResume() {
        super.onResume();

        //Aggiorno la lista
        aggiornaLista(DatabaseManager.getAllCategorie(MainActivity.database, false), true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorie);

        //Recupero l'action bar e la status bar e cambio i loro colori
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorCategorie)));

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorCategorieDark));

        setSupportActionBar(toolbar);

        //Bottone fluttuante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //Mostro il dialog per il nuovo inserimento
                AlertDialog.Builder builder = new AlertDialog.Builder(CategorieActivity.this);
                CategoriaDialog categoriaDialog = CategoriaDialog.newInstance(builder, false);
                categoriaDialog.show(getSupportFragmentManager(),"categoria_dialog");
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

        //Inizializzo la ListView
        listaCategorieView = findViewById(R.id.listaCategorieView);
        elencoCategorie = DatabaseManager.getAllCategorie(MainActivity.database, false);

        //Popolo la lista delle categorie
        aggiornaLista(elencoCategorie, true);

        listaCategorieView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CategoriaDao dao = (CategoriaDao) parent.getItemAtPosition(position);

                //Creo un intent e vado sulla mappa
                Intent intent = new Intent(getApplicationContext(), Categorie_DettaglioActivity.class);
                intent.putExtra("id_categoria", dao.getId());
                intent.putExtra("nome_categoria", dao.getNome());
                intent.putExtra("numero_contenitori", dao.getNumeroContenitori());
                intent.putExtra("numero_oggetti", dao.getNumeroOggetti());
                startActivity(intent);
            };
        });

        listaCategorieView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                //Apre il dialog personalizzato, per modifica e cancellazione del luogo
                CategoriaDao dao = (CategoriaDao) parent.getItemAtPosition(position); //elencoCategorie.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(CategorieActivity.this);
                CategoriaDialog categoriaDialog = CategoriaDialog.newInstance(builder, true);
                categoriaDialog.show(getSupportFragmentManager(),"categoria_dialog");

                //E lo valorizza con gli attributi dell'oggetto su cui abbiamo cliccato
                categoriaDialog.valorizzaDialog(dao.getId(), dao.getNome());


                //Toast.makeText(CategorieActivity.this, "LongClick", Toast.LENGTH_SHORT).show();
                return true;

            }
        });

        //Inzializzo la search view
        searchView = findViewById(R.id.searchCategorie);
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
        //getMenuInflater().inflate(R.menu.categorie, menu);
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

            // Nulla, sono già qui

        }else if (id == R.id.nav_database_export) {

            DatabaseTools.backupDatabase(this, MainActivity.database, getResources().getString(R.string.app_name));

        }else if (id == R.id.nav_database_import) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ElencoFilesDialog dialog = ElencoFilesDialog.newInstance(builder, MainActivity.database, getResources().getString(R.string.app_name));
            dialog.show(getSupportFragmentManager(),"files_dialog");

        }else if (id == R.id.nav_database_delete) {

            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***
     * Aggiorna la lista delle categorie
     * @param elencoNew
     */
    public void aggiornaLista(ArrayList<CategoriaDao> elencoNew, boolean aggiornaDaDB)
    {
        //La variabile globale deve essere aggiornata, ma solo se sto aggiornando la lista dopo una modifica su DB
        if(aggiornaDaDB)
            elencoCategorie = elencoNew;

        ArrayAdapter<CategoriaDao> valori = new CategoriaAdapter(elencoNew, this);
        listaCategorieView.setAdapter(valori);
    }

    /***
     * Effettuo la ricerca nella lista
     *
     * @param searchText
     */
    private void search(String searchText)
    {
        ArrayList<CategoriaDao> elencoRistretto = new ArrayList<CategoriaDao>();

        for (CategoriaDao dao: elencoCategorie) {
            if(dao.searchItem(searchText))
                elencoRistretto.add(dao);
        }

        //Aggiorno la lista in visualizzazione
        aggiornaLista(elencoRistretto, false);
    }
}
