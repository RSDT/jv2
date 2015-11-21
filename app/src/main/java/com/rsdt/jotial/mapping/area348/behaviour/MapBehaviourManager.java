package com.rsdt.jotial.mapping.area348.behaviour;

import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.rsdt.jotial.mapping.area348.JotiInfoWindowAdapter;
import com.rsdt.jotial.mapping.area348.behaviour.events.EventTrigger;
import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEvent;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-11-2015
 * Manages the behaviours.
 */
public class MapBehaviourManager extends ArrayList<MapBehaviour> implements JotiInfoWindowAdapter.OnGetInfoWindowCallback {

    /**
     * Initializes a new instance of MapBehaviourManager.
     * */
    public MapBehaviourManager()
    {

    }

    /**
     * The special events of the MapBehaviourManager.
     * */
    private ArrayList<MapBehaviourEvent> sEvents = new ArrayList<>();

    /**
     * Gets the special events of the MapBehaviourManager.
     * */
    public ArrayList<MapBehaviourEvent> getsEvents() {
        return sEvents;
    }

    @Override
    public void onGetInfoWindow(View view, Marker marker) {
        raiseEvent(new EventTrigger(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW, marker, new Object[]{ view, marker }));
    }

    public void raiseEvent(EventTrigger eventTrigger)
    {
        Log.i("MapBehaviourEventRaiser", "raiseEvent() - event with trigger: " + eventTrigger.getTrigger() + ", applier: " + eventTrigger.getApplier().toString());

        /**
         * Loop through each behaviour.
         * */
        for(int i = 0; i < this.size(); i++)
        {
            /**
             * Raises the event on the EventRaiser, he will raise it on.
             * */
            this.get(i).eventRaiser.raiseEvent(eventTrigger);
        }
    }

    public void destroy()
    {
        super.clear();
        sEvents.clear();
    }


}
