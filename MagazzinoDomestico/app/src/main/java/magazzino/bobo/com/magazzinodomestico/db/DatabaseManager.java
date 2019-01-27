package magazzino.bobo.com.magazzinodomestico.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

import magazzino.bobo.com.magazzinodomestico.db.dao.CategoriaDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.ContenitoreDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.LocationDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.MobileDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.OggettoDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.StanzaDao;

public class DatabaseManager {

//region SEZIONE COMUNE

    /***
     * Crea le tabelle utilizzate dall'applicazione
     * @param database
     */
    public static void createTables(SQLiteDatabase database)
    {

//region SEZIONE CREAZIONE TABELLE

        /*
            TABELLA CATEGORIE
         */
        String sql = "CREATE TABLE IF NOT EXISTS categorie (id INTEGER PRIMARY KEY, nome VARCHAR, colore VARCHAR)";
        database.execSQL(sql);

        /*
            TABELLA STANZE
         */
        sql = "CREATE TABLE IF NOT EXISTS stanze (id INTEGER PRIMARY KEY, nome VARCHAR, descrizione VARCHAR)";
        database.execSQL(sql);

        /*
            TABELLA MOBILI
         */
        sql = "CREATE TABLE IF NOT EXISTS mobili (id INTEGER PRIMARY KEY, nome VARCHAR, descrizione VARCHAR, immagine VARCHAR, id_stanza INTEGER)";
        database.execSQL(sql);

         /*
            TABELLA CONTENITORI
         */
        sql = "CREATE TABLE IF NOT EXISTS contenitori (id INTEGER PRIMARY KEY, nome VARCHAR, descrizione VARCHAR, immagine VARCHAR," +
                " id_categoria INTEGER, id_stanza INTEGER, id_mobile INTEGER)";
        database.execSQL(sql);

        /*
            TABELLA OGGETTI
         */
        sql = "CREATE TABLE IF NOT EXISTS oggetti (id INTEGER PRIMARY KEY, nome VARCHAR, descrizione VARCHAR, immagine VARCHAR, " +
                "id_categoria INTEGER, id_stanza INTEGER, id_mobile INTEGER, id_contenitore INTEGER)";
        database.execSQL(sql);


        //TODO Da riattivare solo al prossimo rilascio
        updateDatabase(database);
    }


    /***
     * Metodo di servizio per l'update della struttura del database
     * @param database
     */
    private static void updateDatabase(SQLiteDatabase database)
    {
        try{

            String sql = "ALTER TABLE stanze ADD COLUMN descrizione VARCHAR";
            database.execSQL(sql);

            sql = "ALTER TABLE mobili ADD COLUMN descrizione VARCHAR";
            database.execSQL(sql);

            sql = "ALTER TABLE contenitori ADD COLUMN descrizione VARCHAR";
            database.execSQL(sql);

            sql = "ALTER TABLE oggetti ADD COLUMN descrizione VARCHAR";
            database.execSQL(sql);

        }catch (Exception e)
        {
            Log.i("DEBUG", "Colonna già presente");
        }
    }

//endregion


//region SEZIONE CATEGORIA


