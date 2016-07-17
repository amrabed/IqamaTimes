package community.icb.iqama.main;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import org.joda.time.DateTime;

import java.util.List;

import community.icb.iqama.R;
import community.icb.iqama.utilities.ApiManager;
import community.icb.iqama.utilities.CalendarUpdateTask;
import community.icb.iqama.utilities.Date;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;

/**
 * Main Activity
 *
 * @author AmrAbed
 */
public class Main extends FragmentActivity implements View.OnClickListener,
        DialogInterface.OnClickListener, PermissionCallbacks,
        CalendarUpdateTask.Listener, ApiManager.Listener
{
    private static final String ACCOUNT_NAME = "Account name";

    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 0;
    private static final int REQUEST_ACCOUNT_PICKER = 1;
    private static final int REQUEST_AUTHORIZATION = 2;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 3;
    private static final String LAST_UPDATE = "last update";

    private ApiManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        ((ViewPager) findViewById(R.id.pager)).setAdapter(new Adapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.calendar:
                new AlertDialog.Builder(this).setTitle(R.string.calendar)
                        .setMessage((isAlreadyAdded() ? R.string.already_added : R.string.calendar_message))
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                manager = new ApiManager(Main.this, Main.this);
                                manager.getApiResults();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isAlreadyAdded()
    {
        final String lastUpdateTime = getPreferences(MODE_PRIVATE).getString(LAST_UPDATE, null);
        if (lastUpdateTime == null)
        {
            return false;
        }
        final DateTime date = DateTime.parse(lastUpdateTime);
        return CalendarUpdateTask.getLastDay(date) == CalendarUpdateTask.getLastDay(Date.today());
    }

    @Override
    public void onClick(View v)
    {
        new AlertDialog.Builder(this).setTitle(R.string.support).setItems(R.array.support, this)
                .create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int i)
    {
        final String[] ids = {"5T6WN83XVX3BC", "N46UMV92GCUD2", "Y75AT2XMDNZ3G"};
        final Uri uri = Uri
                .parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=" + ids[i]);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public void onRequestPermissionsResult(int request, @NonNull String[] permissions,
                                           @NonNull int[] results)
    {
        super.onRequestPermissionsResult(request, permissions, results);
        EasyPermissions.onRequestPermissionsResult(request, permissions, results, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms)
    {
        // Do nothing
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms)
    {
        // Do nothing
    }

    @Override
    public void onCalendarUpdateSuccess()
    {
        getPreferences(MODE_PRIVATE).edit().putString(LAST_UPDATE, Date.today().toString()).apply();
        showSnackbar("Prayer times added successfully");
    }

    @Override
    public void onCalendarUpdateError(Exception error)
    {
        if (error != null)
        {
            if (error instanceof GooglePlayServicesAvailabilityIOException)
            {
                final int code = ((GooglePlayServicesAvailabilityIOException) error)
                        .getConnectionStatusCode();
                showGooglePlayServicesAvailabilityErrorDialog(code);
            }
            else if (error instanceof UserRecoverableAuthIOException)
            {
                startActivityForResult(((UserRecoverableAuthIOException) error).getIntent(),
                        REQUEST_AUTHORIZATION);
            }
            else
            {
                showSnackbar(error.getMessage());
            }
        }
        else
        {
            showSnackbar("Request Cancelled");
        }
    }

    @Override
    public void onConnectionError()
    {
        showSnackbar("Device not connected to the Internet");
    }

    private void showSnackbar(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onApiError(int connectionStatusCode)
    {
        showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
    }

    @Override
    public void onApiReady(GoogleAccountCredential credential)
    {
        new CalendarUpdateTask(this, credential, this).execute();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    @Override
    public void chooseAccount()
    {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS))
        {
            final String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(ACCOUNT_NAME, null);
            if (accountName != null)
            {
                manager.setAccountName(accountName);
                manager.getApiResults();
            }
            else
            {
                startActivityForResult(manager.getCredential().newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        }
        else
        {
            EasyPermissions.requestPermissions(this,
                    "This app requests access to your Google account (via contacts)",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data)
    {
        super.onActivityResult(request, result, data);
        switch (request)
        {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (result != RESULT_OK)
                {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.common_google_play_services_api_unavailable_text)
                            .create().show();
                }
                else
                {
                    manager.getApiResults();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (result == RESULT_OK && data != null && data.getExtras() != null)
                {
                    final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null)
                    {
                        getPreferences(Context.MODE_PRIVATE).edit()
                                .putString(ACCOUNT_NAME, accountName).apply();
                        manager.setAccountName(accountName);
                        manager.getApiResults();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (result == RESULT_OK)
                {
                    manager.getApiResults();
                }
                break;
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode)
    {
        GoogleApiAvailability.getInstance()
                .getErrorDialog(this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES).show();
    }

    private class Adapter extends FragmentPagerAdapter
    {

        public Adapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return Section.newInstance(position);
        }

        @Override
        public int getCount()
        {
            return 100;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return Date.today().plusDays(position).toString(Date.DEFAULT_FORMAT);
        }
    }
}
