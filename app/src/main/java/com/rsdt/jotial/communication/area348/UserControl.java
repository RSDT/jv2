package com.rsdt.jotial.communication.area348;

import android.content.SharedPreferences;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import com.android.internal.util.Predicate;

import com.google.gson.Gson;

import com.rsdt.jotial.JotiApp;

import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiRequest;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.LinkBuilder;

import com.rsdt.jotial.data.structures.area348.receivables.UserInfo;

import com.rsdt.jotial.io.AppData;
import com.rsdt.jotial.mapping.area348.MapManager;

import com.rsdt.jotiv2.Tracker;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-2-2016
 * Class for controlling the user's info.
 */
public class UserControl implements ApiManager.OnApiTaskCompleteCallback {

    /**
     * List of user info retrieved callbacks.
     * */
    private ArrayList<OnUserInfoRetrievedCallback> onUserInfoRetrievedCallbacks = new ArrayList<>();

    /**
     * List of avatar retrieved callbacks.
     * */
    private ArrayList<OnUserAvatarRetrievedCallback> onUserAvatarRetrievedCallbacks = new ArrayList<>();

    /**
     * Buffer to hold the UserInfo when there are no listeners.
     * */
    private UserInfo infoBuffer;

    /**
     * Buffer to hold the Drawable when there are no listeners.
     * */
    private Drawable drawableBuffer;

    public void addInfoListener(OnUserInfoRetrievedCallback callback)
    {
        onUserInfoRetrievedCallbacks.add(callback);

        if(infoBuffer != null)
        {
            callback.onUserInfoRetrieved(infoBuffer);
            infoBuffer = null;
        }
    }

    public void removeInfoListener(OnUserInfoRetrievedCallback callback)
    {
        onUserInfoRetrievedCallbacks.remove(callback);
    }

    public void addAvatarListener(OnUserAvatarRetrievedCallback callback)
    {
        onUserAvatarRetrievedCallbacks.add(callback);

        if(drawableBuffer != null)
        {
            callback.onUserAvatarRetrieved(drawableBuffer);
            drawableBuffer = null;
        }
    }

    public void removeAvatarListener(OnUserAvatarRetrievedCallback callback)
    {
        onUserAvatarRetrievedCallbacks.remove(callback);
    }

    /**
     * Initializes a new instance of UserControl.
     * */
    public UserControl()
    {
        MapManager.getApiManager().addListener(this, new Predicate<ApiResult>() {
            @Override
            public boolean apply(ApiResult result) {
                return (result.getRequest().getUrl().getPath().split("/")[1].equals("gebruiker"));
            }
        });
    }

    public void initialize()
    {
        /**
         * Read the user's avatar.
         * */
        new UserAvatarReadTask().execute(UserAvatarReadTask.USER_AVATAR);
    }

    @Override
    public void onApiTaskCompleted(ArrayList<ApiResult> results, String origin) {
        new UserInfoProcessingTask(origin).execute(results.toArray(new ApiResult[results.size()]));
    }

    /**
     * Retrieves the UserInfo, from the server.
     * */
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

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 8-2-2016
     * Class for processing the users info.
     */
    private class UserInfoProcessingTask extends AsyncTask<ApiResult, Integer, UserInfo>
    {

        private String origin;

        public UserInfoProcessingTask(String origin)
        {
            this.origin = origin;
        }

        @Override
        protected UserInfo doInBackground(ApiResult... params) {


            /**
             * Allocate buffer to hold the result UserInfo.
             * */
            UserInfo buffer;

            /**
             * Allocate result buffer outside loop.
             * */
            ApiResult finalResult = null;

            /**
             * Check if the length of the params is more than one.
             * If so we need to pick the latest one.
             * */
            if(params.length > 1)
            {
                /**
                 * Set the current result to the first one of the array.
                 * */
                finalResult = params[1];

                /**
                 * Loop through each one except the first one.
                 * */
                for(int x = 1; x < params.length; x++)
                {
                    /**
                     * Check the current x result is executed later than the first one, if so
                     * set the current result to the x result.
                     * */
                    if(params[x].getRequest().getExecutionDate().after(finalResult.getRequest().getExecutionDate()) && params[x].getResponseCode() == 200)
                    {
                        finalResult = params[x];
                    }
                }
            }
            else
            {
                if(params[0].getResponseCode() == 200)
                {
                    /**
                     * Set the current result to the first one of the array.
                     * */
                    finalResult = params[0];
                }
            }

            if(finalResult != null)
            {
                /**
                 * Check if the data is produced via the preform method,
                 * if so save the result.
                 * */
                if(origin.equals(ApiManager.ORIGIN_PREFORM))
                {
                    /**
                     * Save the result in the background.
                     * */
                    AppData.saveObjectAsJsonInBackground(finalResult, MapManager.MapStorageUtil.getUser());
                }

                /**
                 * Deserialize the UserInfo.
                 * */
                buffer = new Gson().fromJson(finalResult.getData(), UserInfo.class);

                /**
                 * Get the Preferences editor.
                 * */
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).edit();

                /**
                 * Put the rank into the Preferences.
                 * */
                editor.putString("pref_account_rank", buffer.rank());

                /**
                 * Apply the changes.
                 * */
                editor.apply();

                return buffer;
            }
            return null;
        }


