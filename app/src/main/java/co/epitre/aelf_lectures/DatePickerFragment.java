package co.epitre.aelf_lectures;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Dialog;
import android.os.Build;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.view.ContextThemeWrapper;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener, DialogInterface.OnClickListener {

    protected DatePickerDialog dialog;
    protected boolean canceled = false;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface CalendarDialogListener {
        void onCalendarDialogPicked(int year, int month, int day);
    }

    CalendarDialogListener mListener;

    public void setListener(CalendarDialogListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final Calendar c = Calendar.getInstance();

        // if we got an initial date --> load it
        if(args != null)
        {
            long timems = args.getLong("time");
            if(timems > 0) {
                c.setTimeInMillis(timems);
            }
        }

        // Use the current date as the default date in the picker
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        Context context = getActivity();
        if (isBrokenSamsungDevice()) {
            context = new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog);
        }
        dialog = new DatePickerDialog(context, this, year, month, day);
        dialog.setCancelable(true);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.button_cancel), this);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.button_today), this);
        return dialog;
    }

    private static boolean isBrokenSamsungDevice() {
        // Samsung devices running 5.0 / 5.1 with talkback are broken when selecting a date. Yeah !
        // http://stackoverflow.com/questions/28618405/datepicker-crashes-on-my-device-when-clicked-with-personal-app
        return (Build.MANUFACTURER.equalsIgnoreCase("samsung")
                && Build.VERSION.SDK_INT >= 21
                && Build.VERSION.SDK_INT <= 22
                );
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_NEUTRAL) {
            // set to today
            GregorianCalendar date = new GregorianCalendar();

            // set date picker's date
            DatePickerDialog dateDialog = (DatePickerDialog)dialog;
            dateDialog.updateDate(
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH));

            // trigger listener
            mListener.onCalendarDialogPicked(
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH));

            // quit the dialog
            dialog.dismiss();

            // FIXME: avoid flickering
        } else if(which == DialogInterface.BUTTON_NEGATIVE) {
            this.canceled = true;
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(!this.canceled) {
            mListener.onCalendarDialogPicked(year, month, day);
        }
    }
}
