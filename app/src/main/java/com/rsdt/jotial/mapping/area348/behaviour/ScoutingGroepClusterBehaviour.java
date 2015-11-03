package com.rsdt.jotial.mapping.area348.behaviour;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.rsdt.jotial.mapping.area348.data.MapData;
import com.rsdt.jotial.mapping.area348.data.MapPartState;
import com.rsdt.jotiv2.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 3-11-2015
 * Defines a behaviour for a Scouting Groep Cluster.
 */
public class ScoutingGroepClusterBehaviour extends InfoBehaviour {


    public ScoutingGroepClusterBehaviour() {
        super(null);
    }

    @Override
    protected String getIndicator() {
        return "scc";
    }

    @Override
    public void onGetInfoWindow(View view, Marker marker) {
        String[] data = marker.getTitle().split(";");

        ((TextView) view.findViewById(R.id.infoWindow_infoType)).setText("Cluster");

        ((TextView)view.findViewById(R.id.infoWindow_naam)).setText("ScoutingGroep Cluster");

        ((TextView)view.findViewById(R.id.infoWindow_dateTime_adres)).setText("Size: "+ data[1]);

        ((TextView)view.findViewById(R.id.infoWindow_coordinaat)).setText(data[2] + " , " + data[3]);

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
