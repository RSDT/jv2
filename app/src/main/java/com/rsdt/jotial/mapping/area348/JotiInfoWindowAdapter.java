package com.rsdt.jotial.mapping.area348;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.rsdt.jotiv2.R;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 28-10-2015
 * The InfoWindowAdapter for the JotiApp.
 */
public class JotiInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


    /**
     * Initializes a new instance of JotiInfoWindowAdapter.
     * */
    public JotiInfoWindowAdapter(LayoutInflater layoutInflater)
    {
        this.layoutInflater = layoutInflater;
    }

    /**
     * Initializes a new instance of JotiInfoWindowAdapter.
     * */
    public JotiInfoWindowAdapter(LayoutInflater layoutInflater, OnGetInfoWindowCallback onGetInfoWindowCallback)
    {
        this.layoutInflater = layoutInflater;
        listeners.add(onGetInfoWindowCallback);
    }

    /**
     * The LayoutInflater used for inflating the InfoWindow.
     * */
    private LayoutInflater layoutInflater;

    /**
     * The list of listeners.
     * */
    private ArrayList<OnGetInfoWindowCallback> listeners = new ArrayList<>();

    @Override
    public View getInfoWindow(Marker marker) {

        /**
         * Inflate the info window.
         * */
        View view = layoutInflater.inflate(R.layout.info_window, null);

        /**
         * Allocate callback outside loop.
         * */
        OnGetInfoWindowCallback callback;

        /**
         * Loop through each listener.
         * */
        for(int i = 0; i < listeners.size(); i++)
        {
            /**
             * Set the callback buffer.
             * */
            callback = listeners.get(i);

            /**
             * Check if the callback is not null, if so invoke the callback.
             * */
            if(callback != null) callback.onGetInfoWindow(view, marker);
        }

        /**
         * Return the view.
         * */
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 28-10-2015
     * Interface for creating a listener, on get info window event.
     */
    public interface OnGetInfoWindowCallback
    {
        /**
         * Invoked when the info window is created.
         * */
        void onGetInfoWindow(View view, Marker marker);
    }

}
