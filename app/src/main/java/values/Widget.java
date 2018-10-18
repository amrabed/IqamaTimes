package values;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import community.icb.iqama.R;
import community.icb.iqama.common.Prayers;
import community.icb.iqama.main.Main;
import community.icb.iqama.utilities.Date;

/**
 * Widget
 *
 * @author AmrAbed
 */
public class Widget extends AppWidgetProvider {
    private static void update(Context context, AppWidgetManager manager, int widgetId) {
        final Intent intent = new Intent(context, Main.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        final Prayers prayers = new Prayers(context, Date.today());

        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        views.setTextViewText(R.id.date, Date.today().toString(Date.DEFAULT_FORMAT));

        for (int i = 0; i < 5; i++) {
            views.setTextViewText(LABELS[i], prayers.getEnglishName(i));
            views.setTextViewText(TIMES[i], prayers.getTime(i));
        }

        manager.updateAppWidget(widgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] widgetIds) {
        // Update all active widgets
        for (int widgetId : widgetIds) {
            update(context, manager, widgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static final int[] TIMES = {R.id.fajr, R.id.dhuhr, R.id.asr, R.id.maghrib, R.id.isha};
    private static final int[] LABELS = {R.id.fajr_label, R.id.dhuhr_label, R.id.asr_label,
            R.id.maghrib_label, R.id.isha_label};
}