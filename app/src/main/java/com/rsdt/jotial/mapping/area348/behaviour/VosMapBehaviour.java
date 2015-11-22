package com.rsdt.jotial.mapping.area348.behaviour;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.rsdt.jotial.data.structures.area348.receivables.BaseInfo;
import com.rsdt.jotial.data.structures.area348.receivables.VosInfo;

import com.rsdt.jotial.mapping.area348.MapManager;
import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEvent;
import com.rsdt.jotial.mapping.area348.data.GraphicalMapData;
import com.rsdt.jotial.mapping.area348.data.MapData;
import com.rsdt.jotial.mapping.area348.data.MapDataPair;

import com.rsdt.jotiv2.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 15-11-2015
 * Defines the behaviour for a Vos.
 */
public class VosMapBehaviour extends MapBehaviour {

    public VosMapBehaviour(MapData mapData, GoogleMap googleMap)
    {
        super(mapData, googleMap);


        super.eventRaiser.getEvents().add(new MapBehaviourEvent<Marker>(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW) {
            @Override
            public boolean apply(Marker marker) {
                return (marker.getTitle().startsWith("vos"));
            }

            @Override
            public void onConditionMet(Object[] args) {
                GraphicalMapData.GraphicalMapDataPair<Marker, MarkerOptions> pair = findPair(((Marker) args[1]).getId());

                View view = (View) args[0];

                VosInfo associatedInfo = ((VosInfo) pair.second.second.get(0));

                ((TextView) view.findViewById(R.id.infoWindow_infoType)).setText("VosInfo");

                view.findViewById(R.id.infoWindow_infoType).setBackgroundColor(MapManager.parse(associatedInfo.team, 255));

                ((TextView) view.findViewById(R.id.infoWindow_naam)).setText(associatedInfo.team_naam);

                ((TextView) view.findViewById(R.id.infoWindow_dateTime_adres)).setText(associatedInfo.datetime);

                ((TextView) view.findViewById(R.id.infoWindow_coordinaat)).setText(associatedInfo.latitude + " , " + associatedInfo.longitude);
            }
        });
    }

    /**
     * Describes how this behaviour gets deserializes from a string to MapData.
     * */
    public static MapData toMapData(String data) {

        /**
         * Create a buffer to hold the data.
         * */
        MapData buffer = MapData.empty();

        /**
         * Deserialize the data to a VosInfo array.
         * */
        VosInfo[] infos = VosInfo.fromJsonArray(data);

        /**
         * Creates and configures a polyline, to show the path of the vos.
         * */
        PolylineOptions pOptions = new PolylineOptions();
        pOptions.width(VOS_POLYLINE_WIDTH);
        pOptions.color(MapManager.parse(infos[0].team, 255));

        /**
         * Loop through each info.
         * */
        for(int i = 0; i < infos.length; i++)
        {
            /**
             * Setup marker with the current VosInfo data.
             * */
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.anchor(0.5f, 0.5f);
            mOptions.position(new LatLng(infos[i].latitude, infos[i].longitude));
            mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dot_blauw));

            /**
             * Create a identifier for the marker, so we can find it.
             * NOTE: Maybe not necessary because markers are now pair so if you find the pair you find the options as well.
             * */
            mOptions.title(buildInfoTitle(new String[]{"vos", infos[0].team, ((Integer) infos[i].id).toString(), infos[0].datetime}, " "));

            /**
             * Add a point to the points of the vos line.
             * */
            pOptions.add(new LatLng(infos[i].latitude, infos[i].longitude));


            buffer.getMarkers().add(new MapDataPair<>(mOptions, new ArrayList<BaseInfo>(Arrays.asList(infos[i]))));
        }
        buffer.getPolylines().add(new MapDataPair<>(pOptions, new ArrayList<BaseInfo>(Arrays.asList(infos))));

        /**
         * NOTE: The circles are no longer created in the background and then stored.
         * It makes more sense to make the circle for the desired marker if requested, this way less data needs to be stored.
         * TODO: Consider this note for everything, so never stored the Options. Just recreate them if needed?
         * */

        return buffer;
    }

    /**
     * Defines the with of the vos polyline.
     * */
    public static final int VOS_POLYLINE_WIDTH = 2;

    /**
     * Defines the stroke width of the vos circle.
     * */
    public static final int VOS_CIRCLE_STROKE_WIDTH = 3;

    /**
     * Defines the stroke color of the vos circle.
     * */
    public static final int VOS_CIRCLE_STROKE_COLOR = Color.BLACK;

    /**
     * Defines how much the fill color should be alphed.
     * */
    public static final int VOS_CIRCLE_FILL_COLOR_ALPHA = 98;

}


