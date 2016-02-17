package com.rsdt.jotial.communication;

import android.os.AsyncTask;
import android.util.Log;

import com.android.internal.util.Predicate;
import com.rsdt.jotial.JotiApp;
import com.rsdt.jotiv2.Tracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 19-10-2015
 * Class that represents as a manager for the API.
 */
public class ApiManager {

    /**
     * The array list that contains the queued requests.
     * */
    protected ArrayList<ApiRequest> queued = new ArrayList<>();

    /**
     * The array list that contains the pending requests.
     * */
    protected ArrayList<ApiRequest> pending = new ArrayList<>();

    /**
     * The array list that contains the completed requests as ApiResults.
     * */
    protected ArrayList<ApiResult> completed = new ArrayList<>();

    /**
     * The array list with ongoing tasks.
     * */
    protected ArrayList<ApiTask> tasks = new ArrayList<>();

    /**
     * The list of listeners and there associated encapsulations.
     * */
    protected HashMap<OnApiTaskCompleteCallback, ListenerEncapsulation> listeners = new HashMap<>();

    /**
     * Initializes a new instance of ApiManager.
     * */
    public ApiManager()
    {

    }

    /**
     * Adds a listener to the listeners.
     *
     * @param listener The listener to add.
     * @param filter  The filter that should be applied, null for none.
     * */
    public void addListener(OnApiTaskCompleteCallback listener, Predicate<ApiResult> filter)
    {
        listeners.put(listener, new ListenerEncapsulation(filter));
        Log.i("ApiManager", "addListener() - added listener " + listener.toString());
    }

    /**
     * Remove listener from the listeners.
     * @param listener The listener to remove.
     * */
    public void removeListener(OnApiTaskCompleteCallback listener)
    {
        listeners.remove(listener);
        Log.i("ApiManager", "removeListener() - removed listener " + listener.toString());
    }

    /**
     * Queues the given ApiRequest to be preformed later on.
     * @param request The ApiRequest that should be queued.
     * */
    public void queue(ApiRequest request)
    {
        this.queued.add(request);
    }

    /**
     * Preforms each ApiRequest that is queued.
     * */
    public void preform()
    {
        if(!queued.isEmpty()) {
            /**
             * Create, store and exectue the StaticApiTask.
             * */
            ApiTask task = new ApiTask();
            task.execute(queued.toArray(new ApiRequest[queued.size()]));
            tasks.add(task);

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
            new ResultProcessingTask(ORIGIN_PROCESS).execute(results.toArray(new ApiResult[results.size()]));
            Log.i("ApiManager", "ApiTask.onPostExecute() - started running ResultProcessingTask on results");
            Log.i("ApiManager", "ApiTask.onPostExecute() - completed " + results.size() + " tasks");
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 19-10-2015
     * Class that preforms ApiRequests async.
     */
    protected class ApiTask extends AsyncTask<ApiRequest, Integer, ArrayList<ApiResult>> {


        @Override
        protected ArrayList<ApiResult> doInBackground(ApiRequest... params) {
            ArrayList<ApiResult> results = new ArrayList<>();
            for(int i = 0; i < params.length; i++)
            {
                    try
                    {
                        ApiRequest currentRequest = params[i];

                        /**
                         * Open a connection to the URL.
                         * */
                        HttpURLConnection connection = (HttpURLConnection)currentRequest.getUrl().openConnection();

                        /**
                         * Checks if the request contains data, if so write it to the output stream.
                         * */
                        if(currentRequest.getData() != null)
                        {
                            connection.setDoOutput(true);
                            connection.setRequestMethod("POST");

                            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
                            streamWriter.write(currentRequest.getData());
                            streamWriter.flush();
                            streamWriter.close();
                        }

                        if(connection.getResponseCode() == 200)
                        {
                            /**
                             * Get the response stream and read it.
                             * */
                            InputStream response = connection.getInputStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response));
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                builder.append(line);
                            }
                            bufferedReader.close();

                            /**
                             * Create and add a ApiResult with the new data.
                             * */
                            results.add(new ApiResult(currentRequest, builder.toString(), connection.getResponseCode()));
                        }
                        else
                        {
                            InputStream errorResponse = connection.getErrorStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorResponse));
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                builder.append(line);
                            }
                            bufferedReader.close();
                            results.add(new ApiResult(currentRequest, builder.toString(), connection.getResponseCode()));
                        }

                        /**
                         * Set the execution data to now.
                         * */
                        currentRequest.setExecutionDate(new Date());

                        /**
                         * Disconnect.
                         * */
                        connection.disconnect();

