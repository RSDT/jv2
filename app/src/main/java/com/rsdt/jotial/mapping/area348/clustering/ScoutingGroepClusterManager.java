package com.rsdt.jotial.mapping.area348.clustering;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;
import com.rsdt.jotial.data.structures.area348.receivables.ScoutingGroepInfo;

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


    public ArrayList<ScoutingGroepInfo> getItems() {
        return items;
    }
}
