package com.bobo.iamhere.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bobo.iamhere.LuoghiMemorabiliActivity;
import com.bobo.iamhere.MainActivity;
import com.bobo.iamhere.R;
import com.bobo.iamhere.db.DatabaseManager;


/*
    Nota.
    In questo dialog valoriziamo i campi con i valori passati nel metodo "valorizzaDialog"
    Tuttavia le dua chiamate (onCreateDialog) e "valorizzaDialog" sono tra loro asincrone: chiamandole una dopo l'altra in LuoghiMemorabiliActivity
    non possiamo sapere se "valorizzaDialog" arriverà prima o dopo che il Dialog sia stato effettivamente creato.

    Per questo è necessario utilizzare delle variabili di istanza che facciano un po da ponte: se la chiamata a "valorizzaDialog" sarà arrivata prima
    della effettiva costruzione del dialog allora non valorizzerà le view (che sono ancora NULL) ma valorizzerà le variabili di istanza.
    A quel punto, nell'onCreate tali variabili si troveranno già valorizzate, ed al momento della creazione effettiva del dialog i campi saranno
    subito valorizzati.
 */


public class LuoghiMemorabiliDialog extends DialogFragment {

    //Variabili di istanza
    private long idLuogo;
    private String alias;
    private boolean isPreferito;


    //Elementi view del dialog
    private EditText aliasLuogoView;
    private CheckBox isPreferitoView;

    //Dialog builder
    private AlertDialog.Builder mBuilder;

    public static LuoghiMemorabiliDialog newInstance(AlertDialog.Builder builder){

        LuoghiMemorabiliDialog dialogFragment = new LuoghiMemorabiliDialog();
        dialogFragment.mBuilder = builder;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_luogo_preferito, null);

        //Valorizzo le view del layout
        aliasLuogoView = view.findViewById(R.id.aliasLuogo);
        isPreferitoView = view.findViewById(R.id.isLuogoPreferito);

        //E costruisco il builder
        mBuilder.setView(view)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.location_edit)
                .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String alias = aliasLuogoView.getText().toString().trim();
                        int isPreferito = 0;

                        if(isPreferitoView.isChecked())
                            isPreferito = 1;

                        //Modifico il luogo
                        DatabaseManager.updateLuogoPreferito(MainActivity.database, idLuogo, alias, isPreferito);

                        //Avverto la lista che i dati sono cambiati
                        ((LuoghiMemorabiliActivity)getActivity()).aggiornaListaFromDialog();

                        Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Elimino il posto dall'elenco di quelli memorizzati
                        DatabaseManager.deleteLocation(MainActivity.database, idLuogo);

                        //Avverto la lista che i dati sono cambiati
                        ((LuoghiMemorabiliActivity)getActivity()).aggiornaListaFromDialog();

                        Toast.makeText(getActivity(), R.string.eliminazione_successo, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton(R.string.cancel, null);

        //Se le variabili sono già state valorizzate, le uso per riempire la finestra
        if(this.alias != null && !this.alias.isEmpty()) {
            aliasLuogoView.setText(alias);
            isPreferitoView.setChecked(isPreferito); //Se è valorizzato l'alias, sicuramente lo sarà anche il boolean
        }

        // Create the AlertDialog object and return it
        return mBuilder.create();
    }

    /***
     * Prende in carico le informazioni relative agli attributi del luogo
     *
     * @param idLuogo
     * @param alias
     * @param isPreferito
     */
    public void valorizzaDialog(long idLuogo, String alias, boolean isPreferito)
    {
        //Valorizzo le variabili dell'oggetto
        this.idLuogo = idLuogo;
        this.alias = alias;
        this.isPreferito = isPreferito;

        //Se la view è stata crata, la valorizzo con i dati passati
        if(aliasLuogoView != null)
            aliasLuogoView.setText(alias);

        if(isPreferitoView != null)
            isPreferitoView.setChecked(isPreferito);
    }

}
