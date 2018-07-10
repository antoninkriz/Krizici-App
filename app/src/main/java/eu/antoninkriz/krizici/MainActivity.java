package eu.antoninkriz.krizici;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import eu.antoninkriz.krizici.fragments.FragmentAbout;
import eu.antoninkriz.krizici.fragments.FragmentContacts;
import eu.antoninkriz.krizici.fragments.FragmentMain;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment fragment;
    private long mBackPressed;
    private Bundle b = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get downloaded JSON strings
        Intent i = getIntent();
        String jsonRozvrh = i.getStringExtra("jsonRozvrh");
        String jsonContacts = i.getStringExtra("jsonContacts");

        // Add them to the budnle so we can move this around
        b.putString("jsonRozvrh", jsonRozvrh);
        b.putString("jsonContacts", jsonContacts);

        FragmentMain fm = new FragmentMain();
        fm.setArguments(b);

        changeFragment(fm);

        // Navigation drawer stuff
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (fragment != null && !fragment.isAdded()) {
                    changeFragment(fragment);
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Init navigation drawer and change its title and subtitle
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set some text in navigation drawers header
        View header = navigationView.getHeaderView(0);
        TextView subText = header.findViewById(R.id.navSubtitle);
        subText.setText(BuildConfig.VERSION_NAME);

        // Hide tabs
        TabLayout tl = findViewById(R.id.tab_layout);
        tl.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + 2000 > System.currentTimeMillis()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAndRemoveTask();
                } else {
                    finishAffinity();
                }
                return;
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.doubleclicktoexit), Toast.LENGTH_SHORT).show();
            }

            mBackPressed = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation drawer item clicks
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home: {
                item.setChecked(true);
                fragment = new FragmentMain();
                fragment.setArguments(b);
            }
            break;
            case R.id.nav_contacts: {
                item.setChecked(true);
                fragment = new FragmentContacts();
                fragment.setArguments(b);
            }
            break;
            case R.id.nav_about: {
                item.setChecked(true);
                fragment = new FragmentAbout();
            }
            break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment(Fragment fragment) {
        // Dont change fragment when same fragment is already active
        if (fragment == null) return;
        if (fragment.isAdded()) return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        fragment = null;
        mBackPressed = 0;
        b = null;

        super.onDestroy();
    }
}
