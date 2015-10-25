package com.rsdt.jotial.mapping.area348.extraction;

import com.google.android.gms.maps.model.MarkerOptions;
import com.rsdt.jotial.communication.ApiResult;
import com.rsdt.jotial.data.structures.area348.receivables.VosInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-10-2015
 * Class that holds all the different extractors.
 */
public class Extractors {


    public static Extractor<ApiResult> VosExtractor = new Extractor<ApiResult>() {
        @Override
        public Object[] extract(ApiResult result) {

            /**
             * Deserialize the json into a VosInfo array.
             * */
            VosInfo[] vossen = VosInfo.fromJsonArray(result.getData());

            /**
             * Gets the collection associated with the team value.
             * */
            List<MarkerOptions> markers = new ArrayList<>();

            return new Object[0];
        }
    };




}
