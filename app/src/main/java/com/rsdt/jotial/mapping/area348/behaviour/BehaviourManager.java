package com.rsdt.jotial.mapping.area348.behaviour;

import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.rsdt.jotial.mapping.area348.JotiInfoWindowAdapter;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 3-11-2015
 * Manages the various behaviours and invokes them.
 */
public class BehaviourManager implements JotiInfoWindowAdapter.OnGetInfoWindowCallback {

    /**
     * Initializes a new instance of BehaviourManager.
     * */
    public BehaviourManager()
    {

    }

    /**
     * The list that stores the behaviours.
     * */
    private ArrayList<InfoBehaviour> behaviours = new ArrayList<>();

    /**
     * Registers a behaviour.
     * */
    public boolean registerBehaviour(InfoBehaviour behaviour)
    {
        return behaviours.add(behaviour);
    }

    /**
     * Unregisters a behaviour.
     * */
    public boolean unregisterBehaviour(InfoBehaviour behaviour)
    {
        return behaviours.remove(behaviour);
    }

    @Override
    public void onGetInfoWindow(View view, Marker marker) {
        for(int i = 0; i < behaviours.size(); i++)
        {
            InfoBehaviour currentBehaviour = behaviours.get(i);
            if(currentBehaviour.apply(marker))
            {
                currentBehaviour.onGetInfoWindow(view, marker);
            }
        }
    }

    /**
     * Destroys the behaviour manager by dumping the behaviours.
     * */
    public void destroy()
    {
        this.behaviours.clear();
        this.behaviours = null;
    }

}
