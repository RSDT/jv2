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
 * TODO: Create interface or class for listener?
 */
public class ApiManager {

    private ArrayList<ApiRequest> queued = new ArrayList<>();

    private ArrayList<ApiRequest> pending = new ArrayList<>();

    private ArrayList<ApiResult> completed = new ArrayList<>();

    private OnApiTaskComplete onApiTaskCompleteListener;

    public void addListener(OnApiTaskComplete onApiTaskCompleteListener)
    {
        this.onApiTaskCompleteListener = onApiTaskCompleteListener;
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
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 19-10-2015
     * Class that preforms ApiRequest async.
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
            pending.clear();
            completed.addAll(results);
            if(onApiTaskCompleteListener != null) onApiTaskCompleteListener.onApiTaskCompleted(results);
            Log.i("ApiManager", "ApiTask.onPostExecute() - completed " + results.size() + " tasks");
        }
    }

    /**
     * Interface enables users to execute code on the completion of ApiTask.
     * */
    public interface OnApiTaskComplete
    {
        void onApiTaskCompleted(ArrayList<ApiResult> results);
    }

}
