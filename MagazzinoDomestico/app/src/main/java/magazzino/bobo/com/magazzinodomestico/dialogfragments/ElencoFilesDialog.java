package magazzino.bobo.com.magazzinodomestico.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseTools;

public class ElencoFilesDialog extends DialogFragment {

    String appName;
    SQLiteDatabase database;
    Activity activity;

    //Dialog builder
    private AlertDialog.Builder mBuilder;

    public static ElencoFilesDialog newInstance(AlertDialog.Builder builder, SQLiteDatabase database, String appName){

        ElencoFilesDialog dialogFragment = new ElencoFilesDialog();
        dialogFragment.database = database;
        dialogFragment.appName = appName;
        dialogFragment.mBuilder = builder;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        activity = getActivity();

        //Recupero l'adapter con la lista dei files
        final ArrayAdapter<String> elencoFiles = DatabaseTools.getListBackupFiles(activity, appName);

        //mBuilder.setIcon(android.support.v4.R.drawable.notification_icon_background);
        mBuilder.setTitle("Seleziona il File di Backup");

        mBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mBuilder.setAdapter(elencoFiles, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = elencoFiles.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(activity);
                builderInner.setMessage(strName);
                builderInner.setTitle("Vuoi ripristinare questo file?");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {

                        boolean esito = DatabaseTools.restoreDatabase(activity, database, appName, strName);

                        if(esito)
                            Toast.makeText(activity, R.string.operazione_successo, Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(activity, R.string.errore, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.annulla, null);


                builderInner.show();
            }
        });


        // Create the AlertDialog object and return it
        return mBuilder.create();
    }
}
