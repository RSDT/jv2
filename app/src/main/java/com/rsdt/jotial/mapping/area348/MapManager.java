package com.rsdt.jotial.mapping.area348;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.android.internal.util.Predicate;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import com.rsdt.jotial.JotiApp;
import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiRequest;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.LinkBuilder;
import com.rsdt.jotial.data.structures.area348.receivables.ScoutingGroepInfo;
import com.rsdt.jotial.mapping.area348.behaviour.BehaviourManager;
import com.rsdt.jotial.mapping.area348.behaviour.ScoutingGroepClusterBehaviour;
import com.rsdt.jotial.mapping.area348.clustering.ScoutingGroepInfoRenderer;
import com.rsdt.jotial.mapping.area348.data.GraphicalMapData;
import com.rsdt.jotial.mapping.area348.data.ItemAction;
import com.rsdt.jotial.mapping.area348.data.MapData;
import com.rsdt.jotial.mapping.area348.data.MapPartState;

import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * The final control unit for map managing.
 */

public class MapManager implements ApiManager.OnApiTaskCompleteCallback, JotiInfoWindowAdapter.OnGetInfoWindowCallback, DataManager.OnDataTaskCompletedCallback {

    /**
     * Initializes a new instance of MapManager.
     *
     * @param googleMap The GoogleMap the MapManager should manage on.
     * */
    public MapManager(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        if(!servicesActive)
        {
            apiManager.addListener(dataManager);
            servicesActive = true;
        }
        dataManager.addListener(this);
        apiManager.addListener(this);
        scClusterManager = new ClusterManager<>(JotiApp.getContext(), googleMap);
        scClusterManager.setRenderer(new ScoutingGroepInfoRenderer(JotiApp.getContext(), googleMap, scClusterManager));
    }


    public static boolean servicesActive = false;

    /**
     * The GoogleMap the MapManager should work with.
     * */
    private GoogleMap googleMap;

    /**
     * The behaviour manager of the MapManager.
     * */
    private BehaviourManager behaviourManager = new BehaviourManager();

    /**
     * The data manager of the MapManager.
     * */
    private static DataManager dataManager = new DataManager();

    /**
     * The ApiManager for requesting data, and receiving it.
     * TODO: Look NOTE.
     * NOTE: ApiManager is should be static here, because else data could be lost.
     * While data is still beining received the activity could be dumped and thereby the data.
     * This way we maintain the data.
     * */
    private static ApiManager apiManager = new ApiManager();

    /**
     * The cluster manager for the scouting groups.
     * */
    private ClusterManager<ScoutingGroepInfo> scClusterManager;

    /**
     * Syncs the GraphicalMapData of each state with it's storage.
     * */
    public void sync()
    {
        LinkBuilder.setRoot("http://jotihunt-api.area348.nl");

        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "a", "all"}), null));
        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"sc", "all"}), null));

        googleMap.setOnCameraChangeListener(scClusterManager);
        googleMap.setOnMarkerClickListener(scClusterManager);

        apiManager.preform();
    }

    /**
     * Updates the MapData of each state.
     * */
    public void update()
    {
    }



    Circle circle;

    @Override
    /**
     * The code that gets executed on a getInfoWindow call.
     * */
    public void onGetInfoWindow(View view, Marker marker) {

        if(circle != null)
        {
            circle.remove();
            circle = null;
        }

        /**
         * Invoke the behaviour manager, he will invoke all the behaviours.
         * */
        behaviourManager.onGetInfoWindow(view, marker);

        /**
         * TODO: make switch cases for situation, and change the interface so that the type is already defined. onGetInfoWindow(Marker, MapPart);
         * */
        if(marker.getTitle().split(";")[0].equals("sc"))
        {
            /**
             * NOTE: The circle doesn't have to be shown all the time, only if desired.
             * Consider making the circle visible on infoWindow click.
             * */
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.radius(500);
            circleOptions.center(marker.getPosition());
            circleOptions.fillColor(parse(marker.getTitle().split(";")[5].toLowerCase(), 96));
            circleOptions.strokeWidth(0);
            circle = googleMap.addCircle(circleOptions);
        }
        if(marker.getTitle().startsWith("vos"))
        {
            /**
             * The circle for the possible vos location.
             * */
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(marker.getPosition());
            circleOptions.fillColor(parse(marker.getTitle().split(" ")[1], 96));
            circleOptions.strokeWidth(0);

            /**
             * TODO: Make constants for speed and other settings.
             * */
            try
            {
                String dateTime = marker.getTitle().split(" ")[3] + " " + marker.getTitle().split(" ")[4];
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                Date date = dateFormat.parse(dateTime);

                long duration = new Date().getTime() - date.getTime();

                float diffInHours = TimeUnit.MILLISECONDS.toSeconds(duration)/60f/60f;

                if (diffInHours > 30)
                    diffInHours = 30;
                circleOptions.radius(diffInHours * 6000);
                circle = googleMap.addCircle(circleOptions);
            } catch (Exception e)
            {
                Log.e("MapManger", "onGetInfoWindow(View, Marker) - circle radius calculation with vos's date failed");
            }
        }
    }


    @Override
    public void onApiTaskCompleted(ArrayList<ApiResult> results) {
        ApiResult currentResult;
        for(int r = 0; r < results.size(); r++)
        {
            currentResult = results.get(r);
            if(currentResult.getRequest().getUrl().getPath().split("/")[1].equals("sc"))
            {
                ScoutingGroepInfo[] sc = ScoutingGroepInfo.fromJsonArray(currentResult.getData());
                for(int i = 0; i < sc.length; i++)
                {
                    scClusterManager.addItem(sc[i]);
                }
                scClusterManager.cluster();
                behaviourManager.registerBehaviour(new ScoutingGroepClusterBehaviour());
            }
        }

    }

    @Override
    public void onDataTaskCompleted(ArrayList<MapPartState> mapPartStates) {

        /**
         * State should not be able to be created if there's no MapData.
         * */
        MapPartState currentState;
        for(int i = 0; i < mapPartStates.size(); i++)
        {
            currentState = mapPartStates.get(i);

            /**
             * Checks if the MapData is not null, if it is it indicates a special case.
             * */
            if(currentState.getMapData() != null)
            {
                currentState.setGraphicalMapData(GraphicalMapData.from(currentState.getMapData(), googleMap));
            }
            behaviourManager.registerBehaviour(currentState.getBehaviour());
        }
    }

    /**
     * Disposes the MapManager and removes listeners.
     * */
    public void destroy()
    {
        this.scClusterManager.clearItems();
        this.scClusterManager = null;

        apiManager.removeListener(this);
        dataManager.removeListener(this);

        this.behaviourManager.destroy();
        this.behaviourManager = null;

        this.googleMap = null;
    }

    /**
     * Trim the memory of the MapManager.
     * */
    public static void trim()
    {
        apiManager.trim();
        dataManager.trim();
    }

    public static int parse(String string, int a) {
        switch (string) {
            case "a":
                return Color.argb(a, 255, 0, 0);
            case "b":
                return Color.argb(a, 0, 255, 0);
            case "c":
                return Color.argb(a, 0, 0, 255);
            case "d":
                return Color.argb(a, 0, 255, 255);
            case "e":
                return Color.argb(a, 255, 0, 255);
            case "f":
                return Color.argb(a, 255, 162, 0);
            case "x":
                return Color.argb(a, 0, 0, 0);
            default:
                return Color.WHITE;
        }
    }

}