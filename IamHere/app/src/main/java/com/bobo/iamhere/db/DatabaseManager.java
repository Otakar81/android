package com.bobo.iamhere.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

public class DatabaseManager {

    public static String NOME_LOCATION_VELOCE = "Luogo veloce";


    /***
     * Crea le tabelle utilizzate dall'applicazione
     * @param database
     */
    public static void createTables(SQLiteDatabase database)
    {
        //Creo la tabella dei luoghi
        String sql = "CREATE TABLE IF NOT EXISTS location (id INTEGER PRIMARY KEY, alias VARCHAR, latitudine VARCHAR, longitudine VARCHAR, " +
                "nazione VARCHAR, regione VARCHAR, provincia VARCHAR, comune VARCHAR, cap VARCHAR, indirizzo VARCHAR, is_preferito INTEGER(1))";

        database.execSQL(sql);

        /* Tmp
        sql = "ALTER TABLE location ADD COLUMN is_preferito INTEGER(1)";
        database.execSQL(sql);
        */

        //Creo la tabella delle note
        sql = "CREATE TABLE IF NOT EXISTS note (id INTEGER PRIMARY KEY, titolo VARCHAR, testo VARCHAR, id_location INTEGER)";

        database.execSQL(sql);
    }


    /***
     * Inserisce la location passata come argomento
     *
     * @param database
     * @param location
     */
    public static void insertLocation(SQLiteDatabase database, LocationDao location)
    {
        String sql = "INSERT INTO location (alias, latitudine, longitudine, nazione, regione, provincia, comune, cap, indirizzo, is_preferito)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, location.getAlias());
        stmt.bindString(2, location.getLatitudine() + "");
        stmt.bindString(3, location.getLongitudine() + "");
        stmt.bindString(4, location.getNazione());
        stmt.bindString(5, location.getRegione());
        stmt.bindString(6, location.getProvincia());
        stmt.bindString(7, location.getComune());
        stmt.bindString(8, location.getCap());
        stmt.bindString(9, location.getIndirizzo());
        stmt.bindLong(10, location.getLuogoPreferito());
        stmt.execute();
    }

    /***
     * Update dell'alias della location
     *
     * @param database
     * @param id
     * @param alias
     */
    public static void updateAlias(SQLiteDatabase database, long id, String alias)
    {
        SQLiteStatement stmt = database.compileStatement("UPDATE location SET alias = ? WHERE id = ?");
        stmt.bindString(1, alias);
        stmt.bindLong(2, id);

        stmt.execute();
    }

    public static void updateLuogoPreferito(SQLiteDatabase database, long id, int isLuogoPreferito)
    {
        SQLiteStatement stmt = database.compileStatement("UPDATE location SET is_preferito = ? WHERE id = ?");
        stmt.bindLong(1, isLuogoPreferito);
        stmt.bindLong(2, id);

        stmt.execute();
    }

    /***
     * Cancella la location passata come argomento
     *
     * @param database
     * @param id
     */
    public static void deleteLocation(SQLiteDatabase database, long id)
    {
        database.execSQL("DELETE FROM location where id = " + id);
    }

