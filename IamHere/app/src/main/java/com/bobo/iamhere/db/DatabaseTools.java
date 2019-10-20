package com.bobo.iamhere.db;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bobo.iamhere.MainActivity;
import com.bobo.iamhere.R;
import com.bobo.iamhere.utils.PermissionUtils;

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

    /**
     * Effettua il backup del database attuale sulla memoria esterna del dispositivo
     *
     * @param activity
     * @param database
     * @param appName
     * @return
     */
    public static void backupDatabase(Activity activity, SQLiteDatabase database, String appName)
    {
        boolean esito = true;

        if (PermissionUtils.checkSelfPermission_STORAGE(activity)) { //Se ho il permesso di accedere al disco, procedo

            try {

                //Recupero il path del database
                String pathDB = database.getPath();

                //Recupero il puntamento alla memoria esterna
                File sd = Environment.getExternalStorageDirectory();

                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && sd.canWrite()) {

                    //Il file del database da salvare
                    File dbOriginale = new File(pathDB);

                    //Se non esiste, creo la cartella che dovrà ospitare i backup
                    File folderDestinazione = new File(sd, appName);
                    folderDestinazione.mkdir();

                    //Chiudo il DB
                    database.close();

                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date());
                    String nomeFileBackup = String.format("%s.bak", "backup_" + timeStamp);
                    File dbCopia = new File(folderDestinazione, nomeFileBackup);

                    FileChannel src = new FileInputStream(dbOriginale).getChannel();
                    FileChannel dst = new FileOutputStream(dbCopia).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                } else {

                    Toast.makeText(activity, R.string.errore_memoria_esterna, Toast.LENGTH_LONG).show();
                    esito = false;
                }


            } catch (Exception e) {

                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                esito = false;

            } finally {

                //Riapro la connessione al DB
                MainActivity.database = DatabaseManager.openOrCreateDatabase(activity.getApplicationContext());
            }

            if(esito)
                Toast.makeText(activity, R.string.operazione_successo, Toast.LENGTH_LONG).show();

        }
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
    public static void restoreDatabase(Activity activity, SQLiteDatabase database, String appName, String nomeFileBackup)
    {
        boolean esito = true;

        //Chiedo il permesso di poter leggere e scrivere la memoria
        if (!PermissionUtils.checkSelfPermission_STORAGE(activity)) { //Se non mi è stato dato, lo chiedo nuovamente

            PermissionUtils.requestPermissions(activity, PermissionUtils.PERMISSIONS_STORAGE, PermissionUtils.REQUEST_EXTERNAL_STORAGE);

        } else { //Altrimenti posso procedere con le operazioni

            try {

                //Recupero il path del database
                String pathDB = database.getPath();

                //Recupero il puntamento alla memoria esterna
                File sd = Environment.getExternalStorageDirectory();

                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && sd.canRead()) {

                    //Se non esiste, creo la cartella che dovrà ospitare i backup
                    File folderBackup = new File(sd, appName);

                    File dbBackup = new File(folderBackup, nomeFileBackup);

                    //Il file del database da sovrascrivere
                    File dbOriginale = new File(pathDB);

                    //Chiudo il DB
                    database.close();

                    //Elimino il file corrente
                    dbOriginale.delete();

                    //Copio il vecchio database
                    FileChannel src = new FileInputStream(dbBackup).getChannel();
                    FileChannel dst = new FileOutputStream(dbOriginale).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                } else {

                    Toast.makeText(activity, R.string.errore_memoria_esterna, Toast.LENGTH_LONG).show();
                    esito = false;
                }

            } catch (Exception e) {

                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                esito = false;

            } finally {

                //Riapro la connessione al DB
                MainActivity.database = DatabaseManager.openOrCreateDatabase(activity.getApplicationContext());
            }

            if(esito) {

                //Torno sulla MainActivity
                Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                activity.startActivity(intent);

                Toast.makeText(activity, R.string.operazione_successo, Toast.LENGTH_LONG).show();
            }
        }
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

        //Chiedo il permesso di poter leggere e scrivere la memoria
        if (!PermissionUtils.checkSelfPermission_STORAGE(activity)) { //Se non mi è stato dato, lo chiedo nuovamente

            PermissionUtils.requestPermissions(activity, PermissionUtils.PERMISSIONS_STORAGE, PermissionUtils.REQUEST_EXTERNAL_STORAGE);

        } else {

            try {

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

        }

        return arrayAdapter;
    }


    /***
     * Elimino tutti i file di Backup presenti nella memoria esterna
     *
     * @param activity
     * @param appName
     */
    public static void deleteListBackupFiles(Activity activity, String appName)
    {
        //Chiedo il permesso di poter leggere e scrivere la memoria
        if (!PermissionUtils.checkSelfPermission_STORAGE(activity)) { //Se non mi è stato dato, lo chiedo nuovamente

            PermissionUtils.requestPermissions(activity, PermissionUtils.PERMISSIONS_STORAGE, PermissionUtils.REQUEST_EXTERNAL_STORAGE);

        } else {

            boolean esito = true;

            try {

                //Recupero il puntamento alla memoria esterna
                File sd = Environment.getExternalStorageDirectory();

                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && sd.canRead()) {

                    //Se non esiste, creo la cartella che dovrà ospitare i backup
                    File folderBackup = new File(sd, appName);

                    if(folderBackup.exists())
                    {
                        //Recupero tutti i files .bak dalla cartella dell'app
                        File[] elencoFiles = folderBackup.listFiles(
                                new FilenameFilter() {
                                    public boolean accept(File dir, String name) {
                                        return name.toLowerCase().endsWith(".bak");
                                    }
                                });


                        //E li elimino
                        for (File file: elencoFiles)
                            file.delete();
                    }

                } else {
                    Toast.makeText(activity, R.string.errore_memoria_esterna, Toast.LENGTH_LONG).show();
                    esito = false;
                }

            } catch (Exception e) {

                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                esito = false;
            }

            if(esito)
                Toast.makeText(activity, R.string.operazione_successo, Toast.LENGTH_LONG).show();
        }
    }

}
