package com.bobo.iamhere.db;

public class GooglePlacesTypeDao {

    private int id;
    private String codice;
    private String gruppo;
    private int isTipoPreferito;


    public GooglePlacesTypeDao(int id, String codice, String gruppo, int isTipoPreferito)
    {
        this.id = id;
        this.codice = codice;
        this.gruppo = gruppo;
        this.isTipoPreferito = isTipoPreferito;
    }


    public int getId() {
        return id;
    }

    public String getCodice() {
        return codice;
    }

    public String getGruppo() {
        return gruppo;
    }

    public int isTipoPreferito() {
        return isTipoPreferito;
    }

    public void setTipoPreferito(int tipoPreferito) {
        isTipoPreferito = tipoPreferito;
    }
}
