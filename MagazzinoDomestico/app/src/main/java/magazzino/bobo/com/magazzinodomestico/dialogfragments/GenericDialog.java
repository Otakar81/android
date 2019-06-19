package magazzino.bobo.com.magazzinodomestico.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.MainActivity;
import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.CategoriaDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.ContenitoreDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.LocationDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.MobileDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.StanzaDao;


public class GenericDialog extends DialogFragment {

    public static int TIPO_CAMPO_STANZA = 1;
    public static int TIPO_CAMPO_MOBILE = 2;
    public static int TIPO_CAMPO_CONTENITORE = 3;
    public static int TIPO_CAMPO_CATEGORIA = 4;





    //Tiene conto degli spinner già settati sul dialog chiamante
    LocationDao locationDao;

    //Elementi view del dialog
    private EditText nomeView;
    private EditText descrizioneView;
    private Spinner elencoCategorieView;

    private TextView nomeStanzaView;
    private TextView nomeMobileView;

    //Elementi view del dialog da cui vengo chiamato
    private int tipoCampo;
    private Spinner elencoView;
    private UpdateFieldDialog dialogChiamante;

    //Dialog builder
    private AlertDialog.Builder mBuilder;

    public static GenericDialog newInstance(AlertDialog.Builder builder){

        GenericDialog dialogFragment = new GenericDialog();
        dialogFragment.mBuilder = builder;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_generic, null);

        //Valorizzo le view del layout
        nomeView = view.findViewById(R.id.nome);
        descrizioneView = view.findViewById(R.id.descrizione);
        elencoCategorieView = view.findViewById(R.id.elencoCategorie);
        nomeStanzaView = view.findViewById(R.id.nomeStanzaView);
        nomeMobileView = view.findViewById(R.id.nomeMobileView);


