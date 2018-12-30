package magazzino.bobo.com.magazzinodomestico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.db.dao.StanzaDao;


public class StanzaAdapter extends ArrayAdapter {

    private ArrayList<StanzaDao> dataSet;
    Context mContext;

    //Costruttore
    public StanzaAdapter(ArrayList<StanzaDao> data, Context context) {
        super(context, R.layout.row_oggetto, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        StanzaDao dao = dataSet.get(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_stanza, null);

        TextView nomeView = (TextView) view.findViewById(R.id.nome);
        TextView numeroOggettiView = (TextView) view.findViewById(R.id.numeroOggetti);

        //Valorizzo i campi
        nomeView.setText(dao.getNome());

        //E nascondo quelli che non uso
        numeroOggettiView.setVisibility(View.INVISIBLE);
        //rating.setVisibility(View.INVISIBLE);

        return view;
    }

}
