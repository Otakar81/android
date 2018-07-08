package com.bobo.iamhere.ws.openweathermap;

public class MeteoDao {

    private String data;
    private String orario;
    private String meteo;
    private String meteoDescription;
    private String temperatura;
    private String vento;
    private String icon;

    public MeteoDao(String data, String orario, String meteo, String meteoDescription, String temperatura, String vento, String icon)
    {
        this.data = data;
        this.orario = orario;
        this.meteo = meteo;
        this.meteoDescription = meteoDescription;
        this.temperatura = temperatura;
        this.vento = vento;
        this.icon = icon;
    }


    public String getData() {
        return data;
    }

    public String getOrario() {
        return orario;
    }

    public String getMeteo() {
        return meteo;
    }

    public String getMeteoDescription() {
        return meteoDescription;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public String getVento() {
        return vento;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString()
    {
        return orario + " T: " + temperatura + "° W: " + vento + " - " + meteoDescription;
    }
}