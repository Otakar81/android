package magazzino.bobo.com.magazzinodomestico.db.dao;

public class MobileDao {

    private long id;
    private String nome;
    private String immagine;
    private long id_stanza;

    public MobileDao(long id, String nome, String immagine, long id_stanza) {
        this.id = id;
        this.nome = nome;
        this.immagine = immagine;
        this.id_stanza = id_stanza;
    }

    public MobileDao(long id, String nome, long id_stanza) {
        this.id = id;
        this.nome = nome;
        this.id_stanza = id_stanza;
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
}
