package com.rsdt.jotial.mapping.area348.data;

import android.os.Parcel;
import android.os.Parcelable;

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
public class MapData implements Parcelable {


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
     *           Because the parcelable approach is never used, we use a seriliazable hashmap.
     * */
    protected MapData(Parcel in) {
        in.readTypedList(markers, MapDataPair.CREATOR_MARKER);
        in.readTypedList(polylines, MapDataPair.CREATOR_POLYLINE);
        in.readTypedList(circles, MapDataPair.CREATOR_CIRCLE);
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

    /**
     * A empty instance of MapData.
     * */
    public static final MapData empty() { return new MapData(); }

}
