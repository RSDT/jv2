package com.rsdt.jotial.mapping.area348;

import android.app.Activity;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.internal.util.Predicate;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.rsdt.jotial.JotiApp;
import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiRequest;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.LinkBuilder;
import com.rsdt.jotial.communication.StaticApiManger;
import com.rsdt.jotial.communication.area348.Area348;

import com.rsdt.jotial.communication.area348.Auth;
import com.rsdt.jotial.communication.area348.util.SyncUtil;
import com.rsdt.jotial.data.structures.area348.receivables.ScoutingGroepInfo;

import com.rsdt.jotial.io.AppData;
import com.rsdt.jotial.mapping.area348.behaviour.FotoOpdrachtMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.HunterMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.MapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.MapBehaviourManager;
import com.rsdt.jotial.mapping.area348.behaviour.ScoutingGroepMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.VosMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEvent;
import com.rsdt.jotial.mapping.area348.clustering.ScoutingGroepClusterManager;
import com.rsdt.jotial.mapping.area348.clustering.ScoutingGroepInfoRenderer;
import com.rsdt.jotial.mapping.area348.data.GraphicalMapData;
import com.rsdt.jotial.mapping.area348.data.MapData;

import com.rsdt.jotial.mapping.area348.filtering.MapFilter;
import com.rsdt.jotial.misc.VosUtil;
import com.rsdt.jotiv2.MainActivity;
import com.rsdt.jotiv2.R;
import com.rsdt.jotiv2.Tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * The final control unit for map managing.
 */

public class MapManager implements DataProcessingManager.OnDataTaskCompletedCallback, ApiManager.OnApiTaskCompleteCallback {

    /**
     * Initializes a new instance of MapManager.
     *
     * */
    public MapManager()
    {

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
     * The SpecialEventHandler for the MapManager, it sets up the special events.
     * */
    private SpecialEventHandler specialEventHandler = new SpecialEventHandler();

    /**
     * The SpottingManager of the MapManager, it will control the spotting.
     * */
    private SpottingManager spottingManager = new SpottingManager();

    /**
     * Gets the SpottingManager of the MapManager.
     * */
    public SpottingManager getSpottingManager() { return spottingManager; }

    /**
     * The static ApiManager of the MapManager, it fetches the new data, and then sends
     * it to the dataManager to be processed. There's however a special case, the ScoutingGroep is clustered,
     * this requires a special system. The data cannot be processed before the instance creation of the MapManager,
     * there for the ScoutingGroep data will be posted to the new instance.
     * */
    private static StaticApiManger apiManager = new StaticApiManger();

    /**
     * Gets the StaticApiManager of the MapManager.
     * */
    public static StaticApiManger getApiManager() { return apiManager; }

    /**
     * The static DataProcessingManager of the MapManager, it processes the data and then converts it to the MapData.
     * When a instance of MapManager is created, the generated MapData will be posted to the instance.
     * */
    private static StaticDataProcessingManager dataManager = new StaticDataProcessingManager();

    /**
     * The cluster manager for the scouting groups.
     * */
    private ScoutingGroepClusterManager scClusterManager;

    /**
     * The BundleHelper for the MapManager.
     * */
    private BundleHelper bundleHelper = new BundleHelper();

    /**
     * Gets the BundleHelper of the MapManager.
     * */
    public BundleHelper getBundleHelper() {
        return bundleHelper;
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
         * Applies the preferences to the map.
         * */
        PreferenceHelper.applyPreferences(googleMap);

        /**
         * Register as listener to the ApiManager, with a filter that applies only to ScoutingGroep Data.
         * */
        apiManager.addListener(this, new Predicate<ApiResult>() {
            @Override
            public boolean apply(ApiResult result) {

                return result.getRequest().getUrl().getPath().split("/")[1].equals("sc")
                        && result.getResponseCode() == 200
                        && result.getRequest().getMethod().equals(ApiRequest.GET);
            }
        });

        /**
         * Register as listener to the DataProcessingManager.
         * */
        dataManager.addListener(this);

        /**
         * Initialize the ClusterManager.
         * */
        scClusterManager = new ScoutingGroepClusterManager(JotiApp.getContext(), googleMap);
        scClusterManager.setRenderer(new ScoutingGroepInfoRenderer(JotiApp.getContext(), googleMap, scClusterManager));
        googleMap.setOnCameraChangeListener(scClusterManager);
        googleMap.setOnMarkerClickListener(scClusterManager);

        /**
         * Setup special events, such as the special indicator circle on OnGetInfoWindow().
         * */
        specialEventHandler.initialize();

        /**
         * Check if there's a local buffer, if so apply it.
         * */
        if(this.bundleHelper.hasLocalBuffer())
        {
            bundleHelper.applyFromLocalBuffer();
        }
        else
        {
            /**
             * This is the first create, so move the camera to the standard position.
             * */
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(RP_LAT_LNG, ZOOM, TILT, BEARING)));
        }

    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 4-2-2016
     * Task that reads the stored map data and (re)processes it.
     * */
    private static class MapStorageReadTask extends AsyncTask<String, Integer, ArrayList<ApiResult>>
    {

