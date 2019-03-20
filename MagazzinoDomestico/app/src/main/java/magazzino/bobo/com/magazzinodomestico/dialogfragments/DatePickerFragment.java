package magazzino.bobo.com.magazzinodomestico.dialogfragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import magazzino.bobo.com.magazzinodomestico.R;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public EditText dateView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setDateView(EditText dateView)
    {
        this.dateView = dateView;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, 0, 0);

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        if(dateView != null)
            dateView.setText(df.format(c.getTime()));
    }
}