    /***
     * Inserisce la categoria passata come argomento
     *
     * @param database
     * @param categoriaDao
     */
    public static void insertCategoria(SQLiteDatabase database, CategoriaDao categoriaDao)
    {
        String sql = "INSERT INTO categorie (nome, colore) VALUES (?, ?)";

        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, categoriaDao.getNome());
        stmt.bindString(2, categoriaDao.getColore());
        stmt.execute();
    }

    /***
     * Update categoria
     *
     * @param database
     * @param categoriaDao
     */
    public static void updateCategoria(SQLiteDatabase database, CategoriaDao categoriaDao)
    {
        SQLiteStatement stmt = database.compileStatement("UPDATE categorie SET nome = ?, colore = ? WHERE id = ?");
        stmt.bindString(1, categoriaDao.getNome());
        stmt.bindString(2, categoriaDao.getColore());
        stmt.bindLong(3, categoriaDao.getId());

        stmt.execute();
    }

    /***
     * Cancella la categoria passata come argomento
     *
     * @param database
     * @param id_categoria
     */
    public static void deleteCategoria(SQLiteDatabase database, long id_categoria)
    {
        database.execSQL("DELETE FROM categorie where id = " + id_categoria);

        //Vanno azzerati gli eventuali puntamenti a questa categoria
        database.execSQL("UPDATE contenitori SET id_categoria = -1 WHERE id_categoria = " + id_categoria);
        database.execSQL("UPDATE oggetti SET id_categoria = -1 WHERE id_categoria = " + id_categoria);
    }

    /***
     * Restituisce tutte le categorie presenti nel sistema
     *
     * @param database
     * @return
     */
    public static ArrayList<CategoriaDao> getAllCategorie(SQLiteDatabase database, boolean showDefaultElement)
    {
        ArrayList<CategoriaDao> result = new ArrayList<CategoriaDao>();

        String sql = "SELECT c.id, c.nome, c.colore, " +
                        "(select count(*) from contenitori where id_categoria=c.id) as numero_contenitori, " +
                        "(select count(*) from oggetti where id_categoria=c.id) as numero_oggetti " +
                    "FROM categorie c ORDER BY c.nome";

        Cursor c = database.rawQuery(sql, null);

        int idIndex = c.getColumnIndex("id");
        int nomeIndex = c.getColumnIndex("nome");
        int coloreIndex = c.getColumnIndex("colore");

        if(showDefaultElement) //Se true, inserisco il record di default
            result.add(new CategoriaDao(-1, "-", ""));

        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            String nome = c.getString(nomeIndex);
            String colore = c.getString(coloreIndex);

            int numeroContenitori = c.getInt(c.getColumnIndex("numero_contenitori"));
            int numeroOggetti = c.getInt(c.getColumnIndex("numero_oggetti"));


            CategoriaDao dao = new CategoriaDao(id, nome, colore);
            dao.setNumeroContenitori(numeroContenitori);
            dao.setNumeroOggetti(numeroOggetti);
            result.add(dao);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce la categoria passata come argomento
     *
     * @param database
     * @param id
     * @return
     */
    public static CategoriaDao getCategoria(SQLiteDatabase database, long id)
    {
        CategoriaDao result = null;

        Cursor c = database.rawQuery("SELECT * FROM categorie where id = " + id, null);

        int nomeIndex = c.getColumnIndex("nome");
        int coloreIndex = c.getColumnIndex("colore");

        if (c.moveToNext())
        {
            String nome = c.getString(nomeIndex);
            String colore = c.getString(coloreIndex);

            result = new CategoriaDao(id, nome, colore);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce la categoria passata come argomento
     *
     * @param database
     * @param nome
     * @return
     */
    public static CategoriaDao getCategoriaByName(SQLiteDatabase database, String nome)
    {
        CategoriaDao result = null;

        Cursor c = database.rawQuery("SELECT * FROM categorie where nome = '" + nome + "'", null);

        int idIndex = c.getColumnIndex("id");
        int coloreIndex = c.getColumnIndex("colore");

        if (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            String colore = c.getString(coloreIndex);

            result = new CategoriaDao(id, nome, colore);
        }

        c.close();

        return result;
    }



//endregion

//region SEZIONE stanze

    /***
     * Inserisce la stanza passata come argomento
     *
     * @param database
     * @param stanzaDao
     */
    public static void insertStanza(SQLiteDatabase database, StanzaDao stanzaDao)
    {
        String sql = "INSERT INTO stanze (nome) VALUES (?)";

        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, stanzaDao.getNome());
        stmt.execute();
    }

    /***
     * Update stanza
     *
     * @param database
     * @param stanzaDao
     */
    public static void updateStanza(SQLiteDatabase database, StanzaDao stanzaDao)
    {
        SQLiteStatement stmt = database.compileStatement("UPDATE stanze SET nome = ? WHERE id = ?");
        stmt.bindString(1, stanzaDao.getNome());
        stmt.bindLong(2, stanzaDao.getId());

        stmt.execute();
    }

    /***
     * Cancella la stanza passata come argomento
     *
     * @param database
     * @param id_stanza
     */
    public static void deleteStanza(SQLiteDatabase database, long id_stanza)
    {
        database.execSQL("DELETE FROM stanze where id = " + id_stanza);

        //Vanno azzerati gli eventuali puntamenti a questa categoria
        database.execSQL("UPDATE mobili SET id_stanza = -1 WHERE id_stanza = " + id_stanza);
        database.execSQL("UPDATE contenitori SET id_stanza = -1 WHERE id_stanza = " + id_stanza);
        database.execSQL("UPDATE oggetti SET id_stanza = -1 WHERE id_stanza = " + id_stanza);
    }

    /***
     * Restituisce il numero di associazioni con altri elementi per la stanza passata come argomento
     *
     * @param database
     * @param idStanza
     * @return
     */
    public static int numeroAssociazioniStanza(SQLiteDatabase database, long idStanza)
    {
        int numeroAssociazioni = 0;

        String sql = "SELECT s.id, s.nome, " +
                        "(select count(*) from mobili where id_stanza=s.id) as numero_mobili, " +
                        "(select count(*) from contenitori where id_stanza=s.id) as numero_contenitori, " +
                        "(select count(*) from oggetti where id_stanza=s.id) as numero_oggetti " +
                        "FROM stanze s WHERE s.id = " + idStanza + " " +
                        "ORDER BY s.nome";

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToNext())
        {
            int numeroMobili = c.getInt(c.getColumnIndex("numero_mobili"));
            int numeroContenitori = c.getInt(c.getColumnIndex("numero_contenitori"));
            int numeroOggetti = c.getInt(c.getColumnIndex("numero_oggetti"));

            numeroAssociazioni = numeroMobili + numeroContenitori + numeroOggetti;
        }

        c.close();

        return numeroAssociazioni;
    }

    /***
     * Restituisce tutte le stanze presenti nel sistema
     *
     * @param database
     * @return
     */
    public static ArrayList<StanzaDao> getAllStanze(SQLiteDatabase database)
    {
        ArrayList<StanzaDao> result = new ArrayList<StanzaDao>();

        //String sql = "SELECT id, nome FROM stanze ORDER BY nome";
        String sql = "SELECT s.id, s.nome, " +
                        "(select count(*) from mobili where id_stanza=s.id) as numero_mobili, " +
                        "(select count(*) from contenitori where id_stanza=s.id) as numero_contenitori, " +
                        "(select count(*) from oggetti where id_stanza=s.id) as numero_oggetti " +
                    "FROM stanze s " +
                    "ORDER BY s.nome";

        Cursor c = database.rawQuery(sql, null);

        int idIndex = c.getColumnIndex("id");
        int nomeIndex = c.getColumnIndex("nome");

        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            String nome = c.getString(nomeIndex);

            int numeroMobili = c.getInt(c.getColumnIndex("numero_mobili"));
            int numeroContenitori = c.getInt(c.getColumnIndex("numero_contenitori"));
            int numeroOggetti = c.getInt(c.getColumnIndex("numero_oggetti"));

            StanzaDao dao = new StanzaDao(id, nome);
            dao.setNumeroMobili(numeroMobili);
            dao.setNumeroContenitori(numeroContenitori);
            dao.setNumeroOggetti(numeroOggetti);

            result.add(dao);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce la stanza passata come argomento
     *
     * @param database
     * @param id
     * @return
     */
    public static StanzaDao getStanza(SQLiteDatabase database, long id)
    {
        StanzaDao result = null;

        Cursor c = database.rawQuery("SELECT * FROM stanze where id = " + id, null);

        int nomeIndex = c.getColumnIndex("nome");

        if (c.moveToNext())
        {
            String nome = c.getString(nomeIndex);

            result = new StanzaDao(id, nome);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce la stanza passata come argomento
     *
     * @param database
     * @param nome
     * @return
     */
    public static StanzaDao getStanzaByName(SQLiteDatabase database, String nome)
    {
        StanzaDao result = null;

        Cursor c = database.rawQuery("SELECT * FROM stanze where nome = '" + nome + "'", null);

        int idIndex = c.getColumnIndex("id");

        if (c.moveToNext())
        {
            long id = c.getLong(idIndex);

            result = new StanzaDao(id, nome);
        }

        c.close();

        return result;
    }
//endregion

//region SEZIONE mobili

    /***
     * Inserisce il mobile passata come argomento
     *
     * @param database
     * @param mobileDao
     */
    public static void insertMobile(SQLiteDatabase database, MobileDao mobileDao)
    {
        String sql = "INSERT INTO mobili (nome, immagine, id_stanza) VALUES (?, ?, ?)";

        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, mobileDao.getNome());
        stmt.bindString(2, mobileDao.getImmagine());
        stmt.bindLong(3, mobileDao.getId_stanza());
        stmt.execute();
    }

    /***
     * Update mobile
     *
     * @param database
     * @param mobileDao
     */
    public static void updateMobile(SQLiteDatabase database, MobileDao mobileDao)
    {
        //Update del mobile
        SQLiteStatement stmt = database.compileStatement("UPDATE mobili SET nome = ?, immagine = ?, id_stanza = ? WHERE id = ?");
        stmt.bindString(1, mobileDao.getNome());
        stmt.bindString(2, mobileDao.getImmagine());
        stmt.bindLong(3, mobileDao.getId_stanza());
        stmt.bindLong(4, mobileDao.getId());
        stmt.execute();

        //Devo fare update della stanza anche di tutti i contenitori ed oggetti presenti nel mobile, altrimenti potrei avere dati incongruenti
        stmt = database.compileStatement("UPDATE contenitori SET id_stanza = ? WHERE id_mobile = ?");
        stmt.bindLong(1, mobileDao.getId_stanza());
        stmt.bindLong(2, mobileDao.getId());
        stmt.execute();

        stmt = database.compileStatement("UPDATE oggetti SET id_stanza = ? WHERE id_mobile = ?");
        stmt.bindLong(1, mobileDao.getId_stanza());
        stmt.bindLong(2, mobileDao.getId());
        stmt.execute();
    }

    /***
     * Cancella il mobile passato come argomento
     *
     * @param database
     * @param id_mobile
     * @param eliminaContenuto
     */
    public static void deleteMobile(SQLiteDatabase database, long id_mobile, boolean eliminaContenuto)
    {
        database.execSQL("DELETE FROM mobili where id = " + id_mobile);

        //Vanno azzerati o eliminati gli eventuali puntamenti a questo mobile
        if(eliminaContenuto)
        {
            database.execSQL("DELETE FROM contenitori WHERE id_mobile = " + id_mobile);
            database.execSQL("DELETE FROM oggetti WHERE id_mobile = " + id_mobile);
        }else{
            database.execSQL("UPDATE contenitori SET id_mobile = -1 WHERE id_mobile = " + id_mobile);
            database.execSQL("UPDATE oggetti SET id_mobile = -1 WHERE id_mobile = " + id_mobile);
        }
    }


    /***
     * Restituisce il numero di associazioni che riguardano il mobile passato come argomento
     * @param database
     * @param idMobile
     * @return
     */
    public static int numeroAssociazioniMobile(SQLiteDatabase database, long idMobile)
    {
        int numeroAssociazioni = 0;

        String sql = "SELECT m.id, m.nome, " +
                "(select count(*) from contenitori where id_mobile=m.id) as numero_contenitori, " +
                "(select count(*) from oggetti where id_mobile=m.id) as numero_oggetti " +
                "FROM mobili m WHERE m.id = " + idMobile + " " +
                "ORDER BY m.nome";

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToNext())
        {
            int numeroContenitori = c.getInt(c.getColumnIndex("numero_contenitori"));
            int numeroOggetti = c.getInt(c.getColumnIndex("numero_oggetti"));

            numeroAssociazioni = numeroContenitori + numeroOggetti;
        }

        c.close();

        return numeroAssociazioni;
    }

    /***
     * Restituisce tutti i mobili presenti in archivio
     *
     * @param database
     * @return
     */
    public static ArrayList<MobileDao> getAllMobili(SQLiteDatabase database)
    {
        ArrayList<MobileDao> result = new ArrayList<MobileDao>();

        String sql = "SELECT m.id, m.nome, m.immagine, m.id_stanza, s.nome as nome_stanza, " +
                        "(select count(*) from contenitori where id_mobile=m.id) as numero_contenitori, " +
                        "(select count(*) from oggetti where id_mobile=m.id) as numero_oggetti " +
                     "FROM mobili m LEFT JOIN stanze s ON m.id_stanza = s.id ORDER BY m.nome";

        Cursor c = database.rawQuery(sql, null);

        int idIndex = c.getColumnIndex("id");
        int nomeIndex = c.getColumnIndex("nome");
        int immagineIndex = c.getColumnIndex("immagine");
        int idStanzaIndex = c.getColumnIndex("id_stanza");
        int nomeStanzaIndex = c.getColumnIndex("nome_stanza");

        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            long idStanza = c.getLong(idStanzaIndex);

            String nome = c.getString(nomeIndex);
            String immagine = c.getString(immagineIndex);
            String nomeStanza = "";

            if(!c.isNull(nomeStanzaIndex))
                nomeStanza = c.getString(nomeStanzaIndex);

            int numeroContenitori = c.getInt(c.getColumnIndex("numero_contenitori"));
            int numeroOggetti = c.getInt(c.getColumnIndex("numero_oggetti"));

            MobileDao dao = new MobileDao(id, nome, immagine, idStanza, nomeStanza);
            dao.setNumeroContenitori(numeroContenitori);
            dao.setNumeroOggetti(numeroOggetti);

            result.add(dao);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce tutti i mobili della stanza passata come argomento
     *
     * @param database
     * @param id_stanza
     * @return
     */
    public static ArrayList<MobileDao> getAllMobiliByStanza(SQLiteDatabase database, long id_stanza, boolean showDefaultElement)
    {
        ArrayList<MobileDao> result = new ArrayList<MobileDao>();

        String sql = "SELECT m.id, m.nome, m.immagine, m.id_stanza, s.nome as nome_stanza, " +
                        "(select count(*) from contenitori where id_mobile=m.id) as numero_contenitori, " +
                        "(select count(*) from oggetti where id_mobile=m.id) as numero_oggetti " +
                     "FROM mobili m LEFT JOIN stanze s ON m.id_stanza = s.id " +
                     "WHERE m.id_stanza = " + id_stanza + " ORDER BY m.nome";

        Cursor c = database.rawQuery(sql, null);

        int idIndex = c.getColumnIndex("id");
        int nomeIndex = c.getColumnIndex("nome");
        int immagineIndex = c.getColumnIndex("immagine");
        int idStanzaIndex = c.getColumnIndex("id_stanza");
        int nomeStanzaIndex = c.getColumnIndex("nome_stanza");

        if(showDefaultElement) //Se true, inserisco il record di default
            result.add(new MobileDao(-1, "-", "", -1, ""));

        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            long idStanza = c.getLong(idStanzaIndex);

            String nome = c.getString(nomeIndex);
            String immagine = c.getString(immagineIndex);
            String nomeStanza = "";

            if(!c.isNull(nomeStanzaIndex))
                nomeStanza = c.getString(nomeStanzaIndex);

            int numeroContenitori = c.getInt(c.getColumnIndex("numero_contenitori"));
            int numeroOggetti = c.getInt(c.getColumnIndex("numero_oggetti"));

            MobileDao dao = new MobileDao(id, nome, immagine, idStanza, nomeStanza);
            dao.setNumeroContenitori(numeroContenitori);
            dao.setNumeroOggetti(numeroOggetti);

            result.add(dao);
        }

        c.close();

        return result;
    }


    /***
     * Restituisce il mobile passato come argomento
     * @param database
     * @param id
     * @return
     */
    public static MobileDao getMobile(SQLiteDatabase database, long id)
    {
        MobileDao result = null;

        Cursor c = database.rawQuery("SELECT * FROM mobili m LEFT JOIN stanze s ON m.id_stanza = s.id where m.id = " + id, null);

        int nomeIndex = c.getColumnIndex("nome");
        int immagineIndex = c.getColumnIndex("immagine");
        int idStanzaIndex = c.getColumnIndex("id_stanza");
        int nomeStanzaIndex = c.getColumnIndex("nome_stanza");

        if (c.moveToNext())
        {
            long idStanza = c.getLong(idStanzaIndex);

            String nome = c.getString(nomeIndex);
            String immagine = c.getString(immagineIndex);
            String nomeStanza = "";

            if(!c.isNull(nomeStanzaIndex))
                c.getString(nomeStanzaIndex);

            result = new MobileDao(id, nome, immagine, idStanza, nomeStanza);
        }

        c.close();

        return result;
    }
//endregion

//region SEZIONE contenitori

    /***
     * Inserisce il contenitore passata come argomento
     *
     * @param database
     * @param contenitoreDao
     */
    public static void insertContenitore(SQLiteDatabase database, ContenitoreDao contenitoreDao)
    {
        String sql = "INSERT INTO contenitori (nome, immagine, id_categoria, id_stanza, id_mobile) VALUES (?, ?, ?, ?, ?)";

        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, contenitoreDao.getNome());
        stmt.bindString(2, contenitoreDao.getImmagine());
        stmt.bindLong(3, contenitoreDao.getId_categoria());
        stmt.bindLong(4, contenitoreDao.getId_stanza());
        stmt.bindLong(5, contenitoreDao.getId_mobile());
        stmt.execute();
    }

    /***
     * Update contenitore
     *
     * @param database
     * @param contenitoreDao
     * @param propagaCategoria
     */
    public static void updateContenitore(SQLiteDatabase database, ContenitoreDao contenitoreDao, boolean propagaCategoria)
    {
        SQLiteStatement stmt = database.compileStatement("UPDATE contenitori SET nome = ?, immagine = ?, id_categoria = ?, id_stanza = ?, id_mobile = ? WHERE id = ?");
        stmt.bindString(1, contenitoreDao.getNome());
        stmt.bindString(2, contenitoreDao.getImmagine());
        stmt.bindLong(3, contenitoreDao.getId_categoria());
        stmt.bindLong(4, contenitoreDao.getId_stanza());
        stmt.bindLong(5, contenitoreDao.getId_mobile());
        stmt.bindLong(6, contenitoreDao.getId());
        stmt.execute();

        //Devo fare update della stanza e del mobile anche di tutti gli oggetti presenti nel contenitore, altrimenti potrei avere dati incongruenti
        stmt = database.compileStatement("UPDATE oggetti SET id_stanza = ? WHERE id_contenitore = ?");
        stmt.bindLong(1, contenitoreDao.getId_stanza());
        stmt.bindLong(2, contenitoreDao.getId());
        stmt.execute();

        stmt = database.compileStatement("UPDATE oggetti SET id_mobile = ? WHERE id_contenitore = ?");
        stmt.bindLong(1, contenitoreDao.getId_mobile());
        stmt.bindLong(2, contenitoreDao.getId());
        stmt.execute();

        //Se l'utente lo ha chiesto, propago la categoria scelta per il contenitore a tutti i suoi oggetti
        if(propagaCategoria)
        {
            stmt = database.compileStatement("UPDATE oggetti SET id_categoria = ? WHERE id_contenitore = ?");
            stmt.bindLong(1, contenitoreDao.getId_categoria());
            stmt.bindLong(2, contenitoreDao.getId());
            stmt.execute();
        }
    }

    /***
     * Cancella il contenitore passato come argomento
     *
     * @param database
     * @param id_contenitore
     */
    public static void deleteContenitore(SQLiteDatabase database, long id_contenitore, boolean eliminaContenuto)
    {
        database.execSQL("DELETE FROM contenitori where id = " + id_contenitore);


        //Vanno azzerati o eliminati gli eventuali puntamenti a questo contenitore
        if(eliminaContenuto)
            database.execSQL("DELETE FROM oggetti WHERE id_contenitore = " + id_contenitore);
        else
            database.execSQL("UPDATE oggetti SET id_contenitore = -1 WHERE id_contenitore = " + id_contenitore);
    }


    /**
     * Restituisce il numero di oggetti che referenziano il contenitore passato come argomento
     *
     * @param database
     * @param idContenitore
     * @return
     */
    public static int numeroAssociazioniContenitore(SQLiteDatabase database, long idContenitore)
    {
        int numeroAssociazioni = 0;

        String sql = "select count(*) as numero_oggetti from oggetti where id_contenitore=" + idContenitore;

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToNext())
        {
            numeroAssociazioni = c.getInt(c.getColumnIndex("numero_oggetti"));
        }

        c.close();

        return numeroAssociazioni;
    }

    /***
     * Restituisce tutti i contenitori presenti in archivio
     *
     * @param database
     * @return
     */
    public static ArrayList<ContenitoreDao> getAllContenitori(SQLiteDatabase database)
    {
        ArrayList<ContenitoreDao> result = new ArrayList<ContenitoreDao>();

        String sql = "SELECT c.id, c.nome, c.immagine, c.id_categoria, c.id_stanza, c.id_mobile, " +
                        "m.nome as nome_mobile, s.nome as nome_stanza, cat.nome as nome_categoria, " +
                        "(select count(*) from oggetti where id_contenitore=c.id) as numero_oggetti " +
                        "FROM contenitori c " +
                        "LEFT JOIN mobili m ON c.id_mobile = m.id " +
                        "LEFT JOIN stanze s ON c.id_stanza = s.id " +
                        "LEFT JOIN categorie cat ON c.id_categoria = cat.id " +
                        "ORDER BY c.nome";

        Cursor c = database.rawQuery(sql, null);

        int idIndex = c.getColumnIndex("id");
        int nomeIndex = c.getColumnIndex("nome");
        int immagineIndex = c.getColumnIndex("immagine");
        int idMobileIndex = c.getColumnIndex("id_mobile");
        int nomeMobileIndex = c.getColumnIndex("nome_mobile");
        int idStanzaIndex = c.getColumnIndex("id_stanza");
        int nomeStanzaIndex = c.getColumnIndex("nome_stanza");
        int idCategoriaIndex = c.getColumnIndex("id_categoria");
        int nomeCategoriaIndex = c.getColumnIndex("nome_categoria");

        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            long idStanza = c.getLong(idStanzaIndex);
            long idMobile = c.getLong(idMobileIndex);
            long idCategoria = c.getLong(idCategoriaIndex);

            String nome = c.getString(nomeIndex);
            String immagine = c.getString(immagineIndex);

            String nomeMobile = "";

            if(!c.isNull(nomeMobileIndex))
                nomeMobile = c.getString(nomeMobileIndex);

            String nomeStanza = "";

            if(!c.isNull(nomeStanzaIndex))
                nomeStanza = c.getString(nomeStanzaIndex);

            String nomeCategoria = "";

            if(!c.isNull(nomeCategoriaIndex))
                nomeCategoria = c.getString(nomeCategoriaIndex);

            int numeroOggetti = c.getInt(c.getColumnIndex("numero_oggetti"));

            ContenitoreDao dao = new ContenitoreDao(id, nome, immagine, idCategoria, nomeCategoria,
                    idStanza, nomeStanza, idMobile, nomeMobile);

            dao.setNumeroOggetti(numeroOggetti);

            result.add(dao);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce tutti i contenitori presenti nel luogo specificato
     *
     * @param database
     * @param location
     * @return
     */
    public static ArrayList<ContenitoreDao> getAllContenitoriByLocation(SQLiteDatabase database, LocationDao location, boolean showDefaultElement)
    {
        ArrayList<ContenitoreDao> result = new ArrayList<ContenitoreDao>();

        long id_stanza = location.getId_stanza();
        long id_mobile = location.getId_mobile();
        long id_categoria = location.getId_categoria();

        String sql = "SELECT c.id, c.nome, c.immagine, c.id_categoria, c.id_stanza, c.id_mobile, " +
                "m.nome as nome_mobile, s.nome as nome_stanza, cat.nome as nome_categoria, " +
                "(select count(*) from oggetti where id_contenitore=c.id) as numero_oggetti " +
                "FROM contenitori c " +
                "LEFT JOIN mobili m ON c.id_mobile = m.id " +
                "LEFT JOIN stanze s ON c.id_stanza = s.id " +
                "LEFT JOIN categorie cat ON c.id_categoria = cat.id " +
                "WHERE " +
                "(" + id_categoria + " = -1 OR c.id_categoria = " + id_categoria + " ) AND " +
                "(" + id_stanza + " = -1 OR c.id_stanza = " + id_stanza + " ) AND " +
                "(" + id_mobile + " = -1 OR c.id_mobile = " + id_mobile + " ) " +
                "ORDER BY c.nome";


        if(showDefaultElement) //Se true, inserisco il record di default
            result.add(new ContenitoreDao(-1, "-", "", -1, "", -1, ""));


        Cursor c = database.rawQuery(sql, null);

        int idIndex = c.getColumnIndex("id");
        int nomeIndex = c.getColumnIndex("nome");
        int immagineIndex = c.getColumnIndex("immagine");
        int idMobileIndex = c.getColumnIndex("id_mobile");
        int nomeMobileIndex = c.getColumnIndex("nome_mobile");
        int idStanzaIndex = c.getColumnIndex("id_stanza");
        int nomeStanzaIndex = c.getColumnIndex("nome_stanza");
        int idCategoriaIndex = c.getColumnIndex("id_categoria");
        int nomeCategoriaIndex = c.getColumnIndex("nome_categoria");

        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            long idStanza = c.getLong(idStanzaIndex);
            long idMobile = c.getLong(idMobileIndex);
            long idCategoria = c.getLong(idCategoriaIndex);

            String nome = c.getString(nomeIndex);
            String immagine = c.getString(immagineIndex);

            String nomeMobile = "";

            if(!c.isNull(nomeMobileIndex))
                nomeMobile = c.getString(nomeMobileIndex);

            String nomeStanza = "";

            if(!c.isNull(nomeStanzaIndex))
                nomeStanza = c.getString(nomeStanzaIndex);

            String nomeCategoria = "";

            if(!c.isNull(nomeCategoriaIndex))
                nomeCategoria = c.getString(nomeCategoriaIndex);

            int numeroOggetti = c.getInt(c.getColumnIndex("numero_oggetti"));

            ContenitoreDao dao = new ContenitoreDao(id, nome, immagine, idCategoria, nomeCategoria,
                    idStanza, nomeStanza, idMobile, nomeMobile);

            dao.setNumeroOggetti(numeroOggetti);

            result.add(dao);
        }

        c.close();

        return result;
    }

    /***
     * Mostra SOLO i contenitori direttamente presenti in una stanza (quindi non contenuti in un mobile)
     * @param database
     * @param id_stanza
     * @return
     */
    public static ArrayList<ContenitoreDao> getAllContenitoriByStanza(SQLiteDatabase database, long id_stanza)
    {
        ArrayList<ContenitoreDao> result = new ArrayList<ContenitoreDao>();

        String sql = "SELECT c.id, c.nome, c.immagine, c.id_categoria, c.id_stanza, c.id_mobile, " +
                "m.nome as nome_mobile, s.nome as nome_stanza, cat.nome as nome_categoria " +
                "FROM contenitori c " +
                "LEFT JOIN mobili m ON c.id_mobile = m.id " +
                "LEFT JOIN stanze s ON c.id_stanza = s.id " +
                "LEFT JOIN categorie cat ON c.id_categoria = cat.id " +
                "WHERE " +
                "(" + id_stanza + " = -1 OR c.id_stanza = " + id_stanza + " ) AND " +
                "c.id_mobile = -1 " +
                "ORDER BY c.nome";


        Cursor c = database.rawQuery(sql, null);

        int idIndex = c.getColumnIndex("id");
        int nomeIndex = c.getColumnIndex("nome");
        int immagineIndex = c.getColumnIndex("immagine");
        int idMobileIndex = c.getColumnIndex("id_mobile");
        int nomeMobileIndex = c.getColumnIndex("nome_mobile");
        int idStanzaIndex = c.getColumnIndex("id_stanza");
        int nomeStanzaIndex = c.getColumnIndex("nome_stanza");
        int idCategoriaIndex = c.getColumnIndex("id_categoria");
        int nomeCategoriaIndex = c.getColumnIndex("nome_categoria");

        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            long idStanza = c.getLong(idStanzaIndex);
            long idMobile = c.getLong(idMobileIndex);
            long idCategoria = c.getLong(idCategoriaIndex);

            String nome = c.getString(nomeIndex);
            String immagine = c.getString(immagineIndex);

            String nomeMobile = "";

            if(!c.isNull(nomeMobileIndex))
                nomeMobile = c.getString(nomeMobileIndex);

            String nomeStanza = "";

            if(!c.isNull(nomeStanzaIndex))
                nomeStanza = c.getString(nomeStanzaIndex);

            String nomeCategoria = "";

            if(!c.isNull(nomeCategoriaIndex))
                nomeCategoria = c.getString(nomeCategoriaIndex);


            ContenitoreDao dao = new ContenitoreDao(id, nome, immagine, idCategoria, nomeCategoria,
                    idStanza, nomeStanza, idMobile, nomeMobile);

            result.add(dao);
        }

        c.close();

        return result;
    }


    /***
     * Restituisce il contenitore passato come argomento
     * @param database
     * @param id
     * @return
     */
    public static ContenitoreDao getContenitore(SQLiteDatabase database, long id)
    {
        ContenitoreDao result = null;

        String sql = "SELECT c.id, c.nome, c.immagine, c.id_categoria, c.id_stanza, c.id_mobile, " +
                "m.nome as nome_mobile, s.nome as nome_stanza, cat.nome as nome_categoria " +
                "FROM contenitori c " +
                "LEFT JOIN mobili m ON c.id_mobile = m.id " +
                "LEFT JOIN stanze s ON c.id_stanza = s.id " +
                "LEFT JOIN categorie cat ON c.id_categoria = cat.id " +
                "WHERE c.id = " + id;

        Cursor c = database.rawQuery(sql, null);

        int idIndex = c.getColumnIndex("id");
        int nomeIndex = c.getColumnIndex("nome");
        int immagineIndex = c.getColumnIndex("immagine");
        int idMobileIndex = c.getColumnIndex("id_mobile");
        int nomeMobileIndex = c.getColumnIndex("nome_mobile");
        int idStanzaIndex = c.getColumnIndex("id_stanza");
        int nomeStanzaIndex = c.getColumnIndex("nome_stanza");
        int idCategoriaIndex = c.getColumnIndex("id_categoria");
        int nomeCategoriaIndex = c.getColumnIndex("nome_categoria");


        if (c.moveToNext())
        {
            long idStanza = c.getLong(idStanzaIndex);
            long idMobile = c.getLong(idMobileIndex);
            long idCategoria = c.getLong(idCategoriaIndex);

            String nome = c.getString(nomeIndex);
            String immagine = c.getString(immagineIndex);

            String nomeMobile = "";

            if(!c.isNull(nomeMobileIndex))
                nomeMobile = c.getString(nomeMobileIndex);

            String nomeStanza = "";

            if(!c.isNull(nomeStanzaIndex))
                nomeStanza = c.getString(nomeStanzaIndex);

            String nomeCategoria = "";

            if(!c.isNull(nomeCategoriaIndex))
                nomeCategoria = c.getString(nomeCategoriaIndex);


            result = new ContenitoreDao(id, nome, immagine, idCategoria, nomeCategoria,
                    idStanza, nomeStanza, idMobile, nomeMobile);
        }

        c.close();

        return result;
    }
