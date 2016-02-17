package com.rsdt.jotial.mapping.area348.filtering;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-2-2016
 * Description...
 */
public class FilterUtil {


    /**
     * Filters a list on a condition.
     *
     * @param list The list to filter.
     * @param condition The condition to filter the list.
     * @return ArrayList with the items in the list that applied to the condition.
     * */
    public static <T> ArrayList<T> filterListOnCondition(List<T> list, Predicate<T> condition)
    {
        ArrayList<T> applies = new ArrayList<>();
        T item;
        for(int i = 0; i < list.size(); i++)
        {
            item = list.get(i);
            if(condition.apply(item))
            {
                applies.add(item);
            }
        }
        return applies;
    }

    /**
     * Filters a list on a condition.
     *
     * @param list The list to filter.
     * @param condition The condition to filter the list.
     * @param inverted Value indicating if the condition should be inverted.
     * @return ArrayList with the items in the list that applied to the condition.
     * */
    public static <T> ArrayList<T> filterListOnCondition(List<T> list, Predicate<T> condition, boolean inverted)
    {
        ArrayList<T> applies = new ArrayList<>();
        T item;
        for(int i = 0; i < list.size(); i++)
        {
            item = list.get(i);

            if(inverted)
            {
                if(!condition.apply(item))
                {
                    applies.add(item);
                }
            }
            else
            {
                if(condition.apply(item))
                {
                    applies.add(item);
                }
            }
        }
        return applies;
    }


}
