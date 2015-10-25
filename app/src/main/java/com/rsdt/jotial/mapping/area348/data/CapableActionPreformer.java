package com.rsdt.jotial.mapping.area348.data;

import com.android.internal.util.Predicate;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Description...
 */
public interface CapableActionPreformer {

    void preform(ItemAction action);


    void preform(ItemAction action, Predicate condition);

}
