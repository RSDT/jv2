package com.rsdt.jotial.mapping.area348.data;


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-10-2015
 * Defines a contract for data-to-map data behaviour.
 * TODO: Naming is weird on this one.
 */
public interface ToMapDataBehaviour {

    MapData toMapData(String data);
}

