package magazzino.bobo.com.magazzinodomestico.db;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class DatabaseTools {

    //Permessi necessari per le operazioni di backup e restore
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Effettua il backup del database attuale sulla memoria esterna del dispositivo
     *
     * @param activity
     * @param database
     * @param appName
     * @return
     */
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

    /***
     * Effettua il restore del database partendo dal file di backup passato come argomento
     *
     * @param activity
     * @param database
     * @param appName
     * @param nomeFileBackup
     * @return
     */
    public static boolean restoreDatabase(Activity activity, SQLiteDatabase database, String appName, String nomeFileBackup)
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

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && sd.canRead()) {

                //Se non esiste, creo la cartella che dovrà ospitare i backup
                File folderBackup = new File(sd, appName);

                if(folderBackup.exists())
                {
                    File dbBackup = new File(folderBackup, nomeFileBackup);

                    //Il file del database da sovrascrivere
                    File dbOriginale = new File(pathDB);


                    FileChannel src = new FileInputStream(dbBackup).getChannel();
                    FileChannel dst = new FileOutputStream(dbOriginale).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                }else{
                    return false;
                }

            } else {

                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    /***
     * Restituisce l'array adapter con l'elenco di tutti i files presenti nella cartella di backup
     *
     * @param activity
     * @param appName
     * @return
     */
    public static ArrayAdapter<String> getListBackupFiles(Activity activity, String appName)
    {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                /// Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text size 25 dip for ListView each item
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);

                // Return the view
                return view;
            }
        };

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

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && sd.canRead()) {

                //Se non esiste, creo la cartella che dovrà ospitare i backup
                File folderBackup = new File(sd, appName);

                if(folderBackup.exists())
                {
                    //Recupero tutti i files .bak dalla cartella dell'app
                    String[] elencoFiles = folderBackup.list(
                            new FilenameFilter() {
                                public boolean accept(File dir, String name) {
                                    return name.toLowerCase().endsWith(".bak");
                                }
                            });

                    //Ordino i files in senso alfabetico inverso
                    Arrays.sort(elencoFiles,
                            new Comparator<String>() {
                                public int compare(String a, String b) {
                                    return b.compareTo(a);
                                }
                            });

                    //Popolo la lista
                    arrayAdapter.addAll(elencoFiles);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayAdapter;
    }

}
