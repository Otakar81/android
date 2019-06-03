package magazzino.bobo.com.magazzinodomestico;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;

import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.StanzaDao;
import magazzino.bobo.com.magazzinodomestico.scheduler.AlarmNotificationReceiver;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Database
    public static SQLiteDatabase database;
    public static boolean haveRooms; //True se c'è almeno una stanza nel database

    //Utilizzato per memorizzare gli update fatti sul database e non ripeterli inutilmente
    public static SharedPreferences preferences;

    //Channel ID usato per le notifiche
    public static final String CHANNEL_ID = "bobo_com_magazzinodomestico_01";

    public static final int ORA_SCHEDULER = 9;
    public static final int MINUTO_SCHEDULER = 0;


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

        //Creo il notification channel per la notifica di scadenza prodotti
        createNotificationChannel(CHANNEL_ID);

        //Creo lo scheduler per la notifica di scadenza prodotti
        startNotificationScheduler();

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

        //Se vengo chiamato da fuori (da una notifica di sistema) stabilisco su quale activity fare il redirect
        Intent intent = getIntent();
        String activity_destinazione = intent.getStringExtra("destination_activity");

        if(activity_destinazione != null)
        {
            if(activity_destinazione.equalsIgnoreCase("oggetti_scadenza"))
            {
                Intent outIntent = new Intent(getApplicationContext(), OggettiScadenzaActivity.class);
                startActivity(outIntent);
            }
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


    //Creo il Channel ID con relativa priorità (necessario per android 8.0 e superiore
    private void createNotificationChannel(String channel_ID)
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "bobo_magazzinodomestico_channel"; //getString(R.string.channel_name);
            String description = "Magazzino domestico Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channel_ID, name, importance);
            channel.setDescription(description);

            //Registro il canale nel sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startNotificationScheduler() {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //Setto l'orario giornaliero in cui sarà necessario effettuare la verifica degli oggetti in scadenza
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, ORA_SCHEDULER);
        calendar.set(Calendar.MINUTE, MINUTO_SCHEDULER);

        Intent myIntent = new Intent(this, AlarmNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, myIntent,0);

        //Verifico che l'alarm non esista già
        boolean alarmIsActive = (PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_NO_CREATE) != null);

        if(!alarmIsActive) //Se non è già attivo, procedo a crearlo
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //else
        //    manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+3000, pendingIntent);

    }

}
