package com.rsdt.jotial.mapping.area348.behaviour;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rsdt.jotial.data.structures.area348.receivables.BaseInfo;
import com.rsdt.jotial.data.structures.area348.receivables.HunterInfo;
import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEvent;
import com.rsdt.jotial.mapping.area348.data.MapData;
import com.rsdt.jotial.mapping.area348.data.MapDataPair;
import com.rsdt.jotiv2.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 22-11-2015
 * Description...
 */
public class HunterMapBehaviour extends MapBehaviour {

    /**
     * Initializes a new instance of MapBehaviour.
     *
     * @param mapData   The MapData where the GraphicalMapData should be created from.
     * @param googleMap The GoogleMap used to create the graphical part.
     */
    public HunterMapBehaviour(MapData mapData, GoogleMap googleMap) {
        super(mapData, googleMap);

        super.eventRaiser.getEvents().add(new MapBehaviourEvent<Marker>(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW) {
            @Override
            public boolean apply(Marker marker) {
                return (marker.getTitle().startsWith("hunter"));
            }

            @Override
            public void onConditionMet(Object[] args) {

                View view = (View)args[0];

                GraphicalMapDataPair<Marker, MarkerOptions> pair = findPair(((Marker)args[1]).getId());

                HunterInfo associatedInfo = (HunterInfo)pair.second.second.get(0);

                ((TextView)view.findViewById(R.id.infoWindow_infoType)).setText("HunterInfo");

                ((TextView)view.findViewById(R.id.infoWindow_naam)).setText(associatedInfo.gebruiker);

                ((TextView)view.findViewById(R.id.infoWindow_dateTime_adres)).setText(associatedInfo.datetime);

                ((TextView)view.findViewById(R.id.infoWindow_coordinaat)).setText(associatedInfo.latitude + " , " + associatedInfo.longitude);

            }
        });
    }

    public static final MapData toMapData(String data)
    {
        /**
         * Create a buffer to hold the data.
         * */
        MapData buffer = MapData.empty();

        /**
         * Deserialize the 2D array from the JSON.
         * */
        HunterInfo[][] hunterInfos = HunterInfo.formJsonArray2D(data);


        /**
         * Loop through each user.
         * */
        for (int h = 0; h < hunterInfos.length; h++) {
            /**
             * Setup the polyline for the Hunter.
             * */
            PolylineOptions pOptions = new PolylineOptions();
            pOptions.width(HUNTER_POLYLINE_WIDTH);
            pOptions.color(HUNTER_POLYLINE_COLOR);

            /**
             * Loop through each info.
             * */
            for (int i = 0; i < hunterInfos[h].length; i++) {
                pOptions.add(new LatLng(hunterInfos[h][i].latitude, hunterInfos[h][i].longitude));
            }

            /**
             * Add the polyline to the buffer.
             * */
            buffer.getPolylines().add(new MapDataPair<>(pOptions, new ArrayList<BaseInfo>(Arrays.asList(hunterInfos[h]))));

            /**
             * The lastest info should be the first one in the array.
             * */
            HunterInfo lastestInfo = hunterInfos[h][0];

            /**
             * Setup the marker for the Hunter.
             * */
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.title(buildInfoTitle(new String[]{ "hunter" }, " "));
            mOptions.position(new LatLng(lastestInfo.latitude, lastestInfo.longitude));

            /**
             * Add the marker to the buffer.
             * */
            buffer.getMarkers().add(new MapDataPair<>(mOptions, new ArrayList<BaseInfo>(Arrays.asList(hunterInfos[h][0]))));
        }
        return buffer;
    }


    /**
     * Defines the width of the Hunter polyline.
     * */
    public static final int HUNTER_POLYLINE_WIDTH = 3;

    /**
     * Defines the color of the Hunter polyline.
     * */
    public static final int HUNTER_POLYLINE_COLOR = Color.BLACK;


}
