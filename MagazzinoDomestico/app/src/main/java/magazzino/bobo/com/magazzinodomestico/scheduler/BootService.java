package magazzino.bobo.com.magazzinodomestico.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

import magazzino.bobo.com.magazzinodomestico.MainActivity;

public class BootService extends JobService {

    JobParameters jParameters;
    DoItTask doIt;

    @Override
    public boolean onStartJob(JobParameters params) {

        //Appoggio i parametri nella variabile di isanza, per usarli eventualmente negli altri metodi
        this.jParameters = params;

        Log.i("MagazzinoDomestico", "Il servizio è stato startato");

        //Lancio il task asincrono
        doIt = new DoItTask();
        doIt.execute();

        //Ritorno
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        //Log.i("MagazzinoDomestico", "Il sistema sta provando a stoppare il servizio");

        if(doIt != null)
            doIt.cancel(true);

        return false;
    }


    /***
     * Programma l'avvio dello scheduler quotidiano per la verifica e l'eventuale notifica dei prodotti in scadenza
     */
    private void startAlarm() {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //Setto l'orario giornaliero in cui sarà necessario effettuare la verifica degli oggetti in scadenza
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, MainActivity.ORA_SCHEDULER);
        calendar.set(Calendar.MINUTE, MainActivity.MINUTO_SCHEDULER);

        Intent myIntent = new Intent(this, AlarmNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, myIntent,0);


        int numeroSecondi = 1000 * 60;

        if(true) //Metto true solo per testarlo subito, in questo modo manderà una notifica non appena riavvio il dispositivo
            manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+numeroSecondi,pendingIntent);
        else
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Log.i("MagazzinoDomestico", "Sono nello startAlarm ed ho fatto il mio lavoro");
    }


    //Creo la classe che si occupa di eseguire il task asincrono
    private class DoItTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {

            //Log.i("MagazzinoDomestico", "DoItTask - Pulisco il task qui ed interrompo il job");

            startAlarm(); //Programmo lo scheduler da lanciare ogni mattina

            jobFinished(jParameters, false);

            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Log.i("MyJobService", "DoItTask - do in background");

            return null;
        }
    }
}

