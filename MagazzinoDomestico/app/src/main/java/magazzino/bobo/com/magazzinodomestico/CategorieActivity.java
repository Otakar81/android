package magazzino.bobo.com.magazzinodomestico;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.CategoriaDao;
import magazzino.bobo.com.magazzinodomestico.dialogfragments.CategoriaDialog;

public class CategorieActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listaCategorieView;
    ArrayList<CategoriaDao> elencoCategorie;



    @Override
    protected void onResume() {
        super.onResume();

        //Aggiorno la lista
        aggiornaLista(DatabaseManager.getAllCategorie(MainActivity.database, false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        aggiornaLista(elencoCategorie);

        listaCategorieView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*
                LocationDao locationDao = elencoPostiMemorabili.get(position);

                //Creo un intent e vado sulla mappa
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("latitudine", locationDao.getLatitudine());
                intent.putExtra("longitudine", locationDao.getLongitudine());
                startActivity(intent);
                */

                //Dovrei saltare alla pagina con l'elenco degli elementi associati a questa categoria
                Toast.makeText(CategorieActivity.this, "Click", Toast.LENGTH_SHORT).show();
            };
        });

        listaCategorieView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                //Apre il dialog personalizzato, per modifica e cancellazione del luogo
                CategoriaDao dao = elencoCategorie.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(CategorieActivity.this);
                CategoriaDialog categoriaDialog = CategoriaDialog.newInstance(builder, true);
                categoriaDialog.show(getSupportFragmentManager(),"categoria_dialog");

                //E lo valorizza con gli attributi dell'oggetto su cui abbiamo cliccato
                categoriaDialog.valorizzaDialog(dao.getId(), dao.getNome());


                //Toast.makeText(CategorieActivity.this, "LongClick", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.categorie, menu);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***
     * Aggiorna la lista delle categorie
     * @param elencoCategorieNew
     */
    public void aggiornaLista(ArrayList<CategoriaDao> elencoCategorieNew)
    {
        /*
        ArrayAdapter<CategoriaDao> adapter = new LocationAdapter(elencoPostiMemorabili, this);
        listaPostiView.setAdapter(adapter);
        */

        //La variabile globale deve essere aggiornata
        elencoCategorie = elencoCategorieNew;

        //Per ora stampo solo una lista di stringhe
        ArrayList<String> elencoCategorieString = new ArrayList<String>();

        for (CategoriaDao categoria: elencoCategorieNew) {
            elencoCategorieString.add(categoria.getNome());
        }

        ArrayAdapter<String> valori = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elencoCategorieString);
        listaCategorieView.setAdapter(valori);
    }
}