                        /**
                         * Check if we use a delay, if so delay by the set amount.
                         * */
                        if(APITASK_USE_DELAY)
                        {
                            Thread.sleep(APITASK_DELAY);
                        }

                    }
                    catch(Exception e)
                    {
                        Log.e("ApiManager", e.getMessage(), e);
                    }

            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<ApiResult> results) {
            super.onPostExecute(results);

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

            new ResultProcessingTask(ORIGIN_PREFORM).execute(results.toArray(new ApiResult[results.size()]));
            Log.i("ApiManager", "ApiTask.onPostExecute() - started running ResultProcessingTask on results");
            Log.i("ApiManager", "ApiTask.onPostExecute() - completed " + results.size() + " tasks");
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 19-10-2015
     * Class that processes the ApiResults internally.
     */
    protected class ResultProcessingTask extends AsyncTask<ApiResult, Integer, ArrayList<ApiResult>> {

        /**
         *
         * */
        public ResultProcessingTask(String origin) {
            this.origin = origin;
        }

        /**
         * Value that indicates the origin of the invoke.
         * */
        private String origin;

        @Override
        protected ArrayList<ApiResult> doInBackground(ApiResult... params) {

            /**
             * The ApiResults that are unhandled will be stored here, for a short amount of time.
             * */
            ArrayList<ApiResult> unHandled = new ArrayList<>();

            /**
             * If we have listeners invoke them, else put the results on hold.
             * */
            if(!listeners.isEmpty()) {

                /**
                 * Allocate buffer to hold the current ApiResult while looping.
                 * */
                ApiResult currentResult;

                /**
                 * Loop through each result.
                 * */
                for (int i = 0; i < params.length; i++) {
                    currentResult = params[i];

                    /**
                     * Value indicating if the current result is handled.
                     * */
                    boolean handled = false;

                    /**
                     * Loop through each listener.
                     * */
                    for (Map.Entry<OnApiTaskCompleteCallback, ListenerEncapsulation> entry : listeners.entrySet()) {
                        /**
                         * Check if the listener is not null.
                         * */
                        if (entry.getKey() != null) {

                            /**
                             * Check if the condition is not null.
                             * */
                            if(entry.getValue().condition != null)
                            {
                                /**
                                 * Check if the ApiResult applies to this listener with the listener's filter.
                                 * */
                                if (entry.getValue().condition.apply(currentResult)) {
                                    /**
                                     * If so add the ApiResult.
                                     * */
                                    entry.getValue().buffer.add(currentResult);
                                    entry.getValue().shouldInvoke = true;
                                    handled = true;
                                }
                                else
                                {

                                }
                            }
                            else
                            {
                                /**
                                 * If so add the ApiResult.
                                 * */
                                entry.getValue().buffer.add(currentResult);
                                entry.getValue().shouldInvoke = true;
                                handled = true;
                            }

                        }
                    }
                    /**
                     * If the result is not handled add the result to the unhandled list.
                     * */
                    if (!handled) unHandled.add(currentResult);
                }
            }
            return unHandled;
        }

        @Override
        protected void onPostExecute(ArrayList<ApiResult> apiResults) {
            super.onPostExecute(apiResults);
            for(Map.Entry<OnApiTaskCompleteCallback, ListenerEncapsulation> entry : listeners.entrySet())
            {
                if(entry.getKey() != null && entry.getValue().shouldInvoke)
                {
                    entry.getValue().invokeListener(entry.getKey(), origin);
                    Log.i("ApiManager", "ResultProcessingTask.onPostExecute() - invoked listener " + entry.getKey().toString());
                }
            }
        }
    }

    /**
     * Trims the memory usage of the ApiManager.
     * */
    public void trim()
    {
        this.completed.clear();
        Log.i("ApiManager", "trim() - clearing the completed list");
    }

    /**
     * Class for encapsulating a listener.
     * */
    public class ListenerEncapsulation
    {
        /**
         * Initialize a new instance of ListenerEncapsulation.
         * */
        public ListenerEncapsulation(Predicate<ApiResult> condition)
        {
            this.condition = condition;
        }

        /**
         * The condition is the ApiResult must apply to, if it needs to be added to the buffer.
         * */
        private Predicate<ApiResult> condition;

        /**
         * Buffer to hold the ApiResults.
         * */
        private ArrayList<ApiResult> buffer = new ArrayList<>();

        /**
         * Value indicating if the listener should be invoked.
         * */
        private boolean shouldInvoke = false;

        /**
         * Method to invoke the listener within the ListenerEncapsulation.
         * */
        public void invokeListener(OnApiTaskCompleteCallback listener, String origin)
        {
            if(!buffer.isEmpty())
            {
                /**
                 * Invoke the listener.
                 * */
                listener.onApiTaskCompleted(buffer, origin);

                /**
                 * The data has been posted, so clear the data, it no longer needed.
                 * */
                buffer.clear();

                /**
                 * Listener should no longer be invoked.
                 * */
                shouldInvoke = false;
            }
        }

    }

    /**
     * Interface enables users to execute code on the completion of ApiTask.
     * */
    public interface OnApiTaskCompleteCallback
    {
        /**
         * Gets invoked when new a api task finished.
         * @param results The results of the api task.
         * */
        void onApiTaskCompleted(ArrayList<ApiResult> results, String origin);
    }


    /**
     * The delay for the ApiTask, a delay is needed because if we execute the requests rapidly after each other,
     * the API will get overloaded. So we use a delay. A delay of 1000ms is recommend, lower than this will
     * possibly result in API overload.
     * */
    public static int APITASK_DELAY = 1000;

    /**
     * Value indicating if a delay should be used for the ApiTask, default true.
     * */
    public static final boolean APITASK_USE_DELAY = false;

    /**
     * Defines a Tracker identifier for the completion of the fetching.
     * */
    public static final String TRACKER_APIMANAGER_FETCHING_COMPLETED = "TRACKER_APIMANAGER_FETCHING_COMPLETED";

    /**
     * Defines the origin from the process() method.
     * */
    public static final String ORIGIN_PROCESS = "ORIGIN_PROCESS";

    /**
     * Defines the origin from the perform() method.
     * */
    public static final String ORIGIN_PREFORM = "ORIGIN_PREFORM";

}
