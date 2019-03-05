package magazzino.bobo.com.magazzinodomestico.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    /***
     * Esegue la conversione in base 64 della bitmap passata come argomento
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap)
    {
        String result = "";

        if(bitmap != null)
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);

            byte[] bArray = bos.toByteArray();

            result = Base64.encodeToString(bArray, Base64.DEFAULT);
        }

        return result;
    }

    /***
     * Ottiene la Bitmap originale, partendo dalla sua conversione in Base64
     *
     * @param base64String
     * @return
     */
    public static Bitmap base64ToBitmap(String base64String)
    {
        try{

            byte[] bArray = Base64.decode(base64String, Base64.DEFAULT);

            Bitmap result = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);

            return result;

        } catch (Exception e)
        {
            Log.e("ImageUtils", e.getMessage());

            return null;
        }
    }

}