    /***
     * Restituisce tutte le location salvate su database
     *
     * @param database
     * @return
     */
    public static ArrayList<LocationDao> getAllLocation(SQLiteDatabase database, boolean soloPreferite)
    {
        ArrayList<LocationDao> result = new ArrayList<LocationDao>();

        String sql = "SELECT id, alias, latitudine, longitudine, nazione, regione, provincia, comune, cap, indirizzo, is_preferito FROM location";

        if(soloPreferite)
            sql += " WHERE is_preferito = 1";

        Cursor c = database.rawQuery(sql, null);


        int idIndex = c.getColumnIndex("id");
        int aliasIndex = c.getColumnIndex("alias");
        int latitudineIndex = c.getColumnIndex("latitudine");
        int longitudineIndex = c.getColumnIndex("longitudine");
        int nazioneIndex = c.getColumnIndex("nazione");
        int regioneIndex = c.getColumnIndex("regione");
        int provinciaIndex = c.getColumnIndex("provincia");
        int comuneIndex = c.getColumnIndex("comune");
        int capIndex = c.getColumnIndex("cap");
        int indirizzoIndex = c.getColumnIndex("indirizzo");
        int isLuogoPreferitoIndex = c.getColumnIndex("is_preferito");


        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            String alias = c.getString(aliasIndex);
            double latitudine = Double.parseDouble(c.getString(latitudineIndex));
            double longitudine = Double.parseDouble(c.getString(longitudineIndex));
            String nazione = c.getString(nazioneIndex);
            String regione = c.getString(regioneIndex);
            String provincia = c.getString(provinciaIndex);
            String comune = c.getString(comuneIndex);
            String cap = c.getString(capIndex);
            String indirizzo = c.getString(indirizzoIndex);
            int isLuogoPreferito = c.getInt(isLuogoPreferitoIndex);

            LocationDao locationDao = new LocationDao(id, alias, latitudine, longitudine, nazione, regione, provincia, comune, cap, indirizzo);
            locationDao.setLuogoPreferito(isLuogoPreferito);

            result.add(locationDao);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce la location passata come argomento
     *
     * @param database
     * @param id
     * @return
     */
    public static LocationDao getLocation(SQLiteDatabase database, long id)
    {
        LocationDao result = null;

        Cursor c = database.rawQuery("SELECT * FROM location where id = " + id, null);


        int aliasIndex = c.getColumnIndex("alias");
        int latitudineIndex = c.getColumnIndex("latitudine");
        int longitudineIndex = c.getColumnIndex("longitudine");
        int nazioneIndex = c.getColumnIndex("nazione");
        int regioneIndex = c.getColumnIndex("regione");
        int provinciaIndex = c.getColumnIndex("provincia");
        int comuneIndex = c.getColumnIndex("comune");
        int capIndex = c.getColumnIndex("cap");
        int indirizzoIndex = c.getColumnIndex("indirizzo");
        int isLuogoPreferitoIndex = c.getColumnIndex("is_preferito");


        if (c.moveToNext())
        {
            String alias = c.getString(aliasIndex);
            double latitudine = Double.parseDouble(c.getString(latitudineIndex));
            double longitudine = Double.parseDouble(c.getString(longitudineIndex));
            String nazione = c.getString(nazioneIndex);
            String regione = c.getString(regioneIndex);
            String provincia = c.getString(provinciaIndex);
            String comune = c.getString(comuneIndex);
            String cap = c.getString(capIndex);
            String indirizzo = c.getString(indirizzoIndex);
            int isLuogoPreferito = c.getInt(isLuogoPreferitoIndex);

            result = new LocationDao(id, alias, latitudine, longitudine, nazione, regione, provincia, comune, cap, indirizzo);
            result.setLuogoPreferito(isLuogoPreferito);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce la location veloce<br>
     * @param database
     * @return
     */
    public static LocationDao getLocationVeloce(SQLiteDatabase database)
    {
        LocationDao result = null;

        Cursor c = database.rawQuery("SELECT * FROM location where alias = '" + NOME_LOCATION_VELOCE + "'", null);


        int idIndex = c.getColumnIndex("id");
        int aliasIndex = c.getColumnIndex("alias");
        int latitudineIndex = c.getColumnIndex("latitudine");
        int longitudineIndex = c.getColumnIndex("longitudine");
        int nazioneIndex = c.getColumnIndex("nazione");
        int regioneIndex = c.getColumnIndex("regione");
        int provinciaIndex = c.getColumnIndex("provincia");
        int comuneIndex = c.getColumnIndex("comune");
        int capIndex = c.getColumnIndex("cap");
        int indirizzoIndex = c.getColumnIndex("indirizzo");
        int isLuogoPreferitoIndex = c.getColumnIndex("is_preferito");


        if (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            String alias = c.getString(aliasIndex);
            double latitudine = Double.parseDouble(c.getString(latitudineIndex));
            double longitudine = Double.parseDouble(c.getString(longitudineIndex));
            String nazione = c.getString(nazioneIndex);
            String regione = c.getString(regioneIndex);
            String provincia = c.getString(provinciaIndex);
            String comune = c.getString(comuneIndex);
            String cap = c.getString(capIndex);
            String indirizzo = c.getString(indirizzoIndex);
            int isLuogoPreferito = c.getInt(isLuogoPreferitoIndex);

            result = new LocationDao(id, alias, latitudine, longitudine, nazione, regione, provincia, comune, cap, indirizzo);
            result.setLuogoPreferito(isLuogoPreferito);
        }

        c.close();

        return result;
    }

}
