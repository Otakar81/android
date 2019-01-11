package magazzino.bobo.com.magazzinodomestico.db.dao;

public class StanzaDao {

    private long id;
    private String nome;

    //Contatori per tener traccia del numero di elementi presenti nella stanza
    private int numeroMobili;
    private int numeroContenitori;
    private int numeroOggetti;

    public StanzaDao(long id, String nome) {
        this.id = id;
        this.nome = nome;

        this.numeroMobili = -1;
        this.numeroContenitori = -1;
        this.numeroOggetti = -1;
    }

    public StanzaDao(String nome) {
        this.id = -1;
        this.nome = nome;

        this.numeroMobili = -1;
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

    public int getNumeroMobili() {
        return numeroMobili;
    }

    public void setNumeroMobili(int numeroMobili) {
        this.numeroMobili = numeroMobili;
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
        return nome;
    }
}
