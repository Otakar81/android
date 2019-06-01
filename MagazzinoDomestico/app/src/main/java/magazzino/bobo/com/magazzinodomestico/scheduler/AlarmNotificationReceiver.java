package magazzino.bobo.com.magazzinodomestico.scheduler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.MainActivity;
import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.OggettoDao;


public class AlarmNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Devo verificare se ci sono degli oggetti in scadenza
        //In caso affermativo, manderò una notifica
        if(hasOggettiInScadenza(context))
        {
            String appName = context.getResources().getString(R.string.app_name);
            String prodottiScadenza = context.getResources().getString(R.string.oggetto_in_scadenza);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID);

            //Creo l'intent da chiamare quando l'utente cliccherà sulla notifica
            Intent destinationIntent = new Intent(context, MainActivity.class);
            destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            destinationIntent.putExtra("destination_activity", "oggetti_scadenza");


            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, destinationIntent, 0);


            builder.setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(android.R.drawable.stat_notify_sync_noanim)
                    .setContentTitle(appName)
                    .setContentText(prodottiScadenza)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());

        }
    }

    /***
     * Verifico se ci sono oggetti in scadenza nei prossimi giorni
     * @return
     */
    private boolean hasOggettiInScadenza(Context context)
    {
        //Apro la connessione al database
        SQLiteDatabase database = DatabaseManager.openOrCreateDatabase(context);

        //Recupero tutti gli oggetti che abbiano una scadenza
        ArrayList<OggettoDao> elencoOggetti = DatabaseManager.getAllOggetti(database, true);

        //Verifico che qualcuno di questi oggetti scada nei prossimi due giorni
        for (OggettoDao dao: elencoOggetti) {

            int giorniAllaScadenza = dao.giorniAllaScadenza();

            if(giorniAllaScadenza >= 0 && giorniAllaScadenza < 2) //In caso affermativo, restituisco true
                return true;

        }

        return false;  //Altrimenti restituisco false
    }

}

