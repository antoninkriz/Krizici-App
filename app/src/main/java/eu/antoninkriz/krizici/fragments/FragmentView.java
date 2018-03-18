package eu.antoninkriz.krizici.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

import eu.antoninkriz.krizici.R;

public class FragmentView extends Fragment {

    private int tabposition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // Init controls
        MaterialSpinner s = view.findViewById(R.id.spinner);
        final WebView vw = view.findViewById(R.id.webview);
        vw.getSettings().setJavaScriptEnabled(false);
        vw.getSettings().setLoadWithOverviewMode(true);
        vw.getSettings().setUseWideViewPort(true);
        vw.getSettings().setSupportZoom(true);
        vw.getSettings().setBuiltInZoomControls(true);
        vw.getSettings().setDisplayZoomControls(false);

        // Get selected tab from arguments bundle
        tabposition = getArguments().getInt("pos");

        // If "Supl" then skip
        if (tabposition == -1) {
            s.setVisibility(View.GONE);
            vw.setVisibility(View.INVISIBLE);
            vw.setWebViewClient(new WebViewClient());

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

        // Get list from arguments bundle
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> passedAsArg = getArguments().getStringArrayList("list");
        list.add("Zvolte položu");
        list.addAll(passedAsArg);

        Context c = getContext();

        if (c == null)
            return view;

        s.setItems(list);

        s.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position > 0) {
                    String type = (tabposition == 0) ? "tridy" : (tabposition == 1) ? "ucitele" : "ucebny";
                    vw.loadUrl("https://files.antoninkriz.eu/apps/krizici/img" + type + "-" + (position - 1) + ".png");
                } else {
                    vw.loadUrl("about:blank");
                }
            }
        });

        return view;
    }

}
