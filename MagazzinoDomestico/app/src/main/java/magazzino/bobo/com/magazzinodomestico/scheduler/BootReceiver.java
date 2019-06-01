package magazzino.bobo.com.magazzinodomestico.scheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/***
 * Il Receiver configurato nel manifest per partire al reboot del cellulare.
 * Si occupa, indirettamene, di lanciare la verifica degli oggetti in scadenza ogni giorno, per l'invio della relativa notifica.
 * Da Android 8 in poi è necessario usare in questa fase il JobScheduler al posto del semplice "service", e specificare il permesso
 * RECEIVE_BOOT_COMPLETED nel manifest
 */
public class BootReceiver extends BroadcastReceiver {


    public void onReceive(Context context, Intent intent)
    {
        //Log.i("MagazzinoDomestico", "Sono in BootReceiver");

        //Avvio un JobService al riavvio del dispositivo
        ComponentName componentName = new ComponentName(context, BootService.class);
        JobInfo jInfo = new JobInfo.Builder(1, componentName).setMinimumLatency(1000).build();

        JobScheduler jScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jScheduler.schedule(jInfo); //Schedulo il servizio

        //Log.i("MagazzinoDomestico", "Scheduler lanciato");
    }
}

