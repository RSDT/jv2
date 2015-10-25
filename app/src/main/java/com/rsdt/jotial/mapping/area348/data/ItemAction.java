package com.rsdt.jotial.mapping.area348.data;

import java.lang.reflect.Type;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Interface for creating a item action.
 */
public interface ItemAction<T> {

    Type getType();

    void preform(T item);
}
