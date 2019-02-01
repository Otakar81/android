package magazzino.bobo.com.magazzinodomestico.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

    //Specifica se il dialog da aprire sarà in modalità "edit" oppure "nuova istanza"
    boolean isEditMode;

    //Variabili di istanza
    private long id;
    private String nome;


    //Elementi view del dialog
    private EditText nomeView;

    //Dialog builder
    private AlertDialog.Builder mBuilder;

    public static CategoriaDialog newInstance(AlertDialog.Builder builder, boolean isEditMode){

        CategoriaDialog dialogFragment = new CategoriaDialog();
        dialogFragment.isEditMode = isEditMode;
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
        if(isEditMode) //Finestra per edit di un elemento esistente
        {
            mBuilder.setView(view)
                    .setIcon(R.drawable.nav_categorie)
                    .setTitle(R.string.categoria_modifica)
                    .setPositiveButton(R.string.modifica, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String nome = nomeView.getText().toString().trim();

                            CategoriaDao dao = new CategoriaDao(id, nome);

                            //Modifico il luogo
                            DatabaseManager.updateCategoria(MainActivity.database, dao);

                            //Avverto la lista che i dati sono cambiati
                            ((CategorieActivity)getActivity()).aggiornaLista(DatabaseManager.getAllCategorie(MainActivity.database, false), true);

                            Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();


                            /*Se volessi usare la Snackbar
                            View miaView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
                            Snackbar.make(miaView, R.string.operazione_successo, Snackbar.LENGTH_SHORT).show();
                            */
                        }
                    })
                    .setNegativeButton(R.string.elimina, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Elimino il posto dall'elenco di quelli memorizzati
                            DatabaseManager.deleteCategoria(MainActivity.database, id);

                            //Avverto la lista che i dati sono cambiati
                            ((CategorieActivity)getActivity()).aggiornaLista(DatabaseManager.getAllCategorie(MainActivity.database, false), true);

                            Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNeutralButton(R.string.annulla, null);

            //Se le variabili sono già state valorizzate, le uso per riempire la finestra
            if(this.nome != null) {
                nomeView.setText(nome);
            }

        }else{ //Finestra per nuovo inserimento

            mBuilder.setView(view)
                    .setIcon(R.drawable.nav_categorie)
                    .setTitle(R.string.categoria_nuova)
                    .setPositiveButton(R.string.aggiungi, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String nome = nomeView.getText().toString().trim();

                            if(nome == null || nome.trim().equals(""))
                            {
                                Toast.makeText(getActivity(), R.string.errore_nome, Toast.LENGTH_SHORT).show();
                            }else{

                                //Verifico che il nome passato come argomento non sia già stato usato
                                CategoriaDao categoria = DatabaseManager.getCategoriaByName(MainActivity.database, nome);

                                if(categoria != null)
                                {
                                    Toast.makeText(getActivity(), R.string.errore_nome_in_uso, Toast.LENGTH_SHORT).show();
                                }else{

                                    //Creo e salvo la categoria
                                    categoria = new CategoriaDao(nome);
                                    DatabaseManager.insertCategoria(MainActivity.database, categoria);

                                    ((CategorieActivity)getActivity()).aggiornaLista(DatabaseManager.getAllCategorie(MainActivity.database, false), true);

                                    Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .setNegativeButton(R.string.elimina, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Elimino il posto dall'elenco di quelli memorizzati
                            DatabaseManager.deleteCategoria(MainActivity.database, id);

                            //Avverto la lista che i dati sono cambiati
                            ((CategorieActivity)getActivity()).aggiornaLista(DatabaseManager.getAllCategorie(MainActivity.database, false), true);

                            Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNeutralButton(R.string.annulla, null);

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
