package magazzino.bobo.com.magazzinodomestico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.db.dao.MobileDao;


public class MobileAdapter extends ArrayAdapter {

    private ArrayList<MobileDao> dataSet;
    Context mContext;

    //Costruttore
    public MobileAdapter(ArrayList<MobileDao> data, Context context) {
        super(context, R.layout.row_oggetto_bkp, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        MobileDao dao = dataSet.get(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_mobile, null);

        TextView nomeView = (TextView) view.findViewById(R.id.nome);
        TextView descrizioneView = (TextView) view.findViewById(R.id.descrizione);
        TextView stanzaView = (TextView) view.findViewById(R.id.stanza);
        TextView numeroContenitoriView = (TextView) view.findViewById(R.id.numeroContenitori);
        TextView numeroOggettiView = (TextView) view.findViewById(R.id.numeroOggetti);

        //Valorizzo i campi
        nomeView.setText(dao.getNome());
        descrizioneView.setText(dao.getDescrizione());
        stanzaView.setText("(" + dao.getNomeStanza() + ")");

        numeroContenitoriView.setText("(" + dao.getNumeroContenitori() + ")");
        numeroOggettiView.setText("(" + dao.getNumeroOggetti() + ")");

        //Se la descrizione è vuota, nascondo il campo
        if(dao.getDescrizione() == null || dao.getDescrizione().trim().length() == 0)
            descrizioneView.setVisibility(View.GONE);

        return view;
    }

}
