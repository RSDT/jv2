package com.rsdt.jotial.communication;

import android.util.Log;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-11-2015
 * Class that defines the static behaviour for the ApiManager.
 */
public class StaticApiManger extends ApiManager {

    /**
     * Results that are not handled by a listener will end up here.
     * */
    protected ArrayList<ApiResult> waitList = new ArrayList<>();

    /**
     * Initializes a new instance of StaticApiManager.
     * */
    public StaticApiManger()
    {

    }

    /**
     * Initializes a new instance of StaticApiManager.
     *
     * @param apiTaskCompleteCallback The callback to register.
     * */
    public StaticApiManger(OnApiTaskCompleteCallback apiTaskCompleteCallback)
    {
        super(apiTaskCompleteCallback);
    }

    @Override
    public void addListener(OnApiTaskCompleteCallback listener) {
        super.addListener(listener);

        /**
         * Check if the wait list is empty, if not handle the results that were put on wait.
         * */
        if(!waitList.isEmpty())
        {
            listener.onApiTaskCompleted(waitList);
            waitList.clear();
            Log.i("ApiManager", "ApiTask.addListener() - invoked the new listener with the queued data");
        }
    }

    @Override
    public void preform() {
        pending.addAll(queued);
        queued.clear();
        StaticApiTask task = new StaticApiTask();
        task.execute(pending.toArray(new ApiRequest[pending.size()]));
        tasks.add(task);
        Log.i("ApiManager", "ApiTask.onConditionMet() - preforming  " + pending.size() + " ApiRequests");
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 20-11-2015
     * Class that defines the static behaviour for the ApiTask.
     * */
    private class StaticApiTask extends ApiTask
    {
        @Override
        protected void onPostExecute(ArrayList<ApiResult> results) {
            super.onPostExecute(results);

            /**
             * Checks if there are listeners, if not put the results on the wait list to be posted later on.
             * */
            if(onApiTaskCompleteListeners.isEmpty()) {
                waitList.addAll(results);
                Log.i("ApiManager", "ApiTask.onPostExecute() - 0 listeners, putting results on the wait list");
            }
        }
    }
}
