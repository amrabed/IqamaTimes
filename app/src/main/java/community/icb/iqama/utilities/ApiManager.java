package community.icb.iqama.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

/**
 * Calendar API manager
 *
 * @author AmrAbed
 */
public class ApiManager
{
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    private final Context context;

    public GoogleAccountCredential getCredential()
    {
        return credential;
    }

    private final GoogleAccountCredential credential;
    private final Listener listener;

    public ApiManager(Activity context, Listener listener)
    {
        this.context = context;
        this.listener = listener;

        credential = GoogleAccountCredential.usingOAuth2(
                context.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

    }

    public void getApiResults()
    {
        if (!isGooglePlayServicesAvailable())
        {
            acquireGooglePlayServices();
        }
        else if (credential.getSelectedAccountName() == null)
        {
            listener.chooseAccount();
        }
        else if (!isDeviceOnline())
        {
            listener.onConnectionError();
        }
        else
        {
            listener.onApiReady(credential);
        }

    }

    private boolean isDeviceOnline()
    {
        final ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable()
    {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    private void acquireGooglePlayServices()
    {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (apiAvailability.isUserResolvableError(connectionStatusCode))
        {
            listener.onApiError(connectionStatusCode);
        }
    }

    public void setAccountName(String accountName)
    {
        credential.setSelectedAccountName(accountName);
    }

    public interface Listener
    {
        void onConnectionError();

        void onApiError(int connectionStatusCode);

        void onApiReady(GoogleAccountCredential credential);

        void chooseAccount();
    }

}
