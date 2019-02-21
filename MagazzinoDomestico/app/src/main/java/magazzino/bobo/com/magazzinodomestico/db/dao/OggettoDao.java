package magazzino.bobo.com.magazzinodomestico.db.dao;

public class OggettoDao {

    private long id;
    private String nome;
    private String descrizione;
    private String immagine;
    private int numero_oggetti;

    private long id_categoria;
    private String nome_categoria;

    private long id_stanza;
    private String nome_stanza;

    private long id_mobile;
    private String nome_mobile;

    private long id_contenitore;
    private String nome_contenitore;

    public OggettoDao(long id, String nome, String descrizione, int numero_oggetti, String immagine, long id_categoria, String nome_categoria, long id_stanza, String nome_stanza,
                      long id_mobile, String nome_mobile, long id_contenitore, String nome_contenitore) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.numero_oggetti = numero_oggetti;
        this.immagine = immagine;
        this.id_categoria = id_categoria;
        this.nome_categoria = nome_categoria;
        this.id_stanza = id_stanza;
        this.nome_stanza = nome_stanza;
        this.id_mobile = id_mobile;
        this.nome_mobile = nome_mobile;
        this.id_contenitore = id_contenitore;
        this.nome_contenitore = nome_contenitore;
    }

    public OggettoDao(long id, String nome, String descrizione, int numero_oggetti, long id_categoria, String nome_categoria, long id_stanza, String nome_stanza,
                      long id_mobile, String nome_mobile, long id_contenitore, String nome_contenitore) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.numero_oggetti = numero_oggetti;
        //this.immagine = immagine;
        this.id_categoria = id_categoria;
        this.nome_categoria = nome_categoria;
        this.id_stanza = id_stanza;
        this.nome_stanza = nome_stanza;
        this.id_mobile = id_mobile;
        this.nome_mobile = nome_mobile;
        this.id_contenitore = id_contenitore;
        this.nome_contenitore = nome_contenitore;
    }

    public OggettoDao(String nome, String descrizione, int numero_oggetti, String immagine, long id_categoria, String nome_categoria, long id_stanza, String nome_stanza,
                      long id_mobile, String nome_mobile, long id_contenitore, String nome_contenitore) {
        this.id = -1;
        this.nome = nome;
        this.descrizione = descrizione;
        this.numero_oggetti = numero_oggetti;
        this.immagine = immagine;
        this.id_categoria = id_categoria;
        this.nome_categoria = nome_categoria;
        this.id_stanza = id_stanza;
        this.nome_stanza = nome_stanza;
        this.id_mobile = id_mobile;
        this.nome_mobile = nome_mobile;
        this.id_contenitore = id_contenitore;
        this.nome_contenitore = nome_contenitore;
    }

    public OggettoDao(String nome, String descrizione, int numero_oggetti, long id_categoria, String nome_categoria, long id_stanza, String nome_stanza,
                      long id_mobile, String nome_mobile, long id_contenitore, String nome_contenitore) {
        this.id = -1;
        this.nome = nome;
        this.descrizione = descrizione;
        this.numero_oggetti = numero_oggetti;
        //this.immagine = immagine;
        this.id_categoria = id_categoria;
        this.nome_categoria = nome_categoria;
        this.id_stanza = id_stanza;
        this.nome_stanza = nome_stanza;
        this.id_mobile = id_mobile;
        this.nome_mobile = nome_mobile;
        this.id_contenitore = id_contenitore;
        this.nome_contenitore = nome_contenitore;
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

    public String getNome_categoria() {
        return nome_categoria;
    }

    public void setNome_categoria(String nome_categoria) {
        this.nome_categoria = nome_categoria;
    }

    public String getNome_stanza() {
        return nome_stanza;
    }

    public void setNome_stanza(String nome_stanza) {
        this.nome_stanza = nome_stanza;
    }

    public String getNome_mobile() {
        return nome_mobile;
    }

    public void setNome_mobile(String nome_mobile) {
        this.nome_mobile = nome_mobile;
    }

    public String getNome_contenitore() {
        return nome_contenitore;
    }

    public void setNome_contenitore(String nome_contenitore) {
        this.nome_contenitore = nome_contenitore;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getNumero_oggetti() {
        return numero_oggetti;
    }

    public void setNumero_oggetti(int numero_oggetti) {
        this.numero_oggetti = numero_oggetti;
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
        else if(descrizione != null && descrizione.toUpperCase().contains(searchString.toUpperCase()))
            return true;
        else if(nome_stanza.toUpperCase().contains(searchString.toUpperCase()))
            return true;
        else if(nome_categoria.toUpperCase().contains(searchString.toUpperCase()))
            return true;
        else if(nome_mobile.toUpperCase().contains(searchString.toUpperCase()))
            return true;
        else if(nome_contenitore.toUpperCase().contains(searchString.toUpperCase()))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {

        /*
        String location = "";

        if(id_contenitore != -1)
            location += nome_contenitore + " - ";

        if(id_mobile != -1)
            location += nome_mobile + " - ";


        return nome + " ( " + location + nome_stanza + ")";
        */

        return nome;
    }
}
