package eu.antoninkriz.krizici.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

import eu.antoninkriz.krizici.R;

public class FragmentMain extends Fragment {

    List<ArrayList<String>> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);

        String json = getArguments().getString("jsonRozvrh");

        if (json != null) {
            try {
                JsonObject jo_Object = Json.parse(json).asObject();

                ArrayList<String> templist = new ArrayList<>();

                JsonArray ja_Arr = jo_Object.get("tridy").asArray();
                for (JsonValue v : ja_Arr) {
                    templist.add(v.asString());
                }
                list.add(templist);
                templist = new ArrayList<>();

                ja_Arr = jo_Object.get("ucitele").asArray();
                for (JsonValue v : ja_Arr) {
                    templist.add(v.asString());
                }
                list.add(templist);
                templist = new ArrayList<>();

                ja_Arr = jo_Object.get("ucebny").asArray();
                for (JsonValue v : ja_Arr) {
                    templist.add(v.asString());
                }
                list.add(templist);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Nastala chyba načítání dat. Zkuste to znovu", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // Init viewpager adapter
        ViewPagerAdapter _adapter = new ViewPagerAdapter(getChildFragmentManager());

        // Add fragments to adapter
        _adapter.add(new FragmentView());
        _adapter.add(new FragmentView());
        _adapter.add(new FragmentView());
        _adapter.add(new FragmentView());

        // Set adapter to viewPager
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(_adapter);

        // Setup tabs and show them in a cool way
        TabLayout tl = getActivity().findViewById(R.id.tab_layout);
        tl.animate().scaleY(1).setInterpolator(new DecelerateInterpolator()).start();
        tl.setVisibility(View.VISIBLE);
        tl.setupWithViewPager(viewPager, true);

        // Add tabs to tabLayout, this is defense against reloading this fragment
        int tab_count = tl.getTabCount();
        if (tab_count != 4) {
            tl.removeAllTabs();
            tl.addTab(tl.newTab());
            tl.addTab(tl.newTab());
            tl.addTab(tl.newTab());
            tl.addTab(tl.newTab());
        }

        return view;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> _fragments;

        private ViewPagerAdapter(FragmentManager activity) {
            super(activity);

            this._fragments = new ArrayList<>();
        }

        private void add(Fragment fragment) {
            this._fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            // Add tab pos to show correct supplementation in FragmentDen
            Bundle bundle = new Bundle();
            if (position > 0) bundle.putStringArrayList("list", list.get(position - 1));
            bundle.putInt("pos", (position - 1));
            Fragment f = this._fragments.get(position);
            f.setArguments(bundle);
            return f;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            String title = null;

            switch (position) {
                case 0:
                    title = "Suplování";
                    break;
                case 1:
                    title = "Žáci";
                    break;
                case 2:
                    title = "Učitelé";
                    break;
                case 3:
                    title = "Třídy";
                    break;
            }

            return title;
        }
    }
}