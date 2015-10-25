package com.rsdt.jotial.mapping.area348;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;

import com.rsdt.jotial.communication.ApiManager;
import com.rsdt.jotial.mapping.area348.data.ItemAction;
import com.rsdt.jotial.mapping.area348.data.MapBindObject;
import com.rsdt.jotial.mapping.area348.data.StorageObject;

import java.lang.reflect.Type;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * The final control unit for map managing.
 */

public class MapManager {

    /**
     * Initializes a new instance of MapManager.
     *
     * @param googleMap The GoogleMap the MapManager should manage on.
     * */
    public MapManager(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
    }

    /**
     * The GoogleMap the MapManager should work with.
     * */
    private GoogleMap googleMap;

    /**
     * The ApiManager for requesting data, and receiving it.
     * TODO: Look NOTE.
     * NOTE: ApiManager is should be static here, because else data could be lost.
     * While data is still beining received the activity could be dumped and thereby the data.
     * This way we maintain the data.
     * */
    private ApiManager apiManager = new ApiManager();

    /**
     * Syncs the MapBindObject of each state with it's storage.
     * */
    public void sync()
    {

        StorageObject storageObject = new StorageObject();

        storageObject.getMarkers().add(new MarkerOptions().title("1").position(new LatLng(52, 52)));
        storageObject.getMarkers().add(new MarkerOptions().title("2").position(new LatLng(49, 52)));

        storageObject.preform(new ItemAction<MarkerOptions>() {

            @Override
            public Type getType() {
                return MarkerOptions.class;
            }

            @Override
            public void preform(MarkerOptions item) {
            }
        }, new Predicate<MarkerOptions>() {
            @Override
            public boolean apply(MarkerOptions markerOptions) {
                return markerOptions.getTitle() == "1";
            }
        });


        MapBindObject bindObject = new MapBindObject();

        bindObject.getMarkers().add(googleMap.addMarker(storageObject.getMarkers().get(0)));
        bindObject.getMarkers().add(googleMap.addMarker(storageObject.getMarkers().get(1)));


/*        *//**
         * Example of a
         * *//*
        bindObject.preform(new ItemAction<Marker>() {
            @Override
            public Type getType() {
                return Marker.class;
            }

            @Override
            public void preform(Marker item) {
                item.setVisible(false);
            }
        });

        *//**
         * Example of a action with a condition.
         * *//*
        bindObject.preform(new ItemAction<Marker>() {
            @Override
            public Type getType() {
                return Marker.class;
            }

            @Override
            public void preform(Marker item) {
                item.setVisible(false);
            }
        }, new Predicate<Marker>() {
            @Override
            public boolean apply(Marker marker) {
                return marker.getId() == "1";
            }
        });*/

    }

    /**
     * Updates the StorageObject of each state.
     * */
    public void update()
    {

    }




}