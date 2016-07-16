package community.icb.iqama.utilities;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import community.icb.iqama.R;
import community.icb.iqama.common.Prayers;

/**
 * Calendar Update Task
 *
 * @author AmrAbed
 */
public class CalendarUpdateTask extends AsyncTask<Void, Void, Void>
{
    private static final String PRAYER = " prayer";
    private static final String TIME_ZONE = "America/New_York";

    private final Context context;
    private final Listener listener;

    private Calendar calendar = null;
    private Exception error = null;
    private String calendarId = "primary";

    public CalendarUpdateTask(Context context, GoogleAccountCredential credential, Listener listener)
    {
        this.context = context;
        this.listener = listener;

        final HttpTransport transport = AndroidHttp.newCompatibleTransport();
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        calendar = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            insertData();
        }
        catch (Exception e)
        {
            error = e;
            cancel(true);
        }
        return null;
    }

    private void insertData() throws IOException
    {
        final DateTime date = Date.today();
        final Prayers prayers = new Prayers(context, date);

        for (int i = 0; i < Prayers.COUNT; i++)
        {
            final String title = prayers.getEnglishName(i) + PRAYER;

            final EventDateTime time = getEventTime(prayers.getTime(i) + (i < 1 ? "am" : "pm"), date);

            final Event event = new Event().setSummary(title)
                    .setDescription("Iqama time for Islamic Center of Blacksburg")
                    .setStart(time).setEnd(time).setLocation("Islamic Center of Blacksburg")
                    .setRecurrence(getRecurrenceRule(date))
                    .setReminders(getReminders());

            calendar.events().insert(calendarId, event).execute();
        }
    }

//    private void selectCalendar() throws IOException
//    {
//        String pageToken = null;
//        List<CalendarListEntry> calendars;
//        do
//        {
//            CalendarList calendarList = calendar.calendarList().list().setPageToken(pageToken).execute();
//            calendars = calendarList.getItems();
//            pageToken = calendarList.getNextPageToken();
//        } while (pageToken != null);
//
//        final ArrayList<String> names = new ArrayList<>();
//
//        for (CalendarListEntry calendar : calendars)
//        {
//            names.add(calendar.getSummary());
//        }
//
//        final CalendarListEntry[] calendarArray = calendars.toArray(new CalendarListEntry[0]);
//
//        final Runnable runnable = new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                new AlertDialog.Builder(context).setTitle("Select Calendar")
//                        .setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, names),
//                                new DialogInterface.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int i)
//                                    {
//                                        calendarId = calendarArray[i].getId();
//                                    }
//                                }).create().show();
//
//            }
//        };
//
//        new Handler(context.getMainLooper()).post(runnable);
//    }

    private EventDateTime getEventTime(String iqamaTime, DateTime date)
    {
        final DateTime time = DateTime.parse(iqamaTime, new DateTimeFormatterBuilder()
                .appendPattern("h:mma").toFormatter());

        final long timeMillis = date.withDayOfMonth(getFirstDay(date))
                .withHourOfDay(time.getHourOfDay())
                .withMinuteOfHour(time.getMinuteOfHour()).getMillis();

        return new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(timeMillis))
                .setTimeZone(TIME_ZONE);
    }

    private Event.Reminders getReminders()
    {
        final EventReminder[] reminders = new EventReminder[]
                {new EventReminder().setMethod("popup").setMinutes(15)};
        return new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminders));
    }

    private List<String> getRecurrenceRule(DateTime date)
    {
//        final String until = date.withDayOfMonth(getLastDay(date)).toString("yyyyMMdd") + "T235959Z";
//        final String[] recurrence = {"RRULE:FREQ=DAILY;UNTIL=" + until};
        final String[] recurrence = {"RRULE:FREQ=DAILY;COUNT=" + (getLastDay(date) - getFirstDay(date) + 1)};
        return Arrays.asList(recurrence);
    }

    private int getFirstDay(DateTime date)
    {
        final int day = date.getDayOfMonth();
        if (day <= 10)
        {
            return 1;
        }
        else if (day <= 20)
        {
            return 11;
        }
        else
        {
            return 21;
        }
    }

    public static int getLastDay(DateTime date)
    {
        final int day = date.getDayOfMonth();
        if (day <= 10)
        {
            return 10;
        }
        else if (day <= 20)
        {
            return 20;
        }
        else
        {
            return date.dayOfMonth().withMaximumValue().getDayOfMonth();
        }
    }

    @Override
    protected void onPreExecute()
    {
    }

    @Override
    protected void onPostExecute(Void v)
    {
        listener.onCalendarUpdateSuccess();
    }

    @Override
    protected void onCancelled()
    {
        listener.onCalendarUpdateError(error);
    }

    public interface Listener
    {
        void onCalendarUpdateSuccess();

        void onCalendarUpdateError(Exception error);
    }
}
