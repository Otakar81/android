package com.bobo.iamhere.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobo.iamhere.R;
import com.bobo.iamhere.ws.openweathermap.GiornataMeteoDao;
import com.bobo.iamhere.ws.openweathermap.MeteoDao;

import java.util.ArrayList;


public class MeteoAdapter extends ArrayAdapter {

    private ArrayList<GiornataMeteoDao> dataSet;
    Context mContext;

    //Costruttore
    public MeteoAdapter(ArrayList<GiornataMeteoDao> data, Context context) {
        super(context, R.layout.row_meteo, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        GiornataMeteoDao meteoDao = dataSet.get(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_meteo, null);


        TextView data = (TextView) view.findViewById(R.id.giornoMeteo);
        data.setText(meteoDao.getData());

        //Previsione delle 00
        ImageView immagineMeteo00 = (ImageView) view.findViewById(R.id.immagineMeteo00);
        TextView oraPrevisione00 = (TextView) view.findViewById(R.id.oraPrevisione00);
        TextView tempPrevisione00 = (TextView) view.findViewById(R.id.tempPrevisione00);
        TextView ventoPrevisione00 = (TextView) view.findViewById(R.id.ventoPrevisione00);
        TextView textPrevisione00 = (TextView) view.findViewById(R.id.textPrevisione00);

        MeteoDao previsione = meteoDao.getPrevisione("00:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo00.setVisibility(View.INVISIBLE);
            oraPrevisione00.setVisibility(View.INVISIBLE);
            tempPrevisione00.setVisibility(View.INVISIBLE);
            ventoPrevisione00.setVisibility(View.INVISIBLE);
            textPrevisione00.setVisibility(View.INVISIBLE);
        }else{
            //TODO -> Valorizzare immagine
            oraPrevisione00.setText(previsione.getOrario());
            tempPrevisione00.setText(previsione.getTemperatura() + "°");
            ventoPrevisione00.setText(previsione.getVento() + "m/s");
            textPrevisione00.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 03
        ImageView immagineMeteo03 = (ImageView) view.findViewById(R.id.immagineMeteo03);
        TextView oraPrevisione03 = (TextView) view.findViewById(R.id.oraPrevisione03);
        TextView tempPrevisione03 = (TextView) view.findViewById(R.id.tempPrevisione03);
        TextView ventoPrevisione03 = (TextView) view.findViewById(R.id.ventoPrevisione03);
        TextView textPrevisione03 = (TextView) view.findViewById(R.id.textPrevisione03);

        previsione = meteoDao.getPrevisione("03:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo03.setVisibility(View.INVISIBLE);
            oraPrevisione03.setVisibility(View.INVISIBLE);
            tempPrevisione03.setVisibility(View.INVISIBLE);
            ventoPrevisione03.setVisibility(View.INVISIBLE);
            textPrevisione03.setVisibility(View.INVISIBLE);
        }else{
            //TODO -> Valorizzare immagine
            oraPrevisione03.setText(previsione.getOrario());
            tempPrevisione03.setText(previsione.getTemperatura() + "°");
            ventoPrevisione03.setText(previsione.getVento() + "m/s");
            textPrevisione03.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 06
        ImageView immagineMeteo06 = (ImageView) view.findViewById(R.id.immagineMeteo06);
        TextView oraPrevisione06 = (TextView) view.findViewById(R.id.oraPrevisione06);
        TextView tempPrevisione06 = (TextView) view.findViewById(R.id.tempPrevisione06);
        TextView ventoPrevisione06 = (TextView) view.findViewById(R.id.ventoPrevisione06);
        TextView textPrevisione06 = (TextView) view.findViewById(R.id.textPrevisione06);

        previsione = meteoDao.getPrevisione("06:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo06.setVisibility(View.INVISIBLE);
            oraPrevisione06.setVisibility(View.INVISIBLE);
            tempPrevisione06.setVisibility(View.INVISIBLE);
            ventoPrevisione06.setVisibility(View.INVISIBLE);
            textPrevisione06.setVisibility(View.INVISIBLE);
        }else{
            //TODO -> Valorizzare immagine
            oraPrevisione06.setText(previsione.getOrario());
            tempPrevisione06.setText(previsione.getTemperatura() + "°");
            ventoPrevisione06.setText(previsione.getVento() + "m/s");
            textPrevisione06.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 09
        ImageView immagineMeteo09 = (ImageView) view.findViewById(R.id.immagineMeteo09);
        TextView oraPrevisione09 = (TextView) view.findViewById(R.id.oraPrevisione09);
        TextView tempPrevisione09 = (TextView) view.findViewById(R.id.tempPrevisione09);
        TextView ventoPrevisione09 = (TextView) view.findViewById(R.id.ventoPrevisione09);
        TextView textPrevisione09 = (TextView) view.findViewById(R.id.textPrevisione09);

        previsione = meteoDao.getPrevisione("09:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo09.setVisibility(View.INVISIBLE);
            oraPrevisione09.setVisibility(View.INVISIBLE);
            tempPrevisione09.setVisibility(View.INVISIBLE);
            ventoPrevisione09.setVisibility(View.INVISIBLE);
            textPrevisione09.setVisibility(View.INVISIBLE);
        }else{
            //TODO -> Valorizzare immagine
            oraPrevisione09.setText(previsione.getOrario());
            tempPrevisione09.setText(previsione.getTemperatura() + "°");
            ventoPrevisione09.setText(previsione.getVento() + "m/s");
            textPrevisione09.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 12
        ImageView immagineMeteo12 = (ImageView) view.findViewById(R.id.immagineMeteo12);
        TextView oraPrevisione12 = (TextView) view.findViewById(R.id.oraPrevisione12);
        TextView tempPrevisione12 = (TextView) view.findViewById(R.id.tempPrevisione12);
        TextView ventoPrevisione12 = (TextView) view.findViewById(R.id.ventoPrevisione12);
        TextView textPrevisione12 = (TextView) view.findViewById(R.id.textPrevisione12);

        previsione = meteoDao.getPrevisione("12:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo12.setVisibility(View.INVISIBLE);
            oraPrevisione12.setVisibility(View.INVISIBLE);
            tempPrevisione12.setVisibility(View.INVISIBLE);
            ventoPrevisione12.setVisibility(View.INVISIBLE);
            textPrevisione12.setVisibility(View.INVISIBLE);
        }else{
            //TODO -> Valorizzare immagine
            oraPrevisione12.setText(previsione.getOrario());
            tempPrevisione12.setText(previsione.getTemperatura() + "°");
            ventoPrevisione12.setText(previsione.getVento() + "m/s");
            textPrevisione12.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 15
        ImageView immagineMeteo15 = (ImageView) view.findViewById(R.id.immagineMeteo15);
        TextView oraPrevisione15 = (TextView) view.findViewById(R.id.oraPrevisione15);
        TextView tempPrevisione15 = (TextView) view.findViewById(R.id.tempPrevisione15);
        TextView ventoPrevisione15 = (TextView) view.findViewById(R.id.ventoPrevisione15);
        TextView textPrevisione15 = (TextView) view.findViewById(R.id.textPrevisione15);

        previsione = meteoDao.getPrevisione("15:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo15.setVisibility(View.INVISIBLE);
            oraPrevisione15.setVisibility(View.INVISIBLE);
            tempPrevisione15.setVisibility(View.INVISIBLE);
            ventoPrevisione15.setVisibility(View.INVISIBLE);
            textPrevisione15.setVisibility(View.INVISIBLE);
        }else{
            //TODO -> Valorizzare immagine
            oraPrevisione15.setText(previsione.getOrario());
            tempPrevisione15.setText(previsione.getTemperatura() + "°");
            ventoPrevisione15.setText(previsione.getVento() + "m/s");
            textPrevisione15.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 18
        ImageView immagineMeteo18 = (ImageView) view.findViewById(R.id.immagineMeteo18);
        TextView oraPrevisione18 = (TextView) view.findViewById(R.id.oraPrevisione18);
        TextView tempPrevisione18 = (TextView) view.findViewById(R.id.tempPrevisione18);
        TextView ventoPrevisione18 = (TextView) view.findViewById(R.id.ventoPrevisione18);
        TextView textPrevisione18 = (TextView) view.findViewById(R.id.textPrevisione18);

        previsione = meteoDao.getPrevisione("18:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo18.setVisibility(View.INVISIBLE);
            oraPrevisione18.setVisibility(View.INVISIBLE);
            tempPrevisione18.setVisibility(View.INVISIBLE);
            ventoPrevisione18.setVisibility(View.INVISIBLE);
            textPrevisione18.setVisibility(View.INVISIBLE);
        }else{
            //TODO -> Valorizzare immagine
            oraPrevisione18.setText(previsione.getOrario());
            tempPrevisione18.setText(previsione.getTemperatura() + "°");
            ventoPrevisione18.setText(previsione.getVento() + "m/s");
            textPrevisione18.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 21
        ImageView immagineMeteo21 = (ImageView) view.findViewById(R.id.immagineMeteo21);
        TextView oraPrevisione21 = (TextView) view.findViewById(R.id.oraPrevisione21);
        TextView tempPrevisione21 = (TextView) view.findViewById(R.id.tempPrevisione21);
        TextView ventoPrevisione21 = (TextView) view.findViewById(R.id.ventoPrevisione21);
        TextView textPrevisione21 = (TextView) view.findViewById(R.id.textPrevisione21);

        previsione = meteoDao.getPrevisione("21:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo21.setVisibility(View.INVISIBLE);
            oraPrevisione21.setVisibility(View.INVISIBLE);
            tempPrevisione21.setVisibility(View.INVISIBLE);
            ventoPrevisione21.setVisibility(View.INVISIBLE);
            textPrevisione21.setVisibility(View.INVISIBLE);
        }else{
            //TODO -> Valorizzare immagine
            oraPrevisione21.setText(previsione.getOrario());
            tempPrevisione21.setText(previsione.getTemperatura() + "°");
            ventoPrevisione21.setText(previsione.getVento() + "m/s");
            textPrevisione21.setText(previsione.getMeteoDescription());
        }

        return view;
    }
}
