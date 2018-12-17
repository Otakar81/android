package magazzino.bobo.com.magazzinodomestico.db.dao;

public class MobileDao {

    private long id;
    private String nome;
    private String immagine;
    private long id_stanza;
    private String nomeStanza;

    public MobileDao(long id, String nome, String immagine, long id_stanza, String nomeStanza) {
        this.id = id;
        this.nome = nome;
        this.immagine = immagine;
        this.id_stanza = id_stanza;
        this.nomeStanza = nomeStanza;
    }

    public MobileDao(long id, String nome, long id_stanza, String nomeStanza) {
        this.id = id;
        this.nome = nome;
        this.id_stanza = id_stanza;
        this.nomeStanza = nomeStanza;
        this.immagine = "";
    }

    public MobileDao(String nome, String immagine, long id_stanza, String nomeStanza) {
        this.id = -1;
        this.nome = nome;
        this.immagine = immagine;
        this.id_stanza = id_stanza;
        this.nomeStanza = nomeStanza;
    }

    public MobileDao(String nome, long id_stanza, String nomeStanza) {
        this.id = -1;
        this.nome = nome;
        this.id_stanza = id_stanza;
        this.nomeStanza = nomeStanza;
        this.immagine = "";
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

    @Override
    public String toString() {

        if(id_stanza != -1)
            return nome + " (" + nomeStanza + ")";
        else
            return nome + " ( - )";
    }
}
