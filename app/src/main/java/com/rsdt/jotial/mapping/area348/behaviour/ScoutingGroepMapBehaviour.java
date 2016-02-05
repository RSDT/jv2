package com.rsdt.jotial.mapping.area348.behaviour;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.rsdt.jotial.data.structures.area348.receivables.BaseInfo;
import com.rsdt.jotial.data.structures.area348.receivables.ScoutingGroepInfo;
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
 * @since 3-11-2015
 * Defines the behaviour for ScoutingGroepen.
 */
public class ScoutingGroepMapBehaviour extends MapBehaviour {


    /**
     * Initializes a new instance of MapBehaviour.
     *
     * @param mapData   The MapData where the GraphicalMapData should be created from.
     * @param googleMap The GoogleMap used to create the graphical part.
     */
    public ScoutingGroepMapBehaviour(MapData mapData, GoogleMap googleMap) {
        super(mapData, googleMap);

        super.eventRaiser.getEvents().add(new MapBehaviourEvent<Marker>(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW) {
            @Override
            public boolean apply(Marker marker) {
                return (marker.getTitle().startsWith("sc"));
            }

            @Override
            public void onConditionMet(Object[] args) {
                View view = (View)args[0];

                GraphicalMapDataPair<Marker, MarkerOptions> pair = findPair( ((Marker)args[1]).getId());

                ScoutingGroepInfo associatedInfo = (ScoutingGroepInfo)pair.second.second.get(0);

                ((TextView)view.findViewById(R.id.infoWindow_infoType)).setText("ScoutingGroepInfo");

                ((TextView)view.findViewById(R.id.infoWindow_naam)).setText(associatedInfo.naam);

                ((TextView)view.findViewById(R.id.infoWindow_dateTime_adres)).setText(associatedInfo.adres);

                ((TextView)view.findViewById(R.id.infoWindow_coordinaat)).setText(associatedInfo.latitude + " , " + associatedInfo.longitude);
            }

        });
    }

    @Override
    public void merge(GraphicalMapData other) {

        /**
         * Clear our own marker list and add all the other's.
         * */
        for(int i = 0; i < markers.size(); i++)
        {
            markers.get(i).remove();
        }
        markers.clear();
        markers.addAll(other.getMarkers());

        /**
         * Destroy the other, since it's no longer needed.
         * */
        other.destroy();
    }


    public static final MapData toMapData(String data)
    {
        /**
         * Creates a buffer to hold the data.
         * */
        MapData buffer = MapData.empty();

        /**
         * Deserializes the json into a array of ScoutingGroepInfo.
         * */
        ScoutingGroepInfo[] groepen = ScoutingGroepInfo.fromJsonArray(data);

        /**
         * Loops through each ScoutingGroepInfo, adding a marker and circle for each one.
         * */
        for (int i = 0; i < groepen.length; i++) {

            /**
             * Setups the marker.
             * */
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.position(new LatLng(groepen[i].latitude, groepen[i].longitude));
            mOptions.title(buildInfoTitle(new String[]{"sc", groepen[i].team}, " "));
            mOptions.anchor(0.5f, 0.5f);

            /**
             * Add the circle and the marker to the offline collection and to the map.
             * */
            buffer.getMarkers().add(new MapDataPair<>(mOptions, new ArrayList<BaseInfo>(Arrays.asList(groepen[i]))));
        }
        return buffer;
    }

    /**
     * Defines the radius of the ScoutingGroep circle.
     * */
    public static final int SCOUTING_GROEP_CIRCLE_RADIUS = 500;

    /**
     * Defines the stroke color of the ScoutingGroep circle.
     * */
    public static final int SCOUTING_GROEP_CIRCLE_STROKE_COLOR = Color.BLACK;

    /**
     * Defines the stroke width of the ScoutingGroep circle.
     * */
    public static final int SCOUTING_GROEP_CIRCLE_STROKE_WIDTH = 3;

    /**
     * Defines the how much the fill color should be alphed.
     * */
    public static final int SCOUTING_GROEP_CIRCLE_FILL_COLOR_ALPHA = 98;

}
