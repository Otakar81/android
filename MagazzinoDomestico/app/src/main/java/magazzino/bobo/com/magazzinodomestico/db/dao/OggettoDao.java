package magazzino.bobo.com.magazzinodomestico.db.dao;

public class OggettoDao {

    private long id;
    private String nome;
    private String immagine;
    private long id_categoria;
    private long id_stanza;
    private long id_mobile;
    private long id_contenitore;

    public OggettoDao(long id, String nome, String immagine, long id_categoria, long id_stanza, long id_mobile, long id_contenitore) {
        this.id = id;
        this.nome = nome;
        this.immagine = immagine;
        this.id_categoria = id_categoria;
        this.id_stanza = id_stanza;
        this.id_mobile = id_mobile;
        this.id_contenitore = id_contenitore;
    }

    public OggettoDao(long id, String nome, long id_categoria, long id_stanza, long id_mobile, long id_contenitore) {
        this.id = id;
        this.nome = nome;
        this.id_categoria = id_categoria;
        this.id_stanza = id_stanza;
        this.id_mobile = id_mobile;
        this.id_contenitore = id_contenitore;
    }

    public OggettoDao(long id, String nome, long id_stanza, long id_mobile, long id_contenitore) {
        this.id = id;
        this.nome = nome;
        this.id_stanza = id_stanza;
        this.id_mobile = id_mobile;
        this.id_contenitore = id_contenitore;
        this.id_categoria = -1;
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

    public long getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(long id_categoria) {
        this.id_categoria = id_categoria;
    }

    public long getId_stanza() {
        return id_stanza;
    }

    public void setId_stanza(long id_stanza) {
        this.id_stanza = id_stanza;
    }

    public long getId_mobile() {
        return id_mobile;
    }

    public void setId_mobile(long id_mobile) {
        this.id_mobile = id_mobile;
    }

    public long getId_contenitore() {
        return id_contenitore;
    }

    public void setId_contenitore(long id_contenitore) {
        this.id_contenitore = id_contenitore;
    }
}
