package com.rsdt.jotial.mapping.area348.behaviour;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.rsdt.jotial.mapping.area348.MapManager;
import com.rsdt.jotial.mapping.area348.data.MapData;
import com.rsdt.jotiv2.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 3-11-2015
 * Defines the behaviour for ScoutingGroepen.
 */
public class ScoutingGroepBehaviour extends InfoBehaviour {

    /**
     * Initializes a new instance of ScoutingGroepBehaviour.
     * NOTE: DO NOT USE ASSOCIATED STATE, IT'S NULL.
     * */
    public ScoutingGroepBehaviour() {
        super(null);
    }

    @Override
    protected String getIndicator() {
        return "sc";
    }

    @Override
    public void onGetInfoWindow(View view, Marker marker) {

        String[] data = marker.getTitle().split(";");

        ((TextView) view.findViewById(R.id.infoWindow_infoType)).setText("ScoutingGroepInfo");

        ((TextView)view.findViewById(R.id.infoWindow_naam)).setText(data[1]);

        ((TextView)view.findViewById(R.id.infoWindow_dateTime_adres)).setText(data[2]);

        ((TextView)view.findViewById(R.id.infoWindow_coordinaat)).setText(data[3] + " , " + data[4]);

    }

    @Override
    public boolean apply(Marker marker) {
        return marker.getTitle().split(";")[0].equals(getIndicator());
    }

    @Override
    public MapData toMapData(String data) {
        return null;
    }
}
