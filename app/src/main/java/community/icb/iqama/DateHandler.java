package community.icb.iqama;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.joda.time.DateTime;

/**
 * Handles date updates
 *
 * @author AmrAbed
 */
public class DateHandler implements View.OnClickListener, DatePickerDialog.OnDateSetListener
{
    private static final String FORMAT = "EEE, MMMM d";

    private final Context context;
    private final TextView dateView;
    private final Listener listener;

    public DateTime getCurrentDate()
    {
        return currentDate;
    }

    private DateTime currentDate;

    public DateHandler(Context context, TextView dateView, Listener listener)
    {
        this.context = context;
        this.dateView = dateView;
        this.listener = listener;

        dateView.setOnClickListener(this);

        updateDate(new DateTime());
    }

    @Override
    public void onClick(View v)
    {
        new DatePickerDialog(context, this,
                currentDate.getYear(),
                currentDate.getMonthOfYear() - 1,
                currentDate.getDayOfMonth()).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        updateDate(new DateTime(year, month + 1, day, 0, 0));
    }

    private void updateDate(DateTime date)
    {
        currentDate = date;
        dateView.setText(date.toString(FORMAT));
        listener.onDateChanged(date);
    }

    public interface Listener
    {
        void onDateChanged(DateTime newDate);
    }
}
