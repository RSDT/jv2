package com.rsdt.jotiv2;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.rsdt.jotial.JotiApp;
import com.rsdt.jotial.communication.ApiRequest;
import com.rsdt.jotial.communication.LinkBuilder;
import com.rsdt.jotial.communication.area348.Area348API;
import com.rsdt.jotial.mapping.area348.JotiInfoWindowAdapter;
import com.rsdt.jotial.mapping.area348.MapManager;
import com.rsdt.jotiv2.fragments.JotiMapFragment;
import com.rsdt.jotiv2.fragments.JotiPreferenceFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    MapManager mapManager;

    static AppPages currentPage = AppPages.HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                mapManager.update();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(currentPage == AppPages.MAP)
        {
            if(getFragmentManager().findFragmentById(R.id.content) != null)
            {
                ((MapFragment) getFragmentManager().findFragmentById(R.id.content)).getMapAsync(this);
            }
        }

        /**
         * Initialize maps, without it we can't use certain classes and methods. Such as BitmapDescriptorFactory.
         * */
        MapsInitializer.initialize(JotiApp.getContext());

        /**
         * Get the ApiManager and add a listener to it, the listener is the data manager that will process the data for us.
         * */
        MapManager.getApiManager().addListener(MapManager.getDataManager());

        /**
         * Set the root of the LinkBuilder to the Area348's one.
         * */
        LinkBuilder.setRoot(Area348API.root);

        /**
         * Queue in some requests.
         * */
        MapManager.getApiManager().queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "a", "all"}), null));
        MapManager.getApiManager().queue(new ApiRequest(LinkBuilder.build(new String[]{"sc", "all"}), null));

        /**
         * Preform the requests.
         * */
        MapManager.getApiManager().preform();

    }


    public void onSaveInstanceState(Bundle savedInstanceState) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapManager = new MapManager(googleMap);
        mapManager.sync();
        googleMap.setInfoWindowAdapter(new JotiInfoWindowAdapter(this.getLayoutInflater(), mapManager.getMapBehaviourManager()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = getFragmentManager().findFragmentById(R.id.content);

        boolean switchFragment = false;

        if (id == R.id.nav_map) {
            if(currentPage != AppPages.MAP)
            {
                currentPage = AppPages.MAP;
                switchFragment = true;
                fragment = new JotiMapFragment();
                ((MapFragment)fragment).getMapAsync(this);
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("Map");
            }
        } else if (id == R.id.nav_settings) {
            if(currentPage != AppPages.SETTINGS)
            {
                currentPage = AppPages.SETTINGS;
                switchFragment = true;
                fragment = new JotiPreferenceFragment();
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("Settings");
            }
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }


        if(switchFragment)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content, fragment);
            transaction.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        if(mapManager != null)
        {
            mapManager.destroy();
            mapManager = null;
        }
    }

}
