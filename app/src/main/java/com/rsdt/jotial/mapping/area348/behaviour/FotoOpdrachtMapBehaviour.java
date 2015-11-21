package com.rsdt.jotial.mapping.area348.behaviour;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rsdt.jotial.data.structures.area348.receivables.FotoOpdrachtInfo;
import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEvent;
import com.rsdt.jotial.mapping.area348.data.MapData;
import com.rsdt.jotiv2.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 21-11-2015
 * Description...
 */
public class FotoOpdrachtMapBehaviour extends MapBehaviour {

    /**
     * Initializes a new instance of MapBehaviour.
     *
     * @param mapData   The MapData where the GraphicalMapData should be created from.
     * @param googleMap The GoogleMap used to create the graphical part.
     */
    public FotoOpdrachtMapBehaviour(MapData mapData, GoogleMap googleMap) {
        super(mapData, googleMap);

        super.eventRaiser.getEvents().add(new MapBehaviourEvent<Marker>(MapBehaviourEvent.MAP_BEHAVIOUR_EVENT_TRIGGER_INFO_WINDOW) {
            @Override
            public boolean apply(Marker marker) {
                return (marker.getTitle().startsWith("foto"));
            }

            @Override
            public void onConditionMet(Object[] args) {

                View view = (View)args[0];

                GraphicalMapDataPair<Marker, MarkerOptions> pair = findPair(((Marker)args[1]).getId());

                FotoOpdrachtInfo associatedInfo = (FotoOpdrachtInfo)pair.second.second.get(0);

                ((TextView)view.findViewById(R.id.infoWindow_infoType)).setText("FotoOpdrachtInfo");

            }
        });
    }


    public static final MapData toMapData(String data)
    {
        return null;
    }


}
