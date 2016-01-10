package com.rsdt.jotiv2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.android.internal.util.Predicate;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rsdt.jotial.JotiApp;
import com.rsdt.jotial.mapping.area348.DataManager;
import com.rsdt.jotial.mapping.area348.JotiInfoWindowAdapter;
import com.rsdt.jotial.mapping.area348.MapManager;

import com.rsdt.jotiv2.fragments.JotiMapFragment;
import com.rsdt.jotiv2.fragments.JotiPreferenceFragment;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    /**
     * The MapManager of the app, for managing map usage.
     * */
    private MapManager mapManager = new MapManager();

    /**
     * Static variable that holds the current page of the app.
     * */
    static AppPages currentPage = AppPages.HOME;


    private Fragment mVisible;

    /**
     * The MapFragment of the app, kept in memory for speed.
     * */
    private JotiMapFragment mapFragment;

    /**
     * The PreferenceFragment of the app, kept in memory for speed.
     * */
    private JotiPreferenceFragment preferenceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!MapManager.isInitialized)
        {
            /**
             * Initializes the static part of the MapManager.
             * */
            MapManager.initializeStaticPart();
        }
        else
        {
            /**
             * The MapManager has already been initialized.
             * Load in old map data.
             * */
            if(savedInstanceState != null) {
                mapManager.postponeFromBundle(savedInstanceState.getBundle("mapManager"));
            }
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setupFAB();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpFragments();

        ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    private void setupFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotate = new RotateAnimation(0, 360,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                rotate.setDuration(2000);
                rotate.setRepeatCount(Animation.INFINITE);
                view.startAnimation(rotate);
                view.setEnabled(false);
                MapManager.fetch();

                /**
                 * Subscribe a callback, so we can be informed when the updating has been completed.
                 * */
                JotiApp.MainTracker.subscribe(new Tracker.TrackerSubscriberCallback() {

                    /**
                     * PC PROCESSING COMPLETED
                     * CC CLUSTERING COMPLETED
                     * */
                    private boolean PC = false, CC = false;

                    @Override
                    public void onConditionMet(Tracker.TrackerMessage message) {
                        switch (message.getIdentifier()) {
                            case DataManager.TRACKER_DATAMANAGER_PROCESSING_COMPLETED:
                                PC = true;
                                break;
                            case MapManager.TRACKER_MAPMANAGER_CLUSTERING_COMPLETED:
                                CC = true;
                                break;
                        }

                        /**
                         * Check if both are true, this meaning the processing and clustering are completed.
                         * */
                        if (PC && CC) {
                            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                            fab.clearAnimation();
                            JotiApp.MainTracker.postponeUnsubscribe(this);
                            fab.setEnabled(true);
                        }
                    }
                }, new Predicate<String>() {
                    @Override
                    public boolean apply(String s) {
                        return (s.equals(DataManager.TRACKER_DATAMANAGER_PROCESSING_COMPLETED) || s.equals(MapManager.TRACKER_MAPMANAGER_CLUSTERING_COMPLETED));
                    }
                });

            }
        });
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBundle("mapManager", mapManager.toBundle());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Marker ma = googleMap.addMarker(new MarkerOptions().position(new LatLng(52.015351, 6.025963)).title("lol"));
        mapManager.initialize(googleMap);
        googleMap.setInfoWindowAdapter(new JotiInfoWindowAdapter(this.getLayoutInflater(), mapManager.getMapBehaviourManager()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {

            if(!preferenceFragment.isHidden())
            {
                getFragmentManager().beginTransaction().hide(preferenceFragment).commit();
            }

            getSupportFragmentManager().beginTransaction().show(mapFragment).commit();

        } else if (id == R.id.nav_settings) {

            if(!mapFragment.isHidden())
            {
                getSupportFragmentManager().beginTransaction().hide(mapFragment).commit();
            }

            getFragmentManager().beginTransaction().show(preferenceFragment).commit();

        } else if (id == R.id.nav_spot) {

            /**
             * Checks if the current page is the Map page, if its begin spot.
             * Else switch to the Map page first, and then begin spot.
             * */
            if(currentPage == AppPages.MAP)
            {
                spotVos();
            }
            else
            {
                /**
                 * Switch to the Map page.
                 * */
                spotVos();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Switches to the desired page.
     * */
    private void switchToPage(AppPages page)
    {
        Fragment fragment = null;
        switch (page)
        {
            case HOME:
                if(currentPage != AppPages.HOME)
                {
                    currentPage = AppPages.HOME;
                    ((Toolbar)findViewById(R.id.toolbar)).setTitle("Home");
                }
                break;
            case MAP:
                if(currentPage != AppPages.MAP)
                {

                    currentPage = AppPages.MAP;
                    ((Toolbar)findViewById(R.id.toolbar)).setTitle("Map");

                    if(mapManager.getGoogleMap() == null)
                    {
                        mapFragment.getMapAsync(this);
                    }
                    else
                    {
                        mapManager.getGoogleMap().addMarker(new MarkerOptions().position(new LatLng(52.009463, 5.963567)).title("lol"));
                    }

                }
                break;
            case SETTINGS:
                if(currentPage != AppPages.SETTINGS)
                {

                    currentPage = AppPages.SETTINGS;
                    ((Toolbar)findViewById(R.id.toolbar)).setTitle("Settings");
                }
                break;
        }

/*        *//**
         * Checks if the fragment should be swapped.
         * *//*
        if(fragment != null)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content, fragment);
            transaction.commit();
        }*/
    }

    /**
     * Begins the spotting sequence of a vos.
     * */
    private void spotVos()
    {
        /**
         * Create Snackbar to tell the user what needs to be done.
         * */
        /*Snackbar.make(findViewById(R.id.content), "Markeer de vossen op de kaart.", Snackbar.LENGTH_INDEFINITE).setAction("Klaar!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//**
                 * Report that the location selection has been completed.
                 * *//*
                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_MAINACTIVITY_SPOTTING_LOCATION_SELECTION_COMPLETED, "MainActivity", "The spot has finished."));
            }
        }).setActionTextColor(Color.parseColor("#E91E63")).show();*/

        /**
         * Tell the MapManager that spotting is active.
         * Meaning a marker will be placed on the location the user clicks on the map.
         * */
        mapManager.spot();
    }



    private void setUpFragments() {
        FragmentTransaction sft = getSupportFragmentManager().beginTransaction();

        // If the activity is killed while in BG, it's possible that the
        // fragment still remains in the FragmentManager, so, we don't need to
        // add it again.
        mapFragment = (JotiMapFragment) getSupportFragmentManager().findFragmentByTag(JotiMapFragment.TAG);
        if (mapFragment == null) {
            mapFragment = JotiMapFragment.newInstance();
            sft.add(R.id.container_map, mapFragment, JotiMapFragment.TAG);
        }
        sft.show(mapFragment);
        sft.commit();

        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        preferenceFragment = (JotiPreferenceFragment) getFragmentManager().findFragmentByTag(JotiPreferenceFragment.TAG);
        if (preferenceFragment == null) {
            preferenceFragment = JotiPreferenceFragment.newInstance();
            ft.add(R.id.container_settings, preferenceFragment, JotiPreferenceFragment.TAG);
        }
        ft.hide(preferenceFragment);
        ft.commit();
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        switch(level)
        {
            case TRIM_MEMORY_RUNNING_LOW:
                MapManager.trim();
                break;
        }

    }

    public void onDestroy()
    {
        super.onDestroy();

        /**
         * Check if the MapManager isn't null, if so then destroy it and set it to null.
        * */
        if(mapManager != null)
        {
            mapManager.destroy();
            mapManager = null;
        }
    }

    /**
     * Defines a identifier for the completion of the location selection.
     * */
    public static final String TRACKER_MAINACTIVITY_SPOTTING_LOCATION_SELECTION_COMPLETED = "TRACKER_MAINACTIVITY_SPOTTING_LOCATION_SELECTION_COMPLETED";
}
