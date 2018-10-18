package community.icb.iqama.calendar;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import org.joda.time.DateTime;

import java.util.List;

import androidx.annotation.NonNull;
import community.icb.iqama.R;
import community.icb.iqama.utilities.ApiManager;
import community.icb.iqama.utilities.Date;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Settings activity
 *
 * @author AmrAbed
 */
public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener,
        ApiManager.Listener, EasyPermissions.PermissionCallbacks,
        CalendarUpdateTask.Listener {

    // ToDo (AmrAbed): Call SyncAdapter upon success
    private static final String PREF_NAME = "pref_add_to_calendar";
    private static final String ACCOUNT_NAME = "Account name";

    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 0;
    private static final int REQUEST_ACCOUNT_PICKER = 1;
    private static final int REQUEST_AUTHORIZATION = 2;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 3;
    private static final String LAST_UPDATE = "last update";

    private static final long INTERVAL_MILLIS = 1000 * 60 * 60 * 24 * 10;

    private ApiManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_NAME)) {
            manager = ApiManager.newInstance(Settings.this, Settings.this);
            manager.getApiResults(this);

//			new AlertDialog.Builder(this).setTitle(R.string.calendar)
//					.setMessage((isAlreadyAdded() ? R.string.already_added : R.string.calendar_message))
//					.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener()
//					{
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//						}
//					})
//					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
//					{
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							dialog.dismiss();
//						}
//					})
//					.create().show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int request, @NonNull String[] permissions,
                                           @NonNull int[] results) {
        super.onRequestPermissionsResult(request, permissions, results);
        EasyPermissions.onRequestPermissionsResult(request, permissions, results, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // Do nothing
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // Do nothing
    }

    @Override
    public void onCalendarUpdateSuccess() {
        getPreferences(MODE_PRIVATE).edit().putString(LAST_UPDATE, Date.today().toString()).apply();
        showMessage("Prayer times added successfully");
    }

    @Override
    public void onCalendarUpdateError(Exception error) {
        if (error != null) {
            if (error instanceof GooglePlayServicesAvailabilityIOException) {
                final int code = ((GooglePlayServicesAvailabilityIOException) error)
                        .getConnectionStatusCode();
                showGooglePlayServicesAvailabilityErrorDialog(code);
            } else if (error instanceof UserRecoverableAuthIOException) {
                startActivityForResult(((UserRecoverableAuthIOException) error).getIntent(),
                        REQUEST_AUTHORIZATION);
            } else {
                showMessage("An error occurred. If the problem persists, please contact ICB");
            }
        } else {
            showMessage("Request Cancelled");
        }
    }

    @Override
    public void onConnectionError() {
        showMessage("Device not connected to the Internet");
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onApiError(int connectionStatusCode) {
        showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
    }

    @Override
    public void onApiReady(GoogleAccountCredential credential) {
        ApiManager.setCredential(credential);
        if (!isAlreadyAdded()) {
            new CalendarUpdateTask(this, this).execute();
        }
        final DateTime now = DateTime.now();
        final DateTime next = now.withDayOfMonth(CalendarUpdateTask.getLastDay(now));
        final AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(this, CalendarUpdateService.class);
        final PendingIntent calendarIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarm.setRepeating(AlarmManager.RTC, next.getMillis(), INTERVAL_MILLIS, calendarIntent);

        // ToDo: activate service
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    @Override
    public void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            final String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(ACCOUNT_NAME, null);
            if (accountName != null) {
                manager.setAccountName(accountName);
                manager.getApiResults(this);
            } else {
                startActivityForResult(ApiManager.getCredential().newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(this,
                    "This app requests access to your Google account (via contacts)",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        switch (request) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (result != RESULT_OK) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.common_google_play_services_api_unavailable_text)
                            .create().show();
                } else {
                    manager.getApiResults(this);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (result == RESULT_OK && data != null && data.getExtras() != null) {
                    final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        getPreferences(Context.MODE_PRIVATE).edit()
                                .putString(ACCOUNT_NAME, accountName).apply();
                        manager.setAccountName(accountName);
                        manager.getApiResults(this);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (result == RESULT_OK) {
                    manager.getApiResults(this);
                }
                break;
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability.getInstance()
                .getErrorDialog(this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES).show();
    }

    private boolean isAlreadyAdded() {
        final String lastUpdateTime = getPreferences(MODE_PRIVATE).getString(LAST_UPDATE, null);
        if (lastUpdateTime == null) {
            return false;
        }
        final DateTime date = DateTime.parse(lastUpdateTime);
        return CalendarUpdateTask.getLastDay(date) == CalendarUpdateTask.getLastDay(Date.today());
    }

}
