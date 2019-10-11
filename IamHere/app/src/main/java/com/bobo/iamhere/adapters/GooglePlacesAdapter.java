package com.bobo.iamhere.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bobo.iamhere.R;
import com.bobo.iamhere.ws.google.PlaceDao;

import java.util.ArrayList;


public class GooglePlacesAdapter extends ArrayAdapter {

    private ArrayList<PlaceDao> dataSet;
    Context mContext;

    //Costruttore
    public GooglePlacesAdapter(ArrayList<PlaceDao> data, Context context) {
        super(context, R.layout.row_google_places, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        PlaceDao placeDao = dataSet.get(position);

        String localeApertoString = parent.getResources().getString(R.string.google_places_aperto);
        String localeChiusoString = parent.getResources().getString(R.string.google_places_chiuso);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_google_places, null);

        TextView nome = (TextView) view.findViewById(R.id.locationName);
        TextView indirizzo = (TextView) view.findViewById(R.id.locationAddress);
        TextView stato = (TextView) view.findViewById(R.id.statoGooglePlace);
        TextView distanza = (TextView) view.findViewById(R.id.distanzaGooglePlace);
        RatingBar rating = (RatingBar) view.findViewById(R.id.ratingGooglePlace);

        //Valorizzo i campi
        nome.setText(placeDao.getName());
        indirizzo.setText(placeDao.getFormattedAddress());
        distanza.setText(placeDao.getFormattedDistanzaDaMe());

        if(placeDao.getRating() > 0) //Ho un rating
            rating.setRating(placeDao.getRating());
        else
            rating.setVisibility(View.INVISIBLE);

        //Valorizzo il testo e cambio colore allo stato
        if(placeDao.isOpenNow() == 1) //Aperto
        {
            stato.setText(localeApertoString);
            stato.setTextColor(Color.parseColor("#ff669900"));

        }else if(placeDao.isOpenNow() == 0){ //Chiuso

            stato.setText(localeChiusoString);

        }else{ //Nessuna info

            stato.setText("");
        }


        return view;
    }
}
