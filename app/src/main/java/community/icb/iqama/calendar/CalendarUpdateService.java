package community.icb.iqama.calendar;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;

import org.joda.time.DateTime;

import community.icb.iqama.common.Prayers;
import community.icb.iqama.utilities.Date;

/**
 * Service to update calendar every 10 days
 *
 * @author AmrAbed
 */
class CalendarUpdateService extends AbstractThreadedSyncAdapter
{
	//    private static final String CALENDAR_ID = "primary";
	private static final String PRAYER = " prayer";
	private static final String TIME_ZONE = "America/New_York";

	public CalendarUpdateService(Context context, boolean autoInitialize,
								 boolean allowParallelSyncs)
	{
		super(context, autoInitialize, allowParallelSyncs);
	}

	private static int getLastDay(DateTime date)
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
	public void onPerformSync(Account account, Bundle bundle, String s,
							  ContentProviderClient contentProviderClient, SyncResult syncResult)
	{
		insertData(account);
	}

	private void insertData(Account account)
	{
		// ToDo (AmrAbed): Fix Friday prayer time
		final DateTime date = Date.today();
		final Prayers prayers = new Prayers(date);
		final ContentResolver resolver = getContentResolver(account);

		for (int i = 0; i < Prayers.COUNT; i++)
		{
			final String title = prayers.getEnglishName(getContext(), i) + PRAYER;
			final DateTime dt = prayers.getDateTime(i, date);

			final ContentValues event = new ContentValues();
			event.put(Events.TITLE, title);
			event.put(Events.DESCRIPTION, "Iqama time for Islamic Center of Blacksburg");
			event.put(Events.EVENT_LOCATION, "Islamic Center of Blacksburg");
			event.put(Events.DTSTART, dt.getMillis());
			event.put(Events.DTEND, dt.getMillis());
			event.put(Events.EVENT_TIMEZONE, TIME_ZONE);
			event.put(Events.RRULE, getRecurrenceRule(date));

			if (resolver != null)
			{
				final Uri eventUri = resolver.insert(asSyncAdapter(Events.CONTENT_URI, account), event);

				if (eventUri != null)
				{
					final String segment = eventUri.getLastPathSegment();
					assert segment != null;
					final long eventID = Long.parseLong(segment);

					final ContentValues reminder = new ContentValues();
					reminder.put(Reminders.MINUTES, 15);
					reminder.put(Reminders.EVENT_ID, eventID);
					reminder.put(Reminders.METHOD, Reminders.METHOD_ALERT);
					resolver.insert(asSyncAdapter(Reminders.CONTENT_URI, account), event);
				}
			}
		}
	}

	private Uri asSyncAdapter(Uri uri, Account account)
	{
		return uri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name)
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, account.type)
				.build();
	}

	private String getRecurrenceRule(DateTime date)
	{
		return "RRULE:FREQ=DAILY;COUNT=" + (getLastDay(date) - getFirstDay(date) + 1);
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

	private ContentResolver getContentResolver(Account account)
	{
		// ToDo (AmrAbed): do this!
		return null;
		//..addPeriodicSync(account, null, null, 100);
	}
}
