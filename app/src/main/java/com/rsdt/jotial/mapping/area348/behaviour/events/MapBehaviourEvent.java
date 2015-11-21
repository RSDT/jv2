package com.rsdt.jotial.mapping.area348.behaviour.events;

import com.android.internal.util.Predicate;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 15-11-2015
 * Defines a event.
 * */
public abstract class MapBehaviourEvent<T> implements Predicate<T> {

    /**
     * The trigger for the event.
     * */
    private int trigger;

    /**
     * Initializes a new instance of MapBehaviourEvent.
     *
     * @param trigger The type of the trigger.
     * */
    public MapBehaviourEvent(int trigger)
    {
        this.trigger = trigger;
    }

    /**
     * Gets the trigger of the event.
     * */
    public int getTrigger() {
        return trigger;
    }

    /**
     * Gets the code that should be executed if the condition is met.
     * */
    public abstract void onConditionMet(Object[] args);

    /**
     * The constant for the trigger InfoWindow.
     * */
    public static final int MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW = 0;

}