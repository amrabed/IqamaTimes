package community.icb.iqama.utilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Handles date
 *
 * @author AmrAbed
 */
public class Date
{
	public static final String DEFAULT_FORMAT = "EEE, MMMM d";
	private static final DateTimeZone TIME_ZONE = DateTimeZone.getDefault();

	private Date()
	{
	}

	public static DateTime today()
	{
		return DateTime.now(TIME_ZONE);
	}

	public static DateTime tomorrow()
	{
		return today().plusDays(1);
	}

	// For debugging purposes only
	public static DateTime get(int year, int month, int day)
	{
		return new DateTime(year, month, day, 3, 0, TIME_ZONE);
	}
}
