package com.rsdt.jotial.mapping.area348.clustering;

import android.content.Context;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.rsdt.jotial.data.structures.area348.receivables.ScoutingGroepInfo;
import com.rsdt.jotial.mapping.area348.filtering.FilterUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-2-2016
 * Description...
 */
public class ScoutingGroepClusterManager extends ClusterManager<ScoutingGroepInfo> {

    /**
     * The list that holds the items, so that we can access them.
     * */
    private ArrayList<ScoutingGroepInfo> items = new ArrayList<>();

    public ScoutingGroepClusterManager(Context context, GoogleMap map) {

        super(context, map);
        setAlgorithm(new ScoutingGroepAlgorithm());
    }

    @Override
    public void addItem(ScoutingGroepInfo myItem) {
        super.addItem(myItem);
        items.add(myItem);
    }

    @Override
    public void addItems(Collection<ScoutingGroepInfo> items) {
        super.addItems(items);
        items.addAll(items);
    }

    @Override
    public void removeItem(ScoutingGroepInfo item) {
        super.removeItem(item);
        items.remove(item);
    }

    @Override
    public void clearItems() {
        super.clearItems();
        items.clear();
    }

    /**
     * Adds the local stored items to the ClusterManager if the item applies to a certain condition.
     *
     * @param condition The condition where the item needs to apply to.
     * */
    public void addLocalItemsOnCondition(Predicate<ScoutingGroepInfo> condition)
    {
        addItems(FilterUtil.filterListOnCondition(this.items, condition));
    }

    /**
     * Adds the local stored items to the ClusterManager if the item applies to a certain condition.
     *
     * @param condition The condition where the item needs to apply to.
     * */
    public void addLocalItemsOnConditionInverted(Predicate<ScoutingGroepInfo> condition)
    {
        addItems(FilterUtil.filterListOnCondition(this.items, condition, true));
    }

    /**
     * Only clears the cluster items of the ClusterManager, but not
     * of the ScoutingGroepClusterManager's local collection.
     * TODO: Think of a better name for this, and a better way.
     * */
    public void clearClusterItems()
    {
        super.clearItems();
    }


    public ArrayList<ScoutingGroepInfo> getItems() {
        return items;
    }
}
