package com.rsdt.jotial;

import android.app.IntentService;
import android.content.Intent;

import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.communication.ApiRequest;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.communication.LinkBuilder;
import com.rsdt.jotial.communication.area348.Area348API;
import com.rsdt.jotial.mapping.area348.behaviour.MapBehaviour;
import com.rsdt.jotial.mapping.area348.behaviour.VosMapBehaviour;
import com.rsdt.jotial.mapping.area348.data.MapData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 18-11-2015
 * Description...
 */
public class UpdateService extends IntentService implements ApiManager.OnApiTaskCompleteCallback {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateService() {
        super("UpdateService");
    }

    /**
     * The ApiManger for requesting data.
     * */
    ApiManager apiManager = new ApiManager();

    @Override
    protected void onHandleIntent(Intent intent) {
        apiManager.addListener(this);
        switch (intent.getAction())
        {
            case UPDATE_SERVICE_ACTIONS_REFRESH:
                LinkBuilder.setRoot(Area348API.root);
                apiManager.queue(new ApiRequest(LinkBuilder.build(new String[]{"vos", "a", "all"})));
                apiManager.preform();
                break;
        }
    }

    @Override
    public void onApiTaskCompleted(ArrayList<ApiResult> results) {

        /**
         * Allocate a ApiResult as a buffer once.
         * */
        ApiResult currentResult;

        /**
         * Holds the pairs of String and MapData, for example [vos, MapData]
         * */
        HashMap<String, MapData> dataHashMap = new HashMap<>();

        /**
         * Loop through each ApiResult.
         * */
        for(int i = 0; i < results.size(); i++)
        {
            currentResult = results.get(i);

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
                case "sc":
                    dataHashMap.put("sc", null);
                    break;
            }


        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(UPDATE_SERVICE_ACTIONS_RENEW);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("hashMap", dataHashMap);
        sendBroadcast(broadcastIntent);
    }

    /**
     * Defines the refresh action.
     * */
    public static final String UPDATE_SERVICE_ACTIONS_REFRESH = "update_service_actions_refresh";

    public static final String UPDATE_SERVICE_ACTIONS_RENEW = "update_service_actions_renew";



}
