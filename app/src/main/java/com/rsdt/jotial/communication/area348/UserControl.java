package com.rsdt.jotial.communication.area348;

import android.content.SharedPreferences;

import android.preference.PreferenceManager;

import com.android.internal.util.Predicate;

import com.google.gson.Gson;

import com.rsdt.jotial.JotiApp;

import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiRequest;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.LinkBuilder;

import com.rsdt.jotial.data.structures.area348.receivables.UserInfo;

import com.rsdt.jotial.mapping.area348.MapManager;

import com.rsdt.jotiv2.Tracker;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-2-2016
 * Class for controlling the user's info.
 */
public class UserControl implements ApiManager.OnApiTaskCompleteCallback {

    /**
     * The UserInfo of the UserControl.
     * */
    private UserInfo userInfo;

    /**
     * Gets the current UserInfo.
     * */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public UserControl()
    {
        MapManager.getApiManager().addListener(this, new Predicate<ApiResult>() {
            @Override
            public boolean apply(ApiResult result) {
                return (result.getRequest().getUrl().getPath().split("/")[1].equals("gebruiker"));
            }
        });
    }

    @Override
    public void onApiTaskCompleted(ArrayList<ApiResult> results, String origin) {

        /**
         * Allocate result buffer outside loop.
         * */
        ApiResult currentResult;

        /**
         * Loop through each result.
         * */
        for(int i = 0; i < results.size(); i++)
        {
            currentResult = results.get(i);

            if(currentResult.getResponseCode() == 200)
            {
                userInfo = new Gson().fromJson(currentResult.getData(), UserInfo.class);

                new Thread(new DownloadAvatarTask()).run();

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).edit();

                /**
                 * Put the rank into the Preferences.
                 * */
                editor.putString("pref_account_rank", userInfo.rank());

                /**
                 * Apply the changes.
                 * */
                editor.apply();

                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_USERCONTROL_RETRIEVE_SUCCEEDED, "UserControl", "Successfully retrieved the user info"));
            }
            else
            {
                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_USERCONTROL_RETRIEVE_FAILED, "UserControl", "Failed to retrieve user info, code " + currentResult.getResponseCode()));
            }

        }

    }


    public void retrieve()
    {
        String key = JotiApp.Auth.getKey();

        if(key != null && !key.isEmpty())
        {
            MapManager.getApiManager().queue(new ApiRequest(LinkBuilder.build(new String[]{ "gebruiker", key, "info"})));
            MapManager.getApiManager().preform();
        }
        else
        {
            JotiApp.Auth.requireAuth();
        }
    }


    public class DownloadAvatarTask implements Runnable {

        @Override
        public void run() {

            /**
             * Gets the avatar.
             * */
            userInfo.avatarDrawable = UserInfo.getAvatar(userInfo.avatar);
            JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_USERCONTROL_AVATAR_RETRIEVED, "DownloadAvatarTask", "The avatar has been retrieved"));
        }
    }

    public static final String TRACKER_USERCONTROL_RETRIEVE_SUCCEEDED = "TRACKER_USERCONTROL_RETRIEVE_SUCCEEDED";

    public static final String TRACKER_USERCONTROL_RETRIEVE_FAILED = "TRACKER_USERCONTROL_RETRIEVE_FAILED";

    public static final String TRACKER_USERCONTROL_AVATAR_RETRIEVED = "TRACKER_USERCONTROL_AVATAR_RETRIEVED";

}
