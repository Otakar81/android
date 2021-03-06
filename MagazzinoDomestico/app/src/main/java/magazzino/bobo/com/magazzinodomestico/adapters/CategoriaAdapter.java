package magazzino.bobo.com.magazzinodomestico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.db.dao.CategoriaDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.StanzaDao;


public class CategoriaAdapter extends ArrayAdapter {

    private ArrayList<CategoriaDao> dataSet;
    Context mContext;

    //Costruttore
    public CategoriaAdapter(ArrayList<CategoriaDao> data, Context context) {
        super(context, R.layout.row_categoria, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        CategoriaDao dao = dataSet.get(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_categoria, null);

        TextView nomeView = (TextView) view.findViewById(R.id.nome);
        TextView numeroContenitoriView = (TextView) view.findViewById(R.id.numeroContenitori);
        TextView numeroOggettiView = (TextView) view.findViewById(R.id.numeroOggetti);

        //Valorizzo i campi
        nomeView.setText(dao.getNome());

        numeroContenitoriView.setText("(" + dao.getNumeroContenitori() + ")");
        numeroOggettiView.setText("(" + dao.getNumeroOggetti() + ")");

        return view;
    }

}
