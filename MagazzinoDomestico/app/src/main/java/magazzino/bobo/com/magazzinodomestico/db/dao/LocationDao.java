package magazzino.bobo.com.magazzinodomestico.db.dao;

/***
 * Dao di utilità.
 * Identifica un "luogo" possibile in cui si trova un oggetto.
 * Viene usato essenzialmente per restringere la ricerca degli oggetti/contenitori ad un sottoinsieme di posti possibili
 */
public class LocationDao {

    //Possibili tipi di Location Types
    public static final int STANZA = 1;
    public static final int MOBILE = 2;
    public static final int CONTENITORE = 3;
    public static final int CATEGORIA = 4;
    public static final int OGGETTO = 10; //Non è un vero tipo di location, ma viene usato come valore di utilità in altre parti dell'applicazione

    //Variabili di istanza
    private long id_categoria;
    private long id_stanza;
    private long id_mobile;
    private long id_contenitore;


    public LocationDao(long id_categoria, long id_stanza, long id_mobile, long id_contenitore) {
        this.id_categoria = id_categoria;
        this.id_stanza = id_stanza;
        this.id_mobile = id_mobile;
        this.id_contenitore = id_contenitore;
    }

    public long getId_categoria() {
        return id_categoria;
    }

    public long getId_stanza() {
        return id_stanza;
    }

    public long getId_mobile() {
        return id_mobile;
    }

    public long getId_contenitore() {
        return id_contenitore;
    }

    /***
     * Restituisce il tipo di location da cui viene la chiamata
     * @return
     */
    public int getLocationType()
    {
        if(id_categoria != -1 && id_stanza == -1 && id_mobile == -1 && id_contenitore == -1)
            return CATEGORIA;

        if(id_contenitore != -1)
            return CONTENITORE;

        if(id_mobile != -1)
            return MOBILE;

        if(id_stanza != -1)
            return STANZA;

        return OGGETTO;
    }

}
