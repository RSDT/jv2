package com.rsdt.jotial.mapping.area348;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;

import com.rsdt.jotial.JotiApp;
import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiRequest;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.LinkBuilder;
import com.rsdt.jotial.communication.StaticApiManger;
import com.rsdt.jotial.communication.area348.Area348API;

import com.rsdt.jotial.data.structures.area348.receivables.ScoutingGroepInfo;

import com.rsdt.jotial.data.structures.area348.sendables.VosInfoSendable;
import com.rsdt.jotial.mapping.area348.behaviour.FotoOpdrachtMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.HunterMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.MapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.MapBehaviourManager;
import com.rsdt.jotial.mapping.area348.behaviour.ScoutingGroepMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.VosMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEvent;
import com.rsdt.jotial.mapping.area348.clustering.ScoutingGroepInfoRenderer;
import com.rsdt.jotial.mapping.area348.data.GraphicalMapData;
import com.rsdt.jotial.mapping.area348.data.MapData;
import com.rsdt.jotiv2.MainActivity;
import com.rsdt.jotiv2.R;
import com.rsdt.jotiv2.Tracker;

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

public class MapManager implements DataManager.OnDataTaskCompletedCallback, ApiManager.OnApiTaskCompleteCallback {

    /**
     * Initializes a new instance of MapManager.
     *
     * */
    public MapManager()
    {

    }

    /**
     * Initializes the MapManager for usage.
     *
     * @param googleMap The GoogleMap the MapManager should manage on.
     * */
    public void initialize(GoogleMap googleMap)
    {
        this.googleMap = googleMap;

        /**
         * Check if the buffer is null, if not there's previous instance data available.
         * */
        if(fromBundleBuffer != null)
        {
            /**
             * Extract the previous instance data, then set the buffer to null.
             * */
            fromBundle(fromBundleBuffer);
            fromBundleBuffer = null;
        }

        /**
         * Register as listener to the ApiManager, with a filter that applies only to ScoutingGroep Data.
         * */
        apiManager.addListener(this, new Predicate<ApiResult>() {
            @Override
            public boolean apply(ApiResult result) {
                return result.getRequest().getUrl().getPath().split("/")[1].equals("sc");
            }
        });

        /**
         * Register as listener to the DataManager.
         * */
        dataManager.addListener(this);

        /**
         * Initialize the ClusterManager.
         * */
        scClusterManager = new ClusterManager<>(JotiApp.getContext(), googleMap);
        scClusterManager.setRenderer(new ScoutingGroepInfoRenderer(JotiApp.getContext(), googleMap, scClusterManager));
        googleMap.setOnCameraChangeListener(scClusterManager);
        googleMap.setOnMarkerClickListener(scClusterManager);

        /**
         * Setup special events, such as the special indicator circle on OnGetInfoWindow().
         * */
        setupSpecialEvents();
    }

    /**
     * The GoogleMap the MapManager should work with.
     * */
    private GoogleMap googleMap;

    /**
     * Gets the GoogleMap of the MapManager.
     * */
    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    /**
     * The MapBehaviour manager for the MapManager, it holds the GraphicalMapData,
     * with its associated behaviour. The behaviour represents the way that should be acted,
     * on a certain event. Such like OnGetInfoWindow().
     * */
    private MapBehaviourManager mapBehaviourManager = new MapBehaviourManager();

    /**
     * Gets the MapBehaviourManager of the MapManager.
     * */
    public MapBehaviourManager getMapBehaviourManager() {
        return mapBehaviourManager;
    }

    /**
     * The static ApiManager of the MapManager, it fetches the new data, and then sends
     * it to the dataManager to be processed. There's however a special case, the ScoutingGroep is clustered,
     * this requires a special system. The data cannot be processed before the instance creation of the MapManager,
     * there for the ScoutingGroep data will be posted to the new instance.
     * */
    private static StaticApiManger apiManager = new StaticApiManger();

    /**
     * The static DataManager of the MapManager, it processes the data and then converts it to the MapData.
     * When a instance of MapManager is created, the generated MapData will be posted to the instance.
     * */
    private static StaticDataManager dataManager = new StaticDataManager();

    /**
     * The cluster manager for the scouting groups.
     * */
    private ClusterManager<ScoutingGroepInfo> scClusterManager;

    /**
     * The buffer that holds the last MapData for a short amount of time.
     * */
    private Bundle fromBundleBuffer = null;

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


