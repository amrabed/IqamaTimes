package community.icb.iqama;

import org.joda.time.DateTime;
import org.joda.time.chrono.StrictChronology;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Iqama Times for selected date
 *
 * @author AmrAbed
 */
public class IqamaTimes
{
    private static final String FORMAT = "HH:mm";

    public static String[] get(DateTime date)
    {
        final int year = date.getYear();
        final int month = date.getMonthOfYear();
        final int day = date.getDayOfMonth();

        final String[] times = iqamaTimes[getIndex(day, month)];

        final int shift = getTimeShift(day, month, year);
        if(shift == 0)
        {
            return times;
        }

        final ArrayList<String> newTimes = new ArrayList<>();
        for(String time : times)
        {
            newTimes.add(DateTime.parse(time).plusHours(shift).toString(FORMAT));
        }
        return (String []) newTimes.toArray();

    }

    private static int getIndex(int day, int month)
    {
        return 3 * (month - 1) + (day / 10);
    }

    private static int getTimeShift(int day, int month, int year)
    {
        // ToDo (AmrAbed): Handle dayligth saving
        return 0;
    }

    private static final String iqamaTimes[][] = {{"6:40", "12:45", "3:15", "5:30", "7:00"},
            {"6:40", "12:45", "3:30", "5:40", "7:00"},
            {"6:40", "12:45", "3:30", "5:50", "7:10"},
            {"6:40", "12:45", "3:45", "6:00", "7:20"},
            {"6:30", "12:45", "3:45", "6:10", "7:30"},
            {"6:20", "12:45", "4:00", "6:20", "7:40"},
            {"6:00", "12:45", "4:00", "6:30", "7:50"},
            {"6:50", "1:45", "5:00", "7:40", "9:00"},
            {"6:40", "1:45", "5:15", "7:50", "9:10"},
            {"6:20", "1:45", "5:15", "8:00", "9:20"},
            {"6:00", "1:45", "5:15", "8:05", "9:30"},
            {"5:50", "1:30", "5:15", "8:15", "9:40"},
            {"5:30", "1:30", "5:15", "8:25", "9:50"},
            {"5:20", "1:30", "5:15", "8:35", "10:00"},
            {"5:10", "1:30", "5:15", "8:40", "10:10"},
            {"5:00", "1:30", "5:30", "8:45", "10:20"},
            {"5:00", "1:45", "5:30", "8:50", "10:20"},
            {"5:00", "1:45", "5:30", "8:50", "10:20"},
            {"5:10", "1:45", "5:30", "8:50", "10:20"},
            {"5:20", "1:45", "5:30", "8:50", "10:20"},
            {"5:30", "1:45", "5:30", "8:45", "10:10"},
            {"5:40", "1:45", "5:30", "8:35", "10:00"},
            {"5:50", "1:45", "5:30", "8:25", "9:40"},
            {"6:00", "1:45", "5:15", "8:10", "9:30"},
            {"6:10", "1:30", "5:15", "7:55", "9:20"},
            {"6:20", "1:30", "5:00", "7:40", "9:00"},
            {"6:30", "1:30", "5:00", "7:25", "8:50"},
            {"6:40", "1:30", "4:45", "7:10", "8:30"},
            {"6:50", "1:30", "4:30", "6:55", "8:20"},
            {"7:00", "1:15", "4:15", "6:45", "8:00"},
            {"6:10", "12:15", "3:15", "5:30", "7:00"},
            {"6:20", "12:30", "3:00", "5:20", "7:00"},
            {"6:30", "12:30", "3:00", "5:15", "7:00"},
            {"6:30", "12:30", "3:00", "5:10", "7:00"},
            {"6:40", "12:30", "3:00", "5:15", "7:00"},
            {"6:40", "12:45", "3:00", "5:20", "7:00"}};
}
