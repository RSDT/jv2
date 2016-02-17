package com.rsdt.jotial.mapping.area348.clustering;

import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.rsdt.jotial.data.structures.area348.receivables.ScoutingGroepInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-2-2016
 * Description...
 */
public class ScoutingGroepAlgorithm extends NonHierarchicalDistanceBasedAlgorithm<ScoutingGroepInfo> {


    @Override
    public void removeItem(ScoutingGroepInfo item) {
        getItems().remove(item);
    }
}
