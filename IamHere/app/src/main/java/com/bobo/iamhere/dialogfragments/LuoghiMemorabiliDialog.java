package com.bobo.iamhere.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.bobo.iamhere.MainActivity;
import com.bobo.iamhere.R;
import com.bobo.iamhere.db.DatabaseManager;

public class LuoghiMemorabiliDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_luogo_preferito, null))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminazione luogo")
                .setMessage("Sei sicuro di voler eliminare questo luogo?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                    }
                })
                .setNegativeButton("No", null)
                .show();


        // Create the AlertDialog object and return it
        return builder.create();
    }

}
