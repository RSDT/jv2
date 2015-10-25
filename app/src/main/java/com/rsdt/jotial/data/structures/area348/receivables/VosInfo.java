package com.rsdt.jotial.data.structures.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

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

}
