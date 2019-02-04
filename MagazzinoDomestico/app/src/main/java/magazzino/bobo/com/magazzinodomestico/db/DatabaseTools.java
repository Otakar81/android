package magazzino.bobo.com.magazzinodomestico.db;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseTools {

    //Permessi necessari per le operazioni di backup e restore
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean backupDatabase(Activity activity, SQLiteDatabase database, String appName)
    {
        //Recupero il path del database
        String pathDB = database.getPath();

        try {

            //Chiedo il permesso di poter leggere e scrivere la memoria
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }

            //Recupero il puntamento alla memoria esterna
            File sd = Environment.getExternalStorageDirectory();

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && sd.canWrite()) {

                //Il file del database da salvare
                File dbOriginale = new File(pathDB);

                //Se non esiste, creo la cartella che dovrà ospitare i backup
                File folderDestinazione = new File(sd, appName);
                folderDestinazione.mkdir();

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date());
                String nomeFileBackup = String.format("%s.bak", "backup_" + timeStamp);
                File dbCopia = new File(folderDestinazione, nomeFileBackup);

                FileChannel src = new FileInputStream(dbOriginale).getChannel();
                FileChannel dst = new FileOutputStream(dbCopia).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            } else {

                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

}
