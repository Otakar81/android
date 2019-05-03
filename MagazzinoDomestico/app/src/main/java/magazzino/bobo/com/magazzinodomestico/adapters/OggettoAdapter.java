package magazzino.bobo.com.magazzinodomestico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import magazzino.bobo.com.magazzinodomestico.db.dao.OggettoDao;
import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.utils.DateUtils;


public class OggettoAdapter extends ArrayAdapter {

    private ArrayList<OggettoDao> dataSet;
    Context mContext;

    private boolean evidenziaDataScadenza;

    //Costruttore
    public OggettoAdapter(ArrayList<OggettoDao> data, Context context) {
        super(context, R.layout.row_oggetto, data);
        this.dataSet = data;
        this.mContext=context;

        this.evidenziaDataScadenza = false;
    }

    /***
     * Scelgo se evidenziare in visualizzazione la scadenza degli oggetti in funzione di quanto tempo manca da oggi
     * @param evidenziaDataScadenza
     */
    public void setEvidenziaDataScadenza(boolean evidenziaDataScadenza)
    {
        this.evidenziaDataScadenza = evidenziaDataScadenza;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        OggettoDao dao = dataSet.get(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_oggetto, null);

        TextView nomeView = (TextView) view.findViewById(R.id.nome);
        TextView descrizioneView = (TextView) view.findViewById(R.id.descrizione);
        TextView categoriaView = (TextView) view.findViewById(R.id.categoria);
        TextView stanzaView = (TextView) view.findViewById(R.id.stanza);
        TextView mobileView = (TextView) view.findViewById(R.id.mobile);
        TextView contenitoreView = (TextView) view.findViewById(R.id.contenitore);
        TextView numeroOggettiView = (TextView) view.findViewById(R.id.numeroOggetti);

        TextView dataScadenzaView = (TextView) view.findViewById(R.id.dataScadenza);

        //Valorizzo i campi

        //Compongo il campo nome tenendo conto anche del numero di oggetti eventualmente specificato
        nomeView.setText(dao.getNome());

        descrizioneView.setText(dao.getDescrizione());
        categoriaView.setText(dao.getNome_categoria());
        stanzaView.setText("(" + dao.getNome_stanza() + ")");
        mobileView.setText(dao.getNome_mobile());
        contenitoreView.setText(dao.getNome_contenitore());

        //Valorizzo il campo data di scadenza, o lo nascondo se non specificato
        if(dao.getDataScadenza() != null)
        {
            String etichettaDataView = dataScadenzaView.getText().toString();

            String dataScadenza = DateUtils.convertToDateView(dao.getDataScadenza(), Locale.getDefault());
            dataScadenzaView.setText("("+ etichettaDataView + ": " + dataScadenza + ")");

            if(this.evidenziaDataScadenza)
            {

                //Cambio colore alla scritta a seconda di quanto tempo manca alla scadenza
                int giorniAllaScadenza = dao.giorniAllaScadenza();

                int coloreScritta;

                if(giorniAllaScadenza < 0) //Scaduto
                    coloreScritta = -1; //Per ora lo lascio grigio standard
                else if(giorniAllaScadenza < 1) //Oggi
                    coloreScritta = R.color.colorStanzeDark;
                else if(giorniAllaScadenza < 3) //Due o tre giorni
                    coloreScritta = R.color.colorMobiliDark;
                else //Molti giorni
                    coloreScritta = R.color.colorCategorieDark;

                if(coloreScritta != -1)
                    dataScadenzaView.setTextColor(this.getContext().getResources().getColor(coloreScritta));

            }

        }else{
            dataScadenzaView.setVisibility(View.GONE);
        }


        //Valorizzo il campo numero oggetti, o lo nascondo se non specificato
        if(dao.getNumero_oggetti() > -1)
        {
            //int[] values = this.getContext().getResources().getIntArray(R.array.numero_oggetti_value);
            //String[] labels = this.getContext().getResources().getStringArray(R.array.numero_oggetti_label);

            String quantita = numeroOggettiView.getText().toString(); //Recupero il segnaposto dal file di layout e valorizzo il numero corretto
            //quantita = quantita.replaceAll("XX", getLabelFromValue(values, labels, dao.getNumero_oggetti()));
            quantita = quantita.replaceAll("XX", dao.getNumero_oggetti() + "");

            numeroOggettiView.setText(quantita);
        }
        else
        {
            numeroOggettiView.setVisibility(View.GONE);
        }

        //Se la descrizione è vuota, nascondo il campo
        if(dao.getDescrizione() == null || dao.getDescrizione().trim().length() == 0)
            descrizioneView.setVisibility(View.GONE);

        return view;
    }

    /**
     * Restituisce la label associata al valore numerico passato come argomento
     * @param values
     * @param labels
     * @param value
     * @return
     */
    private String getLabelFromValue(int[] values, String[] labels, int value)
    {
        for (int i = 0; i < values.length; i++)
        {
            if(value == values[i])
                return labels[i];
        }

        return "-";
    }


}