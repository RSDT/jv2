package com.rsdt.jotial.mapping.area348;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import com.rsdt.jotial.JotiApp;
import com.rsdt.jotial.UpdateService;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.StaticApiManger;
import com.rsdt.jotial.data.structures.area348.receivables.ScoutingGroepInfo;
import com.rsdt.jotial.mapping.area348.behaviour.FotoOpdrachtMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.HunterMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.MapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.MapBehaviourManager;
import com.rsdt.jotial.mapping.area348.behaviour.ScoutingGroepMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.VosMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEvent;
import com.rsdt.jotial.mapping.area348.clustering.ScoutingGroepInfoRenderer;
import com.rsdt.jotial.mapping.area348.data.MapData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * The final control unit for map managing.
 */

public class MapManager implements JotiInfoWindowAdapter.OnGetInfoWindowCallback, DataManager.OnDataTaskCompletedCallback {

    /**
     * Initializes a new instance of MapManager.
     *
     * @param googleMap The GoogleMap the MapManager should manage on.
     * */
    public MapManager(GoogleMap googleMap)
    {
        this.googleMap = googleMap;

        dataManager.addListener(this);

        scClusterManager = new ClusterManager<>(JotiApp.getContext(), googleMap);
        scClusterManager.setRenderer(new ScoutingGroepInfoRenderer(JotiApp.getContext(), googleMap, scClusterManager));
        googleMap.setOnCameraChangeListener(scClusterManager);
        googleMap.setOnMarkerClickListener(scClusterManager);

        setupSpecialEvents();
    }

    /**
     * The GoogleMap the MapManager should work with.
     * */
    private GoogleMap googleMap;

    /**
     * The MapBehaviour manager for the MapManager.
     * */
    private MapBehaviourManager mapBehaviourManager = new MapBehaviourManager();

    /**
     * Gets the MapBehaviourManager of the MapManager.
     * */
    public MapBehaviourManager getMapBehaviourManager() {
        return mapBehaviourManager;
    }

    /**
     *
     * */
    private static StaticApiManger apiManager = new StaticApiManger();

    /**
     * Gets the ApiManager of the MapManager.
     * */
    public static StaticApiManger getApiManager() {
        return apiManager;
    }

    /**
     *
     * */
    private static StaticDataManager dataManager = new StaticDataManager();

    /**
     * Gets the DataManager of the MapManager.
     * */
    public static StaticDataManager getDataManager() {
        return dataManager;
    }

    /**
     * The cluster manager for the scouting groups.
     * */
    private ClusterManager<ScoutingGroepInfo> scClusterManager;

    /**
     * Syncs the GraphicalMapData of each state with it's storage.
     * */
    public void sync()
    {

    }

    /**
     * Updates the MapData of each state.
     * */
    public void update()
    {


    }

    /**
     * Circle to indicate something.
     * */
    Circle indicatorCircle;

