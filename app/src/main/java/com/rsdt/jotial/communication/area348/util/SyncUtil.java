package com.rsdt.jotial.communication.area348.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rsdt.jotial.JotiApp;
import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiRequest;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.LinkBuilder;
import com.rsdt.jotial.communication.area348.Area348;
import com.rsdt.jotial.mapping.area348.MapManager;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-1-2016
 * Description...
 */
public class SyncUtil {


    /**
     * Sends the given location to the server.
     * */
    public static void sendHunterLocation(LatLng location)
    {
        /**
         * Check if we have auth.
         * */
        if(JotiApp.Auth.isAuth())
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());

            /**
             * Get the hunt name out of the preferences.
             * */
            String hunterName = preferences.getString("pref_map_hunt_name", null);

            /**
             * Check the hunter name.
             * */
            if(hunterName != null && !hunterName.isEmpty())
            {
                /**
                 * Construct json object.
                 * */
                JsonObject object = new JsonObject();
                object.addProperty("SLEUTEL", JotiApp.Auth.getKey());
                object.addProperty("hunter", hunterName);
                object.addProperty("latitude", Double.toString(location.latitude));
                object.addProperty("longitude", Double.toString(location.longitude));
                object.addProperty("icon", preferences.getString("pref_account_icon", "0"));

                /**
                 * Queue in and preform the request with the hunter's location data.
                 * */
                LinkBuilder.setRoot(Area348.API_V2_ROOT);
                MapManager.getApiManager().queue(new ApiRequest(LinkBuilder.build(new String[]{"hunter"}), new Gson().toJson(object)));
                MapManager.getApiManager().addListener(new ApiManager.OnApiTaskCompleteCallback() {
                    @Override
                    public void onApiTaskCompleted(ArrayList<ApiResult> results, String origin) {
                        /**
                         * Write to the log a location has been sent.
                         * */
                        Log.i("LocationSender", "The location has been sent.");
                    }
                }, new Predicate<ApiResult>() {
                    @Override
                    public boolean apply(ApiResult result) {
                        return (result.getRequest().getUrl().getPath().split("/")[1].equals("hunter"));
                    }
                });
                MapManager.getApiManager().preform();

                /**
                 * Write to the log, that a location is being sent.
                 * */
                Log.i("LocationSender", "Sending location " + location + " to the server");
            }
            else
            {
                Log.e("LocationSender", "The huntername is not valid");
            }
        }
        else
        {
            /**
             * Require auth.
             * */
            JotiApp.Auth.requireAuth();
        }
    }

    public static void sendVosLocation(LatLng location, String deelgebied, String info, String icon)
    {
        /**
         * Check if the user is auth.
         * */
        if(JotiApp.Auth.isAuth())
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());

            /**
             * Get the hunt name out of the preferences.
             * */
            String hunterName = preferences.getString("pref_map_hunt_name", null);

            if(hunterName != null && !hunterName.isEmpty())
            {
                /**
                 * Construct json object.
                 * */
                JsonObject object = new JsonObject();
                object.addProperty("SLEUTEL", JotiApp.Auth.getKey());
                object.addProperty("hunter", hunterName);
                object.addProperty("latitude", Double.toString(location.latitude));
                object.addProperty("longitude", Double.toString(location.longitude));
                object.addProperty("team", deelgebied);
                object.addProperty("info", info);
                object.addProperty("icon", icon);

                /**
                 * Queue in and preform the request with the hunter's location data.
                 * */
                LinkBuilder.setRoot(Area348.API_V2_ROOT);
                MapManager.getApiManager().queue(new ApiRequest(LinkBuilder.build(new String[]{"vos"}), new Gson().toJson(object)));
                MapManager.getApiManager().addListener(new ApiManager.OnApiTaskCompleteCallback() {
                    @Override
                    public void onApiTaskCompleted(ArrayList<ApiResult> results, String origin) {
                        /**
                         * Write to the log a location has been sent.
                         * */
                        Log.i("LocationSender", "The location has been sent.");
                    }
                }, new Predicate<ApiResult>() {
                    @Override
                    public boolean apply(ApiResult result) {
                        return (result.getRequest().getUrl().getPath().split("/")[1].equals("vos") && result.getRequest().getUrl().getPath().split("/").length < 3);
                    }
                });
                MapManager.getApiManager().preform();

            }
        }
        else
        {
            /**
             * User isn't authenticated, require auth.
             * */
            JotiApp.Auth.requireAuth();
        }


    }


}
