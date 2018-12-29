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
import magazzino.bobo.com.magazzinodomestico.db.dao.OggettoDao;


public class ContenitoreAdapter extends ArrayAdapter {

    private ArrayList<ContenitoreDao> dataSet;
    Context mContext;

    //Costruttore
    public ContenitoreAdapter(ArrayList<ContenitoreDao> data, Context context) {
        super(context, R.layout.row_oggetto, data);
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
        TextView categoriaView = (TextView) view.findViewById(R.id.categoria);
        TextView stanzaView = (TextView) view.findViewById(R.id.stanza);
        TextView mobileView = (TextView) view.findViewById(R.id.mobile);

        //Valorizzo i campi
        nomeView.setText(dao.getNome());
        categoriaView.setText(dao.getNome_categoria());
        stanzaView.setText(dao.getNome_stanza());
        mobileView.setText(dao.getNome_mobile());

        //E nascondo quelli che non uso
        //distanza.setVisibility(View.INVISIBLE);
        //rating.setVisibility(View.INVISIBLE);

        return view;
    }

}
