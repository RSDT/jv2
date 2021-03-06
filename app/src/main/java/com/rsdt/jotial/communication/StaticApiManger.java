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
         * Check if there's a running ResultProcessingTask.
         * */
        if(processingTasks.size() > 0)
        {
            JotiApp.MainTracker.subscribe(new Tracker.TrackerSubscriberCallback() {
                @Override
                public void onConditionMet(Tracker.TrackerMessage message) {
                    /**
                     * Check if the wait list is empty, if not handle the results that were put on wait.
                     * */
                    if(!waitList.isEmpty())
                    {
                        new StaticResultProcessingTask(ORIGIN_PREFORM).execute(waitList.toArray(new ApiResult[waitList.size()]));
                        waitList.clear();
                    }

                    /**
                     * Unsub from the event.
                     * */
                    JotiApp.MainTracker.postponeUnsubscribe(this);
                }
            }, new Predicate<String>() {
                @Override
                public boolean apply(String s) {
                    return (s.equals(ResultProcessingTask.TRACKER_RESULT_PROCESSING_COMPLETED));
                }
            });
        }
        else
        {

            /**
             * Check if the wait list is empty, if not handle the results that were put on wait.
             * */
            if(!waitList.isEmpty())
            {
                new StaticResultProcessingTask(ORIGIN_PREFORM).execute(waitList.toArray(new ApiResult[waitList.size()]));
                waitList.clear();
            }
        }

    }

    @Override
    public void preform() {
        if(!queued.isEmpty()) {
            /**
             * Create, store and exectue the StaticApiTask.
             * */
            StaticApiTask task = new StaticApiTask();
            task.execute(queued.toArray(new ApiRequest[queued.size()]));
            apiTasks.add(task);

            /**
             * Add all the queued to the pending list.
             * */
            pending.addAll(queued);

            /**
             * Clear the queued list, since now they are pending.
             * */
            queued.clear();

            /**
             * Log the situation.
             * */
            Log.i("ApiManager", "preform() - preforming  " + pending.size() + " ApiRequests");
        }
    }

    /**
     * Processes the results, and invokes the appropriate listeners.
     * */
    public void process(ArrayList<ApiResult> results)
    {
        if(results.size() > 0)
        {
            new StaticResultProcessingTask(ORIGIN_PROCESS).execute(results.toArray(new ApiResult[results.size()]));
            Log.i("ApiManager", "ApiTask.onPostExecute() - started running StaticResultProcessingTask on results");
            Log.i("ApiManager", "ApiTask.onPostExecute() - completed " + results.size() + " apiTasks");
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
             * */
            pending.clear();
            completed.clear();
            completed.addAll(results);

            /**
             * Inform the tracker that fetching is completed.
             * */
            JotiApp.MainTracker.report(new Tracker.TrackerMessage(TRACKER_APIMANAGER_FETCHING_COMPLETED, "ApiManager", "Fetching completed."));

            /**
             * Start result processing on the results.
             * */
            new StaticResultProcessingTask(ORIGIN_PREFORM).execute(results.toArray(new ApiResult[results.size()]));

            /**
             * Log what has been done, and what we are doing.
             * */
            Log.i("ApiManager", "ApiTask.onPostExecute() - started running StaticResultProcessingTask on results");
            Log.i("ApiManager", "ApiTask.onPostExecute() - completed " + results.size() + " apiTasks");
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

        public StaticResultProcessingTask(String origin)
        {
            super(origin);
        }

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

            JotiApp.MainTracker.report(new Tracker.TrackerMessage(ResultProcessingTask.TRACKER_RESULT_PROCESSING_COMPLETED, "ResultProcessingTask", "The processing has been completed"));
        }
    }

}
