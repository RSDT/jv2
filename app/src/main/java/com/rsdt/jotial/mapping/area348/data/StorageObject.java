package com.rsdt.jotial.mapping.area348.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.rsdt.jotial.data.structures.area348.receivables.BaseInfo;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Object for holding data of a MapPartState.
 */
public class StorageObject implements Parcelable, CapableActionPreformer {


    public StorageObject()
    {

    }

    /**
     * Initializes a new instance of StorageObject from the parcel.
     *
     * @param in The parcel where the instance should be created from.
     * */
    protected StorageObject(Parcel in) {
        markers = in.createTypedArrayList(MarkerOptions.CREATOR);
        polylines = in.createTypedArrayList(PolylineOptions.CREATOR);
        circles = in.createTypedArrayList(CircleOptions.CREATOR);
        associatedInfos = in.createTypedArrayList(BaseInfo.CREATOR);
    }

    /**
     * The markers of the StorageObject.
     * */
    private ArrayList<MarkerOptions> markers = new ArrayList<>();

    /**
     * The polylines of the StorageObject.
     * */
    private ArrayList<PolylineOptions> polylines = new ArrayList<>();

    /**
     * The circles of the StorageObject.
     * */
    private ArrayList<CircleOptions> circles = new ArrayList<>();

    /**
     * The infos of the StorageObject.
     * */
    private ArrayList<BaseInfo> associatedInfos = new ArrayList<>();

    public ArrayList<MarkerOptions> getMarkers() {
        return markers;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(markers);
        dest.writeTypedList(polylines);
        dest.writeTypedList(circles);
        dest.writeTypedList(associatedInfos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StorageObject> CREATOR = new Creator<StorageObject>() {
        @Override
        public StorageObject createFromParcel(Parcel in) {
            return new StorageObject(in);
        }

        @Override
        public StorageObject[] newArray(int size) {
            return new StorageObject[size];
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

        if(action.getType() == BaseInfo.class)
        {
            for(int i = 0; i < associatedInfos.size(); i++)
            {
                action.preform(associatedInfos.get(i));
            }
        }

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

        if(action.getType() == BaseInfo.class)
        {
            for(int i = 0; i < associatedInfos.size(); i++)
            {
                if(condition.apply(associatedInfos.get(i))) {
                    action.preform(associatedInfos.get(i));
                }
            }
        }
    }
}
