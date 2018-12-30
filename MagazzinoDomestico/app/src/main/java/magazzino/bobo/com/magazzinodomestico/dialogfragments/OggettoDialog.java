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

import magazzino.bobo.com.magazzinodomestico.Contenitori_DettaglioActivity;
import magazzino.bobo.com.magazzinodomestico.MainActivity;
import magazzino.bobo.com.magazzinodomestico.Mobili_DettaglioActivity;
import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.Stanze_DettaglioActivity;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.CategoriaDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.ContenitoreDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.LocationDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.MobileDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.OggettoDao;
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

    //Specifica se sono in fase di creazione del dialog
    boolean isCreazioneDialog;

    //Specifica l'eventuale location da cui arriva la chiamata
    LocationDao location;

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

    public static OggettoDialog newInstance(AlertDialog.Builder builder, boolean isEditMode, LocationDao location){

        if(location == null)
            location = new LocationDao(-1, -1, -1, -1);

        OggettoDialog dialogFragment = new OggettoDialog();
        dialogFragment.isEditMode = isEditMode;
        dialogFragment.isCreazioneDialog = true;
        dialogFragment.location = location;
        dialogFragment.mBuilder = builder;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_oggetto, null);

        //Valorizzo le view del layout
        nomeView = view.findViewById(R.id.nome);
        elencoStanzeView = view.findViewById(R.id.elencoStanze);
        elencoMobiliView = view.findViewById(R.id.elencoMobili);
        elencoContenitoriView = view.findViewById(R.id.elencoContenitori);
        elencoCategorieView = view.findViewById(R.id.elencoCategorie);

        //Di default, gli oggetti ArrayList saranno vuoti
        elencoStanze = new ArrayList<StanzaDao>();
        elencoMobili = new ArrayList<MobileDao>();
        elencoCategorie = new ArrayList<CategoriaDao>();
        elencoContenitori = new ArrayList<ContenitoreDao>();

        /*
            Setto l'adapter per gli spinner
         */

        //Mostro sempre tutte le categorie
        elencoCategorie = DatabaseManager.getAllCategorie(MainActivity.database, true);
        ArrayAdapter<CategoriaDao> valoriCategorie = new ArrayAdapter<CategoriaDao>(getActivity(), android.R.layout.simple_list_item_1, elencoCategorie);
        elencoCategorieView.setAdapter(valoriCategorie);

        //Mostro sempre tutte le stanze
        elencoStanze = DatabaseManager.getAllStanze(MainActivity.database);
        ArrayAdapter<StanzaDao> valoriStanze = new ArrayAdapter<StanzaDao>(getActivity(), android.R.layout.simple_list_item_1, elencoStanze);
        elencoStanzeView.setAdapter(valoriStanze);

        //Di default, mostro tutti i mobili (poi saranno eventualmente filtrati per stanza)
        elencoMobili = DatabaseManager.getAllMobili(MainActivity.database);
        ArrayAdapter<MobileDao> valoriMobili = new ArrayAdapter<MobileDao>(getActivity(), android.R.layout.simple_list_item_1, elencoMobili);
        elencoMobiliView.setAdapter(valoriMobili);

        //Di default, mostro tutti i contenitori (poi saranno eventualmente filtrati per stanza o mobile)
        elencoContenitori = DatabaseManager.getAllContenitori(MainActivity.database);
        ArrayAdapter<ContenitoreDao> valoriContenitori = new ArrayAdapter<ContenitoreDao>(getActivity(), android.R.layout.simple_list_item_1, elencoContenitori);
        elencoContenitoriView.setAdapter(valoriContenitori);

        elencoStanzeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                StanzaDao stanzaSelezionata = elencoStanze.get(position);

                //Mostro solo i mobili della stanza
                elencoMobili = DatabaseManager.getAllMobiliByStanza(MainActivity.database, stanzaSelezionata.getId(), true);
                ArrayAdapter<MobileDao> valoriMobili = new ArrayAdapter<MobileDao>(getActivity(), android.R.layout.simple_list_item_1, elencoMobili);
                elencoMobiliView.setAdapter(valoriMobili);

                elencoMobiliView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int posizioneMobile, long l) {

                        MobileDao mobileSelezionato = elencoMobili.get(posizioneMobile);

                        //Mostro i contenitori presenti nel mobile
                        LocationDao locationMobile = new LocationDao(-1, mobileSelezionato.getId_stanza(), mobileSelezionato.getId(), -1);

                        elencoContenitori = DatabaseManager.getAllContenitoriByLocation(MainActivity.database, locationMobile);
                        ArrayAdapter<ContenitoreDao> valoriContenitori = new ArrayAdapter<ContenitoreDao>(getActivity(), android.R.layout.simple_list_item_1, elencoContenitori);
                        elencoContenitoriView.setAdapter(valoriContenitori);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                //Mostro solo i contenitori direttamente presenti in una stanza
                LocationDao locationStanza = new LocationDao(-1, stanzaSelezionata.getId(), -1, -1);

                elencoContenitori = DatabaseManager.getAllContenitoriByLocation(MainActivity.database, locationStanza);
                ArrayAdapter<ContenitoreDao> valoriContenitori = new ArrayAdapter<ContenitoreDao>(getActivity(), android.R.layout.simple_list_item_1, elencoContenitori);
                elencoContenitoriView.setAdapter(valoriContenitori);

                //Se sono al primo giro in edit, valorizzo gli spinner con gli attributi dell'istanza
                if(isEditMode)
                {
                    if(isCreazioneDialog)
                    {
                        settaValoriIstanza(nome, id_stanza, id_mobile, id_contenitore, id_categoria);
                        isCreazioneDialog = false;
                    }

                }else //Altrimenti valorizzo con i valori "imposti" dalla location
                {
                    settaValoriIstanza(null, location.getId_stanza(), location.getId_mobile(), location.getId_contenitore(), location.getId_categoria());

                    //E disabilito gli spinner già valorizzati
                    disabilitaSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //E costruisco il builder
        if(isEditMode) //Finestra per edit di un elemento esistente
        {
            mBuilder.setView(view)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Modifica oggetto")
                    .setPositiveButton("Modifica", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String nome = nomeView.getText().toString().trim();

                            StanzaDao stanza = (StanzaDao) elencoStanzeView.getSelectedItem();
                            MobileDao mobile = (MobileDao) elencoMobiliView.getSelectedItem();
                            CategoriaDao categoria = (CategoriaDao) elencoCategorieView.getSelectedItem();
                            ContenitoreDao contenitore = (ContenitoreDao) elencoContenitoriView.getSelectedItem();


                            OggettoDao dao = new OggettoDao(id, nome, "", categoria.getId(), categoria.getNome(),
                                    stanza.getId(), stanza.getNome(), mobile.getId(), mobile.getNome(), contenitore.getId(), contenitore.getNome());

                            //Modifico
                            DatabaseManager.updateOggetto(MainActivity.database, dao);

                            //Avverto la lista che i dati sono cambiati
                            updateAdapterLocation();

                            Toast.makeText(getActivity(), "Modifica effettuata con successo", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Elimina", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Elimino il posto dall'elenco di quelli memorizzati
                            DatabaseManager.deleteOggetto(MainActivity.database, id);

                            //Avverto la lista che i dati sono cambiati
                            updateAdapterLocation();

                            Toast.makeText(getActivity(), "Eliminazione effettuata con successo", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNeutralButton("Cancella", null);


            //Se le variabili sono già state valorizzate, le uso per riempire la finestra
            if(this.nome != null)
                settaValoriIstanza(nome, id_stanza, id_mobile, id_contenitore, id_categoria);

        }else{ //Finestra per nuovo inserimento

            mBuilder.setView(view)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Aggiungi oggetto")
                    .setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String nome = nomeView.getText().toString().trim();

                            StanzaDao stanza = (StanzaDao) elencoStanzeView.getSelectedItem();
                            MobileDao mobile = (MobileDao) elencoMobiliView.getSelectedItem();
                            CategoriaDao categoria = (CategoriaDao) elencoCategorieView.getSelectedItem();
                            ContenitoreDao contenitore = (ContenitoreDao) elencoContenitoriView.getSelectedItem();

                            if(nome == null || nome.trim().equals(""))
                            {
                                Toast.makeText(getActivity(), "Specificare un nome valido", Toast.LENGTH_SHORT).show();

                            }else if(stanza == null || mobile == null || categoria == null){

                                Toast.makeText(getActivity(), "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();

                            }else{

                                //Creo e salvo il nuovo elemento
                                OggettoDao dao = new OggettoDao(nome, "", categoria.getId(), categoria.getNome(),
                                        stanza.getId(), stanza.getNome(), mobile.getId(), mobile.getNome(), contenitore.getId(), contenitore.getNome());

                                DatabaseManager.insertOggetto(MainActivity.database, dao);

                                //Avverto la lista che i dati sono cambiati
                                updateAdapterLocation();

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
    public void valorizzaDialog(long id, String nome, long idStanza, long idMobile, long idContenitore, long idCategoria)
    {
        //Valorizzo le variabili dell'oggetto
        this.id = id;
        this.nome = nome;
        this.id_stanza = idStanza;
        this.id_mobile = idMobile;
        this.id_contenitore = idContenitore;
        this.id_categoria = idCategoria;

        //Se la view è stata crata, la valorizzo con i dati passati
        if(nomeView != null)
            settaValoriIstanza(nome, idStanza, idMobile, idContenitore, idCategoria);
    }


    /***
     * Valorizza i campi mostrati nel dialog con gli attributi dell'istanza
     *
     * @param nome
     * @param idStanza
     * @param idMobile
     * @param idCategoria
     */
    private void settaValoriIstanza(String nome, long idStanza, long idMobile, long idContenitore, long idCategoria)
    {
        if(nome != null)
            nomeView.setText(nome);

        //Verifico quale elemento della lista è selezionato per tutti gli spinner
        int posizioneCorrenteInLista = 0;

        //Stanze
        for (StanzaDao stanza:elencoStanze) {
            if(stanza.getId() == idStanza)
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
            if(mobile.getId() == idMobile)
            {
                elencoMobiliView.setSelection(posizioneCorrenteInLista);
                break;
            }else{
                posizioneCorrenteInLista++;
            }
        }

        posizioneCorrenteInLista = 0;

        //Contenitori
        for (ContenitoreDao contenitore:elencoContenitori) {
            if(contenitore.getId() == idContenitore)
            {
                elencoContenitoriView.setSelection(posizioneCorrenteInLista);
                break;
            }else{
                posizioneCorrenteInLista++;
            }
        }

        posizioneCorrenteInLista = 0;

        //Categorie
        for (CategoriaDao categoria:elencoCategorie) {
            if(categoria.getId() == idCategoria)
            {
                elencoCategorieView.setSelection(posizioneCorrenteInLista);
                break;
            }else{
                posizioneCorrenteInLista++;
            }
        }
    }

    /***
     * Se sono in creazione da una precisa location, disabilito gli spinner già valorizzati
     */
    private void disabilitaSpinner()
    {
        if(location.getId_categoria() != -1)
            elencoCategorieView.setEnabled(false);

        if(location.getId_stanza() != -1)
            elencoStanzeView.setEnabled(false);

        if(location.getId_mobile() != -1)
            elencoMobiliView.setEnabled(false);

        if(location.getId_contenitore() != -1)
            elencoContenitoriView.setEnabled(false);
    }

    /***
     * Fa l'update delle liste nell'activity da cui il dialog è stato chiamato
     */
    private void updateAdapterLocation()
    {
        if(location.getLocationType() == LocationDao.CATEGORIA)
        {

        }else if(location.getLocationType() == LocationDao.STANZA)
        {
            ((Stanze_DettaglioActivity)getActivity()).aggiornaListaOggetti(
                    DatabaseManager.getAllOggettiByLocation(MainActivity.database, location), true);

        }else if(location.getLocationType() == LocationDao.MOBILE)
        {
            ((Mobili_DettaglioActivity)getActivity()).aggiornaListaOggetti(
                    DatabaseManager.getAllOggettiByLocation(MainActivity.database, location), true);

        }else if(location.getLocationType() == LocationDao.CONTENITORE) //Dettaglio contenitore
        {
            ((Contenitori_DettaglioActivity)getActivity()).aggiornaLista(
                    DatabaseManager.getAllOggettiByLocation(MainActivity.database, location), true);

        }else{ //Main activity
            ((MainActivity)getActivity()).aggiornaLista(DatabaseManager.getAllOggetti(MainActivity.database), true);
        }
    }


}