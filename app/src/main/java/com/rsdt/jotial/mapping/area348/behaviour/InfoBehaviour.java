package com.rsdt.jotial.mapping.area348.behaviour;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.model.Marker;
import com.rsdt.jotial.mapping.area348.JotiInfoWindowAdapter;
import com.rsdt.jotial.mapping.area348.data.MapPartState;
import com.rsdt.jotial.mapping.area348.data.ToMapDataBehaviour;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-11-2015
 * Defines a default behaviour for a BaseInfo.
 */
public abstract class InfoBehaviour implements ToMapDataBehaviour, JotiInfoWindowAdapter.OnGetInfoWindowCallback, Predicate<Marker> {

    public InfoBehaviour(MapPartState mapPartState)
    {
        this.associatedMapPartState = mapPartState;
    }

    /**
     * Gets the associated MapPartState of the Behaviour.
     * */
    protected MapPartState associatedMapPartState;

    /**
     * Gets the indicator of the InfoBehaviour.
     * */
    protected abstract String getIndicator();


    /**
     * Creates a identifier for the this InfoBehaviour.
     *
     * @param args The arguments that should be included in the identifier.
     * @param seperator The seperator expression.
     *
     * @return The identifier formatted as INDICATOR_arg0_arg1_arg2 and so on. The _ represents the seperator.
     * */
    public String createIdentifier(String[] args, String seperator)
    {
        String buffer = getIndicator();
        for(int i = 0; i < args.length; i++)
        {
            buffer += seperator + args[i];
        }
        return buffer;
    }
}
