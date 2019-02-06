package magazzino.bobo.com.magazzinodomestico.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

import magazzino.bobo.com.magazzinodomestico.db.DatabaseTools;

public class ElencoFilesDialog extends DialogFragment {

    String appName;

    //Dialog builder
    private AlertDialog.Builder mBuilder;

    public static ElencoFilesDialog newInstance(AlertDialog.Builder builder, String appName){

        ElencoFilesDialog dialogFragment = new ElencoFilesDialog();
        dialogFragment.appName = appName;
        dialogFragment.mBuilder = builder;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Recupero l'adapter con la lista dei files
        final ArrayAdapter<String> elencoFiles = DatabaseTools.getListBackupFiles(getActivity(), appName);

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
                String strName = elencoFiles.getItem(which);

                AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });


        // Create the AlertDialog object and return it
        return mBuilder.create();
    }
}
