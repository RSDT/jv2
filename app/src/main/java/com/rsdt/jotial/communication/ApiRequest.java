package com.rsdt.jotial.communication;

import java.net.URL;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 19-10-2015
 * Class that represents a request to a API.
 */
public class ApiRequest {

    /**
     * Initializes a new instance of ApiRequest.
     * @param url The url of the request.
     * */
    public ApiRequest(URL url)
    {
        this.url = url;
        this.type = GET;
    }

    /**
     * Initializes a new instance of ApiRequest.
     * @param url The url of the request.
     * @param data The data that should be send.
     * */
    public ApiRequest(URL url, String data)
    {
        this.url = url;
        this.data = data;

        if(data != null && !data.isEmpty())
        {
            this.type = POST;
        }
        else
        {
            this.type = GET;
        }
    }

    /**
     * The url of the api request.
     * */
    private URL url;

    /**
     * The data that should be send(optional).
     * */
    private String data;

    /**
     * The type of the request, post or get.
     * */
    private String type;


    /**
     * Gets the url of the ApiRequest.
     * */
    public URL getUrl() {
        return url;
    }

    /**
     * Gets the data of the ApiRequest. Returns null if no data has been set.
     * */
    public String getData() {
        return data;
    }

    /**
     * Gets the type of the request.
     * */
    public String getType() {
        return type;
    }

    public static String POST = "POST";
    public static String GET = "GET";
}
