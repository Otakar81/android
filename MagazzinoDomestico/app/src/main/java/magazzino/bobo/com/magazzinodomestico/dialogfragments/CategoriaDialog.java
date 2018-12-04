package magazzino.bobo.com.magazzinodomestico.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import magazzino.bobo.com.magazzinodomestico.CategorieActivity;
import magazzino.bobo.com.magazzinodomestico.MainActivity;
import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.CategoriaDao;


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


public class CategoriaDialog extends DialogFragment {

    //Variabili di istanza
    private long id;
    private String nome;


    //Elementi view del dialog
    private EditText nomeView;

    //Dialog builder
    private AlertDialog.Builder mBuilder;

    public static CategoriaDialog newInstance(AlertDialog.Builder builder){

        CategoriaDialog dialogFragment = new CategoriaDialog();
        dialogFragment.mBuilder = builder;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_categoria, null);

        //Valorizzo le view del layout
        nomeView = view.findViewById(R.id.nomeCategoria);

        //E costruisco il builder
        mBuilder.setView(view)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Modifica luogo")
                .setPositiveButton("Modifica", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String nome = nomeView.getText().toString().trim();

                        CategoriaDao dao = new CategoriaDao(id, nome);

                        //Modifico il luogo
                        DatabaseManager.updateCategoria(MainActivity.database, dao);

                        //Avverto la lista che i dati sono cambiati
                        ((CategorieActivity)getActivity()).aggiornaLista(DatabaseManager.getAllCategorie(MainActivity.database));

                        Toast.makeText(getActivity(), "Luogo modificato", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Elimina luogo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Elimino il posto dall'elenco di quelli memorizzati
                        DatabaseManager.deleteCategoria(MainActivity.database, id);

                        //Avverto la lista che i dati sono cambiati
                        ((CategorieActivity)getActivity()).aggiornaLista(DatabaseManager.getAllCategorie(MainActivity.database));

                        Toast.makeText(getActivity(), "Luogo eliminato", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Cancella", null);


        //Se le variabili sono già state valorizzate, le uso per riempire la finestra
        if(this.nome != null) {
            nomeView.setText(nome);
        }

        // Create the AlertDialog object and return it
        return mBuilder.create();
    }

    /***
     * Prende in carico le informazioni relative agli attributi
     *
     * @param id
     * @param nome
     */
    public void valorizzaDialog(long id, String nome)
    {
        //Valorizzo le variabili dell'oggetto
        this.id = id;
        this.nome = nome;

        //Se la view è stata crata, la valorizzo con i dati passati
        if(nomeView != null)
            nomeView.setText(nome);
    }

}
