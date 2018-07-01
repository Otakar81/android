package com.bobo.iamhere.db;

public class NotaDao {

    private long id;
    private String titolo;
    private String testoNota;
    private long idLocation;

    public NotaDao(String titolo, String testoNota)
    {
        this.id = -1;
        this.titolo = titolo;
        this.testoNota = testoNota;
        this.idLocation = -1;
    }

    public NotaDao(long id, String titolo, String testoNota)
    {
        this.id = id;
        this.titolo = titolo;
        this.testoNota = testoNota;
        this.idLocation = -1;
    }

    public NotaDao(long id, String titolo, String testoNota, long idLocation)
    {
        this.id = id;
        this.titolo = titolo;
        this.testoNota = testoNota;
        this.idLocation = idLocation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTestoNota() {
        return testoNota;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTestoNota(String testoNota) {
        this.testoNota = testoNota;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public long getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(long idLocation) {
        this.idLocation = idLocation;
    }

    public String getNotaPerLista()
    {
        if(titolo != null && titolo.trim().length() > 0)
            return titolo;
        else
        {
            if(testoNota != null && testoNota.trim().length() > 30)
                return testoNota.substring(0, 27) + "...";
            else
                return testoNota;
        }
    }

    @Override
    public boolean equals(Object obj) {

        NotaDao daConfrontare = (NotaDao) obj;

        if(titolo.equals(daConfrontare.getTitolo()) && testoNota.equals(daConfrontare.getTestoNota()))
            return true;
        else
            return false;
    }
}
