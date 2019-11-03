package com.bobo.iamhere.db;

import androidx.annotation.NonNull;

public class GooglePlacesTypeDao implements Comparable {

    private int id;
    private String codice;
    private String gruppo;
    private String nomeDescrittivo;
    private int isTipoPreferito;


    public GooglePlacesTypeDao(int id, String codice, String gruppo, int isTipoPreferito)
    {
        this.id = id;
        this.codice = codice;
        this.gruppo = gruppo;
        this.isTipoPreferito = isTipoPreferito;

        this.nomeDescrittivo = "";
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

    public void setNomeDescrittivo(String nomeDescrittivo) {
        this.nomeDescrittivo = nomeDescrittivo;
    }

    public String getNomeDescrittivo() {
        return nomeDescrittivo;
    }

    @Override
    public int compareTo(@NonNull Object o) {

        GooglePlacesTypeDao oggettoDaConfrontare = (GooglePlacesTypeDao) o;

        return this.nomeDescrittivo.compareTo(oggettoDaConfrontare.getNomeDescrittivo());
    }
}
