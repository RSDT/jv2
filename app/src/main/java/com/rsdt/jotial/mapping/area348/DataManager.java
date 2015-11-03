package com.rsdt.jotial.mapping.area348;

import android.os.AsyncTask;
import android.util.Log;

import com.android.internal.util.Predicate;
import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.area348.Area348API;

import com.rsdt.jotial.mapping.area348.data.MapPartState;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 26-10-2015
 * Description...
 */
public class DataManager implements ApiManager.OnApiTaskCompleteCallback {


    /**
     * The listeners to the on data task completed event.
     * */
    private ArrayList<OnDataTaskCompletedCallback> onDataTaskCompletedListeners = new ArrayList<>();

    /**
     * The wait list, that holds the MapPartStates if there are no listeners.
     * */
    private ArrayList<MapPartState> waitList = new ArrayList<>();

    /**
     * Add a listener.
     * */
    public void addListener(OnDataTaskCompletedCallback listener)
    {
        onDataTaskCompletedListeners.add(listener);

        /**
         * Check if the waitList contains items, if so invoke the new listener and clear the waitList.
         * */
        if(!waitList.isEmpty())
        {
            listener.onDataTaskCompleted(waitList);
            waitList.clear();
        }
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
        new DataTask().execute(results.toArray(new ApiResult[results.size()]));
    }


    private class DataTask extends AsyncTask<ApiResult, Integer, ArrayList<MapPartState>>
    {


        @Override
        protected ArrayList<MapPartState> doInBackground(ApiResult... params) {

            /**
             * Create array list for containing the results states from the task.
             * */
            ArrayList<MapPartState> states = new ArrayList<>();

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
                states.add(MapPartState.from(currentResult));
            }

            return states;
        }





        @Override
        protected void onPostExecute(ArrayList<MapPartState> mapPartStates) {

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
                        callback.onDataTaskCompleted(mapPartStates);
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
            else
            {
                waitList.addAll(mapPartStates);
                Log.i("DataManager", "DataTask.onPostExecute() - 0 listeners, putting results on hold");
            }
            Log.i("DataManager", "DataTask.onPostExecute() - completed " + mapPartStates.size() + "data tasks");
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
        void onDataTaskCompleted(ArrayList<MapPartState> mapPartStates);
    }

    /**
     * Defines a callback for on handling ApiResults but the callback only will be invoked when the condition is met.
     * TODO: This interface is not in use, and is not implemented in DataManger.
     * */
    public interface OnConditionalApiResultHandlingCallback
    {

        Predicate getCondition();

        void OnApiResultConditionMet(ApiResult result);

    }



}
