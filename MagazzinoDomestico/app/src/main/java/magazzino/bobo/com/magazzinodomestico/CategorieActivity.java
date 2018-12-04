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

public class CategorieActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listaCategorieView;


    @Override
    protected void onResume() {
        super.onResume();

        //Aggiorno la lista
        aggiornaLista(DatabaseManager.getAllCategorie(MainActivity.database));
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

                //Recupero l'oggetto che mi permetterà di caricare i layout
                LayoutInflater inflater = CategorieActivity.this.getLayoutInflater();
                final View dialoglayout = inflater.inflate(R.layout.dialog_categoria, null);

                AlertDialog dialog = new AlertDialog.Builder(CategorieActivity.this)
                        .setTitle("Aggiunta categoria")
                        .setMessage("Specificare il nome della categoria")
                        .setView(dialoglayout)
                        .setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText nomeView = dialoglayout.findViewById(R.id.nomeCategoria);
                                String nome = nomeView.getText().toString();

                                if(nome == null || nome.trim().equals(""))
                                {
                                    Snackbar.make(view, "Specificare il nome di una categoria", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }else{

                                    //Verifico che il nome passato come argomento non sia già stato usato
                                    CategoriaDao categoria = DatabaseManager.getCategoriaByName(MainActivity.database, nome);

                                    if(categoria != null)
                                    {
                                        Snackbar.make(view, "Nome già in uso", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    }else{

                                        //Creo e salvo la categoria
                                        categoria = new CategoriaDao(nome);
                                        DatabaseManager.insertCategoria(MainActivity.database, categoria);

                                        aggiornaLista(DatabaseManager.getAllCategorie(MainActivity.database));

                                        Snackbar.make(view, "Inserimento avvenuto con successo", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancella", null)
                        .create();

                dialog.show();
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
        final ArrayList<CategoriaDao> elencoCategorie = DatabaseManager.getAllCategorie(MainActivity.database);

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

                Toast.makeText(CategorieActivity.this, "Click", Toast.LENGTH_SHORT).show();
            };
        });

        listaCategorieView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                /*
                //Apre il dialog personalizzato, per modifica e cancellazione del luogo
                LocationDao locationDao = elencoPostiMemorabili.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(LuoghiMemorabiliActivity.this);
                LuoghiMemorabiliDialog luoghiMemorabiliDialog = LuoghiMemorabiliDialog.newInstance(builder);
                luoghiMemorabiliDialog.show(getSupportFragmentManager(),"luoghi_dialog");

                //E lo valorizza con gli attributi dell'oggetto su cui abbiamo cliccato
                luoghiMemorabiliDialog.valorizzaDialog(locationDao.getId(), locationDao.getAlias(), locationDao.getLuogoPreferito() == 1);
*/

                Toast.makeText(CategorieActivity.this, "LongClick", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_mobili) {

            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_contenitori) {

            Toast.makeText(this, "Funzione in lavorazione", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_categorie) {

            // Nulla, sono già qui
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***
     * Aggiorna la lista delle categorie
     * @param elencoCategorie
     */
    public void aggiornaLista(ArrayList<CategoriaDao> elencoCategorie)
    {
        /*
        ArrayAdapter<CategoriaDao> adapter = new LocationAdapter(elencoPostiMemorabili, this);
        listaPostiView.setAdapter(adapter);
        */

        //Per ora stampo solo una lista di stringhe
        ArrayList<String> elencoCategorieString = new ArrayList<String>();

        for (CategoriaDao categoria: elencoCategorie) {
            elencoCategorieString.add(categoria.getNome());
        }

        ArrayAdapter<String> valori = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elencoCategorieString);
        listaCategorieView.setAdapter(valori);
    }
}
