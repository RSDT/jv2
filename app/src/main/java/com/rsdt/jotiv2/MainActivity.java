package com.rsdt.jotiv2;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiRequest;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.LinkBuilder;
import com.rsdt.jotial.data.structures.area348.receivables.VosInfo;
import com.rsdt.jotiv2.fragments.JotiMapFragment;
import com.rsdt.jotiv2.fragments.JotiPreferenceFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ApiManager.OnApiTaskComplete {

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ApiManager apiManager = new ApiManager();

        LinkBuilder.setRoot("http://jotihunt-api.area348.nl");

        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "a", "all"}), null));
        apiManager.addListener(this);

        apiManager.preform();
    }

    @Override
    public void onApiTaskCompleted(ArrayList<ApiResult> results) {
        ApiResult result = results.get(0);
        String[] args = result.getRequest().getUrl().getPath().split("/");
        switch(args[1])
        {
            case "vos":
                VosInfo[] data = VosInfo.fromJsonArray(result.getData());
                Log.i("MainActivity", "Data deserialized.");
                break;
        }
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
            if(!(fragment instanceof JotiMapFragment))
            {
                switchFragment = true;
                fragment = new JotiMapFragment();
            }
            // Handle the camera action
        } else if (id == R.id.nav_settings) {
            if(!(fragment instanceof JotiPreferenceFragment))
            {
                switchFragment = true;
                fragment = new JotiPreferenceFragment();
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
}
