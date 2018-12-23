package magazzino.bobo.com.magazzinodomestico.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.ContenitoriActivity;
import magazzino.bobo.com.magazzinodomestico.MainActivity;
import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.CategoriaDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.ContenitoreDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.MobileDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.StanzaDao;


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


public class OggettoDialog extends DialogFragment {

    //Specifica se il dialog da aprire sarà in modalità "edit" oppure "nuova istanza"
    boolean isEditMode;

    //Variabili di istanza
    private long id;
    private String nome;
    private long id_stanza;
    private long id_mobile;
    private long id_categoria;
    private long id_contenitore;


    private ArrayList<StanzaDao> elencoStanze;
    private ArrayList<MobileDao> elencoMobili;
    private ArrayList<CategoriaDao> elencoCategorie;
    private ArrayList<ContenitoreDao> elencoContenitori;



    //Elementi view del dialog
    private EditText nomeView;
    private Spinner elencoStanzeView;
    private Spinner elencoMobiliView;
    private Spinner elencoCategorieView;
    private Spinner elencoContenitoriView;


    //Dialog builder
    private AlertDialog.Builder mBuilder;

    public static OggettoDialog newInstance(AlertDialog.Builder builder, boolean isEditMode){

        OggettoDialog dialogFragment = new OggettoDialog();
        dialogFragment.isEditMode = isEditMode;
        dialogFragment.mBuilder = builder;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_oggetto, null);

        //Valorizzo le view del layout
        nomeView = view.findViewById(R.id.nomeOggetto);
        elencoStanzeView = view.findViewById(R.id.elencoStanze);
        elencoMobiliView = view.findViewById(R.id.elencoMobili);
        elencoContenitoriView = view.findViewById(R.id.elencoContenitori);
        elencoCategorieView = view.findViewById(R.id.elencoCategorie);


        //Setto l'adapter per gli spinner
        elencoStanze = DatabaseManager.getAllStanze(MainActivity.database);
        ArrayAdapter<StanzaDao> valoriStanze = new ArrayAdapter<StanzaDao>(getActivity(), android.R.layout.simple_list_item_1, elencoStanze);
        elencoStanzeView.setAdapter(valoriStanze);

        elencoStanzeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                StanzaDao stanzaSelezionata = elencoStanze.get(position);

                elencoMobili = DatabaseManager.getAllMobiliByStanza(MainActivity.database, stanzaSelezionata.getId(), true);
                ArrayAdapter<MobileDao> valoriMobili = new ArrayAdapter<MobileDao>(getActivity(), android.R.layout.simple_list_item_1, elencoMobili);
                elencoMobiliView.setAdapter(valoriMobili);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Di default creo la lista vuota. Poi saranno filtrati per stanza
        elencoMobili = new ArrayList<MobileDao>();


        elencoCategorie = DatabaseManager.getAllCategorie(MainActivity.database, true);
        ArrayAdapter<CategoriaDao> valoriCategorie = new ArrayAdapter<CategoriaDao>(getActivity(), android.R.layout.simple_list_item_1, elencoCategorie);
        elencoCategorieView.setAdapter(valoriCategorie);

