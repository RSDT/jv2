package com.rsdt.jotial.mapping.area348.behaviour.events;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 18-11-2015
 * Class that implements all the callbacks possible for the MapBehaviour, and chains on the callbacks.
 */
public final class MapBehaviourEventRaiser {

    /**
     * The list with the behaviour's events.
     * */
    protected ArrayList<MapBehaviourEvent> events = new ArrayList<>();

    /**
     * Gets the events that this EventRaiser will raise.
     * */
    public ArrayList<MapBehaviourEvent> getEvents() {
        return events;
    }

    /**
     * Raises a map behaviour event.
     * */
    public void raiseEvent(EventTrigger eventTrigger)
    {
        MapBehaviourEvent mapBehaviourEvent;
        for(int e = 0; e < events.size(); e++)
        {
            mapBehaviourEvent = events.get(e);

            /**
             * Checks if the event has the same trigger as the EventTrigger.
             * */
            if(mapBehaviourEvent.getTrigger() == eventTrigger.getTrigger())
            {
                /**
                 * Checks if this EventTrigger should apply on the current event.
                 * */
                if(mapBehaviourEvent.apply(eventTrigger.getApplier()))
                {
                    mapBehaviourEvent.onConditionMet(eventTrigger.getArgs());
                }
            }
        }
    }
}
