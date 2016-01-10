package com.rsdt.jotial.mapping.area348;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.rsdt.jotiv2.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 28-10-2015
 * The InfoWindowAdapter for the joti app.
 */
public class JotiInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    /**
     * Initializes a new instance of JotiInfoWindowAdapter.
     * */
    public JotiInfoWindowAdapter(LayoutInflater layoutInflater, OnGetInfoWindowCallback onGetInfoWindowCallback)
    {
        this.layoutInflater = layoutInflater;
        this.onGetInfoWindowCallback = onGetInfoWindowCallback;
    }

    /**
     * The LayoutInflater used for inflating the InfoWindow.
     * */
    private final LayoutInflater layoutInflater;

    /**
     * The listener.
     * TODO: Make a list of listeners?
     * */
    private final OnGetInfoWindowCallback onGetInfoWindowCallback;

    @Override
    public View getInfoWindow(Marker marker) {

        /**
         * Inflate the info window.
         * */
        View view = layoutInflater.inflate(R.layout.info_window, null);

        /**
         * Invoke the listener.
         * */
        onGetInfoWindowCallback.onGetInfoWindow(view, marker);
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
