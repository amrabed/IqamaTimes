package community.icb.iqama.common;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import community.icb.iqama.R;
import community.icb.iqama.utilities.Date;

/**
 * Prayer times of given date
 *
 * @author AmrAbed
 */
public class Prayers {
    private static final int FRIDAY = 5;

    public static final int FAJR = 0;
    public static final int DHUHR = 1;
    public static final int ASR = 2;
    public static final int MAGHRIB = 3;
    public static final int ISHA = 4;
    public static final int COUNT = 5;

    private final Context context;
    private final DateTime date;

    private final int index;
    private final int timeShift;

    public Prayers(Context context, DateTime date) {
        this.context = context;
        this.date = date;

        index = getIndex(date);
        timeShift = getTimeShift(date);
    }

    private static DateTimeFormatter getFormatter(String format) {
        return new DateTimeFormatterBuilder().appendPattern(format).toFormatter();
    }

    public String getTime(int prayer) {
        if (isFridayPrayer(prayer)) {
            // Friday prayer is 1:30PM all year
            return "1:30";
        }
        final String time = iqamaTimes[index][prayer];
        return DateTime.parse(time, getFormatter("h:mm")).plusHours(timeShift).toString("h:mm");
    }

    public String getArabicName(int prayer) {
        if (isFridayPrayer(prayer)) {
            return context.getString(R.string.friday_ar);
        }
        return context.getResources().getStringArray(R.array.prayers_ar)[prayer];
    }

    public String getEnglishName(int prayer) {
        if (isFridayPrayer(prayer)) {
            return context.getString(R.string.friday_en);
        }
        return context.getResources().getStringArray(R.array.prayers_en)[prayer];
    }

    public boolean isNextPrayer(int prayer) {
        if (!Date.today().equals(date)) {
            return false;
        }

        final DateTime now = DateTime.now();
        final DateTime prayerTime = getDateTime(prayer, now);
        DateTime previousPrayerTime = null;
        if (prayer > 0) {
            previousPrayerTime = getDateTime(prayer - 1, now);
        }

        return now.isBefore(prayerTime) && ((previousPrayerTime == null) || now.isAfter(previousPrayerTime));
    }

    public DateTime getDateTime(int prayer, DateTime date) {
        final String time = getTime(prayer) + ((prayer > 0) ? "pm" : "am");
        return DateTime.parse(time, getFormatter("h:mma"))
                .withDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
    }

    private int getIndex(DateTime date) {
        final int month = date.getMonthOfYear();
        final int day = date.getDayOfMonth();

        final int offset = (day != 31) ? (day - 1) / 10 : 2;
        return 3 * (month - 1) + offset;
    }

    private int getTimeShift(DateTime date) {
        final int month = date.getMonthOfYear();
        // ToDo (AmrAbed): Handle dayligth saving
        if (month != 3 && month != 11) {
            return 0;
        }

        if (month == 3) {
            if (isStandardTime(date)) {
                return -1;
            } else {
                return 0;
            }
        }

        //if(month == 11)
        {
            if (isStandardTime(date)) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private boolean isFridayPrayer(int prayer) {
        return prayer == DHUHR && date.getDayOfWeek() == FRIDAY;
    }

    private boolean isStandardTime(DateTime dateTime) {
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
