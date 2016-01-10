package com.rsdt.jotial.mapping.area348;

import android.util.Log;

import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.mapping.area348.data.MapData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 21-11-2015
 * Defines the static behaviour for the DataManager class.
 */
public class StaticDataManager extends DataManager {

    /**
     * The wait list, that holds the MapPartStates if there are no listeners.
     * */
    protected HashMap<String, MapData> waitList = new HashMap<>();

    @Override
    public void addListener(OnDataTaskCompletedCallback listener) {
        super.addListener(listener);

        /**
         * Check if the waitList contains items, if so invoke the new listener and clear the waitList.
         * */
        if(!waitList.isEmpty())
        {
            listener.onDataTaskCompleted(waitList);
            waitList.clear();
            Log.i("DataManager", "addListener() - invoked the new listener with the queued data");
        }
    }

    @Override
    public void onApiTaskCompleted(ArrayList<ApiResult> results) {
        StaticDataTask dataTask = new StaticDataTask();
        dataTask.execute(results.toArray(new ApiResult[results.size()]));
        tasks.add(dataTask);
        Log.i("DataManager", " onApiTaskCompleted() - executing " + results.size() + " data tasks");
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 21-11-2015
     * Defines the static behaviour for the DataTask.
     * */
    protected class StaticDataTask extends DataTask
    {

        @Override
        protected void onPostExecute(HashMap<String, MapData> hashMap) {
            super.onPostExecute(hashMap);

            /**
             * Checks if there are any listeners, if not add the results to the waitList, to be posted later on.
             * */
            if(onDataTaskCompletedListeners.isEmpty())
            {
                waitList.putAll(hashMap);
                Log.i("DataManager", "DataTask.onPostExecute() - 0 listeners, putting results on the waitList");
            }
        }
    }










}
