package com.rsdt.jotial.mapping.area348.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.internal.util.Predicate;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Object for holding data of a Map.
 */
public class MapData implements Parcelable, CapableActionPreformer {


    /**
     * Initializes a new instance of MapData.
     * */
    private MapData() { }

    /**
     * Initializes a new instance of MapData from the parcel.
     *
     * @param in The parcel where the instance should be created from.
     *
     * TODO: The data that is coming in is never added again? But at runtime there are no errors and is the data there, why?
     * */
    protected MapData(Parcel in) {

    }

    /**
     * The list of markers and associated info paired of the MapData.
     * */
    private ArrayList<MapDataPair<MarkerOptions>> markers = new ArrayList<>();

    /**
     * The various PolylineOptions of the MapData.
     * */
    private ArrayList<MapDataPair<PolylineOptions>> polylines = new ArrayList<>();

    /**
     * The various CircleOptions of the MapData.
     * */
    private ArrayList<MapDataPair<CircleOptions>> circles = new ArrayList<>();

    /**
     * Gets the array list of IOBIPairs that each contain the MarkerOptions and infos.
     * */
    public ArrayList<MapDataPair<MarkerOptions>> getMarkers() {
        return markers;
    }

    /**
     * Gets the array list of IOBIPairs that each contain the PolylineOptions and infos.
     * */
    public ArrayList<MapDataPair<PolylineOptions>> getPolylines() {
        return polylines;
    }

    /**
     * Gets the array list of IOBIPairs that each contain the CircleOptions and infos.
     * */
    public ArrayList<MapDataPair<CircleOptions>> getCircles() {
        return circles;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(markers);
        dest.writeTypedList(polylines);
        dest.writeTypedList(circles);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MapData> CREATOR = new Creator<MapData>() {
        @Override
        public MapData createFromParcel(Parcel in) {
            return new MapData(in);
        }

        @Override
        public MapData[] newArray(int size) {
            return new MapData[size];
        }
    };

    @Override
    public void preform(ItemAction action) {

        if(action.getType() == MarkerOptions.class)
        {
            for(int i = 0; i < markers.size(); i++)
            {
                action.preform(markers.get(i));
            }
        }

        if(action.getType() == PolylineOptions.class)
        {
            for(int i = 0; i < polylines.size(); i++)
            {
                action.preform(polylines.get(i));
            }
        }


        if(action.getType() == CircleOptions.class)
        {
            for(int i = 0; i < circles.size(); i++)
            {
                action.preform(circles.get(i));
            }
        }

/*        if(action.getType() == BaseInfo.class)
        {
            for(int i = 0; i < associatedInfos.size(); i++)
            {
                action.preform(associatedInfos.get(i));
            }
        }*/

    }

    @Override
    public void preform(ItemAction action, Predicate condition) {
        if(action.getType() == MarkerOptions.class)
        {
            for(int i = 0; i < markers.size(); i++)
            {
                if(condition.apply(markers.get(i)))
                {
                    action.preform(markers.get(i));
                }
            }
        }

        if(action.getType() == PolylineOptions.class)
        {
            for(int i = 0; i < polylines.size(); i++)
            {
                if(condition.apply(polylines.get(i))) {
                    action.preform(polylines.get(i));
                }
            }
        }


        if(action.getType() == CircleOptions.class)
        {
            for(int i = 0; i < circles.size(); i++)
            {
                if(condition.apply(circles.get(i))) {
                    action.preform(circles.get(i));
                }
            }
        }

/*        if(action.getType() == BaseInfo.class)
        {
            for(int i = 0; i < associatedInfos.size(); i++)
            {
                if(condition.apply(associatedInfos.get(i))) {
                    action.preform(associatedInfos.get(i));
                }
            }
        }*/
    }


    /**
     * Gets the MapData from some data with the given behaviour.
     *
     * @param data The data the behaviour should behave on.
     * @param behaviour  The behaviour that should apply on this data.
     *
     * @return The MapData generated by the behaviour using the data.
     * */
    public static MapData from(String data, ToMapDataBehaviour behaviour)
    {
        return behaviour.toMapData(data);
    }

    /**
     * A empty instance of MapData.
     * */
    public static final MapData empty = new MapData();

}
