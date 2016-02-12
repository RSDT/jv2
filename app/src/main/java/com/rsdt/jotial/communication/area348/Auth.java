package com.rsdt.jotial.communication.area348;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.internal.util.Predicate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.rsdt.jotial.JotiApp;

import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiRequest;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.LinkBuilder;

import com.rsdt.jotial.mapping.area348.MapManager;

import com.rsdt.jotial.misc.AeSimpleSHA1;

import com.rsdt.jotiv2.MainActivity;
import com.rsdt.jotiv2.Tracker;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 15-1-2016
 * Description...
 */
public class Auth implements ApiManager.OnApiTaskCompleteCallback {

    /**
     * Value indicating if the user is authenticated.
     * */
    private boolean isAuth = false;

    /**
     * Value indicating if the user needs a auth.
     * */
    private boolean requiresAuth = true;

    /**
     * Value indicating if a auth dialog is active.
     * */
    private boolean authDialogActive = false;

    /**
     * The API key used to access the API.
     * */
    public String getKey()
    {
        return PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).getString("pref_account_key", "");
    }

    /**
     * Initializes a new instance of Auth.
     * */
    public Auth()
    {
        /**
         * Add callback, with the filter of the login page.
         * */
        MapManager.getApiManager().addListener(this, new Predicate<ApiResult>() {
            @Override
            public boolean apply(ApiResult result) {
                return (result.getRequest().getUrl().getPath().split("/")[1].equals("login"));
            }
        });
    }

    public void initialize()
    {
        /**
         * Get the existing key.
         * */
        String key = getKey();

        /**
         * Check the existing key.
         * */
        if(key != null && !key.isEmpty())
        {
            requiresAuth = false;
            isAuth = true;
        }

        /**
         * Add a listener to the UI available message.
         * */
        JotiApp.MainTracker.subscribe(new Tracker.TrackerSubscriberCallback() {
            @Override
            public void onConditionMet(Tracker.TrackerMessage message) {
                if(requiresAuth)
                {
                    JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_AUTH_REQUIRED, "Auth", "A auth is required."));
                    requiresAuth = false;
                }
            }
        }, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return (s.equals(MainActivity.TRACKER_MAINACTIVITY_UI_AVAILABLE));
            }
        });
    }

    /**
     * Gets the value indicating if the user is auth.
     * */
    public boolean isAuth() {
        return isAuth;
    }

    /**
     * Sets the value indicating if the auth dialog is active.
     * */
    public void setAuthDialogActive(boolean authDialogActive) {
        this.authDialogActive = authDialogActive;
    }

    /**
     * Gets the value indicating if the auth dialog is active.
     * */
    public boolean isAuthDialogActive() {
        return authDialogActive;
    }

    /**
     * Require a auth.
     * */
    public void requireAuth()
    {
        requiresAuth = true;
        JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_AUTH_REQUIRED, "Auth", "A auth is required."));
    }

    /**
     * Authenticates the user by requesting a API key.
     * */
    public void auth(String username, String password)
    {

        /**
         * Get preferences and put the username in there.
         * */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pref_account_username", username);
        editor.apply();

        /**
         * Buffer to hold data that will be sent later on.
         * */
        String data = null;
        try
        {
            /**
             * Create JsonObject to hold the data.
             * */
            JsonObject object = new JsonObject();
            object.addProperty("gebruiker", username);
            object.addProperty("ww", AeSimpleSHA1.SHA1(password));

            /**
             * Convert object to Json.
             * */
            data = new Gson().toJson(object);
        } catch(Exception e) {}

        /**
         * Queue in and preform a ApiRequest, with the login data to the login page.
         * */
        LinkBuilder.setRoot(Area348.API_V2_ROOT);
        MapManager.getApiManager().queue(new ApiRequest(LinkBuilder.build(new String[]{"login"}), data));
        MapManager.getApiManager().preform();
    }

    @Override
    public void onApiTaskCompleted(ArrayList<ApiResult> results, String origin) {
        /**
         * Allocate buffer outside the loop.
         * */
        ApiResult currentResult;

        /**
         * Loop through each result.
         * */
        for(int i = 0; i < results.size(); i++)
        {
            currentResult = results.get(i);

            /**
             * Check the response code, if it is equal to 200 then extract the data.
             * */
            if(currentResult.getResponseCode() == 200)
            {
                /**
                 * Extract key from Json.
                 * */
                JsonParser parser = new JsonParser();
                JsonReader reader = new JsonReader(new StringReader(currentResult.getData()));
                reader.setLenient(true);
                JsonObject jsonObject = parser.parse(reader).getAsJsonObject();

                /**
                 * Get preferences and put the new key in there.
                 * */
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("pref_account_key", jsonObject.get("SLEUTEL").getAsString());
                editor.apply();

                /**
                 * Set control values.
                 * */
                isAuth = true;
                requiresAuth = false;

                /**
                 * Report that the Auth succeeded.
                 * */
                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_AUTH_SUCCEEDED, "Auth", "Authentication succeeded, new key available."));
            }
            else
            {
                /**
                 * Error occurred, report that the auth failed.
                 * */
                isAuth = false;
                requiresAuth = true;
                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_AUTH_FAILED_UNAUTHORIZED, "Auth", "Authentication failed."));
            }
        }
    }

    /**
     * Disposes the object.
     * */
    public void destroy()
    {
        /**
         * Remove the callback, so we don't leak the object.
         * */
        MapManager.getApiManager().removeListener(this);
    }

    /**
     * Defines a tracker identifier for authentication failed cause of unauthorized.
     * */
    public static final String TRACKER_AUTH_FAILED_UNAUTHORIZED = "TRACKER_AUTH_FAILED_UNAUTHORIZED";

    /**
     * Defines a tracker identifier for authentication succeeded.
     * */
    public static final String TRACKER_AUTH_SUCCEEDED = "TRACKER_AUTH_SUCCEEDED";

    /**
     * Defines a tracker identifier for requiring auth.
     * */
    public static final String TRACKER_AUTH_REQUIRED = "TRACKER_AUTH_REQUIRED";

}