        //E costruisco il builder
        mBuilder.setView(view)
                .setPositiveButton(R.string.aggiungi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(tipoCampo == TIPO_CAMPO_STANZA)
                            salvaStanza();
                        else if(tipoCampo == TIPO_CAMPO_MOBILE)
                            salvaMobile();
                        else if(tipoCampo == TIPO_CAMPO_CONTENITORE)
                            salvaContenitore();
                        else if(tipoCampo == TIPO_CAMPO_CATEGORIA)
                            salvaCategoria();
                    }
                })
                .setNegativeButton(R.string.annulla, null);

        //Setto l'icona ed il titolo del box, e nascondo eventualmente le view che non mi servono
        if(tipoCampo == TIPO_CAMPO_STANZA)
        {
            mBuilder.setIcon(R.drawable.nav_stanze2);
            mBuilder.setTitle(R.string.stanza_nuovo);

            //Nascondo le view che non mi servono
            descrizioneView.setVisibility(View.GONE);
            elencoCategorieView.setVisibility(View.GONE);
            nomeStanzaView.setVisibility(View.GONE);
            nomeMobileView.setVisibility(View.GONE);

            //Mostro l'hint adeguato per il campo nome
            nomeView.setHint(R.string.stanza_nome);

        }else if(tipoCampo == TIPO_CAMPO_MOBILE)
        {
            mBuilder.setIcon(R.drawable.nav_mobili);
            mBuilder.setTitle(R.string.mobile_nuovo);

            //Nascondo le view che non mi servono
            elencoCategorieView.setVisibility(View.GONE);
            nomeMobileView.setVisibility(View.GONE);

            //Mostro l'hint adeguato per il campo nome
            nomeView.setHint(R.string.mobile_nome);

            //Valorizzo opportunamente le view che descrivono la "location"
            String stanzaLocationView = nomeStanzaView.getText().toString();

            String stanzaLocationName = "-";

            if(locationDao.getId_stanza() != -1)
            {
                StanzaDao stanzaDao = DatabaseManager.getStanza(MainActivity.database, locationDao.getId_stanza());

                if(stanzaDao != null)
                    stanzaLocationName = stanzaDao.getNome();
            }

            stanzaLocationView += ": " + stanzaLocationName;
            nomeStanzaView.setText(stanzaLocationView);

        }else if(tipoCampo == TIPO_CAMPO_CONTENITORE)
        {
            mBuilder.setIcon(R.drawable.nav_contenitori);
            mBuilder.setTitle(R.string.contenitore_nuovo);

            //Mostro l'hint adeguato per il campo nome
            nomeView.setHint(R.string.contenitore_nome);

            //Mostro tutte le categorie
            ArrayList<CategoriaDao> elencoCategorie = DatabaseManager.getAllCategorie(MainActivity.database, true);
            ArrayAdapter<CategoriaDao> valoriCategorie = new ArrayAdapter<CategoriaDao>(getActivity(), android.R.layout.simple_list_item_1, elencoCategorie);
            elencoCategorieView.setAdapter(valoriCategorie);

            //Valorizzo opportunamente le view che descrivono la "location"
            String stanzaLocationView = nomeStanzaView.getText().toString();

            String stanzaLocationName = "-";

            if(locationDao.getId_stanza() != -1)
            {
                StanzaDao stanzaDao = DatabaseManager.getStanza(MainActivity.database, locationDao.getId_stanza());

                if(stanzaDao != null)
                    stanzaLocationName = stanzaDao.getNome();
            }

            stanzaLocationView += ": " + stanzaLocationName;
            nomeStanzaView.setText(stanzaLocationView);

            //Mobile
            String mobileLocationView = nomeMobileView.getText().toString();

            String mobileLocationName = "-";

            if(locationDao.getId_mobile() != -1)
            {
                MobileDao mobileDao = DatabaseManager.getMobile(MainActivity.database, locationDao.getId_mobile());

                if(mobileDao != null)
                    mobileLocationName = mobileDao.getNome();
            }

            mobileLocationView += ": " + mobileLocationName;
            nomeMobileView.setText(mobileLocationView);

        }if(tipoCampo == TIPO_CAMPO_CATEGORIA)
        {
            mBuilder.setIcon(R.drawable.nav_categorie);
            mBuilder.setTitle(R.string.categoria_nuova);

            //Nascondo le view che non mi servono
            descrizioneView.setVisibility(View.GONE);
            elencoCategorieView.setVisibility(View.GONE);
            nomeStanzaView.setVisibility(View.GONE);
            nomeMobileView.setVisibility(View.GONE);

            //Mostro l'hint adeguato per il campo nome
            nomeView.setHint(R.string.categoria_nome);
        }

        // Create the AlertDialog object and return it
        return mBuilder.create();
    }

    private void salvaStanza()
    {
        String nome = nomeView.getText().toString().trim();

        if(nome == null || nome.trim().equals(""))
        {
            Toast.makeText(getActivity(), R.string.errore_nome, Toast.LENGTH_SHORT).show();

        }else{

            //Verifico che il nome passato come argomento non sia già stato usato
            StanzaDao stanza = DatabaseManager.getStanzaByName(MainActivity.database, nome);

            if(stanza != null)
            {
                Toast.makeText(getActivity(), R.string.errore_nome_in_uso, Toast.LENGTH_SHORT).show();
            }else{

                //Creo e salvo la categoria
                stanza = new StanzaDao(nome);
                DatabaseManager.insertStanza(MainActivity.database, stanza);

                updateSpinner(nome);

                Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void salvaMobile()
    {
        String nome = nomeView.getText().toString().trim();
        String descrizione = descrizioneView.getText().toString().trim();

        if(nome == null || nome.trim().equals(""))
        {
            Toast.makeText(getActivity(), R.string.errore_nome, Toast.LENGTH_SHORT).show();

        }else{

            //Creo e salvo il nuovo elemento
            MobileDao dao = new MobileDao(-1, nome, descrizione, "", locationDao.getId_stanza(), "");
            DatabaseManager.insertMobile(MainActivity.database, dao);

            updateSpinner(nome);

            Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();
        }
    }

    private void salvaContenitore()
    {
        String nome = nomeView.getText().toString().trim();
        String descrizione = descrizioneView.getText().toString().trim();
        CategoriaDao categoria = (CategoriaDao) elencoCategorieView.getSelectedItem();

        if(nome == null || nome.trim().equals(""))
        {
            Toast.makeText(getActivity(), R.string.errore_nome, Toast.LENGTH_SHORT).show();

        }else{

            //Creo e salvo il nuovo elemento
            ContenitoreDao dao = new ContenitoreDao(-1, nome, descrizione, "", categoria.getId(), categoria.getNome(),
                    locationDao.getId_stanza(), "", locationDao.getId_mobile(), "");

            DatabaseManager.insertContenitore(MainActivity.database, dao);

            updateSpinner(nome);

            Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();
        }
    }

    private void salvaCategoria()
    {
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

                updateSpinner(nome);

                Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateSpinner(String nomeElemento)
    {
        //Stanze
        if(this.tipoCampo == TIPO_CAMPO_STANZA)
        {
            ArrayList<StanzaDao> elencoStanze = new ArrayList<StanzaDao>();

            elencoStanze = DatabaseManager.getAllStanze(MainActivity.database);
            //ArrayAdapter<StanzaDao> valoriStanze = new ArrayAdapter<StanzaDao>(getActivity(), android.R.layout.simple_list_item_1, elencoStanze);
            //elencoView.setAdapter(valoriStanze);

            this.dialogChiamante.updateSpinner(elencoStanze, nomeElemento, GenericDialog.TIPO_CAMPO_STANZA);

        //Mobili
        } else if(this.tipoCampo == TIPO_CAMPO_MOBILE)
        {
            ArrayList<MobileDao> elencoMobili = new ArrayList<MobileDao>();

            elencoMobili = DatabaseManager.getAllMobiliByStanza(MainActivity.database, locationDao.getId_stanza(), true);

            this.dialogChiamante.updateSpinner(elencoMobili, nomeElemento, GenericDialog.TIPO_CAMPO_MOBILE);

        //Contenitori
        } else if(this.tipoCampo == TIPO_CAMPO_CONTENITORE)
        {
            ArrayList<ContenitoreDao> elencoContenitori = new ArrayList<ContenitoreDao>();

            elencoContenitori = DatabaseManager.getAllContenitoriByLocation(MainActivity.database, locationDao, true);

            this.dialogChiamante.updateSpinner(elencoContenitori, nomeElemento, GenericDialog.TIPO_CAMPO_CONTENITORE);

        //Categoria
        } else if(this.tipoCampo == TIPO_CAMPO_CATEGORIA)
        {
            ArrayList<CategoriaDao> elencoCategorie = new ArrayList<CategoriaDao>();

            elencoCategorie = DatabaseManager.getAllCategorie(MainActivity.database, true);

            this.dialogChiamante.updateSpinner(elencoCategorie, nomeElemento, GenericDialog.TIPO_CAMPO_CATEGORIA);
        }


    }

    /***
     * Set per il view che fa riferimento al dialog da cui sono stato chiamato
     * @param elencoView
     */
    public void setElencoView(Spinner elencoView) {
        this.elencoView = elencoView;
    }

    public void setTipoCampo(int tipoCampo) {
        this.tipoCampo = tipoCampo;
    }

    public void setDialogChiamante(UpdateFieldDialog dialogChiamante) {
        this.dialogChiamante = dialogChiamante;
    }

    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }
}
