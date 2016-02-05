package com.rsdt.jotial.data.structures.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as a deserialization object for the most abstract Info.
 */
public class BaseInfo implements Parcelable {

    /**
     * Initializes a new instance of BaseInfo from the parcel.
     * */
    protected BaseInfo(Parcel in) {
        id = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    /**
     * The id of the Info.
     */
    public int id;

    /**
     * The latitude of the Info.
     */
    public double latitude;

    /**
     * The longitude of the Info.
     */
    public double longitude;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseInfo> CREATOR = new Creator<BaseInfo>() {
        @Override
        public BaseInfo createFromParcel(Parcel in) {
            return new BaseInfo(in);
        }

        @Override
        public BaseInfo[] newArray(int size) {
            return new BaseInfo[size];
        }
    };
}
