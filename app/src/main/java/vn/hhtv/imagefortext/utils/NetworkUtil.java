package vn.hhtv.imagefortext.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vinh on 10/8/15.
 */
public class NetworkUtil {
    private static NetworkUtil instance = null;

    private ConnectivityManager connectivityManager;

    private NetworkUtil(Context context) {
        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static NetworkUtil getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtil(context);
        }

        return instance;
    }

    /**
     * Method use to check network of device is really available
     *
     * @return true if really connect, else return false
     */
    public boolean isConnection(String urlAddress) {
        boolean isOnline = false;
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(1000);
            connection.connect();
            isOnline = connection.getResponseCode() == 200;
        } catch (Exception e) {
        }
        return isOnline;
    }

    /**
     * Method use to check network of device
     *
     * @return true if really connect, else return false
     */
    public boolean isConnect() {
        return isConnectWifi() || isConnect3G();
    }

    /**
     * Method used to check connect wifi
     *
     * @return : true if connect, else false
     */
    public boolean isConnectWifi() {
        NetworkInfo wifi = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null) {
            return wifi.isConnected();
        }
        return false;
    }

    /**
     * Method used to check connect 3G
     *
     * @return : true if connect, else false;
     */
    public boolean isConnect3G() {
        NetworkInfo mobile3G = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile3G != null) {
            return mobile3G.isConnected();
        }
        return false;
    }
}
