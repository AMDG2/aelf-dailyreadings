package co.epitre.aelf_lectures.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.epitre.aelf_lectures.NetworkStatusMonitor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jean-tiare on 9/02/18.
 */

// singleton
// provide API to batch requests
// manage the cache
// deal with static / pre-loaded assets
// deal with missing network
public class AelfApi implements NetworkStatusMonitor.NetworkStatusChangedListener, SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * Configuration
     */
    public static final String API_ENDPOINT = "https://api.app.epitre.co";
    private static final String PREF_KEY_SERVER = "pref_participate_server";
    private static final String PREF_KEY_BETA = "pref_participate_beta";
    private static final String PREF_KEY_NO_CACHE = "pref_participate_nocache";

    /**
     * Clients
     */
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Was 60 seconds
            .writeTimeout  (60, TimeUnit.SECONDS) // Was 10 minutes
            .readTimeout   (60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    /**
     * Internal state
     */
    private Context ctx;
    private SharedPreferences preference;
    private NetworkStatusMonitor networkStatusMonitor;

    private String endpoint = API_ENDPOINT;
    private boolean isNetworkAvailable;
    private boolean bypassRemoteCache;

    public AelfApi(Context ctx) {
        this.ctx = ctx;

        // Get preferences
        preference = PreferenceManager.getDefaultSharedPreferences(ctx);
        preference.registerOnSharedPreferenceChangeListener(this);
        updateBaseUrl();

        // Get network status
        networkStatusMonitor = NetworkStatusMonitor.getInstance();
        networkStatusMonitor.registerNetworkStatusChangeListener(this);
        isNetworkAvailable = networkStatusMonitor.isNetworkAvailable();
    }

    /**
     * Send a GET request to the API server
     *
     * @param path API path, like /47/office/...
     * @return
     * @throws IOException
     */
    public String get(String path) throws IOException {
        // TODO: skip network access when there is no network
        // TODO: cache results
        // TODO: load from asset in last resort + change storage format (hash ?)
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(endpoint+path);

        if (bypassRemoteCache) {
            requestBuilder.addHeader("x-aelf-nocache", "1");
        }

        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private void updateBaseUrl() {
        boolean pref_beta = preference.getBoolean(PREF_KEY_BETA, false);
        bypassRemoteCache = preference.getBoolean(PREF_KEY_NO_CACHE, false);
        String endpoint = preference.getString(PREF_KEY_SERVER, "");

        // If the URL was not overloaded, build it
        if (endpoint.equals("")) {
            endpoint = API_ENDPOINT;

            // If applicable, switch to beta
            if (pref_beta) {
                endpoint = endpoint.replaceAll("^(https?://)", "$1beta.");
            }
        }

        this.endpoint = endpoint;
    }

    @Override
    public void onNetworkStatusChanged(NetworkStatusMonitor.NetworkStatusEvent networkStatusEvent) {
        isNetworkAvailable = networkStatusMonitor.isNetworkAvailable();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PREF_KEY_BETA:
            case PREF_KEY_SERVER:
            case PREF_KEY_NO_CACHE:
                updateBaseUrl();
                break;
        }
    }
}
