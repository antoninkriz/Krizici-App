package eu.antoninkriz.krizici.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

import eu.antoninkriz.krizici.R;

public class FragmentContacts extends Fragment {

    private class Contact {
        String predmety;
        String jmeno;
        String zkratka;
        String telefon;
        String email;
    }

    private List<Contact> contacts = new ArrayList<>();

    private Context c;
    private float scale;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        TabLayout tl = getActivity().findViewById(R.id.tab_layout);
        tl.animate().scaleY(1).setInterpolator(new DecelerateInterpolator()).start();
        tl.setVisibility(View.GONE);

        LinearLayout lnl = view.findViewById(R.id.linearScrollLayout);
        scale = getResources().getDisplayMetrics().density;
        c = getContext();

        String json = getArguments().getString("jsonContacts");

        if (json != null) {
            try {
                JsonArray jo_Arr = Json.parse(json).asArray();

                for (JsonValue val : jo_Arr) {
                    JsonObject obj = val.asObject();
                    Contact c = new Contact();
                    c.predmety = obj.get("Předměty").isNull() ? "" : obj.get("Předměty").asString();
                    c.jmeno = obj.get("Jmeno").isNull() ? "" : obj.get("Jmeno").asString();
                    c.zkratka = obj.get("Zkratka").isNull() ? "" : obj.get("Zkratka").asString();
                    c.telefon = obj.get("Telefon").isNull() ? "" : obj.get("Telefon").asString();
                    c.email = obj.get("Email").isNull() ? "" : obj.get("Email").asString();
                    contacts.add(c);
                }

                for (int i = 0; i < jo_Arr.size(); i++) {
                    lnl.addView(addContactsCard(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Nastala chyba načítání kontaktů", Toast.LENGTH_SHORT).show();
                return view;
            }
        }

        return view;
    }

    private View addContactsCard(final int id) {
        int dp4 = dpToPx(4);
        int dp5 = dpToPx(5);
        
        // CardView
        CardView cw = new CardView(c);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp5, dp5, dp5, (id == contacts.size() - 1) ? 0 : dp5);
        cw.setLayoutParams(lp);

        // Layout inside CardWiew
        LinearLayout lnin = new LinearLayout(c);
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lnin.setLayoutParams(lp);
        lnin.setOrientation(LinearLayout.VERTICAL);
        lnin.setPadding(dp5, dp5, dp5, dp5);

        // TextView layout params
        ViewGroup.MarginLayoutParams lpm = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        
        TextView twName = new TextView(c);
        twName.setTextSize(18);
        twName.setText(contacts.get(id).zkratka + " - " + contacts.get(id).jmeno);
        twName.setLayoutParams(lpm);
        twName.setPadding(dp4, dp4, dp4, dp4);
        lnin.addView(twName);

        TextView twPredmety = new TextView(c);
        twPredmety.setText(contacts.get(id).predmety);
        twPredmety.setLayoutParams(lpm);
        twPredmety.setPadding(dp4, dp4, dp4, dp4);
        lnin.addView(twPredmety);

        if (!contacts.get(id).email.equals("")) {
            TextView twEmail = new TextView(c);
            SpannableString content = new SpannableString(contacts.get(id).email);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            twEmail.setText(content);
            twEmail.setLayoutParams(lpm);
            twEmail.setPadding(dp4, dp4, dp4, dp4);
            twEmail.setTextColor(getResources().getColor(R.color.colorPrimary));
            twEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{contacts.get(id).email});
                    startActivity(Intent.createChooser(emailIntent, "Napsat email..."));
                }
            });
            lnin.addView(twEmail);
        }

        if (!contacts.get(id).telefon.equals("")) {
            TextView twTelefon = new TextView(c);
            SpannableString content = new SpannableString(contacts.get(id).telefon);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            twTelefon.setText(content);
            twTelefon.setLayoutParams(lpm);
            twTelefon.setPadding(dp4, dp4, dp4, dp4);
            twTelefon.setTextColor(getResources().getColor(R.color.colorPrimary));
            twTelefon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contacts.get(id).telefon));
                    startActivity(intent);
                }
            });
            lnin.addView(twTelefon);
        }

        cw.addView(lnin);
        return cw;
    }

    private int dpToPx(int dp) {
        return (int) (dp * scale + 0.5f);
    }
}