    /**
     * Gets invoked when ScoutingGroep data is available, cause of the filter applied to this callback.
     * */
    public void onApiTaskCompleted(ArrayList<ApiResult> results) {

        /**
         * Clear the old items.
         * */
        scClusterManager.clearItems();

        /**
         * Allocate one ApiResult out the loop as buffer.
         * */
        ApiResult currentResult;

        /**
         * Loop through each result.
         * */
        for(int r = 0; r < results.size(); r++)
        {
            currentResult = results.get(r);

            /**
             * Deserialize the ScoutingGroep data.
             * */
            ScoutingGroepInfo[] sc = ScoutingGroepInfo.fromJsonArray(currentResult.getData());

            /**
             * Loop through them.
             * */
            for(int i = 0; i < sc.length; i++)
            {
                /**
                 * Add the data to the cluster manager.
                 * */
                scClusterManager.addItem(sc[i]);
            }

            /**
             * Signal the ClusterManager to cluster.
             * */
            scClusterManager.cluster();
        }

        /**
         * Inform the tracker that the clustering is completed.
         * */
        JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_MAPMANAGER_CLUSTERING_COMPLETED, "MapManager", "Clustering has been completed."));
    }

    @Override
    /**
     * Gets invoked when new MapData is available.
     * */
    public void onDataTaskCompleted(HashMap<String, MapData> hashMap) {
        /**
         * Allocate buffer outside loop.
         * */
        MapBehaviour behaviour = null;

        /**
         * Loop through each.
         * */
        for(Map.Entry<String, MapData> entry : hashMap.entrySet())
        {
            /**
             * Check if there's already existing MapData, if not add a new MapBehaviour.
             * */
            if(!this.mapBehaviourManager.containsKey(entry.getKey()))
            {
                if(entry.getKey().startsWith("vos")) {
                    behaviour = new VosMapBehaviour(entry.getKey().split(" ")[1].toCharArray()[0], entry.getValue(), googleMap);
                }
                else
                {
                    switch (entry.getKey())
                    {
                        case "hunter":
                            behaviour = new HunterMapBehaviour(entry.getValue(), googleMap);
                            break;
                        case "sc":
                            behaviour = new ScoutingGroepMapBehaviour(entry.getValue(), googleMap);
                            break;
                        case "foto":
                            behaviour = new FotoOpdrachtMapBehaviour(entry.getValue(), googleMap);
                            break;
                    }
                }
                this.mapBehaviourManager.put(entry.getKey(), behaviour);
            }
            else
            {
                /**
                 * There's already MapData of this type, we need to merge it.
                 * */
                this.mapBehaviourManager.get(entry.getKey()).merge(GraphicalMapData.from(entry.getValue(), googleMap));
            }
        }
    }

    /**
     * Preforms the spotting logic.
     * */
    public void spot()
    {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            /**
             * The value indicating if the spot has been initialized.
             * Meaning: a subscribe to the MainTracker.
             * */
            boolean initialized = false;

            /**
             * The marker that represents the vos location.
             * */
            Marker spotMarker = null;

            @Override
            public void onMapClick(LatLng latLng) {

                /**
                 * Checks if there already was a initialize.
                 * */
                if(!initialized)
                {
                    /**
                     * Subscribe to be noticed of the completion of location selection.
                     * */
                    JotiApp.MainTracker.subscribe(new Tracker.TrackerSubscriberCallback() {
                        @Override
                        public void onConditionMet(Tracker.TrackerMessage message) {
                            /**
                             * Serialize the data to a sendable json format.
                             * */
                            String data = new Gson().toJson(new VosInfoSendable("Bram", spotMarker.getPosition().latitude, spotMarker.getPosition().longitude, "Alpha", "testtest"));

                            /**
                             * Queue in the request and preform it.
                             * */
                            apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "vos" }), data));
                            apiManager.preform();

                            /**
                             * Unsubscribe from the Tracker.
                             * */
                            JotiApp.MainTracker.postponeUnsubscribe(this);
                        }
                    }, new Predicate<String>() {
                        @Override
                        public boolean apply(String s) {
                            return (s.equals(MainActivity.TRACKER_MAINACTIVITY_SPOTTING_LOCATION_SELECTION_COMPLETED));
                        }
                    });
                    initialized = true;
                }

                /**
                 * Checks if the spotMarker is not null, if it is set the position of the marker to the new one.
                 * */
                if(spotMarker != null)
                {
                    spotMarker.setPosition(latLng);
                }
                else
                {
                    /**
                     * Marker has not been created, so create one.
                     * Setup the marker below.
                     * */
                    MarkerOptions mOptions = new MarkerOptions();
                    mOptions.position(latLng);
                    mOptions.title("spotMarker");
                    mOptions.snippet("sfsf");
                    mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location_found));

                    /**
                     * Add the marker to the map.
                     * */
                    spotMarker = googleMap.addMarker(mOptions);
                }
            }
        });
    }


    /**
     * Puts the MapManager's data into a bundle.
     *
     * @return A bundle with the MapManager's data in it.
     * */
    public Bundle toBundle()
    {
        Bundle buffer = new Bundle();
        toBundle(buffer);
        return buffer;
    }

    /**
     * Puts the MapManager's data into a bundle.
     *
     * @param bundle The bundle you want the data to be put in.
     * */
    public void toBundle(Bundle bundle)
    {
        MapBehaviour behaviour;
        String[] keywords = new String[mapBehaviourManager.size()];
        int count = 0;

        for(int i = 0; i < mapBehaviourManager.size(); i++)
        {
            behaviour = mapBehaviourManager.get(i);
            if(behaviour instanceof VosMapBehaviour)
            {
                bundle.putParcelable("vos " + ((VosMapBehaviour) behaviour).getDeelgebied(), behaviour.to());
                keywords[count] = "vos " + ((VosMapBehaviour) behaviour).getDeelgebied();
            }

            if(behaviour instanceof HunterMapBehaviour)
            {
                bundle.putParcelable("hunter", behaviour.to());
                keywords[count] = "hunter";
            }

            if (behaviour instanceof ScoutingGroepMapBehaviour)
            {
                bundle.putParcelable("sc", behaviour.to());
                keywords[count] = "sc";
            }

            if(behaviour instanceof FotoOpdrachtMapBehaviour)
            {
                bundle.putParcelable("foto", behaviour.to());
                keywords[count] = "foto";
            }
            count++;
        }
        bundle.putStringArray("keywords", keywords);
    }

    /**
     * Postpones the from bundle creation until the initialize.
     * */
    public void postponeFromBundle(Bundle bundle)
    {
        fromBundleBuffer = bundle;
    }

    /**
     * Gets the previous data of the MapManager from the bundle, and adds it.
     * */
    public void fromBundle(Bundle bundle)
    {
        /**
         * Get the keywords for the various MapData.
         * */
        String[] keywords = bundle.getStringArray("keywords");

        /**
         * Allocate buffer outside loop.
         * */
        MapBehaviour behaviour = null;

        /**
         * Loop through each keyword with it's associated MapData.
         * */
        for(int i = 0; i < keywords.length; i++)
        {
            if(keywords[i].startsWith("vos")) {
                behaviour = new VosMapBehaviour(keywords[i].split(" ")[1].toCharArray()[0], (MapData)bundle.getParcelable(keywords[i]), googleMap);
            }
            else
            {
                switch (keywords[i])
                {
                    case "hunter":
                        behaviour = new HunterMapBehaviour((MapData)bundle.getParcelable(keywords[i]), googleMap);
                        break;
                    case "sc":
                        behaviour = new ScoutingGroepMapBehaviour((MapData)bundle.getParcelable(keywords[i]), googleMap);
                        break;
                    case "foto":
                        behaviour = new FotoOpdrachtMapBehaviour((MapData)bundle.getParcelable(keywords[i]), googleMap);
                        break;
                }
            }
            this.mapBehaviourManager.put(keywords[i], behaviour);
        }
    }

    /**
     * Disposes the MapManager and removes the listeners.
     * */
    public void destroy()
    {
        if(this.scClusterManager != null)
        {
            this.scClusterManager.clearItems();
            this.scClusterManager = null;
        }

        if(this.mapBehaviourManager != null)
        {
            this.mapBehaviourManager.destroy();
            this.mapBehaviourManager = null;
        }

        apiManager.removeListener(this);
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

    /**
     * Value indicating if the static part of the MapManager is initialized.
     * */
    public static boolean isInitialized = false;

    /**
     * Value indicating if the static part of the MapManager is fetching some data.
     * */
    public static boolean isFetching = false;

    /**
     * Initializes the static part of the MapManager.
     * */
    public static void initializeStaticPart()
    {
        if(!isInitialized)
        {
            /**
             * Initialize maps, without it we can't use certain classes and methods. Such as BitmapDescriptorFactory.
             * */
            MapsInitializer.initialize(JotiApp.getContext());

            JotiApp.MainTracker.subscribe(new Tracker.TrackerSubscriberCallback() {
                @Override
                public void onConditionMet(Tracker.TrackerMessage message) {
                    isFetching = false;
                }
            }, new Predicate<String>() {
                @Override
                public boolean apply(String s) {
                    return (s.equals(ApiManager.TRACKER_APIMANAGER_FETCHING_COMPLETED));
                }
            });


            /**
             * Get the ApiManager and add a listener to it, the listener is the data manager that will process the data for us.
             * */
            apiManager.addListener(dataManager, new Predicate<ApiResult>() {
                @Override
                public boolean apply(ApiResult result) {
                    /**
                     * The DataManager handles all results except from the sc.
                     * */
                    return !result.getRequest().getUrl().getPath().split("/")[1].equals("sc");
                }
            });

            /**
             * Set the root of the LinkBuilder to the Area348's one.
             * */
            LinkBuilder.setRoot(Area348API.root);

            /**
             * Fetch the latest data.
             * */
            fetch();

            /**
             * Indicate that the static part is initialized.
             * */
            isInitialized = true;
        }
    }

    /**
     * Fetches the latest data from the data.
     * */
    public static void fetch()
    {
        /**
         * Checks if the fetching is not already on going, if so don't fetch the new data, else do.
         * */
        if(!isFetching)
        {
            /**
             * Queue in some requests.
             * */
            apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "a", "all"}), null));
            apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "b", "all"}), null));
            apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "c", "all"}), null));
            apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "d", "all"}), null));
            apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "e", "all"}), null));
            apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "f", "all"}), null));
            apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "x", "all"}), null));

            apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"sc", "all"}), null));

            apiManager.preform();

            /**
             * Indicate that the static part is fetching data.
             * */
            isFetching = true;
        }
    }

    public static final String TRACKER_MAPMANAGER_CLUSTERING_COMPLETED = "TRACKER_MAPMANAGER_CLUSTERING_COMPLETED";

}