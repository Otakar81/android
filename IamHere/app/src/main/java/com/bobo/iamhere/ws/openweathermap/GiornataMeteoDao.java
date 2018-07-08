package com.bobo.iamhere.ws.openweathermap;

import java.util.ArrayList;

public class GiornataMeteoDao {

    private String data;
    private ArrayList<MeteoDao> previsioniGiornata;

    public GiornataMeteoDao(String data)
    {
        this.data = data;
        this.previsioniGiornata = new ArrayList<MeteoDao>();
    }

    public void addPrevisione(MeteoDao previsione)
    {
        this.previsioniGiornata.add(previsione);
    }

    public String getData() {
        return data;
    }

    public ArrayList<MeteoDao> getPrevisioniGiornata() {
        return previsioniGiornata;
    }

    /**
     * Restituisce la previsione relativa all'orario (nel formato HH:mm) passato come argomento
      * @param orarioPrevisione
     * @return
     */
    public MeteoDao getPrevisione(String orarioPrevisione)
    {
        for (MeteoDao previsione:previsioniGiornata) {
            if(previsione.getOrario().equalsIgnoreCase(orarioPrevisione))
                return previsione;
        }

        return null;
    }
}
