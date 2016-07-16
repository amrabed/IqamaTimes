package values;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import community.icb.iqama.Main;
import community.icb.iqama.R;
import community.icb.iqama.common.Prayers;
import community.icb.iqama.utilities.Date;

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
		final Prayers prayers = new Prayers(context, Date.today());

		views.setOnClickPendingIntent(R.id.widget, pendingIntent);
		views.setTextViewText(R.id.date, Date.today().toString(Date.DEFAULT_FORMAT));

		views.setTextViewText(R.id.fajr_label, prayers.getEnglishName(Prayers.FAJR));
		views.setTextViewText(R.id.dhuhr_label, prayers.getEnglishName(Prayers.DHUHR));
		views.setTextViewText(R.id.asr_label, prayers.getEnglishName(Prayers.ASR));
		views.setTextViewText(R.id.maghrib_label, prayers.getEnglishName(Prayers.MAGHRIB));
		views.setTextViewText(R.id.isha_label, prayers.getEnglishName(Prayers.ISHA));

		views.setTextViewText(R.id.fajr, prayers.getTime(Prayers.FAJR));
		views.setTextViewText(R.id.dhuhr, prayers.getTime(Prayers.DHUHR));
		views.setTextViewText(R.id.asr, prayers.getTime(Prayers.ASR));
		views.setTextViewText(R.id.maghrib, prayers.getTime(Prayers.MAGHRIB));
		views.setTextViewText(R.id.isha, prayers.getTime(Prayers.ISHA));

		manager.updateAppWidget(widgetId, views);
	}
}