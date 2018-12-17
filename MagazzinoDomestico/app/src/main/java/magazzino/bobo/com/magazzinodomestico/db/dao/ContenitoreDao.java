package magazzino.bobo.com.magazzinodomestico.db.dao;

public class ContenitoreDao {

    private long id;
    private String nome;
    private String immagine;

    private long id_categoria;
    public String nome_categoria;

    private long id_stanza;
    private String nome_stanza;

    private long id_mobile;
    private String nome_mobile;

    public ContenitoreDao(long id, String nome, String immagine, long id_categoria, String nome_categoria,
                          long id_stanza, String nome_stanza, long id_mobile, String nome_mobile) {
        this.id = id;
        this.nome = nome;
        this.immagine = immagine;

        this.id_categoria = id_categoria;
        this.nome_categoria = nome_categoria;

        this.id_stanza = id_stanza;
        this.nome_stanza = nome_stanza;

        this.id_mobile = id_mobile;
        this.nome_mobile = nome_mobile;
    }

    public ContenitoreDao(long id, String nome, long id_categoria, String nome_categoria,
                          long id_stanza, String nome_stanza, long id_mobile, String nome_mobile) {
        this.id = id;
        this.nome = nome;
        this.immagine = "";

        this.id_categoria = id_categoria;
        this.nome_categoria = nome_categoria;

        this.id_stanza = id_stanza;
        this.nome_stanza = nome_stanza;

        this.id_mobile = id_mobile;
        this.nome_mobile = nome_mobile;
    }

    public ContenitoreDao(long id, String nome, String immagine,
                          long id_stanza, String nome_stanza, long id_mobile, String nome_mobile) {
        this.id = id;
        this.nome = nome;
        this.immagine = immagine;

        this.id_categoria = -1;
        this.nome_categoria = "";

        this.id_stanza = id_stanza;
        this.nome_stanza = nome_stanza;

        this.id_mobile = id_mobile;
        this.nome_mobile = nome_mobile;
    }

    public ContenitoreDao(long id, String nome, long id_stanza, String nome_stanza, long id_mobile, String nome_mobile) {
        this.id = id;
        this.nome = nome;
        this.immagine = "";

        this.id_categoria = -1;
        this.nome_categoria = "";

        this.id_stanza = id_stanza;
        this.nome_stanza = nome_stanza;

        this.id_mobile = id_mobile;
        this.nome_mobile = nome_mobile;
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

    public String getNome_categoria() {
        return nome_categoria;
    }

    public String getNome_stanza() {
        return nome_stanza;
    }

    public String getNome_mobile() {
        return nome_mobile;
    }

    @Override
    public String toString() {

        return nome + " ( " + nome_mobile + " - " + nome_stanza + ")";
    }
}
