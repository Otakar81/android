package magazzino.bobo.com.magazzinodomestico.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.MainActivity;
import magazzino.bobo.com.magazzinodomestico.MobiliActivity;
import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.Stanze_DettaglioActivity;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.LocationDao;
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


public class MobileDialog extends DialogFragment implements UpdateFieldDialog {

    //Specifica se il dialog da aprire sarà in modalità "edit" oppure "nuova istanza"
    boolean isEditMode;

    //Activity da cui viene chiamato il Dialog
    Activity activityChiamante;

    //Specifica l'eventuale location da cui arriva la chiamata
    LocationDao location;

    //Variabili di istanza
    private long id;
    private String nome;
    private String descrizione;
    private long id_stanza;

    private ArrayList<StanzaDao> elencoStanze;


    //Elementi view del dialog
    private EditText nomeView;
    private EditText descrizioneView;
    private Spinner elencoStanzeView;

    //Dialog builder
    private AlertDialog.Builder mBuilder;

    public static MobileDialog newInstance(AlertDialog.Builder builder, boolean isEditMode, LocationDao location){

        if(location == null)
            location = new LocationDao(-1, -1, -1, -1);

        MobileDialog dialogFragment = new MobileDialog();
        dialogFragment.isEditMode = isEditMode;
        dialogFragment.location = location;
        dialogFragment.mBuilder = builder;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        activityChiamante = getActivity();

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_mobile, null);

        //Valorizzo le view del layout
        nomeView = view.findViewById(R.id.nomeMobile);
        descrizioneView = view.findViewById(R.id.descrizione);
        elencoStanzeView = view.findViewById(R.id.elencoStanze);

        //Setto l'adapter per lo spinner
        elencoStanze = DatabaseManager.getAllStanze(MainActivity.database);

        ArrayAdapter<StanzaDao> valori = new ArrayAdapter<StanzaDao>(getActivity(), android.R.layout.simple_list_item_1, elencoStanze);
        elencoStanzeView.setAdapter(valori);

        /*
            Bottoni "add" accanto agli spinner
         */
        final MobileDialog thisDialog = this;

        //Dialog addStanza
        ImageView addStanzaButton = view.findViewById(R.id.addStanzaButton);
        addStanzaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creo il dialog per l'inserimento
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                GenericDialog dialog = GenericDialog.newInstance(builder);

                dialog.setElencoView(elencoStanzeView);
                dialog.setTipoCampo(GenericDialog.TIPO_CAMPO_STANZA);
                dialog.setDialogChiamante(thisDialog);

