package com.rsdt.jotial.mapping.area348.extraction;

import com.rsdt.jotial.communication.ApiResult;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-10-2015
 * Interface that defines a contract for extracting data.
 */
public interface Extractor<T extends ApiResult> {

    Object[] extract(T result);

}
