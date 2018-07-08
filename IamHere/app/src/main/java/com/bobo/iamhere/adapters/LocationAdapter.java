package com.bobo.iamhere.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bobo.iamhere.R;
import com.bobo.iamhere.db.LocationDao;


import java.util.ArrayList;


public class LocationAdapter extends ArrayAdapter {

    private ArrayList<LocationDao> dataSet;
    Context mContext;

    //Costruttore
    public LocationAdapter(ArrayList<LocationDao> data, Context context) {
        super(context, R.layout.row_google_places, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        LocationDao placeDao = dataSet.get(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_google_places, null);

        TextView nome = (TextView) view.findViewById(R.id.giornoMeteo);
        TextView indirizzo = (TextView) view.findViewById(R.id.oraPrevisione);
        TextView stato = (TextView) view.findViewById(R.id.statoGooglePlace);
        TextView distanza = (TextView) view.findViewById(R.id.distanzaGooglePlace);
        RatingBar rating = (RatingBar) view.findViewById(R.id.ratingGooglePlace);

        //Valorizzo i campi
        nome.setText(placeDao.getAlias());
        indirizzo.setText(placeDao.getIndirizzo());
        stato.setText(placeDao.getComune());

        //E nascondo quelli che non uso
        distanza.setVisibility(View.INVISIBLE);
        rating.setVisibility(View.INVISIBLE);

        return view;
    }
}
