package com.rsdt.jotial.data.structures.official;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-11-2015
 * Description...
 */
public class Vossen implements Parcelable {


    public String version;

    public int last_update;

    public ArrayList<VossenStatus> data;

    protected Vossen(Parcel in) {
        version = in.readString();
        last_update = in.readInt();
        data = in.createTypedArrayList(VossenStatus.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeInt(last_update);
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static class VossenStatus implements Parcelable
    {
        String team;

        String status;

        protected VossenStatus(Parcel in) {
            team = in.readString();
            status = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(team);
            dest.writeString(status);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<VossenStatus> CREATOR = new Creator<VossenStatus>() {
            @Override
            public VossenStatus createFromParcel(Parcel in) {
                return new VossenStatus(in);
            }

            @Override
            public VossenStatus[] newArray(int size) {
                return new VossenStatus[size];
            }
        };
    }

    public static final Creator<Vossen> CREATOR = new Creator<Vossen>() {
        @Override
        public Vossen createFromParcel(Parcel in) {
            return new Vossen(in);
        }

        @Override
        public Vossen[] newArray(int size) {
            return new Vossen[size];
        }
    };


    /**
     * Deserializes a Vossen object from the given JSON.
     *
     * @param json The JSON where the Vossen object should be deserialized from.
     * */
    public static Vossen fromJson(String json)
    {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, Vossen.class);
    }

}
