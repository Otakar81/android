package magazzino.bobo.com.magazzinodomestico.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static String bitmapToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);

        byte[] bArray = bos.toByteArray();

        String result = Base64.encodeToString(bArray, Base64.DEFAULT);

        return result;
    }

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
