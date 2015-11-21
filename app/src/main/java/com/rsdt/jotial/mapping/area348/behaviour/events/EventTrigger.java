package com.rsdt.jotial.mapping.area348.behaviour.events;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-11-2015
 *
 */
public class EventTrigger {

    /**
     * The trigger of the event.
     * */
    private int trigger;

    /**
     * The object that will be used to check if the MapBehaviourEvent should apply.
     * */
    private Object applier;

    /**
     * The arguments given with to the MapBehaviourEvent.onConditionMet().
     * */
    private Object[] args;

    /**
     * Initializes a new instance of EventTrigger.
     * */
    public EventTrigger(int trigger, Object applier, Object[] args)
    {
        this.trigger = trigger;
        this.applier = applier;
        this.args = args;
    }

    /**
     * Gets the trigger of the event.
     * */
    public int getTrigger() {
        return trigger;
    }

    /**
     * Gets the applier of the event.
     * */
    public Object getApplier() {
        return applier;
    }

    /**
     * Gets the arguments of the event.
     * */
    public Object[] getArgs() {
        return args;
    }
}
