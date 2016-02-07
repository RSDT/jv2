package com.rsdt.jotial.data.structures.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.util.Map;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the HunterInfo.
 */
public class HunterInfo extends BaseInfo implements Parcelable {

    /**
     * Initializes a new instance of HunterInfo from the parcel.
     *
     * @param in The parcel where the instance should be created from.
     */
    protected HunterInfo(Parcel in) {
        super(in);
        datetime = in.readString();
        hunter = in.readString();
        icon = in.readInt();
    }

    /**
     * The dateTime the HunterInfo was created.
     */
    public String datetime;

    /**
     * The user of the HunterInfo.
     */
    public String hunter;

    /**
     * The icon of the HunterInfo.
     * */
    public int icon;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(datetime);
        dest.writeString(hunter);
        dest.writeInt(icon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HunterInfo> CREATOR = new Creator<HunterInfo>() {
        @Override
        public HunterInfo createFromParcel(Parcel in) {
            return new HunterInfo(in);
        }

        @Override
        public HunterInfo[] newArray(int size) {
            return new HunterInfo[size];
        }
    };

    /**
     * Deserializes a HunterInfo from JSON.
     *
     * @param json The JSON where the HunterInfo should be deserialized from.
     * @return A HunterInfo.
     */
    public static HunterInfo fromJson(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, HunterInfo.class);
    }

    /**
     * Deserializes a array of HunterInfo from JSON.
     *
     * @param json The JSON where the array of HunterInfo should be deserialized from.
     * @return A array of HunterInfo.
     */
    public static HunterInfo[] fromJsonArray(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, HunterInfo[].class);
    }

    /**
     * Deserializes a 2D array of HunterInfo from JSON.
     *
     * @param json The JSON where the 2D array of HunterInfo should be deserialized from.
     * @return A 2D array of HunterInfo.
     */
    public static HunterInfo[][] formJsonArray2D(String json) {

        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(jsonReader);
        HunterInfo[][] buffer = new HunterInfo[object.entrySet().size()][];
        int count = 0;
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            buffer[count] = fromJsonArray(entry.getValue().toString());
            count++;
        }
        return buffer;
    }
}
