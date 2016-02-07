package com.rsdt.jotial.mapping.area348.behaviour;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.rsdt.jotial.JotiApp;
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

    /**
     * The deelgebied of the VosMapBehaviour.
     * */
    private char deelgebied;

    /**
     * Gets the deelgebied of the VosMapBehaviour.
     * */
    public char getDeelgebied() {
        return deelgebied;
    }

    /**
     * Initializes a new instance of VosMapBehaviour.
     * */
    public VosMapBehaviour(char deelgebied, MapData mapData, GoogleMap googleMap)
    {
        super(mapData, googleMap);

        this.deelgebied = deelgebied;

        super.eventRaiser.getEvents().add(new MapBehaviourEvent<Marker>(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW) {
            @Override
            public boolean apply(Marker marker) {
                return (marker.getTitle().startsWith("vos") && marker.getTitle().charAt(4) == getDeelgebied());
            }

            @Override
            public void onConditionMet(Object[] args) {
                GraphicalMapData.GraphicalMapDataPair<Marker, MarkerOptions> pair = findPair(((Marker) args[1]).getId());

                View view = (View) args[0];

                VosInfo associatedInfo = ((VosInfo) pair.second.second.get(0));

                ((TextView) view.findViewById(R.id.infoWindow_infoType)).setText("VosInfo");

                view.findViewById(R.id.infoWindow_infoType).setBackgroundColor(MapManager.parse(associatedInfo.team, 255));

                ((TextView) view.findViewById(R.id.infoWindow_naam)).setText(associatedInfo.opmerking);

                ((TextView) view.findViewById(R.id.infoWindow_dateTime_adres)).setText(associatedInfo.datetime);

                ((TextView) view.findViewById(R.id.infoWindow_coordinaat)).setText(associatedInfo.latitude + " , " + associatedInfo.longitude);
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
         * Clear our own polyline list and add all the other's.
         * */
        for(int i = 0; i < polylines.size(); i++)
        {
            polylines.get(i).remove();
        }
        polylines.clear();
        polylines.addAll(other.getPolylines());

        /**
         * Destroy the other, since it's no longer needed.
         * */
        other.destroy();
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

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 4;
            /**
             * Allocate bitmap as buffer for the icon.
             * */
            Bitmap bitmap;

            /**
             * Checks if the info is the first, this meaning the latest.
             * The latest info should have a special icon.
             * */
            if(i == 0)
            {
                bmOptions.inSampleSize = 2;
                switch(infos[0].team)
                {
                    case "a":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.target_rood ,bmOptions);
                        break;
                    case "b":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.target_groen ,bmOptions);
                        break;
                    case "c":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.target_blauw ,bmOptions);
                        break;
                    case "d":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.target_turquoise ,bmOptions);
                        break;
                    case "e":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.target_paars ,bmOptions);
                        break;
                    case "f":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.target_geel ,bmOptions);
                        break;
                    case "x":
                        bitmap = BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.target_zwart ,bmOptions);
                        break;
                    default:
                        bitmap = null;
                        break;
                }
            }
            else
            {
                switch(infos[0].team)
                {
                    case "a":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.dot_rood ,bmOptions);
                        break;
                    case "b":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.dot_groen ,bmOptions);
                        break;
                    case "c":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.dot_blauw ,bmOptions);
                        break;
                    case "d":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.dot_turquoise ,bmOptions);
                        break;
                    case "e":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.dot_paars ,bmOptions);
                        break;
                    case "f":
                        bitmap =BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.dot_geel ,bmOptions);
                        break;
                    case "x":
                        bitmap = BitmapFactory.decodeResource(JotiApp.getContext().getResources(),
                                R.drawable.dot_zwart ,bmOptions);
                        break;
                    default:
                        bitmap = null;
                        break;
                }
            }
            mOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

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
    public static final int VOS_POLYLINE_WIDTH = 10;

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


