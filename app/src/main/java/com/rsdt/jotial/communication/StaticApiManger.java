package com.rsdt.jotial.communication;

import android.util.Log;

import com.android.internal.util.Predicate;
import com.rsdt.jotial.JotiApp;
import com.rsdt.jotiv2.Tracker;

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

    @Override
    public void addListener(OnApiTaskCompleteCallback listener, Predicate<ApiResult> filter) {
        super.addListener(listener, filter);

        /**
         * Check if the wait list is empty, if not handle the results that were put on wait.
         * */
        if(!waitList.isEmpty())
        {
            new StaticResultProcessingTask().execute(waitList.toArray(new ApiResult[waitList.size()]));
            waitList.clear();
        }
    }

    @Override
    public void preform() {
        if(!queued.isEmpty()) {
            pending.addAll(queued);
            queued.clear();

            StaticApiTask task = new StaticApiTask();
            task.execute(pending.toArray(new ApiRequest[pending.size()]));
            tasks.add(task);
            Log.i("ApiManager", "preform() - preforming  " + pending.size() + " ApiRequests");
        }
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
            /**
             * Clear the pending and completed list, the completed list only contains one ApiTask's results.
             * TODO: Implement completed to file method? To maintain data.
             * */
            pending.clear();
            completed.clear();
            completed.addAll(results);

            /**
             * Inform the tracker that fetching is completed.
             * */
            JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_APIMANAGER_FETCHING_COMPLETED, "ApiManager", "Fetching completed."));

            new StaticResultProcessingTask().execute(results.toArray(new ApiResult[results.size()]));
            Log.i("ApiManager", "ApiTask.onPostExecute() - started running StaticResultProcessingTask on results");
            Log.i("ApiManager", "ApiTask.onPostExecute() - completed " + results.size() + " tasks");
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 20-11-2015
     * Class that defines the static behaviour for the ResultProcessingTask.
     * */
    private class StaticResultProcessingTask extends ResultProcessingTask
    {
        @Override
        protected void onPostExecute(ArrayList<ApiResult> apiResults) {
            super.onPostExecute(apiResults);

            /**
             * If there are unhandled results add them to the waiting list.
             * */
            if(!apiResults.isEmpty())
            {
                waitList.addAll(apiResults);
            }
        }
    }

}
