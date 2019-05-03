package magazzino.bobo.com.magazzinodomestico;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.StanzaDao;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Database
    public static SQLiteDatabase database;
    public static boolean haveRooms; //True se c'è almeno una stanza nel database

    //Utilizzato per memorizzare gli update fatti sul database e non ripeterli inutilmente
    public static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Inizializzo le shared preferences
        preferences = getSharedPreferences("magazzino.bobo.com.magazzinodomestico", Context.MODE_PRIVATE);

        //Creo il database
        //database = this.openOrCreateDatabase("magazzino_db", Context.MODE_PRIVATE, null);
        database = DatabaseManager.openOrCreateDatabase(getApplicationContext());

        //Se non esistono, creo le tabelle
        DatabaseManager.createTables(database);

        //Se non ci sono ancora stanze su database, faccio redirect sull'activity relativa
        ArrayList<StanzaDao> elencoStanze = DatabaseManager.getAllStanze(database);

        if(elencoStanze.size() == 0) {

            haveRooms = false;

            Intent intent = new Intent(getApplicationContext(), StanzeActivity.class);
            startActivity(intent);

        }else{ //Altrimenti faccio il redirect sull'activity degli oggetti

            haveRooms = true;

            Intent intent = new Intent(getApplicationContext(), OggettiActivity.class);
            startActivity(intent);
        }
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
