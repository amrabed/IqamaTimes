package values;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import community.icb.iqama.Main;
import community.icb.iqama.R;
import community.icb.iqama.utilities.Date;
import community.icb.iqama.utilities.IqamaTimes;

/**
 * Widget
 *
 * @author AmrAbed
 */
public class Widget extends AppWidgetProvider
{
	@Override
	public void onUpdate(Context context, AppWidgetManager manager, int[] widgetIds)
	{
		// Update all active widgets
		for (int widgetId : widgetIds)
		{
			update(context, manager, widgetId);
		}
	}

	@Override
	public void onEnabled(Context context)
	{
		// Enter relevant functionality for when the first widget is created
	}

	@Override
	public void onDisabled(Context context)
	{
		// Enter relevant functionality for when the last widget is disabled
	}

	private static void update(Context context, AppWidgetManager manager, int widgetId)
	{
		final Intent intent = new Intent(context, Main.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		final String[] iqamaTimes = IqamaTimes.get(Date.today());

		views.setOnClickPendingIntent(R.id.widget, pendingIntent);
		views.setTextViewText(R.id.date, Date.today().toString(Date.DEFAULT_FORMAT));
		views.setTextViewText(R.id.fajr, iqamaTimes[0]);
		views.setTextViewText(R.id.dhuhr, iqamaTimes[1]);
		views.setTextViewText(R.id.asr, iqamaTimes[2]);
		views.setTextViewText(R.id.maghrib, iqamaTimes[3]);
		views.setTextViewText(R.id.isha, iqamaTimes[4]);

		manager.updateAppWidget(widgetId, views);
	}
}