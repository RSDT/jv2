package com.rsdt.jotial.mapping.area348.behaviour;

import com.google.android.gms.maps.GoogleMap;

import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEventRaiser;
import com.rsdt.jotial.mapping.area348.data.GraphicalMapData;
import com.rsdt.jotial.mapping.area348.data.MapData;


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 15-11-2015
 * Defines a MapBehaviour, a MapBehaviour is build up out of MapEvents with a condition sticked to them,
 * together named MapBehaviourEvent. On these events certain code is executed if the condition is met.
 */
public abstract class MapBehaviour extends GraphicalMapData {

    /**
     * The event raiser for this behaviour.
     * */
    protected MapBehaviourEventRaiser eventRaiser = new MapBehaviourEventRaiser();

    /**
     * Gets the event raiser of the MapBehaviour.
     * */
    public MapBehaviourEventRaiser getEventRaiser() {
        return eventRaiser;
    }

    /**
     * Initializes a new instance of MapBehaviour.
     *
     * @param mapData The MapData where the GraphicalMapData should be created from.
     * @param googleMap The GoogleMap used to create the graphical part.
     * */
    public MapBehaviour(MapData mapData, GoogleMap googleMap)
    {
        super(mapData, googleMap);
    }

    /**
     * Merges the GraphicalMapData with the a other MapData of the same type.
     *
     * @param other The other GraphicalMapData that should be merged with.
     * */
    public abstract void merge(GraphicalMapData other);

    /**
     * Helper method to build a title for a info.
     * */
    public static String buildInfoTitle(String[] args, String seperator)
    {
        String buffer = args[0];
        for(int i = 1; i < args.length; i++)
        {
            buffer += seperator + args[i];
        }
        return buffer;
    }
}
