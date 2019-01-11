package magazzino.bobo.com.magazzinodomestico.db.dao;

public class MobileDao {

    private long id;
    private String nome;
    private String immagine;
    private long id_stanza;
    private String nomeStanza;

    //Contatori per tener traccia del numero di elementi presenti nella stanza
    private int numeroContenitori;
    private int numeroOggetti;

    public MobileDao(long id, String nome, String immagine, long id_stanza, String nomeStanza) {
        this.id = id;
        this.nome = nome;
        this.immagine = immagine;
        this.id_stanza = id_stanza;
        this.nomeStanza = nomeStanza;

        this.numeroContenitori = -1;
        this.numeroOggetti = -1;
    }

    public MobileDao(long id, String nome, long id_stanza, String nomeStanza) {
        this.id = id;
        this.nome = nome;
        this.id_stanza = id_stanza;
        this.nomeStanza = nomeStanza;
        this.immagine = "";

        this.numeroContenitori = -1;
        this.numeroOggetti = -1;
    }

    public MobileDao(String nome, String immagine, long id_stanza, String nomeStanza) {
        this.id = -1;
        this.nome = nome;
        this.immagine = immagine;
        this.id_stanza = id_stanza;
        this.nomeStanza = nomeStanza;

        this.numeroContenitori = -1;
        this.numeroOggetti = -1;
    }

    public MobileDao(String nome, long id_stanza, String nomeStanza) {
        this.id = -1;
        this.nome = nome;
        this.id_stanza = id_stanza;
        this.nomeStanza = nomeStanza;
        this.immagine = "";

        this.numeroContenitori = -1;
        this.numeroOggetti = -1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public long getId_stanza() {
        return id_stanza;
    }

    public void setId_stanza(long id_stanza) {
        this.id_stanza = id_stanza;
    }

    public String getNomeStanza() {
        return nomeStanza;
    }

    public void setNomeStanza(String nomeStanza) {
        this.nomeStanza = nomeStanza;
    }

    public int getNumeroContenitori() {
        return numeroContenitori;
    }

    public void setNumeroContenitori(int numeroContenitori) {
        this.numeroContenitori = numeroContenitori;
    }

    public int getNumeroOggetti() {
        return numeroOggetti;
    }

    public void setNumeroOggetti(int numeroOggetti) {
        this.numeroOggetti = numeroOggetti;
    }

    @Override
    public String toString() {

        /*
        if(id_stanza != -1)
            return nome + " (" + nomeStanza + ")";
        else
            return nome;
        */

        return nome;
    }

    /***
     * Restituisce true se la stringa passata come argomento "trova" l'oggetto
     *
     * @param searchString
     * @return
     */
    public boolean searchItem(String searchString)
    {
        if(nome.toUpperCase().contains(searchString.toUpperCase()))
            return true;
        else if(nomeStanza.toUpperCase().contains(searchString.toUpperCase()))
            return true;
        else
            return false;
    }
}
