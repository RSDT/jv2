package com.rsdt.jotial.data.structures.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the VosInfo.
 */
public class VosInfo extends BaseInfo implements Parcelable {

    /**
     * Initializes a new instance of VosInfo from the parcel.
     *
     * @param in The parcel where the instance should be created from.
     */
    protected VosInfo(Parcel in) {
        super(in);
        datetime = in.readString();
        team = in.readString();
        team_naam = in.readString();
        opmerking = in.readString();
        gebruiker = in.readString();
    }

    /**
     * The dateTime the vosInfo was created.
     */
    public String datetime;

    /**
     * The team of the VosInfo as a char.
     */
    public String team;

    /**
     * The team of the VosInfo as a whole name.
     */
    public String team_naam;

    /**
     * A extra of the VosInfo.
     */
    public String opmerking;

    /**
     * The user of the VosInfo.
     */
    public String gebruiker;


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(datetime);
        dest.writeString(team);
        dest.writeString(team_naam);
        dest.writeString(opmerking);
        dest.writeString(gebruiker);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VosInfo> CREATOR = new Creator<VosInfo>() {
        @Override
        public VosInfo createFromParcel(Parcel in) {
            return new VosInfo(in);
        }

        @Override
        public VosInfo[] newArray(int size) {
            return new VosInfo[size];
        }
    };

    /**
     * Deserializes a VosInfo from JSON.
     * @param json The JSON where the VosInfo should be deserialized from.
     * @return The VosInfo deserialized from the JSON.
     */
    public static VosInfo fromJson(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, VosInfo.class);
    }

    /**
     * Deserializes a VosInfo array from JSON.
     * @param json The JSON where the array should be deserialized from.
     * @return The array of VosInfo deserialized from the JSON.
     */
    public static VosInfo[] fromJsonArray(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, VosInfo[].class);
    }

}
