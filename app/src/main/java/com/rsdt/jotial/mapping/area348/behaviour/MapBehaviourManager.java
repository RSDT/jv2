package com.rsdt.jotial.mapping.area348.behaviour;

import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.rsdt.jotial.mapping.area348.JotiInfoWindowAdapter;
import com.rsdt.jotial.mapping.area348.behaviour.events.EventTrigger;
import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEvent;
import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEventRaiser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-11-2015
 * Manages the behaviours.
 */
public class MapBehaviourManager extends HashMap<String, MapBehaviour> implements JotiInfoWindowAdapter.OnGetInfoWindowCallback {

    /**
     * Initializes a new instance of MapBehaviourManager.
     * */
    public MapBehaviourManager()
    {

    }

    /**
     * The event raiser for the special events.
     * */
    private MapBehaviourEventRaiser sEventRaiser = new MapBehaviourEventRaiser();

    /**
     * Gets the special event raiser.
     * */
    public MapBehaviourEventRaiser getsEventRaiser() {
        return sEventRaiser;
    }

    @Override
    public void onGetInfoWindow(View view, Marker marker) {
        raiseEvent(new EventTrigger(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW, marker, new Object[]{view, marker}));
    }

    public void raiseEvent(EventTrigger eventTrigger)
    {
        /**
         * Loop through each entry.
         * */
        for(Map.Entry<String, MapBehaviour> entry : this.entrySet())
        {
            /**
             * Raise the event on the behaviour via the behaviour's event raiser.
             * */
            entry.getValue().eventRaiser.raiseEvent(eventTrigger);
        }

        this.sEventRaiser.raiseEvent(eventTrigger);
    }

    public void destroy()
    {
        super.clear();
    }


}
