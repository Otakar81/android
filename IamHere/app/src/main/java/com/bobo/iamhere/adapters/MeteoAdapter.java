package com.bobo.iamhere.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
        TextView umidityPrevisione00 = (TextView) view.findViewById(R.id.umidityPrevisione00);
        TextView ventoPrevisione00 = (TextView) view.findViewById(R.id.ventoPrevisione00);
        TextView textPrevisione00 = (TextView) view.findViewById(R.id.textPrevisione00);

        MeteoDao previsione = meteoDao.getPrevisione("00:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo00.setAlpha(0.2f);
            oraPrevisione00.setAlpha(0.2f);
            tempPrevisione00.setAlpha(0.2f);
            umidityPrevisione00.setAlpha(0.2f);
            ventoPrevisione00.setAlpha(0.2f);
            textPrevisione00.setAlpha(0.2f);
        }else{
            immagineMeteo00.setImageResource(getImageFromIconCode(previsione.getIcon()));
            oraPrevisione00.setText(previsione.getOrario());
            tempPrevisione00.setText(previsione.getTemperatura() + "°");
            umidityPrevisione00.setText("U " + previsione.getUmidita());
            ventoPrevisione00.setText(previsione.getVento() + "m/s");
            textPrevisione00.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 03
        ImageView immagineMeteo03 = (ImageView) view.findViewById(R.id.immagineMeteo03);
        TextView oraPrevisione03 = (TextView) view.findViewById(R.id.oraPrevisione03);
        TextView tempPrevisione03 = (TextView) view.findViewById(R.id.tempPrevisione03);
        TextView umidityPrevisione03 = (TextView) view.findViewById(R.id.umidityPrevisione03);
        TextView ventoPrevisione03 = (TextView) view.findViewById(R.id.ventoPrevisione03);
        TextView textPrevisione03 = (TextView) view.findViewById(R.id.textPrevisione03);

        previsione = meteoDao.getPrevisione("03:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo03.setAlpha(0.2f);
            oraPrevisione03.setAlpha(0.2f);
            tempPrevisione03.setAlpha(0.2f);
            umidityPrevisione03.setAlpha(0.2f);
            ventoPrevisione03.setAlpha(0.2f);
            textPrevisione03.setAlpha(0.2f);
        }else{
            immagineMeteo03.setImageResource(getImageFromIconCode(previsione.getIcon()));
            oraPrevisione03.setText(previsione.getOrario());
            tempPrevisione03.setText(previsione.getTemperatura() + "°");
            umidityPrevisione03.setText("U " + previsione.getUmidita());
            ventoPrevisione03.setText(previsione.getVento() + "m/s");
            textPrevisione03.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 06
        ImageView immagineMeteo06 = (ImageView) view.findViewById(R.id.immagineMeteo06);
        TextView oraPrevisione06 = (TextView) view.findViewById(R.id.oraPrevisione06);
        TextView tempPrevisione06 = (TextView) view.findViewById(R.id.tempPrevisione06);
        TextView umidityPrevisione06 = (TextView) view.findViewById(R.id.umidityPrevisione06);
        TextView ventoPrevisione06 = (TextView) view.findViewById(R.id.ventoPrevisione06);
        TextView textPrevisione06 = (TextView) view.findViewById(R.id.textPrevisione06);

        previsione = meteoDao.getPrevisione("06:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo06.setAlpha(0.2f);
            oraPrevisione06.setAlpha(0.2f);
            tempPrevisione06.setAlpha(0.2f);
            umidityPrevisione06.setAlpha(0.2f);
            ventoPrevisione06.setAlpha(0.2f);
            textPrevisione06.setAlpha(0.2f);
        }else{
            immagineMeteo06.setImageResource(getImageFromIconCode(previsione.getIcon()));
            oraPrevisione06.setText(previsione.getOrario());
            tempPrevisione06.setText(previsione.getTemperatura() + "°");
            umidityPrevisione06.setText("U " + previsione.getUmidita());
            ventoPrevisione06.setText(previsione.getVento() + "m/s");
            textPrevisione06.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 09
        ImageView immagineMeteo09 = (ImageView) view.findViewById(R.id.immagineMeteo09);
        TextView oraPrevisione09 = (TextView) view.findViewById(R.id.oraPrevisione09);
        TextView tempPrevisione09 = (TextView) view.findViewById(R.id.tempPrevisione09);
        TextView umidityPrevisione09 = (TextView) view.findViewById(R.id.umidityPrevisione09);
        TextView ventoPrevisione09 = (TextView) view.findViewById(R.id.ventoPrevisione09);
        TextView textPrevisione09 = (TextView) view.findViewById(R.id.textPrevisione09);

        previsione = meteoDao.getPrevisione("09:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo09.setAlpha(0.2f);
            oraPrevisione09.setAlpha(0.2f);
            tempPrevisione09.setAlpha(0.2f);
            umidityPrevisione09.setAlpha(0.2f);
            ventoPrevisione09.setAlpha(0.2f);
            textPrevisione09.setAlpha(0.2f);
        }else{
            immagineMeteo09.setImageResource(getImageFromIconCode(previsione.getIcon()));
            oraPrevisione09.setText(previsione.getOrario());
            tempPrevisione09.setText(previsione.getTemperatura() + "°");
            umidityPrevisione09.setText("U " + previsione.getUmidita());
            ventoPrevisione09.setText(previsione.getVento() + "m/s");
            textPrevisione09.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 12
        ImageView immagineMeteo12 = (ImageView) view.findViewById(R.id.immagineMeteo12);
        TextView oraPrevisione12 = (TextView) view.findViewById(R.id.oraPrevisione12);
        TextView tempPrevisione12 = (TextView) view.findViewById(R.id.tempPrevisione12);
        TextView umidityPrevisione12 = (TextView) view.findViewById(R.id.umidityPrevisione12);
        TextView ventoPrevisione12 = (TextView) view.findViewById(R.id.ventoPrevisione12);
        TextView textPrevisione12 = (TextView) view.findViewById(R.id.textPrevisione12);

        previsione = meteoDao.getPrevisione("12:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo12.setAlpha(0.2f);
            oraPrevisione12.setAlpha(0.2f);
            tempPrevisione12.setAlpha(0.2f);
            umidityPrevisione12.setAlpha(0.2f);
            ventoPrevisione12.setAlpha(0.2f);
            textPrevisione12.setAlpha(0.2f);
        }else{
            immagineMeteo12.setImageResource(getImageFromIconCode(previsione.getIcon()));
            oraPrevisione12.setText(previsione.getOrario());
            tempPrevisione12.setText(previsione.getTemperatura() + "°");
            umidityPrevisione12.setText("U " + previsione.getUmidita());
            ventoPrevisione12.setText(previsione.getVento() + "m/s");
            textPrevisione12.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 15
        ImageView immagineMeteo15 = (ImageView) view.findViewById(R.id.immagineMeteo15);
        TextView oraPrevisione15 = (TextView) view.findViewById(R.id.oraPrevisione15);
        TextView tempPrevisione15 = (TextView) view.findViewById(R.id.tempPrevisione15);
        TextView umidityPrevisione15 = (TextView) view.findViewById(R.id.umidityPrevisione15);
        TextView ventoPrevisione15 = (TextView) view.findViewById(R.id.ventoPrevisione15);
        TextView textPrevisione15 = (TextView) view.findViewById(R.id.textPrevisione15);

        previsione = meteoDao.getPrevisione("15:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo15.setAlpha(0.2f);
            oraPrevisione15.setAlpha(0.2f);
            tempPrevisione15.setAlpha(0.2f);
            umidityPrevisione15.setAlpha(0.2f);
            ventoPrevisione15.setAlpha(0.2f);
            textPrevisione15.setAlpha(0.2f);
        }else{
            immagineMeteo15.setImageResource(getImageFromIconCode(previsione.getIcon()));
            oraPrevisione15.setText(previsione.getOrario());
            tempPrevisione15.setText(previsione.getTemperatura() + "°");
            umidityPrevisione15.setText("U " + previsione.getUmidita());
            ventoPrevisione15.setText(previsione.getVento() + "m/s");
            textPrevisione15.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 18
        ImageView immagineMeteo18 = (ImageView) view.findViewById(R.id.immagineMeteo18);
        TextView oraPrevisione18 = (TextView) view.findViewById(R.id.oraPrevisione18);
        TextView tempPrevisione18 = (TextView) view.findViewById(R.id.tempPrevisione18);
        TextView umidityPrevisione18 = (TextView) view.findViewById(R.id.umidityPrevisione18);
        TextView ventoPrevisione18 = (TextView) view.findViewById(R.id.ventoPrevisione18);
        TextView textPrevisione18 = (TextView) view.findViewById(R.id.textPrevisione18);

        previsione = meteoDao.getPrevisione("18:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo18.setAlpha(0.2f);
            oraPrevisione18.setAlpha(0.2f);
            tempPrevisione18.setAlpha(0.2f);
            umidityPrevisione18.setAlpha(0.2f);
            ventoPrevisione18.setAlpha(0.2f);
            textPrevisione18.setAlpha(0.2f);
        }else{
            immagineMeteo18.setImageResource(getImageFromIconCode(previsione.getIcon()));
            oraPrevisione18.setText(previsione.getOrario());
            tempPrevisione18.setText(previsione.getTemperatura() + "°");
            umidityPrevisione18.setText("U " + previsione.getUmidita());
            ventoPrevisione18.setText(previsione.getVento() + "m/s");
            textPrevisione18.setText(previsione.getMeteoDescription());
        }

        //Previsione delle 21
        ImageView immagineMeteo21 = (ImageView) view.findViewById(R.id.immagineMeteo21);
        TextView oraPrevisione21 = (TextView) view.findViewById(R.id.oraPrevisione21);
        TextView tempPrevisione21 = (TextView) view.findViewById(R.id.tempPrevisione21);
        TextView umidityPrevisione21 = (TextView) view.findViewById(R.id.umidityPrevisione21);
        TextView ventoPrevisione21 = (TextView) view.findViewById(R.id.ventoPrevisione21);
        TextView textPrevisione21 = (TextView) view.findViewById(R.id.textPrevisione21);

        previsione = meteoDao.getPrevisione("21:00");

        if(previsione == null) //Spengo
        {
            immagineMeteo21.setAlpha(0.2f);
            oraPrevisione21.setAlpha(0.2f);
            tempPrevisione21.setAlpha(0.2f);
            umidityPrevisione21.setAlpha(0.2f);
            ventoPrevisione21.setAlpha(0.2f);
            textPrevisione21.setAlpha(0.2f);
        }else{
            immagineMeteo21.setImageResource(getImageFromIconCode(previsione.getIcon()));
            oraPrevisione21.setText(previsione.getOrario());
            tempPrevisione21.setText(previsione.getTemperatura() + "°");
            umidityPrevisione21.setText("U " + previsione.getUmidita());
            ventoPrevisione21.setText(previsione.getVento() + "m/s");
            textPrevisione21.setText(previsione.getMeteoDescription());
        }

        return view;
    }

    private int getImageFromIconCode(String iconCode)
    {
        if(iconCode.equalsIgnoreCase("01d"))
            return R.drawable.meteo_01d;
        else if(iconCode.equalsIgnoreCase("01n"))
            return R.drawable.meteo_01n;
        else if(iconCode.equalsIgnoreCase("02d"))
            return R.drawable.meteo_02d;
        else if(iconCode.equalsIgnoreCase("02n"))
            return R.drawable.meteo_02n;
        else if(iconCode.equalsIgnoreCase("03d"))
            return R.drawable.meteo_03d;
        else if(iconCode.equalsIgnoreCase("03n"))
            return R.drawable.meteo_03n;
        else if(iconCode.equalsIgnoreCase("04d"))
            return R.drawable.meteo_04d;
        else if(iconCode.equalsIgnoreCase("04n"))
            return R.drawable.meteo_04n;

        else if(iconCode.equalsIgnoreCase("09d"))
            return R.drawable.meteo_09d;
        else if(iconCode.equalsIgnoreCase("09n"))
            return R.drawable.meteo_09n;
        else if(iconCode.equalsIgnoreCase("10d"))
            return R.drawable.meteo_10d;
        else if(iconCode.equalsIgnoreCase("10n"))
            return R.drawable.meteo_10n;
        else if(iconCode.equalsIgnoreCase("11d"))
            return R.drawable.meteo_11d;
        else if(iconCode.equalsIgnoreCase("11n"))
            return R.drawable.meteo_11n;

        else if(iconCode.equalsIgnoreCase("50d"))
            return R.drawable.meteo_50d;
        else if(iconCode.equalsIgnoreCase("50n"))
            return R.drawable.meteo_50n;

        else
            return R.drawable.nav_meteo;
    }
}
