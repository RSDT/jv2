package com.rsdt.jotial.data.structures.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.maps.android.clustering.ClusterItem;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the ScoutingGroepInfo.
 * NOTE: This class implements ClusterItem and thereby can be clustered.
 */
public class ScoutingGroepInfo extends BaseInfo implements Parcelable, ClusterItem {

    /**
     * Initializes a new instance of ScoutingGroepInfo from the parcel.
     *
     * @param in The parcel where the instance should be created from.
     */
    protected ScoutingGroepInfo(Parcel in) {
        super(in);
        naam = in.readString();
        adres = in.readString();
        deelgebied = in.readString();
    }

    /**
     * The name of the ScoutingGroepInfo.
     */
    public String naam;

    /**
     * The address of the ScoutingGroepInfo.
     */
    public String adres;

    /**
     * The area where the ScoutingGroepInfo is located.
     */
    public String deelgebied;

    @Override
    /**
     * Cluster item implementation.
     * */
    public LatLng getPosition() {
        return new LatLng(super.latitude, super.longitude);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(naam);
        dest.writeString(adres);
        dest.writeString(deelgebied);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScoutingGroepInfo> CREATOR = new Creator<ScoutingGroepInfo>() {
        @Override
        public ScoutingGroepInfo createFromParcel(Parcel in) {
            return new ScoutingGroepInfo(in);
        }

        @Override
        public ScoutingGroepInfo[] newArray(int size) {
            return new ScoutingGroepInfo[size];
        }
    };

    /**
     * Deserializes a ScoutingGroepInfo from JSON.
     * @param json The JSON where the ScoutingGroepInfo should be deserialized from.
     * @return The deserialized ScoutingGroepInfo.
     */
    public static ScoutingGroepInfo fromJson(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, ScoutingGroepInfo.class);
    }

    /**
     * Deserializes a array of ScoutingGroepInfo from JSON.
     * @param json The JSON where the array of ScoutingGroepInfo should be deserialized from.
     * @return The deserialized array of ScoutingGroepInfo.
     */
    public static ScoutingGroepInfo[] fromJsonArray(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, ScoutingGroepInfo[].class);
    }
}