                dialog.show(getActivity().getSupportFragmentManager(),"stanza_dialog");
            }
        });

        //E costruisco il builder
        if(isEditMode) //Finestra per edit di un elemento esistente
        {
            mBuilder.setView(view)
                    .setIcon(R.drawable.nav_mobili)
                    .setTitle(R.string.mobile_modifica)
                    .setPositiveButton(R.string.modifica, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String nome = nomeView.getText().toString().trim();
                            String descrizione = descrizioneView.getText().toString().trim();
                            StanzaDao stanza = (StanzaDao) elencoStanzeView.getSelectedItem();

                            MobileDao dao = new MobileDao(id, nome, descrizione, "", stanza.getId(), stanza.getNome());

                            //Modifico
                            DatabaseManager.updateMobile(MainActivity.database, dao);

                            //Avverto la lista che i dati sono cambiati
                            updateAdapterLocation();

                            Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.elimina, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            int numeroAssociazioniMobile = DatabaseManager.numeroAssociazioniMobile(MainActivity.database, id);
                            final long idEliminare = id;

                            //Se il mobile non è vuoto, chiedo una ulteriore conferma
                            if(numeroAssociazioniMobile > 0)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(R.string.mobile_non_vuoto)
                                        .setPositiveButton(R.string.elimina_ma_mantieni_contenuto, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int id) {

                                                //Elimino il posto dall'elenco di quelli memorizzati
                                                DatabaseManager.deleteMobile(MainActivity.database, idEliminare, false);

                                                //Aggiorno l'adapter dell'activity da cui sono stato chiamato
                                                updateAdapterLocation();

                                                Toast.makeText(activityChiamante, R.string.eliminazione_successo, Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .setNegativeButton(R.string.elimina_insieme_al_contenuto, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int id) {

                                                //Elimino il posto dall'elenco di quelli memorizzati
                                                DatabaseManager.deleteMobile(MainActivity.database, idEliminare, true);

                                                //Aggiorno l'adapter dell'activity da cui sono stato chiamato
                                                updateAdapterLocation();

                                                Toast.makeText(activityChiamante, R.string.eliminazione_successo, Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .setNeutralButton(R.string.annulla, null);

                                builder.show();

                            } else {

                                //Elimino il posto dall'elenco di quelli memorizzati
                                DatabaseManager.deleteMobile(MainActivity.database, id, false);

                                ///Aggiorno l'adapter dell'activity da cui sono stato chiamato
                                updateAdapterLocation();

                                Toast.makeText(getActivity(), R.string.eliminazione_successo, Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNeutralButton(R.string.annulla, null);


            //Se le variabili sono già state valorizzate, le uso per riempire la finestra
            if(this.nome != null)
                settaValoriIstanza(nome, descrizione, id_stanza);

        }else{ //Finestra per inserimento di un nuovo Mobile

            mBuilder.setView(view)
                    .setIcon(R.drawable.nav_mobili)
                    .setTitle(R.string.mobile_nuovo)
                    .setPositiveButton(R.string.aggiungi, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String nome = nomeView.getText().toString().trim();
                            String descrizione = descrizioneView.getText().toString().trim();
                            StanzaDao stanza = (StanzaDao) elencoStanzeView.getSelectedItem();

                            if(nome == null || nome.trim().equals(""))
                            {
                                Toast.makeText(getActivity(), R.string.errore_nome, Toast.LENGTH_SHORT).show();
                            }else{

                                //Creo e salvo il nuovo elemento
                                MobileDao dao = new MobileDao(id, nome, descrizione, "", stanza.getId(), stanza.getNome());
                                DatabaseManager.insertMobile(MainActivity.database, dao);

                                //Aggiorno l'adapter dell'activity da cui sono stato chiamato
                                updateAdapterLocation();

                                Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();

                            }
                        }
                    })
                    .setNegativeButton(R.string.annulla, null);

            //Valorizzo eventualmente i campi necessari del dialog, se sto creando un nuovo elemento da una location specifica
            settaValoriIstanza(null, null, location.getId_stanza());

            //E disabilito gli spinner già valorizzati
            disabilitaSpinner();

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
    public void valorizzaDialog(long id, String nome, String descrizione, long idStanza)
    {
        //Valorizzo le variabili dell'oggetto
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.id_stanza = idStanza;

        //Se la view è stata crata, la valorizzo con i dati passati
        if(nomeView != null)
            settaValoriIstanza(nome, descrizione, idStanza);
    }

    /***
     * Valorizza i campi mostrati nel dialog con gli attributi dell'istanza
     *
     * @param nome
     * @param idStanza
     */
    private void settaValoriIstanza(String nome, String descrizione, long idStanza) {

        if (nome != null)
            nomeView.setText(nome);

        if(descrizione != null)
            descrizioneView.setText(descrizione);

        //Verifico quale elemento della lista è selezionato per tutti gli spinner
        int posizioneCorrenteInLista = 0;

        //Stanze
        for (StanzaDao stanza : elencoStanze) {
            if (stanza.getId() == idStanza) {
                elencoStanzeView.setSelection(posizioneCorrenteInLista);
                break;
            } else {
                posizioneCorrenteInLista++;
            }
        }
    }

    /***
     * Se sono in creazione da una precisa location, disabilito gli spinner già valorizzati
     */
    private void disabilitaSpinner()
    {
        if(location.getId_stanza() != -1)
            elencoStanzeView.setEnabled(false);
    }

    /***
     * Fa l'update delle liste nell'activity da cui il dialog è stato chiamato
     */
    private void updateAdapterLocation()
    {
        if(location.getLocationType() == LocationDao.STANZA)
        {
            ((Stanze_DettaglioActivity)activityChiamante).aggiornaListaMobili(
                    DatabaseManager.getAllMobiliByStanza(MainActivity.database, location.getId_stanza(), false), true);

        }else if(location.getLocationType() == -1) //Mobili_DettaglioActivity
        {
            ((MobiliActivity)activityChiamante).aggiornaLista(DatabaseManager.getAllMobili(MainActivity.database), true);
        }
    }

    @Override
    public void updateSpinner(ArrayList elencoElementi, String nomeElemento, int tipoField) {
        if(tipoField == GenericDialog.TIPO_CAMPO_STANZA)
        {
            //Aggiorno l'ArrayList delle stanze
            this.elencoStanze = (ArrayList<StanzaDao>) elencoElementi;

            //Aggiorno il relativo spinner
            ArrayAdapter<StanzaDao> valoriStanze = new ArrayAdapter<StanzaDao>(getActivity(), android.R.layout.simple_list_item_1, elencoStanze);
            elencoStanzeView.setAdapter(valoriStanze);

            //Setto sullo spinner il record appena inserito
            int posizioneCorrenteInLista = 0;

            for (StanzaDao stanza: elencoStanze)
            {
                if (stanza.getNome().equalsIgnoreCase(nomeElemento)) {
                    elencoStanzeView.setSelection(posizioneCorrenteInLista);
                    break;
                } else {
                    posizioneCorrenteInLista++;
                }
            }
        }
    }
}
