package eu.antoninkriz.krizici.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

import eu.antoninkriz.krizici.R;

public class FragmentView extends Fragment {

    private WebView vw;
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            displayError(view);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        private void displayError(WebView wv) {
            wv.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Nastala chyba při načítání. Zkuste to znovu", Toast.LENGTH_LONG).show();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // Init controls
        MaterialSpinner s = view.findViewById(R.id.spinner);
        vw = view.findViewById(R.id.webview);
        vw.getSettings().setJavaScriptEnabled(false);
        vw.getSettings().setLoadWithOverviewMode(true);
        vw.getSettings().setUseWideViewPort(true);
        vw.getSettings().setSupportZoom(true);
        vw.getSettings().setBuiltInZoomControls(true);
        vw.getSettings().setDisplayZoomControls(false);
        vw.setWebViewClient(webViewClient);

        // Get selected tab from arguments bundle
        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey("pos")) {
            Toast.makeText(getContext(), "Nastala chyba při načítání rozvrhů. Zkuste to znovu", Toast.LENGTH_LONG).show();
            return view;
        }

        int tabposition = getArguments().getInt("pos");

        // If tab "Supl"
        if (tabposition == -1) {
            s.setVisibility(View.GONE);
            vw.setVisibility(View.INVISIBLE);

            final Button btnLoadSupl = view.findViewById(R.id.buttonLoadSupl);
            btnLoadSupl.setVisibility(View.VISIBLE);

            btnLoadSupl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vw.loadUrl("https://nastenka.skolakrizik.cz");
                    btnLoadSupl.setVisibility(View.GONE);
                    vw.setVisibility(View.VISIBLE);
                }
            });

            return view;
        }

        // Continue if not tab "Supl"
        // Get list from arguments bundle
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> passedAsArg = getArguments().getStringArrayList("list");
        list.add("Zvolte položu");
        list.addAll(passedAsArg);

        final String type = (tabposition == 0) ? "tridy" : (tabposition == 1) ? "ucitele" : "ucebny";
        final String urlFormat = "https://files.antoninkriz.eu/apps/krizici/img" + type + "-%s.png";

        s.setItems(list);
        s.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position > 0) {
                    vw.loadUrl(String.format(urlFormat, (position - 1)));
                } else {
                    vw.loadUrl("about:blank");
                }
            }
        });

        return view;
    }
}