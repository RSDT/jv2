package com.rsdt.jotial.mapping.area348.data;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Object that holds the map items of the MapPartState.
 */
public class MapBindObject implements CapableActionPreformer {

    /**
     * Initializes a new instance of MapBindObject.
     * */
    public MapBindObject()
    {

    }

    /**
     * The markers of the MapBindObject.
     * */
    private ArrayList<Marker> markers = new ArrayList<>();

    /**
     * The polylines of the MapBindObject.
     * */
    private ArrayList<Polyline> polylines = new ArrayList<>();

    /**
     * The circles of the MapBindObject.
     * */
    private ArrayList<Circle> circles = new ArrayList<>();

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    @Override
    /**
     * Preforms a action to each item.
     * */
    public void preform(ItemAction action) {

        if(action.getType() == Marker.class)
        {
            for(int i = 0; i < markers.size(); i++)
            {
                action.preform(markers.get(i));
            }
        }

        if(action.getType() == Polyline.class)
        {
            for(int i = 0; i < markers.size(); i++)
            {
                action.preform(markers.get(i));
            }
        }

        if(action.getType() == Circle.class)
        {
            for(int i = 0; i < markers.size(); i++)
            {
                action.preform(markers.get(i));
            }
        }
    }

    @Override
    /**
     * Preforms a action to each item when the condition is met.
     * */
    public void preform(ItemAction action, Predicate condition) {
        if(action.getType() == Marker.class)
        {
            for(int i = 0; i < markers.size(); i++)
            {
                if(condition.apply(markers.get(i)))
                {
                    action.preform(markers.get(i));
                }
            }
        }

        if(action.getType() == Polyline.class)
        {
            for(int i = 0; i < polylines.size(); i++)
            {
                if(condition.apply(polylines.get(i))) {
                    action.preform(polylines.get(i));
                }
            }
        }

        if(action.getType() == Circle.class)
        {
            for(int i = 0; i < circles.size(); i++)
            {
                if(condition.apply(circles.get(i))) {
                    action.preform(circles.get(i));
                }
            }
        }
    }
}
