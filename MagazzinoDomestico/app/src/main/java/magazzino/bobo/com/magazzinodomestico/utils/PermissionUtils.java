package magazzino.bobo.com.magazzinodomestico.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionUtils {

    //Permessi necessari per le operazioni di backup e restore
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Permessi necessari per l'utilizzo della fotocamera
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };


    /**
     * FOTOCAMERA
     */

    /***
     * Verifica che il dispositivo abbia una fotocamera utilizzabile
     * @param activity
     * @return
     */
    public static boolean hasSystemFeature_CAMERA(Activity activity)
    {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }


    /***
     * Verifica che l'utente abbia dato il permesso all'APP di utilizzare la fotocamera
     * Nel caso in cui la SDK VERSION sia < 23, restituisce true
     * @param activity
     * @return
     */
    public static boolean checkSelfPermission_CAMERA(Activity activity)
    {
        if(Build.VERSION.SDK_INT < 23) //Non ho bisogno di chiedere il permesso
            return true;
        else
            return activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * STORAGE
     */
    public static boolean checkSelfPermission_STORAGE(Activity activity)
    {
        if(Build.VERSION.SDK_INT < 23) //Non ho bisogno di chiedere il permesso
            return true;
        else
            return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /***
     * Richiede il permesso specificato, ma solo per le versioni di android superiori alla 23
     *
     * @param activity
     * @param permissions
     * @param requestCode
     */
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode)
    {
        if(Build.VERSION.SDK_INT >= 23) //Non ho bisogno di chiedere il permesso per versioni precedenti
            activity.requestPermissions(permissions, requestCode);
    }


    /***
     * Fonde i due Array di permessi passati come argomenti in modo da ottenerne uno solo
     * @param permissions1
     * @param permissions2
     * @return
     */
    public static String[] mergePermissions(String[] permissions1, String[] permissions2)
    {
        String[] permissionsResult = new String[permissions1.length + permissions2.length];

        int index = 0;

        for (String permission: permissions1)
            permissionsResult[index++] = permission;

        for (String permission: permissions2)
            permissionsResult[index++] = permission;

        return permissionsResult;
    }

}
