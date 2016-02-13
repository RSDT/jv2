package com.rsdt.jotial.mapping.area348.behaviour;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rsdt.jotial.data.structures.area348.receivables.BaseInfo;
import com.rsdt.jotial.data.structures.area348.receivables.FotoOpdrachtInfo;
import com.rsdt.jotial.mapping.area348.behaviour.events.MapBehaviourEvent;
import com.rsdt.jotial.mapping.area348.data.GraphicalMapData;
import com.rsdt.jotial.mapping.area348.data.MapData;
import com.rsdt.jotial.mapping.area348.data.MapDataPair;
import com.rsdt.jotiv2.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 21-11-2015
 * Defines the behaviour of the FotoOpdracht.
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
                if(marker.getTitle() != null) {
                    return (marker.getTitle().startsWith("foto"));
                }
                return false;
            }

            @Override
            public void onConditionMet(Object[] args) {

                View view = (View) args[0];

                GraphicalMapDataPair<Marker, MarkerOptions> pair = findPair(((Marker) args[1]).getId());

                FotoOpdrachtInfo associatedInfo = (FotoOpdrachtInfo) pair.second.second.get(0);

                ((TextView) view.findViewById(R.id.infoWindow_infoType)).setText("FotoOpdrachtInfo");

                ((TextView) view.findViewById(R.id.infoWindow_naam)).setText(associatedInfo.info);

                ((TextView) view.findViewById(R.id.infoWindow_dateTime_adres)).setText(associatedInfo.extra);

                ((TextView) view.findViewById(R.id.infoWindow_coordinaat)).setText(associatedInfo.latitude + " , " + associatedInfo.longitude);
            }
        });
    }

    @Override
    public void merge(GraphicalMapData other) {

        /**
         * Clear our own marker list and add all the other's.
         * */
        for(int i = 0; i < markers.size(); i++)
        {
            markers.get(i).remove();
        }
        markers.clear();
        markers.addAll(other.getMarkers());

        /**
         * Destroy the other, since it's no longer needed.
         * */
        other.destroy();
    }


    /**
     * Deserializes a string of data to the FotoOpdrachtMapBehaviour MapData.
     * */
    public static final MapData toMapData(String data)
    {

         /**
         * Create a buffer to store the data.
         * */
        MapData buffer = MapData.empty();

        /**
         * Deserialize the array of FotoOpdrachtInfo.
         * */
        FotoOpdrachtInfo[] fotoOpdrachten = FotoOpdrachtInfo.fromJsonArray(data);

        /**
         * Loops through each FotoOpdrachtInfo.
         * */
        for (int i = 0; i < fotoOpdrachten.length; i++) {
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.anchor(0.5f, 0.5f);
            mOptions.position(new LatLng(fotoOpdrachten[i].latitude, fotoOpdrachten[i].longitude));
            mOptions.title(buildInfoTitle(new String[]{"foto"}, " "));

            if(fotoOpdrachten[i].klaar)
            {
                mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.foto_klaar));
            }
            else
            {
                mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.foto_todo));
            }

            buffer.getMarkers().add(new MapDataPair<>(mOptions, new ArrayList<BaseInfo>(Arrays.asList(fotoOpdrachten[i]))));
        }
        return buffer;
    }

}
