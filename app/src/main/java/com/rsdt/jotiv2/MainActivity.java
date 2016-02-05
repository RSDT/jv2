package com.rsdt.jotiv2;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.internal.util.Predicate;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.rsdt.jotial.JotiApp;

import com.rsdt.jotial.communication.area348.Auth;
import com.rsdt.jotial.mapping.area348.DataProcessingManager;
import com.rsdt.jotial.mapping.area348.JotiInfoWindowAdapter;
import com.rsdt.jotial.mapping.area348.MapManager;

import com.rsdt.jotiv2.fragments.JotiLoginDialogFragment;
import com.rsdt.jotiv2.fragments.JotiPreferenceFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, Tracker.TrackerSubscriberCallback {

    /**
     * The MapManager of the app, for managing map usage.
     * */
    private MapManager mapManager = new MapManager();

    /**
     * The FragmentNavigationManager of the app, for managing the navigation.
     * */
    private FragmentNavigationManager navigationManager = new FragmentNavigationManager();

    /**
     * The SpottingManager of the app, for the control of spotting.
     * */
    private SpottingManager spottingManager = new SpottingManager();


    private FloatingActionButtonMenu floatingActionButtonMenu = new FloatingActionButtonMenu();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(!MapManager.StaticPart.isInitialized)
        {
            /**
             * Initializes the static part of the MapManager.
             * */
            MapManager.StaticPart.initialize();
        }
        else
        {

        }

        /**
         * Subscribe to the MainTracker, with no filter so that we receive all messages.
         * */
        JotiApp.MainTracker.subscribe(this, null);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationManager.initialize(savedInstanceState);

        if(savedInstanceState != null)
        {
            navigationManager.mapFragment.onCreate(savedInstanceState);
        }

        //setupFAB();


        floatingActionButtonMenu.initialize();

        updateNavHeader();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void updateNavHeader()
    {
        /**
         * Get the NavigaitionView.
         * */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        /**
         * Get the preferences.
         * */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());

        /**
         * Set the navigation name view to the current name.
         * */
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_name)).setText(preferences.getString("pref_account_username", "Guest"));

        /**
         * Set the navigation rank view to the current rank.
         * */
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_rank)).setText(preferences.getString("pref_account_rank", "Guest"));

    }



    /**
     * Class that controls and maintains the FloatingActionButton menu.
     * */
    private class FloatingActionButtonMenu implements SnackbarControl.OnSnackBarShowCallback
    {

        /**
         * Value indicating if the menu is visible or not.
         * */
        public boolean visible = false;

        /**
         * Initializes the menu.
         * */
        public void initialize()
        {
            /**
             * Listen to the OnShowSnackbar event, if it occurs we need to hide the menu.
             * */
            SnackbarControl.addListener(this);

            /**
             * Find the main FAB, and hook the show and hide method to the click event.
             * */
            FloatingActionButton main = (FloatingActionButton)findViewById(R.id.fab_main);
            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!visible)
                    {
                        floatingActionButtonMenu.show();
                        visible = true;
                    }
                    else
                    {
                        floatingActionButtonMenu.hide();
                        visible = false;
                    }
                }
            });

            /**
             * Find the sync mini, then setup the animation and sync.
             * */
            FloatingActionButton sync = (FloatingActionButton)findViewById(R.id.fab_sync);
            sync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RotateAnimation rotate = new RotateAnimation(0, 360,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                            0.5f);
                    rotate.setDuration(2000);
                    rotate.setRepeatCount(Animation.INFINITE);
                    v.startAnimation(rotate);
                    v.setEnabled(false);


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
                                case DataProcessingManager.TRACKER_DATAMANAGER_PROCESSING_COMPLETED:
                                    PC = true;
                                    break;
                                case MapManager.TRACKER_MAPMANAGER_CLUSTERING_COMPLETED:
                                    CC = true;
                                    break;
                                case MapManager.Fetcher.TRACKER_FETCHER_FETCHING_FAILED_AUTH_REQUIRED:
                                    PC = true;
                                    CC = true;
                                    break;
                            }

                            /**
                             * Check if both are true, this meaning the processing and clustering are completed.
                             * */
                            if (PC && CC) {
                                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_sync);
                                fab.clearAnimation();
                                JotiApp.MainTracker.postponeUnsubscribe(this);
                                fab.setEnabled(true);
                            }
                        }
                    }, new Predicate<String>() {
                        @Override
                        public boolean apply(String s) {
                            return (s.equals(DataProcessingManager.TRACKER_DATAMANAGER_PROCESSING_COMPLETED) ||
                                    s.equals(MapManager.TRACKER_MAPMANAGER_CLUSTERING_COMPLETED) ||
                                    s.equals(MapManager.Fetcher.TRACKER_FETCHER_FETCHING_FAILED_AUTH_REQUIRED));
                        }
                    });

                    /**
                     * Tell the Syncer to sync.
                     * */
                    MapManager.Syncer.sync();
                }
            });

            FloatingActionButton search = (FloatingActionButton)findViewById(R.id.fab_search);


            FloatingActionButton follow = (FloatingActionButton)findViewById(R.id.fab_follow);
        }

        /**
         * Shows the menu.
         * */
        public void show()
        {
            repositionFab((FloatingActionButton)findViewById(R.id.fab_sync), FAB_SHOW, 1);
            repositionFab((FloatingActionButton)findViewById(R.id.fab_search), FAB_SHOW, 2);
            repositionFab((FloatingActionButton)findViewById(R.id.fab_follow), FAB_SHOW, 3);
        }

        /**
         * Hides the menu.
         * */
        public void hide()
        {
            repositionFab((FloatingActionButton)findViewById(R.id.fab_sync), FAB_HIDE, 1);
            repositionFab((FloatingActionButton)findViewById(R.id.fab_search), FAB_HIDE, 2);
            repositionFab((FloatingActionButton)findViewById(R.id.fab_follow), FAB_HIDE, 3);
        }

        private void repositionFab(FloatingActionButton fab, String action, int anim)
        {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();

            Animation animation = null;

            switch (action)
            {
                case FAB_SHOW:
                    fab.setClickable(true);
                    switch (anim)
                    {
                        case 1:
                            layoutParams.leftMargin += (int) (fab.getWidth() * horizontalMargin);
                            layoutParams.bottomMargin += (int) (fab.getHeight() * verticalMargin);
                            animation = AnimationUtils.loadAnimation(JotiApp.getContext(), R.anim.fab1_show);
                            break;
                        case 2:
                            layoutParams.bottomMargin += (int) (fab.getHeight() * verticalMarginTop);
                            animation = AnimationUtils.loadAnimation(JotiApp.getContext(), R.anim.fab2_show);
                            break;
                        case 3:
                            layoutParams.rightMargin += (int) (fab.getWidth() * horizontalMargin);
                            layoutParams.bottomMargin += (int) (fab.getHeight() * verticalMargin);
                            animation = AnimationUtils.loadAnimation(JotiApp.getContext(), R.anim.fab3_show);
                            break;
                    }
                    break;
                case FAB_HIDE:
                    fab.setClickable(false);
                    switch (anim)
                    {
                        case 1:
                            layoutParams.leftMargin -= (int) (fab.getWidth() * horizontalMargin);
                            layoutParams.bottomMargin -= (int) (fab.getHeight() * verticalMargin);
                            animation = AnimationUtils.loadAnimation(JotiApp.getContext(), R.anim.fab1_hide);
                            break;
                        case 2:
                            layoutParams.bottomMargin -= (int) (fab.getHeight() * verticalMarginTop);
                            animation = AnimationUtils.loadAnimation(JotiApp.getContext(), R.anim.fab2_hide);
                            break;
                        case 3:
                            layoutParams.rightMargin -= (int) (fab.getWidth() * horizontalMargin);
                            layoutParams.bottomMargin -= (int) (fab.getHeight() * verticalMargin);
                            animation = AnimationUtils.loadAnimation(JotiApp.getContext(), R.anim.fab3_hide);
                            break;
                    }
                    break;
            }
            fab.setLayoutParams(layoutParams);
            fab.startAnimation(animation);
        }

        @Override
        public void onSnackbarShow(Snackbar snackbar) {
            //hide();
        }

        public void destroy()
        {
            SnackbarControl.removeListener(this);
        }

        /**
         * Defines the horizontal margin between the main FAB and the minis.
         * */
        public static final float horizontalMargin = 1.5f;

        /**
         * Defines the vertical margin between the border and the minis.
         * */
        public static final float verticalMargin = 0.25f;

        /**
         * Defines the vertical margin between the top mini and the main FAB.
         * */
        public static final float verticalMarginTop = 1.7f;

        /**
         * Defines the action show.
         * */
        public static final String FAB_SHOW = "FAB_SHOW";

        /**
         * Defines the action hide.
         * */
        public static final String FAB_HIDE = "FAB_HIDE";

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState.putBundle("mapManager", mapManager.toBundle());
        //navigationManager.saveInstanceState(savedInstanceState);
        navigationManager.mapFragment.onSaveInstanceState(savedInstanceState);
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

    public void onResume()
    {
        super.onResume();

        /**
         * Get the map async.
         * */
        ((MapFragment)getFragmentManager().findFragmentById(R.id.container_map)).getMapAsync(this);

        /**
         * Report to the tracker, that the UI is available.
         * */
        JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_MAINACTIVITY_UI_AVAILABLE, "MainActivity", "The UI is available."));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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

            /**
             *
             * Check if there's a spot going on, if so end it.
             * NOTE: While spotting, when the user clicks on the map item,
             * he probably will intend to end the spotting.
             * */
            if(spottingManager.isActive)
            {
                spottingManager.endSpot();
            }

            navigationManager.switchTo(FragmentNavigationManager.MAP_TAG);
        } else if (id == R.id.nav_settings) {
            navigationManager.switchTo(FragmentNavigationManager.PREFERENCE_TAG);
        } else if (id == R.id.nav_spot) {
            /**
             * Switch to the Map page.
             * */
            navigationManager.switchTo(FragmentNavigationManager.MAP_TAG);

            /**
             * Begin spotting.
             * */
            spottingManager.beginSpot();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    /**
     * The callback for the TrackerSubscriber MainActivity.
     * */
    public void onConditionMet(Tracker.TrackerMessage message) {
        switch (message.getIdentifier())
        {
            case Auth.TRACKER_AUTH_REQUIRED:

                if(!JotiApp.Auth.isAuthDialogActive())
                {
                    DialogFragment fragment = new JotiLoginDialogFragment();
                    fragment.show(getFragmentManager(), "login");
                    JotiApp.Auth.setAuthDialogActive(true);
                }

                break;
            case TRACKER_MAINACTIVITY_UI_NAV_SWITCH:

                /**
                 * Check if there's a spotting active, if so interrupt it.
                 * */
                if(spottingManager.isActive)
                {
                    /**
                     * End the spot.
                     * */
                    spottingManager.endSpot();
                }
                break;
            case Auth.TRACKER_AUTH_SUCCEEDED:
                SnackbarControl.show(Snackbar.make(findViewById(R.id.content_layout), "Succesvol ingelogd.", Snackbar.LENGTH_SHORT));

                /**
                 * Update the navigation header, with the latest details.
                 * */
                updateNavHeader();
                break;
            case Auth.TRACKER_AUTH_FAILED_UNAUTHORIZED:
                SnackbarControl.show(Snackbar.make(findViewById(R.id.content_layout), "Kon niet inloggen.", Snackbar.LENGTH_LONG).setAction("Opnieuw", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JotiApp.Auth.requireAuth();
                    }
                }).setActionTextColor(Color.parseColor("#E91E63")));
                break;
            case TRACKER_MAINACTIVITY_PREFERENCE_REQUIRED:
                SnackbarControl.show(Snackbar.make(findViewById(R.id.content_layout), "Zet de " + message.getDescripition() + " eigenschap bij settings.", Snackbar.LENGTH_LONG).setAction("Ga naar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigationManager.switchTo(FragmentNavigationManager.PREFERENCE_TAG);
                    }
                }).setActionTextColor(Color.parseColor("#E91E63")));
                break;
        }
    }

    /**
     * Inner class for managing the navigation and fragment rotation.
     * */
    private class FragmentNavigationManager
    {
        /**
         * The tag of the current active fragment.
         * */
        private String currentFragmentTag;

        /**
         * The MapFragment of the app, kept in memory for speed.
         * */
        private MapFragment mapFragment;

        /**
         * The PreferenceFragment of the app, kept in memory for speed.
         * */
        private JotiPreferenceFragment preferenceFragment;

        /**
         * Initializes the FragmentNavigationManager.
         * */
        public void initialize(Bundle savedInstanceState)
        {
            setUpFragments();

            /**
             * Check if the saved state isn't null, if so get the saved current fragment tag.
             * */
            if(savedInstanceState != null)
            {
                /**
                 * Switch to the saved tag.
                 * */
                //switchTo(savedInstanceState.getString("currentFragmentTag"));

                updateCheckedState();
            }
        }

        public void updateCheckedState()
        {
            /**
             * Set the NavigationView's checked item, depending on the tag.
             * */
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            switch(currentFragmentTag)
            {
                case MAP_TAG:
                    navigationView.getMenu().findItem(R.id.nav_map).setChecked(true);
                    break;
                case PREFERENCE_TAG:
                    navigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
                    break;
            }
        }

        /**
         * Save the state of the FragmentNavigationManager.
         * */
        public void saveInstanceState(Bundle savedInstanceState)
        {
            savedInstanceState.putString("currentFragmentTag", currentFragmentTag);
        }

        /**
         * Gets the value indicating if the FragmentNavigationManager is on the given tag.
         * */
        public boolean isOnFragment(String tag) { return (tag.equals(currentFragmentTag)); }

        /**
         * Switches to the desired fragment based on the given tag.
         * */
        public void switchTo(String tag)
        {
            /**
             * Check to see if we're not already on the fragment.
             * */
            if(!isOnFragment(tag))
            {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                switch (tag)
                {
                    case MAP_TAG:
                        fragmentTransaction.hide(preferenceFragment);
                        fragmentTransaction.show(mapFragment);
                        ((Toolbar)findViewById(R.id.toolbar)).setTitle("Map");
                        break;
                    case PREFERENCE_TAG:
                        fragmentTransaction.hide(mapFragment);
                        fragmentTransaction.show(preferenceFragment);
                        ((Toolbar)findViewById(R.id.toolbar)).setTitle("Settings");
                        break;
                }
                fragmentTransaction.commit();
                currentFragmentTag = tag;

                updateCheckedState();

                /**
                 * Inform possible listeners that a fragment is switched.
                 * */
                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_MAINACTIVITY_UI_NAV_SWITCH, "FragmentNavigationManager", "A fragment has been switched."));
            }
            else
            {
                /**
                 * Already on the same page.
                 * */
            }
        }

        /**
         * Setup the fragments.
         * */
        private void setUpFragments() {
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            // If the activity is killed while in BG, it's possible that the
            // fragment still remains in the FragmentManager, so, we don't need to
            // add it again.
            mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MAP_TAG);
            if (mapFragment == null) {
                mapFragment = MapFragment.newInstance();
                ft.add(R.id.container_map, mapFragment, MAP_TAG);
            }
            ft.hide(mapFragment);

            preferenceFragment = (JotiPreferenceFragment) getFragmentManager().findFragmentByTag(PREFERENCE_TAG);
            if (preferenceFragment == null) {
                preferenceFragment = JotiPreferenceFragment.newInstance();
                ft.add(R.id.container_settings, preferenceFragment, PREFERENCE_TAG);
            }
            ft.hide(preferenceFragment);
            ft.commit();
        }


        /**
         * Disposes the object.
         * */
        public void destroy()
        {

            /**
             * Check if the mapFragment isn't null, if so set it to null.
             * */
            if(mapFragment != null)
            {
                mapFragment = null;
            }

            /**
             * Check if the preferenceFragment isn't null, if so set it to null.
             * */
            if(preferenceFragment != null)
            {
                preferenceFragment = null;
            }
        }

        /**
         * Defines the tag for the MapFragment.
         * */
        public static final String MAP_TAG = "MAP";

        /**
         * Defines the tag for the PreferenceFragment.
         * */
        public static final String PREFERENCE_TAG = "PREFERENCE";

    }

    /**
     * Class for the spotting of the vos.
     * */
    private class SpottingManager
    {

        /**
         * Value indicating if there is a spot active.
         * */
        boolean isActive = false;

        /**
         * The Snackbar made to inform the user what to do.
         * */
        Snackbar snackbar;

        /**
         * Begins the spot.
         * */
        public void beginSpot()
        {
            /**
             * Create Snackbar to tell the user what needs to be done.
             * */
            snackbar = Snackbar.make(findViewById(R.id.container_map), "Markeer de vossen op de kaart.", Snackbar.LENGTH_INDEFINITE).setAction("Klaar!", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * Report that the location selection has been completed.
                     * */
                    JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_MAINACTIVITY_SPOTTING_LOCATION_SELECTION_COMPLETED, "MainActivity", "The spot has finished."));

                    /**
                     * Set the Spot item checked property to false, since we're no longer spotting.
                     * */
                    ((NavigationView)findViewById(R.id.nav_view)).getMenu().findItem(R.id.nav_spot).setChecked(false);

                    /**
                     * We're now on the map, so set the Map item checked property to true.
                     * */
                    ((NavigationView)findViewById(R.id.nav_view)).getMenu().findItem(R.id.nav_map).setChecked(true);
                }
            }).setActionTextColor(Color.parseColor("#E91E63"));

            /**
             * Show the Snackbar with the manager.
             * */
            SnackbarControl.show(snackbar);

            /**
             * Begin the spotting.
             * */
            mapManager.getSpottingManager().beginSpot();

            /**
             * Spotting is now active.
             * */
            isActive = true;
        }

        /**
         * End the spotting.
         * */
        public void endSpot()
        {
            /**
             * Close the Snackbar.
             * */
            snackbar.dismiss();

            /**
             * End the spotting.
             * */
            mapManager.getSpottingManager().endSpot();

            /**
             * Spotting is no longer active.
             * */
            isActive = false;
        }

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

        /**
         * Check if the FragmentNavigationManager isn't null, if so then destroy it and set it to null.
         * */
        if(navigationManager != null)
        {
            navigationManager.destroy();
            navigationManager = null;
        }

        /**
         * Check if the FloatingActionButtonMenu isn't null, if so then destroy it and set it to null.
         * */
        if(floatingActionButtonMenu != null)
        {
            floatingActionButtonMenu.destroy();
            floatingActionButtonMenu = null;
        }

        /**
         * Unsubscribe to prevent leakage.
         * */
        JotiApp.MainTracker.unsubscribe(this);
    }


    /**
     * Defines a tracker identifier for the completion of the location selection.
     * */
    public static final String TRACKER_MAINACTIVITY_SPOTTING_LOCATION_SELECTION_COMPLETED = "TRACKER_MAINACTIVITY_SPOTTING_LOCATION_SELECTION_COMPLETED";

    /**
     * Defines a tracker identifier for when the UI is available.
     * */
    public static final String TRACKER_MAINACTIVITY_UI_AVAILABLE = "TRACKER_MAINACTIVITY_UI_AVAILABLE";

    /**
     * Defines a tracker identifier for when the navigation switches fragment.
     * */
    public static final String TRACKER_MAINACTIVITY_UI_NAV_SWITCH = "TRACKER_MAINACTIVITY_UI_NAV_SWITCH";

    /**
     * Defines a tracker identifier for when a preference is required.
     * */
    public static final String TRACKER_MAINACTIVITY_PREFERENCE_REQUIRED = "TRACKER_MAINACTIVITY_PREFERENCE_REQUIRED";

}
