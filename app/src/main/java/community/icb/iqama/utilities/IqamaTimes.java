package community.icb.iqama.utilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Iqama Times for selected date
 *
 * @author AmrAbed
 */
public class IqamaTimes
{
    private static final String FORMAT = "h:mm";

    public static String[] get(DateTime date)
    {
        final String[] times = iqamaTimes[getIndex(date)];
        final int shift = getTimeShift(date);

        if (shift == 0)
        {
            return times;
        }
        
        final ArrayList<String> newTimes = new ArrayList<>();
        for (String time : times)
        {
            final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern(FORMAT).toFormatter();
            newTimes.add(DateTime.parse(time, formatter).plusHours(shift).toString(FORMAT));
        }
        return newTimes.toArray(new String[0]);
    }

    private static int getIndex(DateTime date)
    {
        final int month = date.getMonthOfYear();
        final int day = date.getDayOfMonth();

        final int offset = (day != 31) ? (day - 1) / 10 : 2;
        return 3 * (month - 1) + offset;
    }

    private static int getTimeShift(DateTime date)
    {
        final int month = date.getMonthOfYear();
        // ToDo (AmrAbed): Handle dayligth saving
        if (month != 3 && month != 11)
        {
            return 0;
        }

        if (month == 3)
        {
            if (isStandardTime(date))
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }

        //if(month == 11)
        {
            if (isStandardTime(date))
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    }

    private static boolean isStandardTime(DateTime dateTime)
    {
        return DateTimeZone.getDefault().isStandardOffset(dateTime.toInstant().getMillis());
    }

    private static final String iqamaTimes[][] = {{"6:40", "12:45", "3:15", "5:30", "7:00"},
            {"6:40", "12:45", "3:30", "5:40", "7:00"},
            {"6:40", "12:45", "3:30", "5:50", "7:10"},
            {"6:40", "12:45", "3:45", "6:00", "7:20"},
            {"6:30", "12:45", "3:45", "6:10", "7:30"},
            {"6:20", "12:45", "4:00", "6:20", "7:40"},
            {"7:00", "1:45", "5:00", "7:30", "8:50"},
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
