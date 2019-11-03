package com.bobo.iamhere.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.ArrayAdapter;

import com.bobo.iamhere.R;
import com.bobo.iamhere.db.DatabaseTools;


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
        if(elencoFiles.getCount() == 0)
            mBuilder.setTitle(R.string.database_restore_no_file);
        else
            mBuilder.setTitle(R.string.database_seleziona_file);

        mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                builderInner.setTitle(R.string.database_seleziona_file_conferma);
                builderInner.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseTools.restoreDatabase(activity, database, appName, strName);
                    }
                })
                .setNegativeButton(R.string.cancel, null);


                builderInner.show();
            }
        });


        // Create the AlertDialog object and return it
        return mBuilder.create();
    }
}
