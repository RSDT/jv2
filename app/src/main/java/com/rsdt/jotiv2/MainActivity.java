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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.rsdt.jotial.mapping.area348.JotiInfoWindowAdapter;
import com.rsdt.jotial.mapping.area348.MapManager;
import com.rsdt.jotial.mapping.area348.data.MapData;
import com.rsdt.jotial.mapping.area348.behaviour.VosInfoBehaviour;
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
                ((ImageButton)view).startAnimation(rotate);
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

        if(savedInstanceState != null)
        {
     /*       MapDataPair<MarkerOptions> pair2 = savedInstanceState.getBundle("pairBundle").getParcelable("pair");
            System.out.print("");
            System.out.print("LOL");*/


            MapData mapData = savedInstanceState.getParcelable("mapData");
            System.out.print("");
        }
    }


    String data = "[{\"id\":\"30\",\"datetime\":\"2015-10-18 14:27:29\",\"latitude\":\"52.180247501296\",\"longitude\":\"6.1351861143609\",\"team\":\"a\",\"team_naam\":\"Alpha\",\"opmerking\":\"\",\"gebruiker\":\"31\"}]";

    public void onSaveInstanceState(Bundle savedInstanceState) {
   /*     MapDataPair<MarkerOptions> pair = new MapDataPair<>(new MarkerOptions(), new ArrayList<BaseInfo>());
        Bundle bundle = new Bundle();
        bundle.putParcelable("pair", pair);
        savedInstanceState.putBundle("pairBundle", bundle);*/
        MapData mapData = MapData.from(data, new VosInfoBehaviour(null));
        savedInstanceState.putParcelable("mapData", mapData);
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
        googleMap.setInfoWindowAdapter(new JotiInfoWindowAdapter(this.getLayoutInflater(), mapManager));
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
