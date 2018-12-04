package magazzino.bobo.com.magazzinodomestico.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import magazzino.bobo.com.magazzinodomestico.db.dao.CategoriaDao;
import magazzino.bobo.com.magazzinodomestico.db.dao.ContenitoreDao;
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
        sql = "CREATE TABLE IF NOT EXISTS stanze (id INTEGER PRIMARY KEY, nome VARCHAR)";
        database.execSQL(sql);

        /*
            TABELLA MOBILI
         */
        sql = "CREATE TABLE IF NOT EXISTS mobili (id INTEGER PRIMARY KEY, nome VARCHAR, immagine VARCHAR, id_stanza INTEGER)";
        database.execSQL(sql);

         /*
            TABELLA CONTENITORI
         */
        sql = "CREATE TABLE IF NOT EXISTS contenitori (id INTEGER PRIMARY KEY, nome VARCHAR, immagine VARCHAR," +
                " id_categoria INTEGER, id_stanza INTEGER, id_mobile INTEGER)";
        database.execSQL(sql);

        /*
            TABELLA OGGETTI
         */
        sql = "CREATE TABLE IF NOT EXISTS oggetti (id INTEGER PRIMARY KEY, nome VARCHAR, immagine VARCHAR, " +
                "id_categoria INTEGER, id_stanza INTEGER, id_mobile INTEGER, id_contenitore INTEGER)";
        database.execSQL(sql);
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
        SQLiteStatement stmt = database.compileStatement("UPDATE mobili SET nome = ?, immagine = ?, id_stanza = ? WHERE id = ?");
        stmt.bindString(1, mobileDao.getNome());
        stmt.bindString(2, mobileDao.getImmagine());
        stmt.bindLong(3, mobileDao.getId_stanza());
        stmt.bindLong(4, mobileDao.getId());

        stmt.execute();
    }

    /***
     * Cancella il mobile passato come argomento
     *
     * @param database
     * @param id_mobile
     */
    public static void deleteMobile(SQLiteDatabase database, long id_mobile)
    {
        database.execSQL("DELETE FROM mobili where id = " + id_mobile);

        //Vanno azzerati gli eventuali puntamenti a questa categoria
        database.execSQL("UPDATE contenitori SET id_mobile = -1 WHERE id_mobile = " + id_mobile);
        database.execSQL("UPDATE oggetti SET id_mobile = -1 WHERE id_mobile = " + id_mobile);
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
     */
    public static void updateContenitore(SQLiteDatabase database, ContenitoreDao contenitoreDao)
    {
        SQLiteStatement stmt = database.compileStatement("UPDATE contenitori SET nome = ?, immagine = ?, id_categoria = ?, id_stanza = ?, id_mobile = ? WHERE id = ?");
        stmt.bindString(1, contenitoreDao.getNome());
        stmt.bindString(2, contenitoreDao.getImmagine());
        stmt.bindLong(3, contenitoreDao.getId_categoria());
        stmt.bindLong(4, contenitoreDao.getId_stanza());
        stmt.bindLong(5, contenitoreDao.getId_mobile());
        stmt.bindLong(6, contenitoreDao.getId());

        stmt.execute();
    }

    /***
     * Cancella il contenitore passato come argomento
     *
     * @param database
     * @param id_contenitore
     */
    public static void deleteContenitore(SQLiteDatabase database, long id_contenitore)
    {
        database.execSQL("DELETE FROM contenitori where id = " + id_contenitore);

        //Vanno azzerati gli eventuali puntamenti a questa categoria
        database.execSQL("UPDATE oggetti SET id_contenitore = -1 WHERE id_contenitore = " + id_contenitore);
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
        String sql = "INSERT INTO oggetti (nome, immagine, id_categoria, id_stanza, id_mobile, id_contenitore) VALUES (?, ?, ?, ?, ?, ?)";

        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, oggettoDao.getNome());
        stmt.bindString(2, oggettoDao.getImmagine());
        stmt.bindLong(3, oggettoDao.getId_categoria());
        stmt.bindLong(4, oggettoDao.getId_stanza());
        stmt.bindLong(5, oggettoDao.getId_mobile());
        stmt.bindLong(6, oggettoDao.getId_contenitore());
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
                "SET nome = ?, immagine = ?, id_categoria = ?, id_stanza = ?, id_mobile = ?, id_contenitore = ? WHERE id = ?");

        stmt.bindString(1, oggettoDao.getNome());
        stmt.bindString(2, oggettoDao.getImmagine());
        stmt.bindLong(3, oggettoDao.getId_categoria());
        stmt.bindLong(4, oggettoDao.getId_stanza());
        stmt.bindLong(5, oggettoDao.getId_mobile());
        stmt.bindLong(6, oggettoDao.getId_contenitore());
        stmt.bindLong(7, oggettoDao.getId());

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
//endregion

}

