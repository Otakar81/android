package magazzino.bobo.com.magazzinodomestico.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DATE_PATTERN_DB = "yyyy-MM-dd";


    /***
     * Prende la data in formato string proveniente dal campo input della view e la converte in oggetto Date
     *
     * @param short_date
     * @param locale
     * @return
     */
    public static Date convertToDateFromView(String short_date, Locale locale)
    {
        Date dataConvertita = null;

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);

        try {

            dataConvertita = df.parse(short_date);

        } catch (ParseException e) {
            e.printStackTrace();

            dataConvertita = null;
        }

        return dataConvertita;
    }

    /***
     * Converte nel formato string in cui sarà presentato a video
     *
     * @param date
     * @param locale
     * @return
     */
    public static String convertToDateView(Date date, Locale locale)
    {
        String dataConvertita = null;

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);

        if(date != null)
            dataConvertita = df.format(date);

        return dataConvertita;
    }


    /***
     * Prende la data passata come argomento (nel formato con cui è salvata sul database) e la converte in un oggetto Date
     *
     * @param database_date
     * @return
     */
    public static Date convertToDateFromDatabase(String database_date)
    {
        Date dataConvertita = null;

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_DB);

        try {

            dataConvertita = sdf.parse(database_date);

        } catch (ParseException e) {
            e.printStackTrace();

            dataConvertita = null;
        }

        return dataConvertita;
    }

    /***
     * Converte nel formato con cui è salvata sul database l'oggetto Date passato come argomento
     *
     * @param data
     * @return
     */
    public static String convertToDateDatabase(Date data)
    {
        String dataConvertita = null;

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_DB);

        if(data != null)
            dataConvertita = sdf.format(data);


        return dataConvertita;
    }

}
