package com.rsdt.jotial.data.structures.area348.sendables;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as a serialization object for sending the HunterInfo.
 */
public class HunterInfoSendable {

    /**
     * Initializes a new instance of HunterInfoSendable.
     *
     * @param gebruiker The username of the hunter.
     * @param latitude  The position's latitude value.
     * @param longitude The position's longitude value.
     */
    public HunterInfoSendable(String gebruiker, double latitude, double longitude) {
        this.gebruiker = gebruiker;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * The user of the sendable.
     */
    public String gebruiker;

    /**
     * The latitude of the sendable.
     */
    public double latitude;

    /**
     * The longitude of the sendable.
     */
    public double longitude;


}
