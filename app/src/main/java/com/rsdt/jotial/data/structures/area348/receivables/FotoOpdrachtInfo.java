package com.rsdt.jotial.data.structures.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the FotoOpdrachtInfo.
 */
public class FotoOpdrachtInfo extends BaseInfo implements Parcelable {

    /**
     * Initializes a new instance of FotoOpdrachtInfo from the parcel.
     *
     * @param in The parcel where the instance should be created from.
     */
    protected FotoOpdrachtInfo(Parcel in) {
        super(in);
        naam = in.readString();
        info = in.readString();
        extra = in.readString();
        klaar = in.readByte() != 0;
    }

    /**
     * The name of the FotoOpdrachtInfo.
     */
    public String naam;

    /**
     * The info of the FotoOpdrachtInfo.
     */
    public String info;

    /**
     * The extra of the FotoOpdrachtInfo.
     */
    public String extra;

    /**
     * The value indicating if FotoOpdrachtInfo is completed or not.
     */
    public boolean klaar;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(naam);
        dest.writeString(info);
        dest.writeString(extra);
        dest.writeByte((byte) (klaar ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FotoOpdrachtInfo> CREATOR = new Creator<FotoOpdrachtInfo>() {
        @Override
        public FotoOpdrachtInfo createFromParcel(Parcel in) {
            return new FotoOpdrachtInfo(in);
        }

        @Override
        public FotoOpdrachtInfo[] newArray(int size) {
            return new FotoOpdrachtInfo[size];
        }
    };

    /**
     * Deserializes a FotoOpdrachtInfo from the JSON.
     *
     * @param json The JSON where the FotoOpdrachtInfo should be deserialized from.
     * @return A FotoOpdrachtInfo.
     */
    public static FotoOpdrachtInfo fromJson(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, FotoOpdrachtInfo.class);
    }

    /**
     * Deserializes a array of FotoOpdrachtInfo from the JSON.
     *
     * @param json The JSON where the array of FotoOpdrachtInfo should be deserialized from.
     * @return A array of FotoOpdrachtInfo.
     */
    public static FotoOpdrachtInfo[] fromJsonArray(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, FotoOpdrachtInfo[].class);
    }

}
