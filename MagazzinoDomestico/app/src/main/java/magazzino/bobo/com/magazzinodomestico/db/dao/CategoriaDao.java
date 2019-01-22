package magazzino.bobo.com.magazzinodomestico.db.dao;

public class CategoriaDao {

    private long id;
    private String nome;
    private String colore;

    //Contatori per tener traccia del numero di elementi presenti nella stanza
    private int numeroContenitori;
    private int numeroOggetti;

    public CategoriaDao(long id, String nome, String colore) {
        this.id = id;
        this.nome = nome;
        this.colore = colore;

        this.numeroContenitori = -1;
        this.numeroOggetti = -1;
    }

    public CategoriaDao(long id, String nome) {
        this.id = id;
        this.nome = nome;
        this.colore = "";

        this.numeroContenitori = -1;
        this.numeroOggetti = -1;
    }

    public CategoriaDao(String nome) {
        this.id = -1;
        this.nome = nome;
        this.colore = "";

        this.numeroContenitori = -1;
        this.numeroOggetti = -1;
    }

    public CategoriaDao(String nome, String colore) {
        this.id = -1;
        this.nome = nome;
        this.colore = colore;

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

    public String getColore() {
        return colore;
    }

    public void setColore(String colore) {
        this.colore = colore;
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
        else
            return false;
    }


    @Override
    public String toString() {
        return nome;
    }
}
