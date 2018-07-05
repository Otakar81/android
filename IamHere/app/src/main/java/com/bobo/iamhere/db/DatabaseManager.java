package com.bobo.iamhere.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.bobo.iamhere.GooglePlacesActivity;

import java.util.ArrayList;

public class DatabaseManager {

    public static String NOME_LOCATION_VELOCE = "Luogo veloce";

//region SEZIONE COMUNE

    /***
     * Crea le tabelle utilizzate dall'applicazione
     * @param database
     */
    public static void createTables(SQLiteDatabase database)
    {
        /*
            Creo la tabella dei luoghi
         */
        String sql = "CREATE TABLE IF NOT EXISTS location (id INTEGER PRIMARY KEY, alias VARCHAR, latitudine VARCHAR, longitudine VARCHAR, " +
                "nazione VARCHAR, regione VARCHAR, provincia VARCHAR, comune VARCHAR, cap VARCHAR, indirizzo VARCHAR, is_preferito INTEGER(1))";

        database.execSQL(sql);

        /* Tmp
        sql = "ALTER TABLE location ADD COLUMN is_preferito INTEGER(1)";
        database.execSQL(sql);
*/

        /*
            Creo la tabella delle note
         */
        sql = "CREATE TABLE IF NOT EXISTS note (id INTEGER PRIMARY KEY, titolo VARCHAR, testo VARCHAR, id_location INTEGER)";

        database.execSQL(sql);

        /*
            Creo la tabella delle tipologie di "place" (Google Places API)
         */
        sql = "CREATE TABLE IF NOT EXISTS google_places_type (id INTEGER PRIMARY KEY, chiave VARCHAR, gruppo VARCHAR, is_preferito INTEGER(1))";

        database.execSQL(sql);


        Cursor c = database.rawQuery("SELECT count(*) numero_tipi FROM google_places_type", null);


        int countIndex = c.getColumnIndex("numero_tipi");

        if (c.moveToNext())
        {
            int numeroTipi = c.getInt(countIndex);

            if(numeroTipi == 0) //Valorizzo la tabella
            {
                sql = "INSERT INTO google_places_type (chiave, gruppo, is_preferito) VALUES " +
                    "('accounting', '', 0), ('airport', '', 0), ('amusement_park', '', 0), ('aquarium', '', 0), " +
                    "('art_gallery', '', 0), ('atm', '', 0), ('bakery', '', 0), ('bank', '', 0), " +
                    "('bar', '', 0), ('beauty_salon', '', 0), ('bicycle_store', '', 0), ('book_store', '', 0), " +
                    "('bowling_alley', '', 0), ('bus_station', '', 0), ('cafe', '', 0), ('campground', '', 0), " +
                    "('car_dealer', '', 0), ('car_rental', '', 0), ('car_repair', '', 0), ('car_wash', '', 0), " +
                    "('casino', '', 0), ('cemetery', '', 0), ('church', '', 0), ('city_hall', '', 0), " +
                    "('clothing_store', '', 0), ('convenience_store', '', 0), ('courthouse', '', 0), ('dentist', '', 0), " +
                    "('department_store', '', 0), ('doctor', '', 0), ('electrician', '', 0), ('electronics_store', '', 0), " +
                    "('embassy', '', 0), ('fire_station', '', 0), ('florist', '', 0), ('funeral_home', '', 0), " +
                    "('furniture_store', '', 0), ('gas_station', '', 0), ('gym', '', 0), ('hair_care', '', 0), " +
                    "('hardware_store', '', 0), ('hindu_temple', '', 0), ('home_goods_store', '', 0), ('hospital', '', 0), " +
                    "('insurance_agency', '', 0), ('jewelry_store', '', 0), ('laundry', '', 0), ('lawyer', '', 0), " +
                    "('library', '', 0), ('liquor_store', '', 0), ('local_government_office', '', 0), ('locksmith', '', 0), " +
                    "('lodging', '', 0), ('meal_delivery', '', 0), ('meal_takeaway', '', 0), ('mosque', '', 0), " +
                    "('movie_rental', '', 0), ('movie_theater', '', 0), ('moving_company', '', 0), ('museum', '', 0), " +
                    "('night_club', '', 0), ('painter', '', 0), ('park', '', 0), ('parking', '', 0), " +
                    "('pet_store', '', 0), ('pharmacy', '', 0), ('physiotherapist', '', 0), ('plumber', '', 0), " +
                    "('police', '', 0), ('post_office', '', 0), ('real_estate_agency', '', 0), ('restaurant', '', 0), " +
                    "('roofing_contractor', '', 0), ('rv_park', '', 0), ('school', '', 0), ('shoe_store', '', 0), " +
                    "('shopping_mall', '', 0), ('spa', '', 0), ('stadium', '', 0), ('storage', '', 0), " +
                    "('store', '', 0), ('subway_station', '', 0), ('supermarket', '', 0), ('synagogue', '', 0), " +
                    "('taxi_stand', '', 0), ('train_station', '', 0), ('transit_station', '', 0), ('travel_agency', '', 0), " +
                    "('veterinary_care', '', 0), ('zoo', '', 0)";

                database.execSQL(sql);
            }
        }
        c.close();


    }

//endregion


//region SEZIONE LOCATION


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

//endregion


//region SEZIONE NOTE


