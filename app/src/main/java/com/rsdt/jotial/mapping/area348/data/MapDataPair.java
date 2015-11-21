package com.rsdt.jotial.mapping.area348.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.MarkerOptions;
import com.rsdt.jotial.data.structures.area348.receivables.BaseInfo;


import java.util.ArrayList;

/**
 * Holds a item options object with the associated BaseInfos.
 * MapDataPair stands for ItemOptionsBaseInfosPair.
 * NOTE: Generic type T can only be MarkerOptions, CircleOptions and PolyLineOptions.
 * */
public class MapDataPair<T extends Parcelable> extends Pair<T, ArrayList<BaseInfo>> implements Parcelable
{

    /**
     * Constructor for a MapDataPair.
     *
     * @param first  the first object in the MapDataPair
     * @param second the second object in the MapDataPair
     */
    public MapDataPair(T first, ArrayList<BaseInfo> second) {
        super(first, second);
    }


    /**
     * Creates a new MapDataPair from the Parcel.
     *
     * @param in The Parcel where the MapDataPair should be created from.
     * */
    protected MapDataPair(Parcel in) {
        super((T)in.readParcelable(getAssociatedClassloader(in.readString())), in.createTypedArrayList(BaseInfo.CREATOR));
    }


    /**
     * Gets the associated ClassLoader of the generic object.
     * @param type The type of the generic object.
     * @return The ClassLoader of the generic object.
     */
    private static ClassLoader getAssociatedClassloader(String type)
    {
        try
        {
            return Class.forName(type).getClassLoader();
        } catch(Exception e)
        {
            Log.e("MapDataPair", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(first, flags);
        dest.writeTypedList(second);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MapDataPair> CREATOR = new Creator<MapDataPair>() {
        @Override
        public MapDataPair createFromParcel(Parcel in) {
            return new MapDataPair<>(in);
        }

        @Override
        public MapDataPair[] newArray(int size) {
            return new MapDataPair[size];
        }
    };
}
