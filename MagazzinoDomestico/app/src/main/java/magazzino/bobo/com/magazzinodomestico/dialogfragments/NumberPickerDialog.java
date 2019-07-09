package magazzino.bobo.com.magazzinodomestico.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import magazzino.bobo.com.magazzinodomestico.R;


public class NumberPickerDialog extends DialogFragment {

    //Elementi view del dialog
    private NumberPicker numberPickerView;

    //Elementi view del dialog da cui vengo chiamato
    private EditText numeroOggettiView;
    //private UpdateFieldDialog dialogChiamante;

    //Dialog builder
    private AlertDialog.Builder mBuilder;

    public static NumberPickerDialog newInstance(AlertDialog.Builder builder, EditText numeroOggettiView){

        NumberPickerDialog dialogFragment = new NumberPickerDialog();
        dialogFragment.mBuilder = builder;
        dialogFragment.numeroOggettiView = numeroOggettiView;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_picker, null);


        int numeroOggetti = 0;

        try {
            numeroOggetti = Integer.parseInt(numeroOggettiView.getText().toString().trim());
        }catch (Exception e)
        {
            //L'utente non ha inserito nulla per il campo, quindi lascio il valore di default -1
        }


        //Valorizzo le view del layout
        numberPickerView = view.findViewById(R.id.numberPicker);
        numberPickerView.setMinValue(0);
        numberPickerView.setMaxValue(100);
        numberPickerView.setValue(numeroOggetti);
        numberPickerView.setWrapSelectorWheel(true);

        /* Al momento non serve
        numberPickerView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //TODO -> Modifico il valore del campo sul dialog
            }
        });
        */

        //E costruisco il builder
        mBuilder.setView(view)
                .setPositiveButton(R.string.modifica, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        numeroOggettiView.setText(Integer.toString(numberPickerView.getValue()));
                    }
                })
                .setNegativeButton(R.string.elimina, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        numeroOggettiView.setText("");
                    }
                })
                .setNeutralButton(R.string.annulla, null);

        // Create the AlertDialog object and return it
        return mBuilder.create();
    }

    /*
    private void salvaStanza()
    {
        String nome = nomeView.getText().toString().trim();

        if(nome == null || nome.trim().equals(""))
        {
            Toast.makeText(getActivity(), R.string.errore_nome, Toast.LENGTH_SHORT).show();

        }else{

            //Verifico che il nome passato come argomento non sia già stato usato
            StanzaDao stanza = DatabaseManager.getStanzaByName(MainActivity.database, nome);

            if(stanza != null)
            {
                Toast.makeText(getActivity(), R.string.errore_nome_in_uso, Toast.LENGTH_SHORT).show();
            }else{

                //Creo e salvo la categoria
                stanza = new StanzaDao(nome);
                DatabaseManager.insertStanza(MainActivity.database, stanza);

                updateSpinner(nome);

                Toast.makeText(getActivity(), R.string.operazione_successo, Toast.LENGTH_SHORT).show();
            }
        }
    }

    */
}
