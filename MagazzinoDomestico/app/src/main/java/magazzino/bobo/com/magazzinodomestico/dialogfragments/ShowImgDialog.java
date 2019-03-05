package magazzino.bobo.com.magazzinodomestico.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import magazzino.bobo.com.magazzinodomestico.MainActivity;
import magazzino.bobo.com.magazzinodomestico.R;
import magazzino.bobo.com.magazzinodomestico.StanzeActivity;
import magazzino.bobo.com.magazzinodomestico.db.DatabaseManager;
import magazzino.bobo.com.magazzinodomestico.db.dao.StanzaDao;


/*
    Nota.
    In questo dialog valoriziamo i campi con i valori passati nel metodo "valorizzaDialog"
    Tuttavia le dua chiamate (onCreateDialog) e "valorizzaDialog" sono tra loro asincrone: chiamandole una dopo l'altra in LuoghiMemorabiliActivity
    non possiamo sapere se "valorizzaDialog" arriverà prima o dopo che il Dialog sia stato effettivamente creato.

    Per questo è necessario utilizzare delle variabili di istanza che facciano un po da ponte: se la chiamata a "valorizzaDialog" sarà arrivata prima
    della effettiva costruzione del dialog allora non valorizzerà le view (che sono ancora NULL) ma valorizzerà le variabili di istanza.
    A quel punto, nell'onCreate tali variabili si troveranno già valorizzate, ed al momento della creazione effettiva del dialog i campi saranno
    subito valorizzati.
 */


public class ShowImgDialog extends DialogFragment {

    Bitmap immagine;

    //Elementi view del dialog
    private ImageView imageView;

    //Dialog builder
    private AlertDialog.Builder mBuilder;

    public static ShowImgDialog newInstance(AlertDialog.Builder builder, Bitmap immagine){

        ShowImgDialog dialogFragment = new ShowImgDialog();
        dialogFragment.immagine = immagine;
        dialogFragment.mBuilder = builder;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_show_img, null);

        //Valorizzo le view del layout
        imageView = view.findViewById(R.id.imageView);
        imageView.setImageBitmap(immagine);

        //E costruisco il builder
        mBuilder.setView(view);

        // Create the AlertDialog object and return it
        Dialog dialog = mBuilder.create();

        //Setto le dimensioni del dialog sulla base di quelle dello schermo
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int displayWidth = outMetrics.widthPixels;
        int displayHeigth = outMetrics.heightPixels;

        imageView.setMinimumHeight((int)(displayHeigth * 0.8f));
        imageView.setMinimumWidth((int)(displayWidth * 0.8f));

        return dialog;
    }
}
