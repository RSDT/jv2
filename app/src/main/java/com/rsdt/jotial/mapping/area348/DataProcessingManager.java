package com.rsdt.jotial.mapping.area348;

import android.os.AsyncTask;
import android.util.Log;

import com.rsdt.jotial.JotiApp;
import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiResult;

import com.rsdt.jotial.io.AppData;
import com.rsdt.jotial.mapping.area348.behaviour.FotoOpdrachtMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.HunterMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.ScoutingGroepMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.VosMapBehaviour;
import com.rsdt.jotial.mapping.area348.data.MapData;
import com.rsdt.jotiv2.Tracker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 26-10-2015
 * Receives data and turns it into MapData.
 */
public class DataProcessingManager implements ApiManager.OnApiTaskCompleteCallback {


    /**
     * The listeners to the on data task completed event.
     * */
    protected ArrayList<OnDataTaskCompletedCallback> onDataTaskCompletedListeners = new ArrayList<>();

    /**
     * The tasks that are executed or are executing still.
     * */
    protected ArrayList<DataProcessingTask> tasks = new ArrayList<>();

    /**
     * Add a listener.
     * */
    public void addListener(OnDataTaskCompletedCallback listener)
    {
        onDataTaskCompletedListeners.add(listener);
        Log.i("DataProcessingManager", "addListener() - added listener " + listener.toString());
    }

    /**
     * Remove a listener.
     * */
    public void removeListener(OnDataTaskCompletedCallback listener)
    {
        onDataTaskCompletedListeners.remove(listener);
        Log.i("DataProcessingManager", "removeListener() - removed listener " + listener.toString());
    }

    @Override
    /**
     * Method that will be invoked when ApiRequest queued and preformed at the same time give a result.
     * */
    public void onApiTaskCompleted(ArrayList<ApiResult> results, String origin) {

        DataProcessingTask dataProcessingTask;
        if(origin.equals(ApiManager.ORIGIN_PROCESS))
        {
            dataProcessingTask = new DataProcessingTask(false);
        }
        else
        {
            dataProcessingTask = new DataProcessingTask(true);
        }
        dataProcessingTask.execute(results.toArray(new ApiResult[results.size()]));
        tasks.add(dataProcessingTask);
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 26-10-2015
     * AsyncTask that will turn the ApiResult's data into MapData.
     */
    protected class DataProcessingTask extends AsyncTask<ApiResult, Integer, HashMap<String, MapData>>
    {

        /**
         * Initializes a new instance of DataProcessingTask.
         * */
        public DataProcessingTask(){ }

        /**
         * Initializes a new instance of DataProcessingTask.
         *
         * @param save Value indicating if the incoming data should be saved.
         * */
        public DataProcessingTask(boolean save)
        {
            this.save = save;
        }

        /**
         * Value indicating if the incoming data should be saved,
         * so that we can use this data on later reboot.
         * */
        protected boolean save = true;

        @Override
        protected HashMap<String, MapData> doInBackground(ApiResult... params) {

            /**
             * A buffer to hold the pairs of a String indicator and MapData.
             * */
            HashMap<String, MapData> dataHashMap = new HashMap<>();

            /**
             * Allocate a ApiResult as a buffer once.
             * */
            ApiResult currentResult;

            /**
             * Allocate buffer to hold the MapData outside loop.
             * */
            MapData mapData;

            /**
             * Loop through each ApiResult.
             * */
            for(int i = 0; i < params.length; i++)
            {
                currentResult = params[i];

                /**
                 * Split the url so we can determine the type of the request.
                 * */
                String[] args = currentResult.getRequest().getUrl().getPath().split("/");

                /**
                 * Check if we need to save the result.
                 * */
                if(save)
                {
                    /**
                     * Check if the result is vos data, if so use a different save way.
                     * */
                    if(args[1].equals("vos"))
                    {
                        /**
                         * Save the result as vos {TEAM}.
                         * */
                        AppData.saveObjectAsJson(currentResult, "RESULT_VOS_" + args[3].toUpperCase());
                    }
                    else
                    {
                        /**
                         * Save the result, with a name such as "hunter" or "foto".
                         * */
                        AppData.saveObjectAsJson(currentResult, "RESULT_" + args[1].toUpperCase());
                    }
                }

                /**
                 * Switch on the first arg.
                 * */
                switch(args[1])
                {
                    case "vos":
                        mapData = VosMapBehaviour.toMapData(currentResult.getData());
                        dataHashMap.put("vos " + args[3], mapData);
                        break;
                    case "hunter":
                        mapData = HunterMapBehaviour.toMapData(currentResult.getData());
                        dataHashMap.put("hunter", mapData);
                        break;
                    case "sc":
                        mapData = ScoutingGroepMapBehaviour.toMapData(currentResult.getData());
                        dataHashMap.put("sc", mapData);
                        break;
                    case "foto":
                        mapData = FotoOpdrachtMapBehaviour.toMapData(currentResult.getData());
                        dataHashMap.put("foto", mapData);
                        break;
                }
            }
            return dataHashMap;
        }

        @Override
        protected void onPostExecute(HashMap<String,MapData> hashMap) {

            /**
             * Inform the tracker that processing is completed.
             * */
            JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_DATAMANAGER_PROCESSING_COMPLETED, "DataProcessingManager", "Processing of data completed."));

            /**
             * Buffer to hold listeners that must be removed.
             * */
            ArrayList<OnDataTaskCompletedCallback> mustBeRemoved = new ArrayList<>();

            /**
             * Allocate callback buffer outside loop.
             * */
            OnDataTaskCompletedCallback callback;

            if(!onDataTaskCompletedListeners.isEmpty())
            {
                for(int i = 0; i < onDataTaskCompletedListeners.size(); i++)
                {
                    callback = onDataTaskCompletedListeners.get(i);
                    if(callback != null)
                    {
                        callback.onDataTaskCompleted(hashMap);
                        Log.i("DataProcessingManager", "DataProcessingTask.onPostExecute() - invoked listener " + callback.toString());
                    }
                    else
                    {
                        mustBeRemoved.add(callback);
                        Log.i("DataProcessingManager", "DataProcessingTask.onPostExecute() - removing listener, the listener has a value of null");
                    }
                }

                /**
                 * Remove each listener that is null,
                 * outside the loop. Else the collection would be altered in the loop.
                 * */
                for(int r = 0; r < mustBeRemoved.size(); r++)
                {
                    onDataTaskCompletedListeners.remove(mustBeRemoved.get(r));
                }

                /**
                 * Write to the log how many listeners we invoked.
                 * */
                Log.i("ApiManager", "DataProcessingTask.onPostExecute() - invoked " + onDataTaskCompletedListeners.size() + " listeners");
            }

            /**
             * Remove the task, out of the list of tasks.
             * */
            tasks.remove(this);
        }
    }

    /**
     * Trims the memory of the DataProcessingManager.
     * */
    public void trim()
    {
        tasks.clear();
    }

    /**
     * Defines a callback for on data task completion.
     * */
    public interface OnDataTaskCompletedCallback
    {
        /**
         * Method that gets invoked on completion.
         * */
        void onDataTaskCompleted(HashMap<String, MapData> hashMap);
    }


    /**
     * Defines a Tracker identifier for the completion of processing.
     * */
    public static final String TRACKER_DATAMANAGER_PROCESSING_COMPLETED = "TRACKER_DATAMANAGER_PROCESSING_COMPLETED";
}