    /**
     * Restituisce tutte le note memorizzate
     * @param database
     * @return
     */
    public static ArrayList<NotaDao> getAllNote(SQLiteDatabase database)
    {
        ArrayList<NotaDao> result = new ArrayList<NotaDao>();

        String sql = "SELECT id, titolo, testo, id_location FROM note";

        Cursor c = database.rawQuery(sql, null);


        int idIndex = c.getColumnIndex("id");
        int titoloIndex = c.getColumnIndex("titolo");
        int testoIndex = c.getColumnIndex("testo");
        int id_locationIndex = c.getColumnIndex("id_location");

        while (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            String titolo = c.getString(titoloIndex);
            String testo = c.getString(testoIndex);
            long idLocation = c.getInt(id_locationIndex);

            NotaDao notaDao = new NotaDao(id, titolo, testo, idLocation);

            result.add(notaDao);
        }

        c.close();

        return result;
    }

    /**
     * Restituisce la nota passata come argomento
     * @param database
     * @param idNota
     * @return
     */
    public static NotaDao getNota(SQLiteDatabase database, long idNota)
    {
        NotaDao result = null;

        String sql = "SELECT id, titolo, testo, id_location FROM note WHERE id = " + idNota;

        Cursor c = database.rawQuery(sql, null);


        int idIndex = c.getColumnIndex("id");
        int titoloIndex = c.getColumnIndex("titolo");
        int testoIndex = c.getColumnIndex("testo");
        int id_locationIndex = c.getColumnIndex("id_location");

        if (c.moveToNext())
        {
            long id = c.getLong(idIndex);
            String titolo = c.getString(titoloIndex);
            String testo = c.getString(testoIndex);
            long idLocation = c.getInt(id_locationIndex);

            result = new NotaDao(id, titolo, testo, idLocation);
        }

        c.close();

        return result;
    }

    /**
     * Cancella la nota passata come argomento
     * @param database
     * @param id
     */
    public static void deleteNota(SQLiteDatabase database, long id)
    {
        database.execSQL("DELETE FROM note where id = " + id);
    }

    /**
     * Aggiunge una nuova nota
     * @param database
     * @param nota
     */
    public static void insertNota(SQLiteDatabase database, NotaDao nota)
    {
        String sql = "INSERT INTO note (titolo, testo, id_location)" +
                " VALUES (?, ?, ?)";


        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, nota.getTitolo());
        stmt.bindString(2, nota.getTestoNota());
        stmt.bindString(3, nota.getIdLocation() + "");
        stmt.execute();
    }

    /***
     * Update nota
     *
     * @param database
     * @param nota
     */
    public static void updateNota(SQLiteDatabase database, NotaDao nota)
    {
        SQLiteStatement stmt = database.compileStatement("UPDATE location SET alias = ?, testo = ? WHERE id = ?");
        stmt.bindString(1, nota.getTitolo());
        stmt.bindString(2, nota.getTestoNota());
        stmt.bindLong(3, nota.getId());

        stmt.execute();
    }

//endregion

//region SEZIONE GOOGLE PLACE TYPE

    /***
     * Restituisce tutti i tipi memorizzati su DB
     * @param database
     * @return
     */
    public static ArrayList<GooglePlacesTypeDao> getAllPlaceTypes(SQLiteDatabase database)
    {
        ArrayList<GooglePlacesTypeDao> result = new ArrayList<GooglePlacesTypeDao>();

        String sql = "SELECT id, chiave, gruppo, is_preferito FROM google_places_type";

        Cursor c = database.rawQuery(sql, null);


        int idIndex = c.getColumnIndex("id");
        int chiaveIndex = c.getColumnIndex("chiave");
        int gruppoIndex = c.getColumnIndex("gruppo");
        int preferitoIndex = c.getColumnIndex("is_preferito");

        while (c.moveToNext())
        {
            int id = c.getInt(idIndex);
            String chiave = c.getString(chiaveIndex);
            String gruppo = c.getString(gruppoIndex);
            int isPreferito = c.getInt(preferitoIndex);

            GooglePlacesTypeDao typeDao = new GooglePlacesTypeDao(id, chiave, gruppo, isPreferito);

            result.add(typeDao);
        }

        c.close();

        return result;
    }

    /***
     * Restituisce il tipo richiesto
     *
     * @param database
     * @param id
     * @return
     */
    public static GooglePlacesTypeDao getPlaceType(SQLiteDatabase database, int id)
    {
        GooglePlacesTypeDao result = null;

        String sql = "SELECT chiave, gruppo, is_preferito FROM google_places_type WHERE id = " + id;

        Cursor c = database.rawQuery(sql, null);

        int chiaveIndex = c.getColumnIndex("chiave");
        int gruppoIndex = c.getColumnIndex("gruppo");
        int preferitoIndex = c.getColumnIndex("is_preferito");

        if (c.moveToNext())
        {
            String chiave = c.getString(chiaveIndex);
            String gruppo = c.getString(gruppoIndex);
            int isPreferito = c.getInt(preferitoIndex);

            result = new GooglePlacesTypeDao(id, chiave, gruppo, isPreferito);
        }

        c.close();

        return result;
    }

//endregion


}
