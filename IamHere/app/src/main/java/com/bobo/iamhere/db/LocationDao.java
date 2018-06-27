package com.bobo.iamhere.db;

public class LocationDao {

    private long id;
    private double latitudine;
    private double longitudine;
    private String alias;
    private String nazione;
    private String regione;
    private String provincia;
    private String comune;
    private String cap;
    private String indirizzo;


    public LocationDao(double latitudine, double longitudine, String nazione, String regione, String provincia, String comune, String cap, String indirizzo)
    {
        this.id = -1;
        this.alias = "";
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.nazione = nazione;
        this.regione = regione;
        this.provincia = provincia;
        this.comune = comune;
        this.cap = cap;
        this.indirizzo = indirizzo;
    }

    public LocationDao(long id, double latitudine, double longitudine, String nazione, String regione, String provincia, String comune, String cap, String indirizzo)
    {
        this.id = id;
        this.alias = "";
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.nazione = nazione;
        this.regione = regione;
        this.provincia = provincia;
        this.comune = comune;
        this.cap = cap;
        this.indirizzo = indirizzo;
    }

    public LocationDao(long id, String alias, double latitudine, double longitudine, String nazione, String regione, String provincia, String comune, String cap, String indirizzo)
    {
        this.id = id;
        this.alias = alias;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.nazione = nazione;
        this.regione = regione;
        this.provincia = provincia;
        this.comune = comune;
        this.cap = cap;
        this.indirizzo = indirizzo;
    }
    
    

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public String getNazione() {
        return nazione;
    }

    public String getRegione() {
        return regione;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getComune() {
        return comune;
    }

    public String getCap() {
        return cap;
    }

    public String getIndirizzo() {
        return indirizzo;
    }


    @Override
    public String toString() {
        String objToString = "(" + comune + ") " + indirizzo;

        if(alias != null && !alias.trim().equals(""))
            objToString = alias + "\n" + objToString;

        return objToString;
    }

    /***
     * Se il luogo ha un alias, restituisco solo quello.
     * Altrimenti si comporta come il toString()
     *
     * @return
     */
    public String toStringShort()
    {
        if(alias != null && !alias.trim().equals(""))
            return alias;
        else
            return toString();
    }
}
