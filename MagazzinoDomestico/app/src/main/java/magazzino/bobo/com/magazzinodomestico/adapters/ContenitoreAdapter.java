package magazzino.bobo.com.magazzinodomestico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.db.dao.ContenitoreDao;


public class ContenitoreAdapter extends ArrayAdapter {

    private ArrayList<ContenitoreDao> dataSet;
    Context mContext;

    //Costruttore
    public ContenitoreAdapter(ArrayList<ContenitoreDao> data, Context context) {
        super(context, R.layout.row_oggetto_bkp, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        ContenitoreDao dao = dataSet.get(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_contenitore, null);

        TextView nomeView = (TextView) view.findViewById(R.id.nome);
        TextView descrizioneView = (TextView) view.findViewById(R.id.descrizione);
        TextView categoriaView = (TextView) view.findViewById(R.id.categoria);
        TextView stanzaView = (TextView) view.findViewById(R.id.stanza);
        TextView mobileView = (TextView) view.findViewById(R.id.mobile);
        TextView numeroOggettiView = (TextView) view.findViewById(R.id.numeroOggetti);



        //Valorizzo i campi
        nomeView.setText(dao.getNome());
        descrizioneView.setText(dao.getDescrizione());
        categoriaView.setText(dao.getNome_categoria());
        stanzaView.setText("(" + dao.getNome_stanza() + ")");
        mobileView.setText(dao.getNome_mobile());
        numeroOggettiView.setText("(" + dao.getNumeroOggetti() + ")");

        //Se la descrizione è vuota, nascondo il campo
        if(dao.getDescrizione() == null || dao.getDescrizione().trim().length() == 0)
            descrizioneView.setVisibility(View.GONE);

        return view;
    }

}
