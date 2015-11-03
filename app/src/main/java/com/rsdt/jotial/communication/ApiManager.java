package com.rsdt.jotial.communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;

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
    private ArrayList<ApiRequest> queued = new ArrayList<>();

    /**
     * The array list that contains the pending requests.
     * */
    private ArrayList<ApiRequest> pending = new ArrayList<>();

    /**
     * The array list that contains the completed requests as ApiResults.
     * */
    private ArrayList<ApiResult> completed = new ArrayList<>();

    /**
     * Results that are not handled by a listener will end up here.
     * */
    private ArrayList<ApiResult> waitList = new ArrayList<>();

    /**
     * A list with listeners.
     * */
    private ArrayList<OnApiTaskCompleteCallback> onApiTaskCompleteListeners = new ArrayList<>();

    /**
     * Adds a listener to the listeners.
     * @param listener The listener to add.
     * */
    public void addListener(OnApiTaskCompleteCallback listener)
    {
        onApiTaskCompleteListeners.add(listener);
        Log.i("ApiManager", "ApiTask.addListener() - added listener " + listener.toString());

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

    /**
     * Remove listener from the listeners.
     * @param listener The listener to remove.
     * */
    public void removeListener(OnApiTaskCompleteCallback listener)
    {
        onApiTaskCompleteListeners.remove(listener);
        Log.i("ApiManager", "ApiTask.removeListener() - removed listener " + listener.toString());
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
        pending.addAll(queued);
        queued.clear();
        new ApiTask().execute(pending.toArray(new ApiRequest[pending.size()]));
        Log.i("ApiManager", "ApiTask.preform() - preforming  " + pending.size() + " ApiRequests");
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 19-10-2015
     * Class that preforms ApiRequests async.
     * TODO: Make response code check.
     */
    public class ApiTask extends AsyncTask<ApiRequest, Integer, ArrayList<ApiResult>> {

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
                            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
                            streamWriter.write(currentRequest.getData());
                            streamWriter.flush();
                            streamWriter.close();
                        }

                        /**
                         * Get the response stream and read it.
                         * */
                        InputStream response = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response));
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            builder.append(line);
                            builder.append("/r");
                        }
                        bufferedReader.close();
                        response.close();

                        /**
                         * Create and add a ApiResult with the new data.
                         * */
                        results.add(new ApiResult(currentRequest, builder.toString()));
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
             * TODO: Implement completed to file method? To maintain data.
             * */
            pending.clear();
            completed.clear();
            completed.addAll(results);

            ArrayList<OnApiTaskCompleteCallback> mustBeRemoved = new ArrayList<>();
            /**
             * If we have listeners invoke them, else put the results on hold.
             * */
            if(!onApiTaskCompleteListeners.isEmpty())
            {
                OnApiTaskCompleteCallback listener;
                for(int i = 0; i < onApiTaskCompleteListeners.size(); i++) {
                    listener = onApiTaskCompleteListeners.get(i);
                    /**
                     * Check if the listener is not null, if not signal the listener.
                     * */
                    if(listener != null)
                    {
                       listener.onApiTaskCompleted(results);
                        Log.i("ApiManager", "ApiTask.onPostExecute() - invoked listener " + listener.toString());
                    } else {
                        mustBeRemoved.add(listener);
                        Log.i("ApiManager", "ApiTask.onPostExecute() - removing listener, listener has a value of null");
                    }
                }
                /**
                 * Remove each  referent that is null,
                 * outside the loop. Else the collection would be altered in the loop.
                 * */
                for(int r = 0; r < mustBeRemoved.size(); r++)
                {
                    onApiTaskCompleteListeners.remove(mustBeRemoved.get(r));
                }
                 Log.i("ApiManager", "ApiTask.onPostExecute() - invoked " + onApiTaskCompleteListeners.size() + " listeners");
            }
            else {
                waitList.addAll(results);
                Log.i("ApiManager", "ApiTask.onPostExecute() - 0 listeners, putting results on hold");
            }

            Log.i("ApiManager", "ApiTask.onPostExecute() - completed " + results.size() + " tasks");
        }
    }

    /**
     * Trims the memory usage of the ApiManager.
     * */
    public void trim()
    {
        this.completed.clear();
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
        void onApiTaskCompleted(ArrayList<ApiResult> results);
    }

}
