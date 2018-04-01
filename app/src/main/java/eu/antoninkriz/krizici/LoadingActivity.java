package eu.antoninkriz.krizici;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoadingActivity extends AppCompatActivity {

    SharedPreferences prefs;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Init all needed classes
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        int connErr = -1;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected()))
                connErr = 1;
        } else {
            connErr = -1;
        }

        new Async_GetJson(this).execute(connErr);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finishAffinity();
            }
            return;
        }

        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        prefs = null;
        mBackPressed = 0;
    }

    private static class Async_GetJson extends AsyncTask<Integer, Void, Void> {
        private Result r;
        private WeakReference<LoadingActivity> activityReference;

        Async_GetJson(LoadingActivity context) {
            activityReference = new WeakReference<LoadingActivity>(context);
        }

        protected Void doInBackground(Integer... ints) {
            r = new Result();

            // Check for network
            if (ints[0] == 0 || ints[0] == 1) {
                r.ok = ints[0];
                return null;
            }

            // Connect
            try {
                // Get rozvrhy and contacts
                if (!getStringFromURL(0)) {
                    return null;
                }

                // Skip if failed first getStringFromURL
                getStringFromURL(1);

                return null;
            } catch (Exception e) {
                e.printStackTrace();
                r.ok = 2;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            final LoadingActivity activity = activityReference.get();

            if (r.ok != -1) {
                String error;
                switch (r.ok) {
                    case 0:
                        error = "Nelze získat informace o připojení k internetu";
                        break;
                    case 1:
                        error = "Zařízení není připojeno k internetu";
                        break;
                    case 2:
                        error = "Data nebylo možné stáhnout ze serveru";
                        break;
                    case 3:
                        error = "Není možné navázat připojení k serveru";
                        break;
                    default:
                        error = "Nastala neznámá chyba";
                        break;
                }

                ProgressBar prg = activity.findViewById(R.id.progressBar);
                Button reloadButton = activity.findViewById(R.id.button);
                TextView errorTextView = activity.findViewById(R.id.errorTextView);

                prg.setVisibility(View.GONE);

                reloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.finish();
                        activity.startActivity(activity.getIntent());
                    }
                });
                reloadButton.setVisibility(View.VISIBLE);

                errorTextView.setText(error);
                errorTextView.setVisibility(View.VISIBLE);
            } else {
                Intent i = new Intent(activity, MainActivity.class);
                i.putExtra("jsonRozvrh", r.jRozvrh);
                i.putExtra("jsonContacts", r.jContacts);
                activity.startActivity(i);
                activity.finish();
            }
        }

        private boolean getStringFromURL(int URLId) throws Exception {
            String strUrl = URLId == 0 ? "https://files.antoninkriz.eu/apps/krizici/json.json" : "https://files.antoninkriz.eu/apps/krizici/contacts.json";

            URL url = new URL(strUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

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

                r.ok = -1;
                if (URLId == 0) {
                    r.jRozvrh = response.toString();
                } else {
                    r.jContacts = response.toString();
                }
                return true;
            }

            r.ok = 3;
            return false;
        }

        private class Result {
            int ok;
            String jRozvrh = "";
            String jContacts = "";
        }
    }
}