        //E costruisco il builder
        if(isEditMode) //Finestra per edit di un elemento esistente
        {
            mBuilder.setView(view)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Modifica contenitore")
                    .setPositiveButton("Modifica", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String nome = nomeView.getText().toString().trim();

                            StanzaDao stanza = (StanzaDao) elencoStanzeView.getSelectedItem();
                            MobileDao mobile = (MobileDao) elencoMobiliView.getSelectedItem();
                            CategoriaDao categoria = (CategoriaDao) elencoCategorieView.getSelectedItem();

                            ContenitoreDao dao = new ContenitoreDao(id, nome, categoria.getId(), categoria.getNome(),
                                    stanza.getId(), stanza.getNome(), mobile.getId(), mobile.getNome());


                            //Modifico
                            DatabaseManager.updateContenitore(MainActivity.database, dao);

                            //Avverto la lista che i dati sono cambiati
                            ((ContenitoriActivity)getActivity()).aggiornaLista(DatabaseManager.getAllContenitori(MainActivity.database), true);

                            Toast.makeText(getActivity(), "Modifica effettuata con successo", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Elimina", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Elimino il posto dall'elenco di quelli memorizzati
                            DatabaseManager.deleteContenitore(MainActivity.database, id);

                            //Avverto la lista che i dati sono cambiati
                            ((ContenitoriActivity)getActivity()).aggiornaLista(DatabaseManager.getAllContenitori(MainActivity.database), true);

                            Toast.makeText(getActivity(), "Eliminazione effettuata con successo", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNeutralButton("Cancella", null);


            //Se le variabili sono già state valorizzate, le uso per riempire la finestra
            if(this.nome != null)
                settaValoriIstanza(id, nome, id_stanza, id_mobile, id_categoria);

        }else{ //Finestra per nuovo inserimento

            mBuilder.setView(view)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Aggiungi contenitore")
                    .setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String nome = nomeView.getText().toString().trim();

                            StanzaDao stanza = (StanzaDao) elencoStanzeView.getSelectedItem();
                            MobileDao mobile = (MobileDao) elencoMobiliView.getSelectedItem();
                            CategoriaDao categoria = (CategoriaDao) elencoCategorieView.getSelectedItem();

                            if(nome == null || nome.trim().equals(""))
                            {
                                Toast.makeText(getActivity(), "Specificare un nome valido", Toast.LENGTH_SHORT).show();

                            }else if(stanza == null || mobile == null || categoria == null){

                                Toast.makeText(getActivity(), "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();

                            }else{

                                //Creo e salvo il nuovo elemento
                                ContenitoreDao dao = new ContenitoreDao(id, nome, categoria.getId(), categoria.getNome(),
                                        stanza.getId(), stanza.getNome(), mobile.getId(), mobile.getNome());

                                DatabaseManager.insertContenitore(MainActivity.database, dao);

                                //Avverto la lista che i dati sono cambiati
                                ((ContenitoriActivity)getActivity()).aggiornaLista(DatabaseManager.getAllContenitori(MainActivity.database), true);

                                Toast.makeText(getActivity(), "Inserimento avvenuto con successo", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("Cancella", null);

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
    public void valorizzaDialog(long id, String nome, long idStanza, long idMobile, long idCategoria)
    {
        //Valorizzo le variabili dell'oggetto
        this.id = id;
        this.nome = nome;
        this.id_stanza = idStanza;
        this.id_mobile = idMobile;
        this.id_categoria = idCategoria;

        //Se la view è stata crata, la valorizzo con i dati passati
        if(nomeView != null)
            settaValoriIstanza(id, nome, idStanza, idMobile, idCategoria);
    }


    /***
     * Valorizza i campi mostrati nel dialog con gli attributi dell'istanza
     *
     * @param id
     * @param nome
     * @param idStanza
     * @param idMobile
     * @param idCategoria
     */
    private void settaValoriIstanza(long id, String nome, long idStanza, long idMobile, long idCategoria)
    {
        nomeView.setText(nome);

        //Verifico quale elemento della lista è selezionato per tutti gli spinner
        int posizioneCorrenteInLista = 0;

        //Stanze
        for (StanzaDao stanza:elencoStanze) {
            if(stanza.getId() == id_stanza)
            {
                elencoStanzeView.setSelection(posizioneCorrenteInLista);
                break;
            }else{
                posizioneCorrenteInLista++;
            }
        }

        posizioneCorrenteInLista = 0;

        //Mobili
        for (MobileDao mobile:elencoMobili) {
            if(mobile.getId() == id_mobile)
            {
                elencoMobiliView.setSelection(posizioneCorrenteInLista);
                break;
            }else{
                posizioneCorrenteInLista++;
            }
        }

        posizioneCorrenteInLista = 0;

        //Categorie
        for (CategoriaDao categoria:elencoCategorie) {
            if(categoria.getId() == id_categoria)
            {
                elencoCategorieView.setSelection(posizioneCorrenteInLista);
                break;
            }else{
                posizioneCorrenteInLista++;
            }
        }
    }


}