//endregion

//region SEZIONE oggetti

    /***
     * Inserisce l'oggetto passato come argomento
     *
     * @param database
     * @param oggettoDao
     */
    public static void insertOggetto(SQLiteDatabase database, OggettoDao oggettoDao)
    {
        String sql = "INSERT INTO oggetti (nome, descrizione, immagine, id_categoria, id_stanza, id_mobile, id_contenitore) VALUES (?, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, oggettoDao.getNome());
        stmt.bindString(2, oggettoDao.getDescrizione());
        stmt.bindString(3, oggettoDao.getImmagine());
        stmt.bindLong(4, oggettoDao.getId_categoria());
        stmt.bindLong(5, oggettoDao.getId_stanza());
        stmt.bindLong(6, oggettoDao.getId_mobile());
        stmt.bindLong(7, oggettoDao.getId_contenitore());
        stmt.execute();
    }

    /***
     * Update contenitore
     *
     * @param database
     * @param oggettoDao
     */
    public static void updateOggetto(SQLiteDatabase database, OggettoDao oggettoDao)
    {
        SQLiteStatement stmt = database.compileStatement("UPDATE oggetti " +
                "SET nome = ?, descrizione = ?, immagine = ?, id_categoria = ?, id_stanza = ?, id_mobile = ?, id_contenitore = ? WHERE id = ?");

        stmt.bindString(1, oggettoDao.getNome());
        stmt.bindString(2, oggettoDao.getDescrizione());
        stmt.bindString(3, oggettoDao.getImmagine());
        stmt.bindLong(4, oggettoDao.getId_categoria());
        stmt.bindLong(5, oggettoDao.getId_stanza());
        stmt.bindLong(6, oggettoDao.getId_mobile());
        stmt.bindLong(7, oggettoDao.getId_contenitore());
        stmt.bindLong(8, oggettoDao.getId());

        stmt.execute();
    }

    /***
     * Cancella l'oggetto passato come argomento
     *
     * @param database
     * @param id
     */
    public static void deleteOggetto(SQLiteDatabase database, long id)
    {
        database.execSQL("DELETE FROM oggetti where id = " + id);
    }

    /***
     * Restituisce tutti gli oggetti presenti nel sistema
     *
     * @param database
     * @return
     */
    public static ArrayList<OggettoDao> getAllOggetti(SQLiteDatabase database)
    {
        ArrayList<OggettoDao> result = new ArrayList<OggettoDao>();

        String sql = "SELECT o.id, o.nome, o.descrizione, o.immagine, o.id_categoria, o.id_stanza, o.id_mobile, o.id_contenitore, " +
                "c.nome as nome_contenitore, m.nome as nome_mobile, s.nome as nome_stanza, cat.nome as nome_categoria " +
                "FROM oggetti o " +
                "LEFT JOIN contenitori c ON o.id_contenitore = c.id " +
                "LEFT JOIN mobili m ON o.id_mobile = m.id " +
                "LEFT JOIN stanze s ON o.id_stanza = s.id " +
                "LEFT JOIN categorie cat ON o.id_categoria = cat.id " +
                "ORDER BY o.nome";

        Cursor c = database.rawQuery(sql, null);

        int nomeContenitoreIndex = c.getColumnIndex("nome_contenitore");
        int idMobileIndex = c.getColumnIndex("id_mobile");
        int nomeMobileIndex = c.getColumnIndex("nome_mobile");
        int idStanzaIndex = c.getColumnIndex("id_stanza");
        int nomeStanzaIndex = c.getColumnIndex("nome_stanza");
        int idCategoriaIndex = c.getColumnIndex("id_categoria");
        int nomeCategoriaIndex = c.getColumnIndex("nome_categoria");

        while (c.moveToNext())
        {
            long id = c.getLong(c.getColumnIndex("id"));
            long idStanza = c.getLong(idStanzaIndex);
            long idContenitore = c.getLong(c.getColumnIndex("id_contenitore"));
            long idMobile = c.getLong(idMobileIndex);
            long idCategoria = c.getLong(idCategoriaIndex);

            String nome = c.getString(c.getColumnIndex("nome"));
            String descrizione = c.getString(c.getColumnIndex("descrizione"));
            String immagine = c.getString(c.getColumnIndex("immagine"));

            String nomeContenitore = "";

            if(!c.isNull(nomeContenitoreIndex))
                nomeContenitore = c.getString(nomeContenitoreIndex);

            String nomeMobile = "";

            if(!c.isNull(nomeMobileIndex))
                nomeMobile = c.getString(nomeMobileIndex);

            String nomeStanza = "";

            if(!c.isNull(nomeStanzaIndex))
                nomeStanza = c.getString(nomeStanzaIndex);

            String nomeCategoria = "";

            if(!c.isNull(nomeCategoriaIndex))
                nomeCategoria = c.getString(nomeCategoriaIndex);


            OggettoDao dao = new OggettoDao(id, nome, descrizione, immagine, idCategoria, nomeCategoria, idStanza, nomeStanza,
                idMobile, nomeMobile, idContenitore, nomeContenitore);

            result.add(dao);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce tutti gli oggetti che si trovano nel posto specificato
     *
     * @param database
     * @param location
     * @return
     */
    public static ArrayList<OggettoDao> getAllOggettiByLocation(SQLiteDatabase database, LocationDao location)
    {
        ArrayList<OggettoDao> result = new ArrayList<OggettoDao>();

        long id_categoria = location.getId_categoria();
        long id_stanza = location.getId_stanza();
        long id_mobile = location.getId_mobile();
        long id_contenitore = location.getId_contenitore();


        String sql = "SELECT o.id, o.nome, o.descrizione, o.immagine, o.id_categoria, o.id_stanza, o.id_mobile, o.id_contenitore, " +
                "c.nome as nome_contenitore, m.nome as nome_mobile, s.nome as nome_stanza, cat.nome as nome_categoria " +
                "FROM oggetti o " +
                "LEFT JOIN contenitori c ON o.id_contenitore = c.id " +
                "LEFT JOIN mobili m ON o.id_mobile = m.id " +
                "LEFT JOIN stanze s ON o.id_stanza = s.id " +
                "LEFT JOIN categorie cat ON o.id_categoria = cat.id " +
                "WHERE " +
                "(" + id_categoria + " = -1 OR o.id_categoria = " + id_categoria + " ) AND " +
                "(" + id_stanza + " = -1 OR o.id_stanza = " + id_stanza + " ) AND " +
                "(" + id_mobile + " = -1 OR o.id_mobile = " + id_mobile + " ) AND " +
                "(" + id_contenitore + " = -1 OR o.id_contenitore = " + id_contenitore + " ) " +
                "ORDER BY o.nome";

        Cursor c = database.rawQuery(sql, null);


        int idContenitoreIndex = c.getColumnIndex("id_contenitore");
        int nomeContenitoreIndex = c.getColumnIndex("nome_contenitore");
        int idMobileIndex = c.getColumnIndex("id_mobile");
        int nomeMobileIndex = c.getColumnIndex("nome_mobile");
        int idStanzaIndex = c.getColumnIndex("id_stanza");
        int nomeStanzaIndex = c.getColumnIndex("nome_stanza");
        int idCategoriaIndex = c.getColumnIndex("id_categoria");
        int nomeCategoriaIndex = c.getColumnIndex("nome_categoria");

        while (c.moveToNext())
        {
            long id = c.getLong(c.getColumnIndex("id"));
            long idStanza = c.getLong(idStanzaIndex);
            long idContenitore = c.getLong(idContenitoreIndex);
            long idMobile = c.getLong(idMobileIndex);
            long idCategoria = c.getLong(idCategoriaIndex);

            String nome = c.getString(c.getColumnIndex("nome"));
            String descrizione = c.getString(c.getColumnIndex("descrizione"));
            String immagine = c.getString(c.getColumnIndex("immagine"));

            String nomeContenitore = "";

            if(!c.isNull(nomeContenitoreIndex))
                nomeContenitore = c.getString(nomeContenitoreIndex);

            String nomeMobile = "";

            if(!c.isNull(nomeMobileIndex))
                nomeMobile = c.getString(nomeMobileIndex);

            String nomeStanza = "";

            if(!c.isNull(nomeStanzaIndex))
                nomeStanza = c.getString(nomeStanzaIndex);

            String nomeCategoria = "";

            if(!c.isNull(nomeCategoriaIndex))
                nomeCategoria = c.getString(nomeCategoriaIndex);


            OggettoDao dao = new OggettoDao(id, nome, descrizione, immagine, idCategoria, nomeCategoria, idStanza, nomeStanza,
                    idMobile, nomeMobile, idContenitore, nomeContenitore);

            result.add(dao);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce l'oggetto passato come argomento
     *
     * @param database
     * @param id
     * @return
     */
    public static OggettoDao getOggetto(SQLiteDatabase database, long id)
    {
        OggettoDao result = null;

        String sql = "SELECT o.id, o.nome, o.descrizione, o.immagine, o.id_categoria, o.id_stanza, o.id_mobile, o.id_contenitore, " +
                "c.nome as nome_contenitore, m.nome as nome_mobile, s.nome as nome_stanza, cat.nome as nome_categoria " +
                "FROM oggetti o " +
                "LEFT JOIN contenitori c ON o.id_contenitore = c.id " +
                "LEFT JOIN mobili m ON c.id_mobile = m.id " +
                "LEFT JOIN stanze s ON c.id_stanza = s.id " +
                "LEFT JOIN categorie cat ON c.id_categoria = cat.id " +
                "WHERE o.id = " + id +
                " ORDER BY o.nome";

        Cursor c = database.rawQuery(sql, null);


        int idContenitoreIndex = c.getColumnIndex("id_contenitore");
        int nomeContenitoreIndex = c.getColumnIndex("nome_contenitore");
        int idMobileIndex = c.getColumnIndex("id_mobile");
        int nomeMobileIndex = c.getColumnIndex("nome_mobile");
        int idStanzaIndex = c.getColumnIndex("id_stanza");
        int nomeStanzaIndex = c.getColumnIndex("nome_stanza");
        int idCategoriaIndex = c.getColumnIndex("id_categoria");
        int nomeCategoriaIndex = c.getColumnIndex("nome_categoria");

        if (c.moveToNext())
        {
            long idStanza = c.getLong(idStanzaIndex);
            long idContenitore = c.getLong(idContenitoreIndex);
            long idMobile = c.getLong(idMobileIndex);
            long idCategoria = c.getLong(idCategoriaIndex);

            String nome = c.getString(c.getColumnIndex("nome"));
            String descrizione = c.getString(c.getColumnIndex("descrizione"));
            String immagine = c.getString(c.getColumnIndex("immagine"));

            String nomeContenitore = "";

            if(!c.isNull(nomeContenitoreIndex))
                nomeContenitore = c.getString(nomeContenitoreIndex);

            String nomeMobile = "";

            if(!c.isNull(nomeMobileIndex))
                nomeMobile = c.getString(nomeMobileIndex);

            String nomeStanza = "";

            if(!c.isNull(nomeStanzaIndex))
                nomeStanza = c.getString(nomeStanzaIndex);

            String nomeCategoria = "";

            if(!c.isNull(nomeCategoriaIndex))
                nomeCategoria = c.getString(nomeCategoriaIndex);


            result = new OggettoDao(id, nome, descrizione, immagine, idCategoria, nomeCategoria, idStanza, nomeStanza,
                    idMobile, nomeMobile, idContenitore, nomeContenitore);

        }

        c.close();

        return result;
    }
//endregion

}

