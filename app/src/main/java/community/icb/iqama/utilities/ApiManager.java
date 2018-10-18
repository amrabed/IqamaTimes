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
public class ApiManager {
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    //    private final Context context;
    private static ApiManager instance = null;

    private static GoogleAccountCredential credential;
    private final Listener listener;

    private ApiManager(Listener listener) {
        this.listener = listener;
    }

    public static ApiManager newInstance(Activity context, Listener listener) {
        if (instance == null) {
            instance = new ApiManager(listener);
            credential = GoogleAccountCredential.usingOAuth2(
                    context.getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
        }
        return instance;
    }

    public static GoogleAccountCredential getCredential() {
        return credential;
    }

    public static void setCredential(GoogleAccountCredential credential) {
        ApiManager.credential = credential;
    }

    public void getApiResults(Context context) {
        if (!isGooglePlayServicesAvailable(context)) {
            acquireGooglePlayServices(context);
        } else if (credential.getSelectedAccountName() == null) {
            listener.chooseAccount();
        } else if (!isDeviceOnline(context)) {
            listener.onConnectionError();
        } else {
            listener.onApiReady(credential);
        }

    }

    private boolean isDeviceOnline(Context context) {
        final ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable(Context context) {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    private void acquireGooglePlayServices(Context context) {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            listener.onApiError(connectionStatusCode);
        }
    }

    public void setAccountName(String accountName) {
        credential.setSelectedAccountName(accountName);
    }

    public interface Listener {
        void onConnectionError();

        void onApiError(int connectionStatusCode);

        void onApiReady(GoogleAccountCredential credential);

        void chooseAccount();
    }

}
