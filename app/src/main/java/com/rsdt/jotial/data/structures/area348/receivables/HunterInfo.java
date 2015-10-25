package com.rsdt.jotial.data.structures.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

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
        gebruiker = in.readString();
    }

    /**
     * The dateTime the HunterInfo was created.
     */
    public String datetime;

    /**
     * The user of the HunterInfo.
     */
    public String gebruiker;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(datetime);
        dest.writeString(gebruiker);
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
}