    private void setupSpecialEvents()
    {
        /**
         * Register the special event that defines the special circles on a marker.
         * */
        mapBehaviourManager.getsEventRaiser().getEvents().add(new MapBehaviourEvent<Marker>(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW) {
            @Override
            public boolean apply(Marker marker) {
                return (marker.getTitle().startsWith("vos") || marker.getTitle().startsWith("sc"));
            }

            @Override
            public void onConditionMet(Object[] args) {

                Marker marker = (Marker) args[1];

                /**
                 * Checks if the indicatorCircle is null, if not compare to center.
                 * */
                if (indicatorCircle != null) {
                    /**
                     * Checks if the last circle is placed around the current marker, if so remove the circle.
                     * */
                    if (indicatorCircle.getCenter().equals(marker.getPosition())) {
                        /**
                         * Remove the current indicator circle.
                         * */
                        indicatorCircle.remove();
                        indicatorCircle = null;
                        return;
                    }
                    indicatorCircle.remove();
                }

                CircleOptions cOptions = new CircleOptions();
                cOptions.center(marker.getPosition());

                String[] strArgs = marker.getTitle().split(" ");

                switch (strArgs[0]) {
                    case "vos":
                        /**
                         * TODO: Make constants for speed and other settings.
                         * */
                        try {
                            String dateTime = strArgs[3] + " " + strArgs[4];
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                            Date date = dateFormat.parse(dateTime);

                            long duration = new Date().getTime() - date.getTime();

                            float diffInHours = TimeUnit.MILLISECONDS.toSeconds(duration) / 60f / 60f;

                            if (diffInHours > 30)
                                diffInHours = 30;
                            cOptions.radius(diffInHours * 6000);
                        } catch (Exception e) {
                            Log.e("MapManger", "onGetInfoWindow(View, Marker) - circle radius calculation with vos's date failed");
                        }

                        cOptions.strokeWidth(VosMapBehaviour.VOS_CIRCLE_STROKE_WIDTH);
                        cOptions.strokeColor(VosMapBehaviour.VOS_CIRCLE_STROKE_COLOR);
                        cOptions.fillColor(MapManager.parse(strArgs[1], VosMapBehaviour.VOS_CIRCLE_FILL_COLOR_ALPHA));
                        break;
                    case "sc":
                        cOptions.radius(ScoutingGroepMapBehaviour.SCOUTING_GROEP_CIRCLE_RADIUS);
                        cOptions.strokeWidth(ScoutingGroepMapBehaviour.SCOUTING_GROEP_CIRCLE_STROKE_WIDTH);
                        cOptions.strokeColor(ScoutingGroepMapBehaviour.SCOUTING_GROEP_CIRCLE_STROKE_COLOR);
                        cOptions.fillColor(MapManager.parse(strArgs[1].toLowerCase(), ScoutingGroepMapBehaviour.SCOUTING_GROEP_CIRCLE_FILL_COLOR_ALPHA));
                        break;
                }
                indicatorCircle = googleMap.addCircle(cOptions);
            }
        });
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
            }
        }

    }

    @Override
    public void onDataTaskCompleted(HashMap<String, MapData> hashMap) {
        for(Map.Entry<String, MapData> entry : hashMap.entrySet())
        {
            switch (entry.getKey())
            {
                case "vos":
                    this.mapBehaviourManager.add(new VosMapBehaviour(entry.getValue(), googleMap));
                    break;
                case "hunter":
                    this.mapBehaviourManager.add(new HunterMapBehaviour(entry.getValue(), googleMap));
                    break;
                case "sc":
                    this.mapBehaviourManager.add(new ScoutingGroepMapBehaviour(entry.getValue(), googleMap));
                    break;
                case "foto":
                    this.mapBehaviourManager.add(new FotoOpdrachtMapBehaviour(entry.getValue(), googleMap));
                    break;
            }
        }
    }

    public DataManager.OnCertainKeywordApiResult getSpecial() {
        return new DataManager.OnCertainKeywordApiResult() {
            @Override
            public String getKeyword() {
                return "sc";
            }

            @Override
            public void onConditionMet(ApiResult result) {
                ScoutingGroepInfo[] sc = ScoutingGroepInfo.fromJsonArray(result.getData());
                for(int i = 0; i < sc.length; i++)
                {
                    scClusterManager.addItem(sc[i]);
                }
                scClusterManager.cluster();
            }
        };
    }


    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 18-11-2015
     * Description...
     */
    public static class UpdateServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch(intent.getAction())
            {
                case UpdateService.UPDATE_SERVICE_ACTIONS_RENEW:
                    HashMap<String, MapData> hashMap = (HashMap<String, MapData>)intent.getSerializableExtra("hashMap");

                    System.out.print("");

                    break;
            }

        }
    }


    /**
     * Disposes the MapManager and removes listeners.
     * */
    public void destroy()
    {
        this.scClusterManager.clearItems();
        this.scClusterManager = null;

        this.mapBehaviourManager.destroy();
        this.mapBehaviourManager = null;

        dataManager.removeListener(this);

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