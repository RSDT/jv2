package com.rsdt.jotial.mapping.area348.data;

import android.os.Parcel;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.rsdt.jotial.data.structures.area348.receivables.BaseInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 22-10-2015
 * Description...
 */
public class BaseInfoClusterItem extends BaseInfo implements ClusterItem {


    /**
     * Initializes a new instance of BaseInfo from the parcel.
     *
     * @param in
     */
    protected BaseInfoClusterItem(Parcel in) {
        super(in);
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(super.latitude,  super.longitude);
    }
}
