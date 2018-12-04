package magazzino.bobo.com.magazzinodomestico.db.dao;

public class CategoriaDao {

    private long id;
    private String nome;
    private String colore;

    public CategoriaDao(long id, String nome, String colore) {
        this.id = id;
        this.nome = nome;
        this.colore = colore;
    }

    public CategoriaDao(long id, String nome) {
        this.id = id;
        this.nome = nome;
        this.colore = "";
    }

    public CategoriaDao(String nome) {
        this.id = -1;
        this.nome = nome;
        this.colore = "";
    }

    public CategoriaDao(String nome, String colore) {
        this.id = -1;
        this.nome = nome;
        this.colore = colore;
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

    public String getColore() {
        return colore;
    }

    public void setColore(String colore) {
        this.colore = colore;
    }
}
