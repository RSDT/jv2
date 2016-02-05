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
 * Defines the static behaviour for the DataProcessingManager class.
 */
public class StaticDataProcessingManager extends DataProcessingManager {

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
            Log.i("DataProcessingManager", "addListener() - invoked the new listener with the queued data");
        }
    }

    @Override
    public void onApiTaskCompleted(ArrayList<ApiResult> results) {
        StaticDataProcessingTask dataTask = new StaticDataProcessingTask();
        dataTask.execute(results.toArray(new ApiResult[results.size()]));
        tasks.add(dataTask);
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 21-11-2015
     * Defines the static behaviour for the DataProcessingTask.
     * */
    protected class StaticDataProcessingTask extends DataProcessingTask
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
                Log.i("DataProcessingManager", "DataProcessingTask.onPostExecute() - 0 listeners, putting results on the waitList");
            }
        }
    }

    @Override
    public void trim() {
        super.trim();
        waitList.clear();
    }
}
