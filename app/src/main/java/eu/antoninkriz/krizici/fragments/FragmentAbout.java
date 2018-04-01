package eu.antoninkriz.krizici.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import eu.antoninkriz.krizici.R;

public class FragmentAbout extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        //setHasOptionsMenu(true);

        TabLayout tl = getActivity().findViewById(R.id.tab_layout);
        tl.animate().scaleY(1).setInterpolator(new DecelerateInterpolator()).start();
        tl.setVisibility(View.GONE);

        return view;
    }

    /* @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.mainFragment_menuGroup, false);
    }*/

}
