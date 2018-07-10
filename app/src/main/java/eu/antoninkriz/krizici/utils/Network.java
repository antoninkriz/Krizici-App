package eu.antoninkriz.krizici.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import eu.antoninkriz.krizici.exceptions.network.FailedDownloadException;

public class Network {
    protected static class Result {
        final String result;
        final boolean success;
        final FailedDownloadException exception;

        Result(boolean success, String result, FailedDownloadException ex) {
            this.success = success;
            this.result = result;
            this.exception = ex;
        }
    }

    public static Result downloadString(String requestUrl) {
        try {
            // Init connection
            URL url = new URL(requestUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(Consts.SERVER_TIMEOUT);
            con.setReadTimeout(Consts.SERVER_TIMEOUT);

            // Get response
            int responseCode = con.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                // Read response
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return new Result(true, response.toString(), null);
            }
        } catch (Exception e) {
            Log.i("NETWORK", e.getMessage());
        }

        return new Result(false, null, new FailedDownloadException("Failed to download file '" + requestUrl + "'", 0));
    }

    public static boolean checkNetworkConnection(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        }

        return false;
    }
}