        @Override
        protected ArrayList<ApiResult> doInBackground(String... params) {

            /**
             * Buffer list to hold the results.
             * */
            ArrayList<ApiResult> results = new ArrayList<>();

            /**
             * Get all the keywords.
             * */
            String[] keywords;

            /**
             * Check if all the results should be read.
             * */
            if(params[0].equals(RESULT_ALL))
            {
                /**
                 * If so, set the keywords array to all the RESULT identifiers.
                 * */
                keywords = MapStorageUtil.getAll();
            }
            else
            {
                keywords = params;
            }

            /**
             * Loop through each keyword, meaning each result.
             * */
            for(int i = 0; i < keywords.length; i++)
            {
                /**
                 * Check if the AppData has data of the keyword.
                 * */
                if(AppData.hasSave(keywords[i]))
                {
                    /**
                     * Get the result from the AppData.
                     * */
                    ApiResult result = AppData.getObject(keywords[i], ApiResult.class);

                    /**
                     * Check if it isn't null, if so add it.
                     * */
                    if(result != null)
                    {
                        results.add(result);
                        Log.i("MapStorageReadTask", keywords[i] + " data is found.");
                    }
                }
                else { Log.i("MapStorageReadTask", "No " + keywords[i] + " data is found."); }
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<ApiResult> apiResults) {
            super.onPostExecute(apiResults);


            /**
             * Process the results.
             * */
            MapManager.getApiManager().process(apiResults);
        }


        public static final String RESULT_ALL = "RESULT_ALL";

        public static final String RESULT_SC = "RESULT_SC";
        public static final String RESULT_FOTO = "RESULT_FOTO";
        public static final String RESULT_HUNTER = "RESULT_HUNTER";
        public static final String RESULT_USER = "RESULT_USER";

        public static final String RESULT_VOS_A = "RESULT_VOS_A";
        public static final String RESULT_VOS_B = "RESULT_VOS_B";
        public static final String RESULT_VOS_C = "RESULT_VOS_C";
        public static final String RESULT_VOS_D = "RESULT_VOS_D";
        public static final String RESULT_VOS_E = "RESULT_VOS_E";
        public static final String RESULT_VOS_F = "RESULT_VOS_F";
        public static final String RESULT_VOS_X = "RESULT_VOS_X";
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 4-2-2016
     * Contains various util methods.
     * */
    public static class MapStorageUtil
    {

        public static void readAllFromSave() { new MapStorageReadTask().execute(MapStorageReadTask.RESULT_ALL); }

        public static void readFromSave(String[] parts) { new MapStorageReadTask().execute(parts); }

        public static final String getUser() { return MapStorageReadTask.RESULT_USER; }

        public static final String[] getAll() { return new String[] {
                MapStorageReadTask.RESULT_SC,
                MapStorageReadTask.RESULT_FOTO,
                MapStorageReadTask.RESULT_HUNTER,
                MapStorageReadTask.RESULT_USER,
                MapStorageReadTask.RESULT_VOS_A,
                MapStorageReadTask.RESULT_VOS_B,
                MapStorageReadTask.RESULT_VOS_C,
                MapStorageReadTask.RESULT_VOS_D,
                MapStorageReadTask.RESULT_VOS_E,
                MapStorageReadTask.RESULT_VOS_F,
                MapStorageReadTask.RESULT_VOS_X,
        }; }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 13-2-2016
     *
     * */
    public class SearchHelper
    {


        public void searchFor(String id, String characteristic1)
        {
            switch (id)
            {
                case "SC":

                    ArrayList<ScoutingGroepInfo> items = scClusterManager.getItems();

                    for(int i = 0; i < items.size(); i++)
                    {

                    }

                    break;
            }
        }


    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 4-2-2016
     * Class to help putting the map data into a bundle.
     * */
    public class BundleHelper
    {

        /**
         * A buffer to hold the PreviousInstanceState until the MapManager has been initialized,
         * we only can apply the state when the GoogleMap is active.
         * */
        PreviousInstanceState buffer;

        /**
         * Determines if the BundleHelper has a local buffer.
         *
         * @return Value indicating if the BundleHelper has a local buffer.
         * */
        public boolean hasLocalBuffer() {
            return (buffer != null);
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

            if(googleMap != null)
            {
                /**
                 * Put the camera's position in the bundle.
                 * */
                bundle.putParcelable("camera", googleMap.getCameraPosition());
            }

            if(scClusterManager != null)
            {
                /**
                 * Put the ScoutingGroep items in the bundle.
                 * We can't put a ArrayList in, because it will contain no items when gotten back out of the bundle.
                 * I don't know where this is coming from, it's peculiar that a array does seem to work.
                 * */
                bundle.putParcelableArray("scCluster", scClusterManager.getItems().toArray(new ScoutingGroepInfo[scClusterManager.getItems().size()]));
            }

            if(mapBehaviourManager != null)
            {
                MapBehaviour behaviour;
                String[] keywords = new String[mapBehaviourManager.size()];
                int count = 0;

                for(HashMap.Entry<String, MapBehaviour> entry : mapBehaviourManager.entrySet())
                {
                    behaviour = entry.getValue();
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

        }

        /**
         * Gets the previous data of the MapManager from the bundle, and buffers it locally.
         * So that we can apply the state when the MapManager is initialized.
         *
         * @param bundle The bundle where the data is located.
         * */
        public void fromBundleAndBuffer(Bundle bundle)
        {
            /**
             * Create buffer to hold our data.
             * */
            PreviousInstanceState state = new PreviousInstanceState();

            /**
             * Get the CameraPosition out of the Bundle.
             * */
            state.cameraPosition =  bundle.getParcelable("camera");

            /**
             * Get the scouting groep items out of the Bundle.
             * */
            state.scItems = (ScoutingGroepInfo[])bundle.getParcelableArray("scCluster");

            /**
             * Get the keywords for the various MapData.
             * */
            String[] keywords = bundle.getStringArray("keywords");

            /**
             * Loop through each keyword.
             * */
            for(int i = 0; i < keywords.length; i++)
            {
                state.mapData.put(keywords[i], (MapData)bundle.getParcelable(keywords[i]));
            }
            this.buffer = state;
        }

        @MainThread
        /**
         * Applies the previous instance state to the current.
         * Meaning the camera position, and all markers, polylines and polygons
         * will be added again.
         *
         * @param state The PreviousInstanceState that contains the data.
         * */
        public void apply(PreviousInstanceState state)
        {
            /**
             * Move the camera's position to the old one.
             * */
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(state.cameraPosition));

            /**
             * Add items to the cluster manager, and cluster them.
             * */
            scClusterManager.addItems(new ArrayList<>(Arrays.asList(state.scItems)));
            scClusterManager.cluster();

            /**
             * Allocate buffer outside loop.
             * */
            MapBehaviour behaviour = null;

            /**
             * Loop through each <String, MapData> entry.
             * */
            for(HashMap.Entry<String, MapData> entry : state.mapData.entrySet())
            {
                if(entry.getKey().startsWith("vos"))
                {
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
                mapBehaviourManager.put(entry.getKey(), behaviour);
            }

        }

        @MainThread
        /**
         * Applies the buffered previous instance state to the current.
         * Meaning the camera position, and all markers, polylines and polygons
         * will be added again.
         * */
        private void applyFromLocalBuffer()
        {
            /**
             * Check if the buffer is not null.
             * */
            if(buffer != null)
            {
                /**
                 * Apply the buffer, meaning all saved items will be added to the map.
                 * */
                apply(buffer);

                /**
                 * Destroy and set the state to null.
                 * */
                buffer.destroy();
                buffer = null;
            }
        }

        /**
         * @author Dingenis Sieger Sinke
         * @version 1.0
         * @since 4-2-2016
         * Class for holding the state of a previous instance.
         * */
        public class PreviousInstanceState
        {
            /**
             * The camera position of state.
             * */
            CameraPosition cameraPosition;

            /**
             * The scouting groep items.
             * */
            ScoutingGroepInfo[] scItems;

            /**
             * The map data of the state.
             * */
            HashMap<String, MapData> mapData = new HashMap<>();

            /**
             * Destroys the state.
             * */
            public void destroy()
            {
                cameraPosition = null;

                if(mapData != null)
                {
                    mapData.clear();
                    mapData = null;
                }

                if(scItems != null)
                {
                    scItems = null;
                }
            }

        }
    }


    private class SmartFilter
    {

    }

    public void filter()
    {

        MapFilter.Builder builder = new MapFilter.Builder();
        builder.addFilter(new MapFilter.ScoutingGroepFilter(new Predicate<ScoutingGroepInfo>() {
            @Override
            public boolean apply(ScoutingGroepInfo scoutingGroepInfo) {
                return (scoutingGroepInfo.team.equals("a"));
            }
        }, MapFilter.ACTION_HIDE));

        /**
         * This filter will result in:
         * Every VosMapBehaviour which is not a A deelgebied one,
         * will be hidden.
         * The one that is will be shown, all the other items are not affected.
         * */
        builder.addFilter(new MapFilter.MapBehaviourFilter(new Predicate<MapBehaviour>() {
            @Override
            public boolean apply(MapBehaviour mapBehaviour) {
                /**
                 * Checks if the behaviour is a VosMapBehaviour.
                 * */
                if(mapBehaviour instanceof VosMapBehaviour)
                {
                    /**
                     * For example return the value indicating if its deelgebied is A.
                     * */
                    return (((VosMapBehaviour) mapBehaviour).getDeelgebied() == 'a');
                }
                return false;
            }
        }, new MapFilter.MapBehaviourFilter.CustomFilterAction() {
            @Override
            public int getAction(MapBehaviour behaviour, boolean applies) {
                /**
                 * Checks if the behaviour is a VosMapBehaviour.
                 * */
                if(behaviour instanceof VosMapBehaviour)
                {
                    /**
                     * Checks if the condition applies for this VosMapBehaviour.
                     * */
                    if(applies)
                    {
                        /**
                         * If the condition applies for the item do this.
                         * */
                        return MapFilter.ACTION_SHOW;
                    }
                    else
                    {
                        /**
                         * If the condition doesn't apply for the item.
                         * */
                        return MapFilter.ACTION_HIDE;
                    }
                }
                /**
                 * Ignore the item, since it is not a VosMapBehaviour.
                 * */
                return MapFilter.ACTION_NONE;
            }
        }));

        MapFilter filter = builder.create();

        filter.apply(scClusterManager, mapBehaviourManager);

    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 4-2-2016
     * Class for containing the special events.
     * */
    private class SpecialEventHandler
    {

        /**
         * Handler to handle update apiTasks.
         * */
        Handler circleHandler = new Handler();

        String date;

        /**
         * Value indicating the origin of the special circle.
         * */
        String idCircle;

        /**
         * Circle to indicate something.
         * */
        Circle indicationCircle;

        /**
         * The value indicating if a circle should be put on the map.
         * */
        boolean putCircle = true;

        /**
         * Initializes the SpecialEventHandler.
         * */
        public void initialize()
        {
            setupCircleRemoval();
            setupVos();
            setupSc();
        }

        /**
         * Setups the circle removal.
         * */
        private void setupCircleRemoval()
        {
            mapBehaviourManager.getsEventRaiser().getEvents().add(new MapBehaviourEvent<Marker>(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW) {

                @Override
                public boolean apply(Marker marker) {
                    if(marker.getTitle() != null) {
                        return (marker.getTitle().startsWith("vos") || marker.getTitle().startsWith("sc"));
                    }
                    return false;
                }

                @Override
                public void onConditionMet(Object[] args) {

                    Marker marker = (Marker) args[1];

                    /**
                     * Checks if the indicatorCircle is null, if not compare to center.
                     * */
                    if (indicationCircle != null) {
                        /**
                         * Checks if the last circle is placed around the current marker, if so remove the circle.
                         * */
                        if (indicationCircle.getCenter().equals(marker.getPosition())) {
                            /**
                             * Remove the current indicator circle, and indicate that no further circle should be placed.
                             * */
                            indicationCircle.remove();
                            indicationCircle = null;
                            idCircle = ID_NONE;
                            putCircle = false;
                        } else {
                            /**
                             * The circle should be changed, removed the current and indicate that a new one should be put on the map.
                             * */
                            indicationCircle.remove();
                            indicationCircle = null;
                            idCircle = ID_NONE;
                            putCircle = true;
                        }
                    } else {
                        /**
                         * No circle so put one on the map.
                         * */
                        putCircle = true;
                    }
                }
            });
        }

        /**
         * Setups the Vos special event.
         * */
        private void setupVos()
        {
            mapBehaviourManager.getsEventRaiser().getEvents().add(new MapBehaviourEvent<Marker>(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW) {

                @Override
                public boolean apply(Marker marker) {
                    if(marker.getTitle() != null) {
                        return (marker.getTitle().startsWith("vos"));
                    }
                    return false;
                }

                @Override
                public void onConditionMet(Object[] args) {

                    /**
                     * Check if we need to put a circle on the map, if not just return.
                     * */
                    if(!putCircle) return;

                    /**
                     * Get the marker out of the args.
                     * */
                    Marker marker = (Marker) args[1];

                    /**
                     * Split the title to extract data.
                     * */
                    String[] strArgs = marker.getTitle().split(" ");

                    /**
                     * Setup the circle.
                     * */
                    CircleOptions cOptions = new CircleOptions();
                    cOptions.center(marker.getPosition());
                    cOptions.strokeWidth(VosMapBehaviour.VOS_CIRCLE_STROKE_WIDTH);
                    cOptions.strokeColor(VosMapBehaviour.VOS_CIRCLE_STROKE_COLOR);
                    cOptions.fillColor(MapManager.parse(strArgs[1], VosMapBehaviour.VOS_CIRCLE_FILL_COLOR_ALPHA));
                    cOptions.radius(VosUtil.calculateRadius(strArgs[3] + " " + strArgs[4]));

                    date = strArgs[3] + " " + strArgs[4];

                    /**
                     * Add the circle to the map.
                     * */
                    indicationCircle = googleMap.addCircle(cOptions);

                    idCircle = ID_VOS;

                    /**
                     *
                     * */
                    circleHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(idCircle.equals(ID_VOS))
                            {
                                /**
                                 * Make sure the circle is not null.
                                 * */
                                if(indicationCircle != null)
                                {
                                    float radius = VosUtil.calculateRadius(date);

                                    /**
                                     * Checks if the radius is not the same as the old.
                                     * */
                                    if(indicationCircle.getRadius() != radius)
                                    {
                                        indicationCircle.setRadius(radius);
                                        circleHandler.postDelayed(this, 1000);
                                    }
                                }
                            }
                        }
                    }, 1000);
                }
            });
        }

        /**
         * Setups the Sc special event.
         * */
        private void setupSc()
        {
            /**
             * Setup event for indiviual ScoutingGroep
             * */
            mapBehaviourManager.getsEventRaiser().getEvents().add(new MapBehaviourEvent<Marker>(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW) {

                @Override
                public boolean apply(Marker marker) {
                    if (marker.getTitle() != null) {
                        return (marker.getTitle().startsWith("sc") && !marker.getTitle().startsWith("scc"));
                    }
                    return false;
                }

                @Override
                public void onConditionMet(Object[] args) {


                    /**
                     * Get the view out of the args, this is the InfoWindow view.
                     * */
                    View view = (View) args[0];

                    /**
                     * Get the marker out of the args, this is the marker where is clicked on.
                     * */
                    Marker marker = (Marker) args[1];

                    String[] mArgs = marker.getTitle().split(" ");

                    ((TextView) view.findViewById(R.id.infoWindow_infoType)).setText("ScoutingGroep");
                    ((TextView) view.findViewById(R.id.infoWindow_naam)).setText(mArgs[1]);
                    ((TextView) view.findViewById(R.id.infoWindow_dateTime_adres)).setText(mArgs[2]);
                    ((TextView) view.findViewById(R.id.infoWindow_coordinaat)).setText(mArgs[3] + " , " + mArgs[4]);

                    view.findViewById(R.id.infoWindow_infoType).setBackgroundColor(MapManager.parse(mArgs[5], 255));

                    /**
                     * Check if we need to put a circle on the map, if not just return.
                     * */
                    if (!putCircle) return;


                    /**
                     * Setup the circle.
                     * */
                    CircleOptions cOptions = new CircleOptions();
                    cOptions.center(marker.getPosition());
                    cOptions.radius(ScoutingGroepMapBehaviour.SCOUTING_GROEP_CIRCLE_RADIUS);
                    cOptions.strokeWidth(ScoutingGroepMapBehaviour.SCOUTING_GROEP_CIRCLE_STROKE_WIDTH);
                    cOptions.strokeColor(ScoutingGroepMapBehaviour.SCOUTING_GROEP_CIRCLE_STROKE_COLOR);
                    cOptions.fillColor(MapManager.parse(mArgs[5], ScoutingGroepMapBehaviour.SCOUTING_GROEP_CIRCLE_FILL_COLOR_ALPHA));

                    /**
                     * Add the circle to the map.
                     * */
                    indicationCircle = googleMap.addCircle(cOptions);

                    idCircle = ID_SC;
                }
            });

            /**
             * Setup event for clusters.
             * */
            mapBehaviourManager.getsEventRaiser().getEvents().add(new MapBehaviourEvent<Marker>(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW) {

                @Override
                public boolean apply(Marker marker) {
                    if (marker.getTitle() != null) {
                        return (marker.getTitle().startsWith("scc"));
                    }
                    return false;
                }

                @Override
                public void onConditionMet(Object[] args) {

                    Marker clusterMarker = (Marker) args[1];


                    String[] mArgs = clusterMarker.getTitle().split(" ");

                    View view = (View) args[0];

                    ((TextView) view.findViewById(R.id.infoWindow_infoType)).setText("ScoutingGroepCluster");
                    ((TextView) view.findViewById(R.id.infoWindow_naam)).setText("Een cluster van ScoutingGroepen");
                    ((TextView) view.findViewById(R.id.infoWindow_dateTime_adres)).setText("Grote: " + mArgs[1]);
                    ((TextView) view.findViewById(R.id.infoWindow_coordinaat)).setText(mArgs[2] + " , " + mArgs[3]);

                }
            });
        }


        public void destroy()
        {
            circleHandler = null;
        }

        public static final String ID_VOS = "ID_VOS";
        public static final String ID_SC = "ID_SC";
        public static final String ID_NONE = "ID_NONE";

    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 4-2-2016
     * Class for the spotting of a vos.
     * */
    public class SpottingManager implements GoogleMap.OnMapClickListener, Tracker.TrackerSubscriberCallback {

        /**
         * The value indicating if the spot has been initialized.
         * Meaning: a subscribe to the MainTracker.
         * */
        boolean initialized = false;

        /**
         * The marker that represents the vos location.
         * */
        Marker spotMarker = null;

        /**
         * Begins the spotting behaviour.
         * */
        public void beginSpot() {
            googleMap.setOnMapClickListener(this);
        }

        /**
         * Ends the spotting.
         * */
        public void endSpot()
        {
            if(spotMarker != null)
            {
                spotMarker.remove();
                spotMarker = null;
            }

            if(googleMap != null)
            {
                /**
                 * Set the onMapClickListener to null.
                 * */
                googleMap.setOnMapClickListener(null);
            }

            /**
             * Remove listener to prevent leakage.
             * */
            JotiApp.MainTracker.postponeUnsubscribe(this);
        }

        @Override
        public void onConditionMet(Tracker.TrackerMessage message) {

            /**
             * Send the vos location to the server.
             * */
            SyncUtil.sendVosLocation(spotMarker.getPosition(), "a", "TEST", "0");

            /**
             * Unsubscribe from the Tracker.
             * */
            JotiApp.MainTracker.postponeUnsubscribe(this);

            initialized = false;
        }

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
                JotiApp.MainTracker.subscribe(this, new Predicate<String>() {
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
                mOptions.snippet("you shouldn't be able to see this?");
                mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location_found));

                /**
                 * Add the marker to the map.
                 * */
                spotMarker = googleMap.addMarker(mOptions);
            }
        }

        /**
         * Disposes the object.
         * */
        public void destroy()
        {

            if(spotMarker != null)
            {
                spotMarker.remove();
                spotMarker = null;
            }

            if(googleMap != null)
            {
                /**
                 * Set the onMapClickListener to null.
                 * */
                googleMap.setOnMapClickListener(null);
            }

            /**
             * Remove listener to prevent leakage.
             * */
            JotiApp.MainTracker.unsubscribe(this);
        }
    }

    /**
     * Gets invoked when ScoutingGroep data is available, cause of the filter applied to this callback.
     * */
    public void onApiTaskCompleted(ArrayList<ApiResult> results, String origin) {

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
             * Clear the old items.
             * */
            scClusterManager.clearItems();

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

            /**
             * Check if the origin is the preform() method.
             * If so the data is not saved data, it's straight
             * from a request. So save it.
             * */
            if(origin.equals(ApiManager.ORIGIN_PREFORM))
            {
                /**
                 * Save them in the background with the AppData class.
                 * */
                AppData.saveObjectAsJsonInBackground(currentResult, MapStorageReadTask.RESULT_SC);
            }

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
                    behaviour = new VosMapBehaviour(entry.getKey().split(" ")[1].toLowerCase().toCharArray()[0], entry.getValue(), googleMap);
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
     * Disposes the MapManager and removes the listeners.
     * */
    public void destroy()
    {
        if(this.scClusterManager != null)
        {
            scClusterManager.clearItems();
            scClusterManager = null;
        }

        if(this.spottingManager != null)
        {
            spottingManager.destroy();
            spottingManager = null;
        }

        if(this.specialEventHandler != null)
        {
            specialEventHandler.destroy();
            specialEventHandler = null;
        }

        if(this.mapBehaviourManager != null)
        {
            mapBehaviourManager.destroy();
            mapBehaviourManager = null;
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
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 4-2-2016
     * Represents the static part of the MapManager.
     * */
    public static class StaticPart
    {

        /**
         * Value indicating if the static part of the MapManager is initialized.
         * */
        public static boolean isInitialized = false;

        /**
         * Initializes the static part of the MapManager.
         * */
        public static void initialize()
        {
            if(!isInitialized)
            {
                /**
                 * Initialize maps, without it we can't use certain classes and methods. Such as BitmapDescriptorFactory.
                 * */
                MapsInitializer.initialize(JotiApp.getContext());

                /**
                 * Get the ApiManager and add a listener to it, the listener is the data manager that will process the data for us.
                 * */
                apiManager.addListener(dataManager, new Predicate<ApiResult>() {
                    @Override
                    public boolean apply(ApiResult result) {
                        /**
                         * The DataProcessingManager handles all results except from the sc.
                         * */
                        return !result.getRequest().getUrl().getPath().split("/")[1].equals("sc") &&
                                !result.getRequest().getUrl().getPath().split("/")[1].equals("login")
                                && result.getRequest().getMethod().equals(ApiRequest.GET)
                                && result.getResponseCode() == 200;
                    }
                });

                /**
                 * Setup the response code check, so that error can be reported.
                 * */
                setupResponseCodeCheck();


                /**
                 * Initialize the Syncer, there by automatically sync as well.
                 * */
                Syncer.initialize();


                /**
                 * Indicate that the static part is initialized.
                 * */
                isInitialized = true;
            }
        }

        /**
         * Setup the response code check.
         * */
        private static void setupResponseCodeCheck()
        {
            apiManager.addListener(new ApiManager.OnApiTaskCompleteCallback() {
                @Override
                public void onApiTaskCompleted(ArrayList<ApiResult> results, String origin) {

                    /**
                     * Allocate buffer outside loop.
                     * */
                    ApiResult currentResult;
                    for (int i = 0; i < results.size(); i++) {

                        /**
                         * Assign buffer.
                         * */
                        currentResult = results.get(i);

                        /**
                         * Switch on response code.
                         * */
                        switch (currentResult.getResponseCode()) {
                            case 401:

                                /**
                                 * Require auth.
                                 * */
                                JotiApp.Auth.requireAuth();
                                break;
                        }

                        /**
                         * Make a log entry, the data is the error stream.
                         * */
                        Log.e("StaticPart", currentResult.getData());
                    }
                }
            }, new Predicate<ApiResult>() {
                @Override
                public boolean apply(ApiResult result) {
                    return (result.getResponseCode() == 401 ||
                            result.getResponseCode() == 403 ||
                            result.getResponseCode() == 404 ||
                            result.getResponseCode() == 418);
                }
            });
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 4-2-2016
     *
     * */
    public static class Syncer
    {
        /**
         * The state of syncing.
         * */
        private static SyncState syncState = new SyncState();

        /**
         * Value indicating if there should be smartly synced.
         * */
        public static boolean useSmartSync() { return PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).getBoolean("pref_sync_smart", false); }

        /**
         * Checks if the mobile is connected with the internet.
         * */
        public static boolean isConnected()
        {
            ConnectivityManager connectivityManager = (ConnectivityManager)JotiApp.getContext().getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info != null)
            {
                return info.isConnected();
            }
            else
            {
                return false;
            }
        }

        /**
         * Initializes the Syncer.
         * */
        public static void initialize()
        {

            /**
             * Set the API_V1_ROOT of the LinkBuilder to the Area348's one.
             * */
            LinkBuilder.setRoot(Area348.API_V2_ROOT);

            /**
             * Get all the keywords.
             * */
            String[] keywords = MapStorageUtil.getAll();

            /**
             * Loop through each one.
             * */
            for(int i = 0; i < keywords.length; i++)
            {
                /**
                 * Setup the interval for each item.
                 * */
                long interval;

                switch (keywords[i])
                {
                    case MapStorageReadTask.RESULT_SC:
                        interval = SyncState.SyncStateItem.DONT_USE_INTERVAL;
                        break;
                    case MapStorageReadTask.RESULT_USER:
                        interval = SyncState.SyncStateItem.DONT_USE_INTERVAL;
                        break;
                    case MapStorageReadTask.RESULT_HUNTER:
                        interval = 1000;
                        break;
                    case MapStorageReadTask.RESULT_FOTO:
                        interval = 1000;
                        break;
                    default:
                        interval = 1000;
                        break;
                }

                /**
                 * Check if we have data stored, under the keyword.
                 * */
                if(AppData.hasSave(keywords[i]))
                {
                    /**
                     * Put a item, with needsSync = false and hasLocalData = true.
                     * */
                    syncState.put(keywords[i], new SyncState.SyncStateItem(false, true, interval));
                }
                else
                {
                    /**
                     * Put a item, with needsSync = true and hasLocalData = false.
                     * */
                    syncState.put(keywords[i], new SyncState.SyncStateItem(true, false, interval));
                }
            }

            sync();
        }

        /**
         * Syncs some data.
         * */
        public static void sync()
        {
            /**
             * Check if the user is auth, if not we can't sync.
             * */
            if(JotiApp.Auth.isAuth())
            {

                if(isConnected())
                {
                    /**
                     * Check if Smart Sync is enabled.
                     * */
                    if(useSmartSync())
                    {
                        /**
                         * Allocate outside loop.
                         * */
                        SyncState.SyncStateItem currentItem;

                        /**
                         * Get the hunt naam from the Preferences.
                         * */
                        String huntNaam = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).getString("pref_map_hunt_name", null);

                        /**
                         * Value indicating if the hunt naam is valid.
                         * */
                        boolean huntNaamValid = false;

                        /**
                         * Check if the hunt naam is valid.
                         * */
                        if(huntNaam != null && !huntNaam.isEmpty()) { huntNaamValid = true; }
                        else
                        {
                            JotiApp.MainTracker.report(new Tracker.TrackerMessage(MainActivity.TRACKER_MAINACTIVITY_PREFERENCE_REQUIRED, "Syncer", "Huntnaam"));
                        }

                        /**
                         * Loop through each entry.
                         * */
                        for(HashMap.Entry<String, SyncState.SyncStateItem> entry : syncState.entrySet())
                        {
                            currentItem = entry.getValue();

                            /**
                             * Split the result identifier on "_" symbol.
                             * */
                            String[] args = entry.getKey().split("_");

                            /**
                             * Allocate long, to store the inteval between the last sync and now.
                             * Set default to min value, therefor the item's syncInterval cannot be smaller.
                             * */
                            long syncInterval = Long.MIN_VALUE;

                            /**
                             * Check if the lastSync isn't null, if so calculate sync interval.
                             * */
                            if(currentItem.lastSync != null)
                            {
                                syncInterval =  (new Date().getTime() - currentItem.lastSync.getTime());
                            }

                            /**
                             * Check if the item needs syncing.
                             * */
                            if(currentItem.needsSync | (currentItem.useSyncInterval && syncInterval > currentItem.syncInterval))
                            {
                                if(currentItem.hasBeenSynced)
                                {
                                    if(args[1].equals("VOS"))
                                    {
                                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", JotiApp.Auth.getKey(), args[2], "all", currentItem.lastSync.toString()})));
                                    }
                                    else
                                    {
                                        switch (args[1])
                                        {
                                            case "HUNTER":
                                                if(huntNaamValid) {

                                                    apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "hunter", JotiApp.Auth.getKey(), "andere",  huntNaam, currentItem.lastSync.toString()})));
                                                }
                                                else
                                                {
                                                    apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "hunter", JotiApp.Auth.getKey(), "all", currentItem.lastSync.toString()})));
                                                }
                                                break;
                                            case "FOTO":
                                                apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "foto", JotiApp.Auth.getKey(), "all", currentItem.lastSync.toString() })));
                                                break;
                                            case "SC":
                                                apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "sc", JotiApp.Auth.getKey(), "all" })));
                                                break;
                                            case "USER":
                                                apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "gebruiker", JotiApp.Auth.getKey(), "info" })));
                                                break;
                                        }
                                    }
                                }
                                else
                                {
                                    if(args[1].equals("VOS"))
                                    {
                                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{ "vos", JotiApp.Auth.getKey(), args[2], "all" })));
                                    }
                                    else
                                    {
                                        switch (args[1])
                                        {
                                            case "HUNTER":
                                                if(huntNaamValid) {
                                                    apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"hunter", JotiApp.Auth.getKey(), "andere", huntNaam})));
                                                }
                                                else
                                                {
                                                    apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "hunter", JotiApp.Auth.getKey(), "all" })));
                                                }
                                                break;
                                            case "FOTO":
                                                apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "foto", JotiApp.Auth.getKey(), "all" })));
                                                break;
                                            case "SC":
                                                apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "sc", JotiApp.Auth.getKey(), "all" })));
                                                break;
                                            case "USER":
                                                apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "gebruiker", JotiApp.Auth.getKey(), "info" })));
                                                break;
                                        }
                                    }
                                }

                                /**
                                 * Set the last sync date to now.
                                 * */
                                currentItem.lastSync = new Date();
                                currentItem.hasBeenSynced = true;
                            }
                        }
                    }
                    else
                    {

                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "vos", JotiApp.Auth.getKey(), "a", "all"})));
                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "vos", JotiApp.Auth.getKey(), "b", "all"})));
                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "vos", JotiApp.Auth.getKey(), "c", "all"})));
                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "vos", JotiApp.Auth.getKey(), "d", "all"})));
                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "vos", JotiApp.Auth.getKey(), "e", "all"})));
                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "vos", JotiApp.Auth.getKey(), "f", "all"})));
                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "vos", JotiApp.Auth.getKey(), "x", "all"})));

                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "sc", JotiApp.Auth.getKey(), "all"})));
                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "hunter", JotiApp.Auth.getKey(), "all"})));
                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "foto", JotiApp.Auth.getKey(), "all"})));
                        apiManager.queue(new ApiRequest(LinkBuilder.build(new String[] { "gebruiker", JotiApp.Auth.getKey(), "info"})));
                    }

                    /**
                     * Preform the request now, to maintain a time period between each request.
                     * Preforming all requests at once, will result in a API overload.
                     * */
                    apiManager.preform();
                }
                else
                {
                    /**
                     * We are not connected to the internet.
                     * */
                    JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_SYNCER_SYNC_FAILED_NO_INTERNET, "Syncer", "No internet connection.", Tracker.TrackerMessage.LEVEL_WARNING));
                }