        @Override
        protected void onPostExecute(UserInfo info) {
            super.onPostExecute(info);

            if(info != null)
            {
                /**
                 * Check if there are listeners, if not buffer the result.
                 * */
                if(onUserInfoRetrievedCallbacks.size() > 0)
                {
                    /**
                     * Allocate callback as buffer.
                     * */
                    OnUserInfoRetrievedCallback callback;

                    /**
                     * Loop through each callback.
                     * */
                    for(int i = 0; i < onUserInfoRetrievedCallbacks.size(); i++)
                    {
                        /**
                         * Set the callback.
                         * */
                        callback = onUserInfoRetrievedCallbacks.get(i);

                        /**
                         * Check if it isn't null, if so invoke it.
                         * */
                        if(callback != null)
                        {
                            callback.onUserInfoRetrieved(info);
                        }
                    }
                }
                else
                {
                    infoBuffer = info;
                }

                /**
                 * Download the info's drawable avatar in the background.
                 * */
                new DownloadAvatarTask().execute(info);

                /**
                 * Report that the users info has been retrieved.
                 * */
                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_USERCONTROL_USERINFO_RETRIEVE_SUCCEEDED, "UserControl", "Successfully retrieved the user info"));
            }
            else
            {
                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_USERCONTROL_USERINFO_RETRIEVE_FAILED, "UserControl", "Failed to retrieve user info"));
            }
        }

    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 8-2-2016
     * Class for downloading the avatar of the user.
     */
    private class DownloadAvatarTask extends AsyncTask<UserInfo, Integer, Drawable>
    {

        @Override
        protected Drawable doInBackground(UserInfo... params) {

            /**
             * Allocate buffer, outside the loop.
             * */
            UserInfo info;

            /**
             * Loop through each info.
             * */
            for(int i = 0; i < params.length; i++)
            {
                info = params[i];

                try
                {
                    InputStream is = (InputStream)new URL(Area348.SITE_2016_ROOT + "/img/avatar/" + info.avatar).getContent();
                    return Drawable.createFromStream(is, info.avatar);
                }
                catch (Exception e)
                {
                    Log.e("UserInfo", e.toString(), e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);

            if(drawable != null)
            {
                /**
                 * Save the avatar.
                 * */
                AppData.saveDrawableInBackground(drawable, UserAvatarReadTask.USER_AVATAR);

                /**
                 * Check if there are listeners, if not buffer the result.
                 * */
                if(onUserAvatarRetrievedCallbacks.size() > 0)
                {
                    /**
                     * Allocate callback as buffer.
                     * */
                    OnUserAvatarRetrievedCallback callback;

                    /**
                     * Loop trough each callback.
                     * */
                    for(int i = 0; i < onUserAvatarRetrievedCallbacks.size(); i++) {
                        /**
                         * Set the callback.
                         * */
                        callback = onUserAvatarRetrievedCallbacks.get(i);

                        /**
                         * Check if the callback isn't null, if so invoke it.
                         * */
                        if(callback != null)
                        {
                            callback.onUserAvatarRetrieved(drawable);
                        }
                    }
                }
                else
                {
                    drawableBuffer = drawable;
                }

                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_USERCONTROL_AVATAR_RETRIEVE_SUCCEEDED, "DownloadAvatarTask", "The avatar has successfully been retrieved."));
            }
            else
            {
                JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_USERCONTROL_AVATAR_RETRIEVE_FAILED, "DownloadAvatarTask", "Failed to retrieve avatar."));
            }

        }
    }

    private class UserAvatarReadTask extends AsyncTask<String, Integer, Drawable>
    {
        @Override
        protected Drawable doInBackground(String... params) {
            for(int i = 0; i < params.length; i++)
            {
                if(AppData.hasSave(params[i]))
                {
                    return AppData.getDrawable(params[i]);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);

            /**
             * Check if there are listeners, if not buffer the result.
             * */
            if(onUserAvatarRetrievedCallbacks.size() > 0)
            {
                /**
                 * Allocate callback as buffer.
                 * */
                OnUserAvatarRetrievedCallback callback;

                /**
                 * Loop trough each callback.
                 * */
                for(int i = 0; i < onUserAvatarRetrievedCallbacks.size(); i++)
                {
                    /**
                     * Set the callback.
                     * */
                    callback = onUserAvatarRetrievedCallbacks.get(i);

                    /**
                     * Check if the callback isn't null, if so invoke it.
                     * */
                    if(callback != null)
                    {
                        callback.onUserAvatarRetrieved(drawable);
                    }
                }
            }
            else
            {
                drawableBuffer = drawable;
            }

        }

        public static final String USER_AVATAR = "USER_AVATAR";
    }

    /**
     * Defines a callback for when a UserInfo is retrieved.
     * */
    public interface OnUserInfoRetrievedCallback
    {
        void onUserInfoRetrieved(UserInfo info);
    }

    /**
     * Defines a callback for when a user's avatar is retrieved.
     * */
    public interface OnUserAvatarRetrievedCallback
    {
        void onUserAvatarRetrieved(Drawable avatar);
    }

    public static final String TRACKER_USERCONTROL_AVATAR_RETRIEVE_SUCCEEDED = "TRACKER_USERCONTROL_AVATAR_RETRIEVE_SUCCEEDED";

    public static final String TRACKER_USERCONTROL_AVATAR_RETRIEVE_FAILED = "TRACKER_USERCONTROL_AVATAR_RETRIEVE_FAILED";

    public static final String TRACKER_USERCONTROL_USERINFO_RETRIEVE_SUCCEEDED = "TRACKER_USERCONTROL_USERINFO_RETRIEVE_SUCCEEDED";

    public static final String TRACKER_USERCONTROL_USERINFO_RETRIEVE_FAILED = "TRACKER_USERCONTROL_USERINFO_RETRIEVE_FAILED";

}
