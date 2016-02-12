package com.rsdt.jotial.communication;

import java.util.Date;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 19-10-2015
 * Class that represents the result for a ApiRequest.
 */
public class ApiResult {


    /**
     * Initializes a new instance of ApiResult.
     * */
    public ApiResult(ApiRequest request, String data)
    {
        this.request = request;
        this.data = data;
    }

    /**
     * Initializes a new instance of ApiResult.
     * */
    public ApiResult(ApiRequest request, String data, int responseCode)
    {
        this.request = request;
        this.data = data;
        this.responseCode = responseCode;
    }

    /**
     * The associated request.
     * */
    private ApiRequest request;

    /**
     * The data that the request resulted.
     * */
    private String data;

    /**
     * The response code that has been received.
     * */
    private int responseCode;

    public ApiRequest getRequest() {
        return request;
    }

    public String getData() {
        return data;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