/*                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_SYNC_STARTED, "Syncer", "A sync has been started."));

                JotiApp.MainTracker.subscribe(new Tracker.TrackerSubscriberCallback() {
                    @Override
                    public void onConditionMet(Tracker.TrackerMessage message) {
                        JotiApp.MainTracker.postponeReport(new Tracker.TrackerMessage(TRACKER_SYNC_ENDED, "Syncer", "The sync has been ended."));
                        JotiApp.MainTracker.postponeUnsubscribe(this);
                    }
                }, new Predicate<String>() {
                    @Override
                    public boolean apply(String s) {
                        return s.equals(ApiManager.TRACKER_APIMANAGER_FETCHING_COMPLETED);
                    }
                });*/
            }
            else
            {
                /**
                 * Setup the event, to recall the sync after successful authentication.
                 * */
                JotiApp.MainTracker.subscribe(new Tracker.TrackerSubscriberCallback() {
                    @Override
                    public void onConditionMet(Tracker.TrackerMessage message) {

                        /**
                         * Recall the sync.
                         * */
                        sync();

                        /**
                         * Unsubscribe to prevent leakage.
                         * */
                        JotiApp.MainTracker.postponeUnsubscribe(this);
                    }
                }, new Predicate<String>() {
                    @Override
                    public boolean apply(String s) {
                        return (s.equals(Auth.TRACKER_AUTH_SUCCEEDED));
                    }
                });


                /**
                 * Inform possible listeners that the sync failed because a auth is required.
                 * */
                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_SYNCER_SYNC_FAILED_AUTH_REQUIRED, "Syncer", "Authentication is required."));

                /**
                 * Require a auth, so that we can sync.
                 * */
                JotiApp.Auth.requireAuth();
            }
        }

        /**
         * Class that represents the state of sync.
         * */
        public static class SyncState extends HashMap<String,SyncState.SyncStateItem>
        {

            /**
             * Class that holds the state of a update part.
             * */
            public static class SyncStateItem
            {

                /**
                 * Value indicating if the item needs to be synced.
                 * */
                public boolean needsSync = false;

                /**
                 * Value indicating if the item has been synced.
                 * */
                public boolean hasBeenSynced = false;

                /**
                 * Value indicating if the item has local data.
                 * */
                public boolean hasLocalData = false;

                /**
                 * The min interval the item should be synced.
                 * */
                public long syncInterval;

                /**
                 * The value indicating if syncInterval should be used.
                 * */
                public boolean useSyncInterval = false;

                /**
                 * The last sync of this item.
                 * */
                public Date lastSync;

                /**
                 * Initializes a new instance of SyncStateItem.
                 * */
                public SyncStateItem(boolean needsSync, boolean hasLocalData)
                {
                    this.needsSync = needsSync;
                    this.hasLocalData = hasLocalData;
                }

                /**
                 * Initializes a new instance of SyncStateItem.
                 * */
                public SyncStateItem(boolean needsSync, boolean hasLocalData, long syncInterval)
                {
                    this.needsSync = needsSync;
                    this.hasLocalData = hasLocalData;

                    /**
                     * Check if to use interval.
                     * */
                    if(syncInterval == DONT_USE_INTERVAL)
                    {
                        this.syncInterval = syncInterval;
                        this.useSyncInterval = false;
                    }
                    else
                    {
                        this.syncInterval = syncInterval;
                        this.useSyncInterval = true;
                    }

                }

                public static final long DONT_USE_INTERVAL = -1;

            }
        }

        public static final String TRACKER_SYNCER_SYNC_FAILED_AUTH_REQUIRED = "TRACKER_SYNCER_SYNC_FAILED_AUTH_REQUIRED";

        public static final String TRACKER_SYNCER_SYNC_FAILED_NO_INTERNET = "TRACKER_SYNCER_SYNC_FAILED_NO_INTERNET";

    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 13-2-2016
     * Class for helping with applying the preferences to the GoogleMap.
     * */
    public static class PreferenceHelper
    {

        /**
         * Apply the preferences to the GoogleMap.
         * */
        public static void applyPreferences(GoogleMap googleMap)
        {
            if(googleMap != null)
            {
                googleMap.setMapType(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).getString("pref_map_type", "1")));
            }
        }
    }

    /**
     * Defines a tracker identifier for the clustering process completion.
     * */
    public static final String TRACKER_MAPMANAGER_CLUSTERING_COMPLETED = "TRACKER_MAPMANAGER_CLUSTERING_COMPLETED";

    public static final LatLng RP_LAT_LNG = new LatLng(52.015335, 6.025965);

    public static final float ZOOM = 9;

    public static final float TILT = 0;

    public static final float BEARING = 0;

}