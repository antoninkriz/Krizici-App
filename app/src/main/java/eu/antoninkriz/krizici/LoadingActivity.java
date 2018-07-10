package eu.antoninkriz.krizici;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import eu.antoninkriz.krizici.exceptions.UnknownException;
import eu.antoninkriz.krizici.exceptions.network.FailedDownloadException;
import eu.antoninkriz.krizici.utils.JsonHelper;
import eu.antoninkriz.krizici.utils.Network;


public class LoadingActivity extends AppCompatActivity {

    SharedPreferences prefs;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Init all needed classes
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // Check network
        boolean haveInternet = Network.checkNetworkConnection(getBaseContext());

        // Do stuff async
        new Async_GetJson(this).execute(haveInternet);
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
        prefs = null;
        mBackPressed = 0;

        super.onDestroy();
    }

    private static class Async_GetJson extends AsyncTask<Boolean, Void, Async_GetJson.ERROR> {
        private Result r;
        private WeakReference<LoadingActivity> activityReference;

        Async_GetJson(LoadingActivity context) {
            activityReference = new WeakReference<>(context);
        }

        protected ERROR doInBackground(Boolean... bool) {
            if (!bool[0]) {
                return ERROR.NO_INTERNET;
            }

            r = new Result();

            try {
                r.jRozvrh = JsonHelper.getJson(JsonHelper.DOWNLOADFILE.TIMETABLES);
                r.jContacts = JsonHelper.getJson(JsonHelper.DOWNLOADFILE.CONTACTS);
            } catch (FailedDownloadException e) {
                e.printStackTrace();
                return ERROR.FAILED_DOWNLOAD;
            } catch (UnknownException e) {
                e.printStackTrace();
                return ERROR.UNKNOWN_ERROR;
            }

            return null;
        }

        @Override
        protected void onPostExecute(ERROR error) {
            final LoadingActivity activity = activityReference.get();

            if (error != null) {
                String message = error.message;

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

                errorTextView.setText(message);
                errorTextView.setVisibility(View.VISIBLE);

                return;
            }


            Intent i = new Intent(activity, MainActivity.class);
            i.putExtra("jsonRozvrh", r.jRozvrh);
            i.putExtra("jsonContacts", r.jContacts);
            activity.startActivity(i);
            activity.finish();
        }

        private class Result {
            String jRozvrh = "";
            String jContacts = "";
        }

        public enum ERROR {
            CAN_NOT_CONNECT("Není možné navázat připojení k serveru"),
            FAILED_DOWNLOAD("Data nebylo možné stáhnout ze serveru"),
            NO_INTERNET("Zařízení není připojeno k internetu"),
            NO_INTERNET_PERMISSION("Nelze získat informace o připojení k internetu"),
            UNKNOWN_ERROR("Nastala neznámá chyba");

            private String message;

            ERROR(String message) {
                this.message = message;
            }
        }
    }
}
