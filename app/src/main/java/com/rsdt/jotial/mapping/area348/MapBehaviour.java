package com.rsdt.jotial.mapping.area348;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-11-2015
 * Description...
 */
public abstract class MapBehaviour<T> implements Predicate<T>, GoogleMap.OnMarkerClickListener {


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}
