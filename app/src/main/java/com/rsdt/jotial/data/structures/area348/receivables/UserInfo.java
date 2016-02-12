package com.rsdt.jotial.data.structures.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-2-2016
 * Class represents that the users info.
 */
public class UserInfo  implements Parcelable {


    public int id;

    public String gebruikersnaam;

    public String naam;

    public String achternaam;

    public String email;

    public String sinds;

    public String last;

    public int actief;

    public int toeganslvl;

    public String avatar;

    public String rank() { return IntLvlToStringRank(toeganslvl); }

    protected UserInfo(Parcel in) {
        id = in.readInt();
        gebruikersnaam = in.readString();
        naam = in.readString();
        achternaam = in.readString();
        email = in.readString();
        sinds = in.readString();
        last = in.readString();
        actief = in.readInt();
        toeganslvl = in.readInt();
        avatar = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(gebruikersnaam);
        dest.writeString(naam);
        dest.writeString(achternaam);
        dest.writeString(email);
        dest.writeString(sinds);
        dest.writeString(last);
        dest.writeInt(actief);
        dest.writeInt(toeganslvl);
        dest.writeString(avatar);
    }

    /**
     * Converts the toegangs level to a string rank.
     * */
    public static String IntLvlToStringRank(int toeganslvl)
    {
        /**
         * Allocate String to hold the rank.
         * */
        String rank;

        /**
         * Switch on the toeganslvl, to determine the associated rank name.
         * */
        switch (toeganslvl)
        {
            case 0:
                rank = "Guest";
                break;
            case 25:
                rank = "Member";
                break;
            case 50:
                rank = "Moderator";
                break;
            case 75:
                rank = "Admin";
                break;
            default:
                rank = "unkown";
                break;
        }
        return rank;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

}
