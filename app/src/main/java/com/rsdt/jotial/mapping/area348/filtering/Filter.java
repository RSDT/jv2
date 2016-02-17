package com.rsdt.jotial.mapping.area348.filtering;

import com.rsdt.jotial.mapping.area348.behaviour.MapBehaviourManager;
import com.rsdt.jotial.mapping.area348.clustering.ScoutingGroepClusterManager;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-2-2016
 * Defines a filter for the map.
 */
public interface Filter {


    void apply(ScoutingGroepClusterManager scoutingGroepClusterManager, MapBehaviourManager mapBehaviourManager);

}
