package com.rsdt.jotial.mapping.area348;

import android.os.AsyncTask;
import android.util.Log;

import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.area348.Area348API;

import com.rsdt.jotial.mapping.area348.behaviour.FotoOpdrachtMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.ScoutingGroepMapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.VosMapBehaviour;
import com.rsdt.jotial.mapping.area348.data.MapData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 26-10-2015
 * Receives data and turns it into MapData.
 */
public class DataManager implements ApiManager.OnApiTaskCompleteCallback {


    /**
     * The listeners to the on data task completed event.
     * */
    protected ArrayList<OnDataTaskCompletedCallback> onDataTaskCompletedListeners = new ArrayList<>();

    /**
     * The tasks that are executed or are executing still.
     * */
    protected ArrayList<DataTask> tasks = new ArrayList<>();

    /**
     * Add a listener.
     * */
    public void addListener(OnDataTaskCompletedCallback listener)
    {
        onDataTaskCompletedListeners.add(listener);
    }

    /**
     * Remove a listener.
     * */
    public void removeListener(OnDataTaskCompletedCallback listener)
    {
        onDataTaskCompletedListeners.remove(listener);
    }

    @Override
    /**
     * Method that will be invoked when ApiRequest queued and preformed at the same time give a result.
     * */
    public void onApiTaskCompleted(ArrayList<ApiResult> results) {
        DataTask dataTask = new DataTask();
        dataTask.execute(results.toArray(new ApiResult[results.size()]));
        tasks.add(dataTask);
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 26-10-2015
     * AsyncTask that will turn the ApiResult's data into MapData.
     */
    protected class DataTask extends AsyncTask<ApiResult, Integer, HashMap<String, MapData>>
    {
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
             * Loop through each ApiResult.
             * */
            for(int i = 0; i < params.length; i++)
            {
                currentResult = params[i];

                /**
                 * Check if the ApiResult is for us, by checking the host of the URL.
                 * NOTE: Does not work?
                 * */
                if(currentResult.getRequest().getUrl().getHost().matches(Area348API.root))
                {

                }


                /**
                 * Split the url so we can determine the type of the request.
                 * TODO: determine type in ApiResult?
                 * */
                String[] args = currentResult.getRequest().getUrl().getPath().split("/");


                /**
                 * Switch on the first arg.
                 * */
                switch(args[1])
                {
                    case "vos":
                        dataHashMap.put("vos", VosMapBehaviour.toMapData(currentResult.getData()));
                        break;
                    case "hunter":

                        break;
                    case "sc":
                        dataHashMap.put("sc", ScoutingGroepMapBehaviour.toMapData(currentResult.getData()));
                        break;
                    case "foto":
                        dataHashMap.put("foto", FotoOpdrachtMapBehaviour.toMapData(currentResult.getData()));
                        break;

                }
            }

            return dataHashMap;
        }

        @Override
        protected void onPostExecute(HashMap<String,MapData> hashMap) {
            /**
             * Buffer to hold listeners that must be removed.
             * */
            ArrayList<OnDataTaskCompletedCallback> mustBeRemoved = new ArrayList<>();

            OnDataTaskCompletedCallback callback;
            if(!onDataTaskCompletedListeners.isEmpty())
            {
                for(int i = 0; i < onDataTaskCompletedListeners.size(); i++)
                {
                    callback = onDataTaskCompletedListeners.get(i);
                    if(callback != null)
                    {
                        callback.onDataTaskCompleted(hashMap);
                        Log.i("DataManager", "DataTask.onPostExecute() - invoked listener " + callback.toString());
                    }
                    else
                    {
                        mustBeRemoved.add(callback);
                        Log.i("DataManager", "DataTask.onPostExecute() - removing listener, the listener has a value of null");
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
                Log.i("ApiManager", "DataTask.onPostExecute() - invoked " + onDataTaskCompletedListeners.size() + " listeners");
            }
            Log.i("DataManager", "DataTask.onPostExecute() - completed " + hashMap.size() + "data tasks");
        }
    }

    /**
     * Trims the memory of the DataManager.
     * */
    public void trim()
    {

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
     *
     * */
    public interface OnCertainKeywordApiResult
    {
        /**
         *
         * */
        String getKeyword();

        /**
         *
         * */
        void onConditionMet(ApiResult result);

    }

}
