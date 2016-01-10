package com.rsdt.jotial.data.structures.area348.sendables;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-1-2016
 * Represents the VosInfo that can be send.
 */
public class VosInfoSendable {

    /**
     * Initializes a new instance of VosInfoSendable.
     *
     * @param gebruiker The username of the hunter.
     * @param latitude  The position's latitude value.
     * @param longitude The position's longitude value.
     * @param team  The team of the vos.
     * @param opmerking A note about the info.
     */
    public VosInfoSendable(String gebruiker, double latitude, double longitude, String team, String opmerking) {
        this.gebruiker = gebruiker;
        this.latitude = latitude;
        this.longitude = longitude;
        this.team = team;
        this.opmerking = opmerking;
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

    /**
     * The team of the sendable.
     * */
    public String team;

    /**
     * The note of the sendable.
     * */
    public String opmerking;

}
